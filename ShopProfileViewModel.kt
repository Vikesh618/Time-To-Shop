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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Data class for a post
data class Post(val text: String = "", val shopId: String = "", val timestamp: com.google.firebase.Timestamp? = null)

class ShopProfileViewModel : ViewModel() {
    var shop by mutableStateOf<Shop?>(null)
    val posts = mutableStateListOf<Post>() // To hold the shop's posts
    var isLoading by mutableStateOf(true)

    init {
        fetchOwnedShopAndPosts()
    }

    private fun fetchOwnedShopAndPosts() {
        viewModelScope.launch {
            isLoading = true
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                try {
                    // Fetch the shop
                    val shopQuery = FirebaseFirestore.getInstance().collection("shops")
                        .whereEqualTo("ownerId", userId)
                        .limit(1)
                        .get()
                        .await()
                    
                    if (!shopQuery.isEmpty) {
                        val document = shopQuery.documents.first()
                        val fetchedShop = document.toObject(Shop::class.java)?.copy(id = document.id)
                        shop = fetchedShop
                        
                        // If shop exists, fetch its posts in real-time
                        fetchedShop?.id?.let { shopId ->
                            listenForPosts(shopId)
                        }
                    }
                } catch (e: Exception) {
                    // Handle error
                }
            }
            isLoading = false
        }
    }

    private fun listenForPosts(shopId: String) {
        FirebaseFirestore.getInstance().collection("posts")
            .whereEqualTo("shopId", shopId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val fetchedPosts = it.toObjects(Post::class.java)
                    posts.clear()
                    posts.addAll(fetchedPosts)
                }
            }
    }

    fun updateQueueSize(newSize: Int, onError: (String) -> Unit) {
        val shopId = shop?.id
        if (shopId == null) {
            onError("Cannot update queue size without a shop.")
            return
        }

        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance().collection("shops").document(shopId)
                    .update("currentQueueSize", newSize)
                    .await()
                shop = shop?.copy(currentQueueSize = newSize.toLong())
            } catch (e: Exception) {
                onError(e.message ?: "An error occurred.")
            }
        }
    }
}
