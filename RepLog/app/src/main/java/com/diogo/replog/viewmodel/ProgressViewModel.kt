package com.diogo.replog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diogo.replog.data.cache.AppDataCache
import com.diogo.replog.data.model.Exercise
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.Locale

/**
 * Data for exercise progress display.
 */
data class ExerciseProgress(
    val exercise: Exercise,
    val history: List<Pair<Timestamp, Double>> = emptyList(),
    val currentMax: Double = 0.0,
    val previousMax: Double = 0.0,
    val deltaPercent: Double = 0.0,
    val isPlateaued: Boolean = false,
)

data class ProgressState(
    val exerciseProgressList: List<ExerciseProgress> = emptyList(),
    val selectedExercise: ExerciseProgress? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

/**
 * ViewModel for Progress Charts screen.
 *
 * Reads pre-computed data from [AppDataCache] for instant display.
 * No heavy computation on the UI thread — everything was done in background at login.
 */
class ProgressViewModel : ViewModel() {

    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    init {
        AppDataCache.prefetchAll()

        // 1. Render instantly from cache if already ready
        val cached = AppDataCache.progressCache.value
        if (cached.isReady) {
            _state.value = ProgressState(
                exerciseProgressList = cached.exerciseProgressList,
                isLoading = false
            )
        }

        // 2. Observe cache — updates when background pre-fetch finishes
        AppDataCache.progressCache
            .onEach { cache ->
                if (cache.isReady) {
                    _state.value = _state.value.copy(
                        exerciseProgressList = cache.exerciseProgressList,
                        isLoading = false
                    )
                }
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    fun loadProgress() {
        // If cache is not ready, show loading — it will update when cache emits
        if (!AppDataCache.progressCache.value.isReady) {
            _state.value = _state.value.copy(isLoading = true)
            AppDataCache.prefetchAll()
        }
    }

    fun selectExercise(progress: ExerciseProgress) {
        _state.value = _state.value.copy(selectedExercise = progress)
    }

    fun clearSelection() {
        _state.value = _state.value.copy(selectedExercise = null)
    }

    fun getProgressionSuggestion(progress: ExerciseProgress): String {
        return when {
            progress.isPlateaued -> {
                "⚠️ Plateau detected on ${progress.exercise.name}! " +
                "Try deloading by 10% and using a higher rep range (8-12 reps) for 1-2 weeks, " +
                "then attempt to break through."
            }
            progress.deltaPercent > 5 -> {
                "🔥 Great progress on ${progress.exercise.name}! " +
                "You've improved ${String.format(Locale.getDefault(), "%.1f", progress.deltaPercent)}%. " +
                "Keep adding 2.5kg when you complete all sets comfortably."
            }
            progress.deltaPercent > 0 -> {
                "📈 Steady progress on ${progress.exercise.name}. " +
                "Consider adding one extra rep per set before increasing weight."
            }
            else -> {
                "💪 Keep pushing on ${progress.exercise.name}! " +
                "Focus on form and progressive overload."
            }
        }
    }
}
