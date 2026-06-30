package com.diogo.replog.data.repository

import com.diogo.replog.data.model.Workout
import com.diogo.replog.data.model.WorkoutExercise
import com.diogo.replog.data.model.WorkoutSet
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository for workout CRUD operations.
 * Path: users/{uid}/workouts/{workoutId}
 * Sub-path: users/{uid}/workouts/{workoutId}/exercises/{exerciseEntryId}
 */
class WorkoutRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUid: String get() = auth.currentUser?.uid ?: throw Exception("Not authenticated")

    private fun workoutsCollection(uid: String = currentUid) =
        firestore.collection("users").document(uid).collection("workouts")

    /**
     * Real-time stream of recent workouts using Firestore snapshot listener.
     * Emits a new list whenever any workout is added, modified, or deleted.
     */
    fun observeRecentWorkouts(limit: Int = 5): Flow<List<Workout>> = callbackFlow {
        val uid = try { currentUid } catch (e: Exception) {
            close(e)
            return@callbackFlow
        }
        val registration = workoutsCollection(uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val workouts = snapshot?.mapNotNull {
                    it.toObject(Workout::class.java)
                } ?: emptyList()
                trySend(workouts)
            }
        awaitClose { registration.remove() }
    }

    /**
     * Save a completed workout with all its exercises and sets.
     */
    suspend fun saveWorkout(
        durationMinutes: Int,
        exercises: List<WorkoutExercise>,
        notes: String? = null
    ): Result<String> {
        return try {
            val workoutId = UUID.randomUUID().toString()
            val totalVolume = exercises.sumOf { ex ->
                ex.sets.sumOf { it.volume }
            }

            val workout = Workout(
                id = workoutId,
                userId = currentUid,
                durationMinutes = durationMinutes,
                notes = notes,
                totalVolumeKg = totalVolume,
                exerciseCount = exercises.size
            )

            val workoutRef = workoutsCollection().document(workoutId)
            workoutRef.set(workout).await()

            // Save each exercise as a sub-document
            exercises.forEachIndexed { index, exercise ->
                val exerciseEntry = exercise.copy(
                    id = UUID.randomUUID().toString(),
                    order = index
                )
                workoutRef.collection("exercises")
                    .document(exerciseEntry.id)
                    .set(exerciseEntry).await()
            }

            Result.success(workoutId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get recent workouts for the current user.
     */
    suspend fun getRecentWorkouts(limit: Int = 10): Result<List<Workout>> {
        return try {
            val docs = workoutsCollection()
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get().await()
            val workouts = docs.mapNotNull { it.toObject(Workout::class.java) }
            Result.success(workouts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all workouts for a specific user (used for friend profiles).
     */
    suspend fun getWorkoutsForUser(uid: String, limit: Int = 10): Result<List<Workout>> {
        return try {
            val docs = workoutsCollection(uid)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get().await()
            val workouts = docs.mapNotNull { it.toObject(Workout::class.java) }
            Result.success(workouts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get exercises for a specific workout.
     */
    suspend fun getWorkoutExercises(workoutId: String): Result<List<WorkoutExercise>> {
        return try {
            val docs = workoutsCollection().document(workoutId)
                .collection("exercises")
                .orderBy("order")
                .get().await()
            val exercises = docs.mapNotNull { it.toObject(WorkoutExercise::class.java) }
            Result.success(exercises)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches all workouts and their exercise sub-documents in parallel.
     * Extremely efficient compared to sequential querying.
     */
    suspend fun getAllWorkoutsWithExercises(limit: Int = 100): Result<List<Pair<Workout, List<WorkoutExercise>>>> {
        return try {
            val workoutDocs = workoutsCollection()
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get().await()

            val result = kotlinx.coroutines.coroutineScope {
                workoutDocs.map { doc ->
                    async {
                        val workout = doc.toObject(Workout::class.java) ?: return@async null
                        val exerciseDocs = doc.reference.collection("exercises").get().await()
                        val exercises = exerciseDocs.mapNotNull { it.toObject(WorkoutExercise::class.java) }
                        workout to exercises
                    }
                }.awaitAll().filterNotNull()
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get workout history for a specific exercise (for progress charts).
     * Returns a list of (date, maxWeight) pairs sorted by date.
     */
    suspend fun getExerciseHistory(
        exerciseId: String,
        weeksBack: Int = 12
    ): Result<List<Pair<Timestamp, Double>>> {
        return try {
            val cutoff = Timestamp(
                Timestamp.now().seconds - (weeksBack * 7 * 24 * 60 * 60), 0
            )
            val workoutDocs = workoutsCollection()
                .whereGreaterThan("date", cutoff)
                .orderBy("date")
                .get().await()

            val history = mutableListOf<Pair<Timestamp, Double>>()

            for (workoutDoc in workoutDocs) {
                val workout = workoutDoc.toObject(Workout::class.java)
                val exerciseDocs = workoutDoc.reference.collection("exercises")
                    .whereEqualTo("exerciseId", exerciseId)
                    .get().await()

                for (exDoc in exerciseDocs) {
                    val ex = exDoc.toObject(WorkoutExercise::class.java)
                    val maxWeight = ex.sets.maxOfOrNull { it.weightKg } ?: 0.0
                    if (maxWeight > 0) {
                        history.add(workout.date to maxWeight)
                    }
                }
            }

            Result.success(history)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get today's workouts count.
     */
    suspend fun getTodayWorkoutCount(): Int {
        return try {
            val calendar = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
            }
            val startOfDay = Timestamp(calendar.time)
            workoutsCollection()
                .whereGreaterThanOrEqualTo("date", startOfDay)
                .get().await()
                .size()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Delete a workout.
     */
    suspend fun deleteWorkout(workoutId: String): Result<Unit> {
        return try {
            // Delete all exercises sub-documents first
            val exercises = workoutsCollection().document(workoutId)
                .collection("exercises").get().await()
            for (doc in exercises) {
                doc.reference.delete().await()
            }
            // Delete the workout document
            workoutsCollection().document(workoutId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
