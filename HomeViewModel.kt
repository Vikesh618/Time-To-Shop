package com.example.timetoshop.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {
    val followedPosts = mutableStateListOf<Post>()
    var isLoading by mutableStateOf(true)

    init {
        fetchFollowedShopsPosts()
    }

    private fun fetchFollowedShopsPosts() {
        viewModelScope.launch {
            isLoading = true
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                try {
                    // Get the list of shops the user is following
                    val userDoc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
                    val followingIds = userDoc.get("following") as? List<String> ?: emptyList()

                    if (followingIds.isNotEmpty()) {
                        // Listen for real-time updates on posts from the followed shops
                        FirebaseFirestore.getInstance().collection("posts")
                            .whereIn("shopId", followingIds)
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .addSnapshotListener { snapshot, error ->
                                if (error != null) {
                                    isLoading = false // Stop loading on error
                                    return@addSnapshotListener
                                }
                                snapshot?.let {
                                    val posts = it.toObjects(Post::class.java)
                                    followedPosts.clear()
                                    followedPosts.addAll(posts)
                                    isLoading = false // Stop loading on success
                                }
                            }
                    } else {
                         isLoading = false
                    }
                } catch (e: Exception) {
                     isLoading = false
                    // Handle error
                }
            } else {
                isLoading = false
            }
        }
    }
}
