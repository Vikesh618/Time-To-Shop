package com.example.timetoshop.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ShopWithQueue(val id: String = "", val shopName: String = "", val currentQueueSize: Long = 0)

class QueueViewModel : ViewModel() {
    val followedShops = mutableStateMapOf<String, ShopWithQueue>()
    var isLoading by mutableStateOf(true)

    init {
        fetchFollowedShopsAndListenForUpdates()
    }

    private fun fetchFollowedShopsAndListenForUpdates() {
        viewModelScope.launch {
            isLoading = true
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                try {
                    val userDoc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
                    val followingIds = userDoc.get("following") as? List<String> ?: emptyList()

                    if (followingIds.isNotEmpty()) {
                        // Listen for real-time updates on the followed shops
                        FirebaseFirestore.getInstance().collection("shops")
                            .whereIn("__name__", followingIds)
                            .addSnapshotListener { snapshot, error ->
                                if (error != null) {
                                    isLoading = false // Stop loading on error
                                    return@addSnapshotListener
                                }

                                snapshot?.documents?.forEach { doc ->
                                    val shop = ShopWithQueue(
                                        id = doc.id,
                                        shopName = doc.getString("shopName") ?: "",
                                        currentQueueSize = doc.getLong("currentQueueSize") ?: 0
                                    )
                                    followedShops[doc.id] = shop
                                }
                                isLoading = false // Stop loading on success
                            }
                    } else {
                        isLoading = false
                    }
                } catch (e: Exception) {
                    isLoading = false
                    // Handle fetch error
                }
            } else {
                isLoading = false
            }
        }
    }
}
