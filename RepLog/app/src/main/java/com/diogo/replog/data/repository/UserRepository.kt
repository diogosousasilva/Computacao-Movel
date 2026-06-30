package com.diogo.replog.data.repository

import com.diogo.replog.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.tasks.await

/**
 * Repository for user profile operations.
 */
class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUid: String? get() = auth.currentUser?.uid

    /**
     * Get the current user's profile from Firestore.
     */
    suspend fun getCurrentUser(): Result<User> {
        val uid = currentUid ?: return Result.failure(Exception("Not authenticated"))
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
                ?: return Result.failure(Exception("User not found"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get a user by their UID.
     */
    suspend fun getUserById(uid: String): Result<User> {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
                ?: return Result.failure(Exception("User not found"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update the current user's profile.
     */
    suspend fun updateProfile(updates: Map<String, Any>): Result<Unit> {
        val uid = currentUid ?: return Result.failure(Exception("Not authenticated"))
        return try {
            firestore.collection("users").document(uid).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Compresses the image to maximum 300x300 JPEG and saves as Base64 in Firestore.
     */
    suspend fun uploadProfilePhoto(imageBytes: ByteArray): Result<String> {
        val uid = currentUid ?: return Result.failure(Exception("Not authenticated"))
        return try {
            // Load bitmap
            val originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ?: return Result.failure(Exception("Invalid image data"))

            // Calculate scaled dimensions (max 300x300)
            val maxSize = 300
            val width = originalBitmap.width
            val height = originalBitmap.height
            val (newWidth, newHeight) = if (width > height) {
                val ratio = height.toDouble() / width
                maxSize to (maxSize * ratio).toInt()
            } else {
                val ratio = width.toDouble() / height
                (maxSize * ratio).toInt() to maxSize
            }

            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            
            // Compress to JPEG with low quality (50%)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            val compressedBytes = outputStream.toByteArray()

            // Encode to Base64
            val base64String = Base64.encodeToString(compressedBytes, Base64.DEFAULT)
            val dataUrl = "data:image/jpeg;base64,$base64String"

            // Save directly to Firestore
            firestore.collection("users").document(uid)
                .update("photoUrl", dataUrl).await()

            Result.success(dataUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Search users by email for friend requests.
     */
    suspend fun searchUserByEmail(email: String): Result<User> {
        return try {
            val query = firestore.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get().await()
            if (query.isEmpty) {
                Result.failure(Exception("User not found"))
            } else {
                val user = query.documents.first().toObject(User::class.java)!!
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Increment workout stats on user profile.
     */
    suspend fun incrementWorkoutStats(volumeKg: Double) {
        val uid = currentUid ?: return
        val userRef = firestore.collection("users").document(uid)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentTotal = snapshot.getLong("totalWorkouts") ?: 0
            val currentVolume = snapshot.getDouble("totalVolumeKg") ?: 0.0
            transaction.update(userRef, mapOf(
                "totalWorkouts" to currentTotal + 1,
                "totalVolumeKg" to currentVolume + volumeKg,
                "lastWorkoutDate" to com.google.firebase.Timestamp.now()
            ))
        }.await()
    }

    /**
     * Decrement workout stats on user profile when a workout is deleted.
     * Keeps the cached counters in the User document in sync with the real data.
     */
    suspend fun decrementWorkoutStats(volumeKg: Double) {
        val uid = currentUid ?: return
        val userRef = firestore.collection("users").document(uid)
        try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val currentTotal = (snapshot.getLong("totalWorkouts") ?: 1).coerceAtLeast(1)
                val currentVolume = snapshot.getDouble("totalVolumeKg") ?: 0.0
                transaction.update(userRef, mapOf(
                    "totalWorkouts" to currentTotal - 1,
                    "totalVolumeKg" to (currentVolume - volumeKg).coerceAtLeast(0.0)
                ))
            }.await()
        } catch (_: Exception) {
            // Non-critical: profile stats will still be accurate via dynamic calculation
        }
    }
}
