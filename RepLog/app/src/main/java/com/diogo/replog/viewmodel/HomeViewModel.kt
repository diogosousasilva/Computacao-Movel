package com.diogo.replog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diogo.replog.data.cache.AppDataCache
import com.diogo.replog.data.model.User
import com.diogo.replog.data.model.Workout
import com.diogo.replog.data.repository.UserRepository
import com.diogo.replog.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * State for the Home Dashboard screen.
 */
data class HomeState(
    val user: User? = null,
    val recentWorkouts: List<Workout> = emptyList(),
    val todayWorkoutCount: Int = 0,
    val totalWorkoutCount: Int = 0,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
)

/**
 * ViewModel for the Home Dashboard.
 *
 * Reads pre-computed data from [AppDataCache] for instant display.
 * Falls back to a Firestore real-time stream for live workout list updates.
 */
class HomeViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val workoutRepository = WorkoutRepository()

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState.asStateFlow()

    init {
        // 1. Render instantly from cache if already ready
        val cached = AppDataCache.homeCache.value
        if (cached.isReady) {
            _homeState.value = HomeState(
                recentWorkouts = cached.recentWorkouts,
                todayWorkoutCount = cached.todayWorkoutCount,
                totalWorkoutCount = cached.totalWorkoutCount,
                isLoading = false
            )
        }

        // 2. Observe cache updates — whenever background prefetch finishes,
        //    push the new data to the UI with zero computation on Main thread
        AppDataCache.homeCache
            .onEach { cache ->
                if (cache.isReady) {
                    _homeState.value = _homeState.value.copy(
                        recentWorkouts = cache.recentWorkouts,
                        todayWorkoutCount = cache.todayWorkoutCount,
                        totalWorkoutCount = cache.totalWorkoutCount,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            }
            .catch { /* ignore */ }
            .launchIn(viewModelScope)

        // 3. Real-time Firestore stream keeps the workout list live (adds/deletes)
        workoutRepository.observeRecentWorkouts(limit = 5)
            .onEach { workouts ->
                _homeState.value = _homeState.value.copy(
                    recentWorkouts = workouts,
                    isLoading = false,
                    isRefreshing = false
                )
            }
            .catch { }
            .launchIn(viewModelScope)

        // 4. Load user profile (lightweight, no heavy computation)
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val user = userRepository.getCurrentUser().getOrNull()
            _homeState.value = _homeState.value.copy(user = user)
        }
    }

    /**
     * Pull-to-Refresh: triggers a full cache invalidation and re-fetch.
     * Screen stays reactive — isRefreshing spinner hides when cache emits new data.
     */
    fun refresh() {
        _homeState.value = _homeState.value.copy(isRefreshing = true)
        AppDataCache.invalidateAndRefetch()
    }

    /**
     * Delete a workout with optimistic UI update.
     * Decrements the User document stats so all screens stay in sync.
     */
    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch {
            // Get the workout volume before deleting so we can decrement stats
            val workout = _homeState.value.recentWorkouts.find { it.id == workoutId }

            // Optimistic: hide immediately
            _homeState.value = _homeState.value.copy(
                recentWorkouts = _homeState.value.recentWorkouts.filter { it.id != workoutId }
            )
            workoutRepository.deleteWorkout(workoutId)

            // Decrement static counters on the User document
            if (workout != null) {
                userRepository.decrementWorkoutStats(workout.totalVolumeKg)
            }

            // Refresh counters via cache
            AppDataCache.invalidateAndRefetch()
        }
    }

    // Legacy support — called from LaunchedEffect in some screens
    fun loadDashboard() = refresh()
}
