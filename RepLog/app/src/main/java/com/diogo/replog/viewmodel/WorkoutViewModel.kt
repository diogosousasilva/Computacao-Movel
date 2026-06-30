package com.diogo.replog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diogo.replog.data.cache.AppDataCache
import com.diogo.replog.data.model.Exercise
import com.diogo.replog.data.model.WorkoutExercise
import com.diogo.replog.data.model.WorkoutSet
import com.diogo.replog.data.repository.ExerciseRepository
import com.diogo.replog.data.repository.UserRepository
import com.diogo.replog.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * State for the workout logger screen.
 */
data class WorkoutLoggerState(
    val exercises: List<Exercise> = emptyList(),           // Available exercise library
    val selectedExercises: List<WorkoutExerciseEntry> = emptyList(),  // Exercises in current workout
    val elapsedSeconds: Int = 0,
    val isSaving: Boolean = false,
    val isFinished: Boolean = false,
    val error: String? = null,
    val showExercisePicker: Boolean = false,
)

/**
 * Mutable wrapper for a workout exercise entry being edited.
 */
data class WorkoutExerciseEntry(
    val exercise: Exercise,
    val sets: List<SetEntry> = listOf(SetEntry()),
    val isExpanded: Boolean = true
)

data class SetEntry(
    val reps: String = "",
    val weightKg: String = "",
    val isWarmup: Boolean = false
)

/**
 * ViewModel for the Workout Logger.
 */
class WorkoutViewModel : ViewModel() {
    private val exerciseRepository = ExerciseRepository()
    private val workoutRepository = WorkoutRepository()
    private val userRepository = UserRepository()

    private val _state = MutableStateFlow(WorkoutLoggerState())
    val state: StateFlow<WorkoutLoggerState> = _state.asStateFlow()

    private var startTimeMs: Long = System.currentTimeMillis()

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().fold(
                onSuccess = { _state.value = _state.value.copy(exercises = it) },
            ) { _state.value = _state.value.copy(error = it.localizedMessage) }
        }
    }

    fun toggleExercisePicker(show: Boolean) {
        _state.value = _state.value.copy(showExercisePicker = show)
    }

    fun addExercise(exercise: Exercise) {
        val current = _state.value.selectedExercises.toMutableList()
        current.add(WorkoutExerciseEntry(exercise = exercise))
        _state.value = _state.value.copy(
            selectedExercises = current,
            showExercisePicker = false
        )
    }

    fun removeExercise(index: Int) {
        val current = _state.value.selectedExercises.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _state.value = _state.value.copy(selectedExercises = current)
        }
    }

    fun addSet(exerciseIndex: Int) {
        val exercises = _state.value.selectedExercises.toMutableList()
        if (exerciseIndex in exercises.indices) {
            val entry = exercises[exerciseIndex]
            exercises[exerciseIndex] = entry.copy(sets = entry.sets + SetEntry())
            _state.value = _state.value.copy(selectedExercises = exercises)
        }
    }

    fun removeSet(exerciseIndex: Int, setIndex: Int) {
        val exercises = _state.value.selectedExercises.toMutableList()
        if (exerciseIndex in exercises.indices) {
            val entry = exercises[exerciseIndex]
            val sets = entry.sets.toMutableList()
            if ((setIndex in sets.indices) && sets.size > 1) {
                sets.removeAt(setIndex)
                exercises[exerciseIndex] = entry.copy(sets = sets)
                _state.value = _state.value.copy(selectedExercises = exercises)
            }
        }
    }

    fun updateSet(exerciseIndex: Int, setIndex: Int, reps: String, weight: String) {
        val exercises = _state.value.selectedExercises.toMutableList()
        if (exerciseIndex in exercises.indices) {
            val entry = exercises[exerciseIndex]
            val sets = entry.sets.toMutableList()
            if (setIndex in sets.indices) {
                sets[setIndex] = sets[setIndex].copy(reps = reps, weightKg = weight)
                exercises[exerciseIndex] = entry.copy(sets = sets)
                _state.value = _state.value.copy(selectedExercises = exercises)
            }
        }
    }

    fun toggleSetWarmup(exerciseIndex: Int, setIndex: Int) {
        val exercises = _state.value.selectedExercises.toMutableList()
        if (exerciseIndex in exercises.indices) {
            val entry = exercises[exerciseIndex]
            val sets = entry.sets.toMutableList()
            if (setIndex in sets.indices) {
                sets[setIndex] = sets[setIndex].copy(isWarmup = !sets[setIndex].isWarmup)
                exercises[exerciseIndex] = entry.copy(sets = sets)
                _state.value = _state.value.copy(selectedExercises = exercises)
            }
        }
    }

    fun toggleExerciseExpanded(exerciseIndex: Int) {
        val exercises = _state.value.selectedExercises.toMutableList()
        if (exerciseIndex in exercises.indices) {
            val entry = exercises[exerciseIndex]
            exercises[exerciseIndex] = entry.copy(isExpanded = !entry.isExpanded)
            _state.value = _state.value.copy(selectedExercises = exercises)
        }
    }

    fun finishWorkout(notes: String? = null) {
        val selected = _state.value.selectedExercises
        if (selected.isEmpty()) {
            _state.value = _state.value.copy(error = "Add at least one exercise")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)

            try {
                val durationMinutes = ((System.currentTimeMillis() - startTimeMs) / 60000).toInt()

                val workoutExercises = selected.asSequence().map { entry ->
                    WorkoutExercise(
                        exerciseId = entry.exercise.id,
                        exerciseName = entry.exercise.name,
                        muscleGroup = entry.exercise.muscleGroup.name,
                        sets = entry.sets.mapIndexedNotNull { idx, set ->
                            val reps = set.reps.toIntOrNull() ?: return@mapIndexedNotNull null
                            val weight = set.weightKg.toDoubleOrNull() ?: 0.0
                            WorkoutSet(
                                setNumber = idx + 1,
                                reps = reps,
                                weightKg = weight,
                                isWarmup = set.isWarmup
                            )
                        }
                    )
                }.filter { it.sets.isNotEmpty() }.toList()

                workoutRepository.saveWorkout(
                    durationMinutes = durationMinutes,
                    exercises = workoutExercises,
                    notes = notes
                ).fold(
                    onSuccess = {
                        // Update user stats
                        val totalVolume = workoutExercises.sumOf { ex ->
                            ex.sets.sumOf { it.volume }
                        }
                        userRepository.incrementWorkoutStats(totalVolume)
                        // Invalidate cache so Home & Progress update instantly when user returns
                        AppDataCache.invalidateAndRefetch()
                        _state.value = _state.value.copy(isSaving = false, isFinished = true)
                    },
                ) {
                    _state.value = _state.value.copy(
                        isSaving = false,
                        error = it.localizedMessage
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = e.localizedMessage
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
