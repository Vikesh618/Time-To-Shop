package com.example.timetoshop.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Added currentQueueSize to the main Shop data class
data class Shop(val id: String = "", val shopName: String = "", val shopAddress: String = "", val ownerId: String = "", val currentQueueSize: Long = 0)

class ExploreViewModel : ViewModel() {
    private var allShops by mutableStateOf<List<Shop>>(emptyList())
    var filteredShops by mutableStateOf<List<Shop>>(emptyList())
    var isLoading by mutableStateOf(true)
    var searchQuery by mutableStateOf("")

    init {
        fetchShops()
    }

    private fun fetchShops() {
        viewModelScope.launch {
            isLoading = true
            try {
                val snapshot = FirebaseFirestore.getInstance().collection("shops").get().await()
                allShops = snapshot.documents.mapNotNull {
                    val shop = it.toObject(Shop::class.java)
                    shop?.copy(id = it.id)
                }
                filteredShops = allShops
            } catch (e: Exception) {
                // Handle exception
            }
            isLoading = false
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        filteredShops = if (query.isBlank()) {
            allShops
        } else {
            allShops.filter {
                it.shopName.contains(query, ignoreCase = true)
            }
        }
    }

    fun followShop(shopId: String, onError: (String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onError("You must be logged in to follow a shop.")
            return
        }

        viewModelScope.launch {
            try {
                // Add user to shop's followers
                FirebaseFirestore.getInstance().collection("shops").document(shopId)
                    .update("followers", FieldValue.arrayUnion(userId))
                    .await()

                // Add shop to user's following
                FirebaseFirestore.getInstance().collection("users").document(userId)
                    .update("following", FieldValue.arrayUnion(shopId))
                    .await()
            } catch (e: Exception) {
                onError(e.message ?: "An unknown error occurred.")
            }
        }
    }
}
