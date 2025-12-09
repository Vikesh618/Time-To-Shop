package com.example.timetoshop.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreatePostViewModel : ViewModel() {
    var postText by mutableStateOf("")

    fun onPostTextChange(newText: String) {
        postText = newText
    }

    fun createPost(shopId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (postText.isBlank()) {
            onError("Post cannot be empty.")
            return
        }

        viewModelScope.launch {
            try {
                val post = hashMapOf(
                    "text" to postText,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "shopId" to shopId
                )
                FirebaseFirestore.getInstance().collection("posts").add(post).await()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "An unknown error occurred.")
            }
        }
    }
}
