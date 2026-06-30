package com.diogo.replog.data.repository

import com.diogo.replog.data.model.DefaultExercises
import com.diogo.replog.data.model.Exercise
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repository for the exercise library.
 * Shared exercises: exercises/{exerciseId}
 * Custom exercises: users/{uid}/custom_exercises/{exerciseId}
 */
class ExerciseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUid: String? get() = auth.currentUser?.uid

    /**
     * Get all available exercises (shared + user's custom).
     */
    suspend fun getAllExercises(): Result<List<Exercise>> {
        return try {
            val shared = firestore.collection("exercises").get().await()
                .mapNotNull { it.toObject(Exercise::class.java) }

            val custom = currentUid?.let { uid ->
                firestore.collection("users").document(uid)
                    .collection("custom_exercises").get().await()
                    .mapNotNull { it.toObject(Exercise::class.java) }
            } ?: emptyList()

            Result.success(shared + custom)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Seed the default exercise library into Firestore (run once).
     */
    suspend fun seedDefaultExercises() {
        try {
            val collection = firestore.collection("exercises")
            DefaultExercises.exercises.forEach { exercise ->
                collection.document(exercise.id).set(exercise).await()
            }
        } catch (_: Exception) {
            // Silently fail — exercises will load next time
        }
    }

    /**
     * Add a custom exercise (PRO feature).
     */
    suspend fun addCustomExercise(exercise: Exercise): Result<String> {
        val uid = currentUid ?: return Result.failure(Exception("Not authenticated"))
        return try {
            val customExercise = exercise.copy(isCustom = true, createdBy = uid)
            firestore.collection("users").document(uid)
                .collection("custom_exercises")
                .document(customExercise.id)
                .set(customExercise).await()
            Result.success(customExercise.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
