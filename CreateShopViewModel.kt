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

class CreateShopViewModel : ViewModel() {
    var shopName by mutableStateOf("")
    var shopAddress by mutableStateOf("")

    fun onShopNameChange(newName: String) {
        shopName = newName
    }

    fun onShopAddressChange(newAddress: String) {
        shopAddress = newAddress
    }

    fun saveShop(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            onError("You must be logged in to create a shop.")
            return
        }

        if (shopName.isBlank() || shopAddress.isBlank()) {
            onError("All fields are required.")
            return
        }

        viewModelScope.launch {
            try {
                val shop = hashMapOf(
                    "shopName" to shopName,
                    "shopAddress" to shopAddress,
                    "ownerId" to userId,
                    "currentQueueSize" to 0 // Added queue size
                )
                FirebaseFirestore.getInstance().collection("shops").add(shop).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "An unknown error occurred.")
            }
        }
    }
}
