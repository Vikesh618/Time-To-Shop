package com.example.timetoshop.screens

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpViewModel : ViewModel() {
    var fullName by mutableStateOf("")
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    fun onFullNameChange(newName: String) {
        fullName = newName
    }

    fun onUsernameChange(newUsername: String) {
        username = newUsername
    }

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun signUp(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (fullName.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
            onError("All fields are required.")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError("Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            onError("Password must be at least 6 characters long.")
            return
        }

        viewModelScope.launch {
            try {
                val authResult = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
                // userType is no longer saved
                val user = hashMapOf(
                    "fullName" to fullName,
                    "username" to username,
                    "email" to email
                )
                FirebaseFirestore.getInstance().collection("users").document(authResult.user!!.uid)
                    .set(user)
                    .await()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "An unknown error occurred.")
            }
        }
    }
}
