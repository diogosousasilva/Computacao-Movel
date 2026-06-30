package com.diogo.replog.data.model

import com.google.firebase.Timestamp

/**
 * Represents a single training session.
 * Stored in Firestore at: users/{uid}/workouts/{workoutId}
 */
data class Workout(
    val id: String = "",
    val userId: String = "",
    val date: Timestamp = Timestamp.now(),
    val durationMinutes: Int = 0,
    val notes: String? = null,
    val totalVolumeKg: Double = 0.0,
    val exerciseCount: Int = 0
) {
    constructor() : this(id = "")
}

/**
 * Represents an exercise performed within a workout, including its sets.
 * Stored in Firestore at: users/{uid}/workouts/{workoutId}/exercises/{exerciseEntryId}
 */
data class WorkoutExercise(
    val id: String = "",
    val exerciseId: String = "",       // Reference to Exercise document
    val exerciseName: String = "",     // Denormalized for quick display
    val muscleGroup: String = "",      // Denormalized
    val sets: List<WorkoutSet> = emptyList(),
    val order: Int = 0                 // Order within the workout
) {
    constructor() : this(id = "")
}

/**
 * Represents a single set within an exercise (reps + weight).
 * Stored as an embedded object inside WorkoutExercise.
 */
data class WorkoutSet(
    val setNumber: Int = 0,
    val reps: Int = 0,
    val weightKg: Double = 0.0,
    val isPersonalRecord: Boolean = false,
    val isWarmup: Boolean = false
) {
    constructor() : this(setNumber = 0)

    /** Calculate volume for this set */
    val volume: Double get() = reps * weightKg
}
