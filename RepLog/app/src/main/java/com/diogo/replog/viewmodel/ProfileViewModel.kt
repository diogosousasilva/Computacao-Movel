package com.diogo.replog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diogo.replog.data.model.User
import com.diogo.replog.data.preferences.OnboardingDataStore
import com.diogo.replog.data.repository.AuthRepository
import com.diogo.replog.data.repository.UserRepository
import com.diogo.replog.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false,
    // Computed dynamically from the workouts subcollection — always accurate
    val totalWorkouts: Int = 0,
    val totalVolumeKg: Double = 0.0,
)

/**
 * ViewModel for Profile, Settings, About screens.
 */
class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val workoutRepository = WorkoutRepository()
    private val authRepository = AuthRepository()

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            // Load user profile and workout totals in parallel
            val userResult = userRepository.getCurrentUser()
            val workoutsResult = workoutRepository.getRecentWorkouts(limit = 1000)

            val user = userResult.getOrNull()
            val workouts = workoutsResult.getOrDefault(emptyList())
            val totalWorkouts = workouts.size
            val totalVolumeKg = workouts.sumOf { it.totalVolumeKg }

            if (userResult.isSuccess) {
                _state.value = ProfileState(
                    user = user,
                    isLoading = false,
                    totalWorkouts = totalWorkouts,
                    totalVolumeKg = totalVolumeKg,
                )
            } else {
                _state.value = ProfileState(
                    isLoading = false,
                    error = userResult.exceptionOrNull()?.localizedMessage
                )
            }
        }
    }

    fun updateDisplayName(name: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUpdating = true, updateSuccess = false)
            userRepository.updateProfile(mapOf("displayName" to name)).fold(
                onSuccess = {
                    _state.value = _state.value.copy(
                        isUpdating = false,
                        updateSuccess = true,
                        user = _state.value.user?.copy(displayName = name)
                    )
                },
                onFailure = {
                    _state.value = _state.value.copy(
                        isUpdating = false,
                        error = it.localizedMessage
                    )
                }
            )
        }
    }

    fun uploadPhoto(imageBytes: ByteArray) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUpdating = true)
            userRepository.uploadProfilePhoto(imageBytes).fold(
                onSuccess = { url ->
                    _state.value = _state.value.copy(
                        isUpdating = false,
                        user = _state.value.user?.copy(photoUrl = url)
                    )
                },
                onFailure = {
                    _state.value = _state.value.copy(
                        isUpdating = false,
                        error = it.localizedMessage
                    )
                }
            )
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun resetProfile(context: android.content.Context, onResetComplete: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // Clear local preferences
            OnboardingDataStore.resetOnboarding(context)
            
            // Clear database biometric fields to allow fresh recalibration
            userRepository.updateProfile(mapOf(
                "weightKg" to 0f,
                "gender" to "",
                "goal" to "",
                "experience" to "",
                "heightCm" to 0,
                "weeklyFrequency" to ""
            ))
            
            _state.value = _state.value.copy(isLoading = false)
            onResetComplete()
        }
    }
}
