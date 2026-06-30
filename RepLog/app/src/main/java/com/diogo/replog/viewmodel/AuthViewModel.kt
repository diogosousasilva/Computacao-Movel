package com.diogo.replog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diogo.replog.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Authentication state for Login/Register screens.
 */
sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class Error(val message: String) : AuthState()
}

/**
 * ViewModel for authentication operations.
 */
class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    val isLoggedIn: Boolean get() = authRepository.isLoggedIn

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signIn(email, password).fold(
                onSuccess = { _authState.value = AuthState.Success(it) },
            ) { _authState.value = AuthState.Error(it.localizedMessage ?: "Login failed") }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            _authState.value = AuthState.Error("Please fill in all fields")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signUp(email, password, displayName).fold(
                onSuccess = { _authState.value = AuthState.Success(it) },
            ) { _authState.value = AuthState.Error(it.localizedMessage ?: "Registration failed") }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Idle
    }

    fun signInWithGoogle(credential: com.google.firebase.auth.AuthCredential) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.signInWithCredential(credential).fold(
                onSuccess = { _authState.value = AuthState.Success(it) },
            ) { _authState.value = AuthState.Error(it.localizedMessage ?: "Google Sign-In failed") }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
