package com.example.timetoshop.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddCameraViewModel : ViewModel() {
    var cameraName by mutableStateOf("")
    var cameraUrl by mutableStateOf("")

    fun onCameraNameChange(newName: String) {
        cameraName = newName
    }

    fun onCameraUrlChange(newUrl: String) {
        cameraUrl = newUrl
    }

    fun linkCamera(shopId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (cameraName.isBlank() || cameraUrl.isBlank()) {
            onError("All fields are required.")
            return
        }

        viewModelScope.launch {
            try {
                val cameraData = hashMapOf(
                    "cameraName" to cameraName,
                    "cameraUrl" to cameraUrl
                )
                // Add camera info to a subcollection within the shop
                FirebaseFirestore.getInstance().collection("shops").document(shopId)
                    .collection("cameras").add(cameraData).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "An unknown error occurred.")
            }
        }
    }
}
