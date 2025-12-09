package com.example.timetoshop.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class User(val fullName: String = "", val username: String = "", val email: String = "", val following: List<String> = emptyList(), val followers: List<String> = emptyList())

class ProfileViewModel : ViewModel() {
    var user by mutableStateOf<User?>(null)
    var hasShop by mutableStateOf(false)
    var isLoading by mutableStateOf(true)

    init {
        fetchCurrentUserAndShopStatus()
    }

    private fun fetchCurrentUserAndShopStatus() {
        viewModelScope.launch {
            isLoading = true
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val userId = firebaseUser.uid
                // Fetch user data
                try {
                    val userDoc = FirebaseFirestore.getInstance().collection("users").document(userId).get().await()
                    user = userDoc.toObject(User::class.java)
                } catch (e: Exception) {
                    // Handle user fetch error
                }

                // Check for shop ownership
                try {
                    val shopQuery = FirebaseFirestore.getInstance().collection("shops")
                        .whereEqualTo("ownerId", userId)
                        .limit(1)
                        .get()
                        .await()
                    hasShop = !shopQuery.isEmpty
                } catch (e: Exception) {
                    // Handle shop fetch error
                    hasShop = false
                }
            }
            isLoading = false
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }
}
