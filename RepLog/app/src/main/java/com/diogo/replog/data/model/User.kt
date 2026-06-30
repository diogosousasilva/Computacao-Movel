package com.diogo.replog.data.model

import com.google.firebase.Timestamp

/**
 * Represents a registered RepLog user.
 * Stored in Firestore at: users/{uid}
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val isPro: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val streakDays: Int = 0,
    val totalWorkouts: Int = 0,
    val totalVolumeKg: Double = 0.0,
    val lastWorkoutDate: Timestamp? = null,
    val weightKg: Float = 70f,
    val gender: String = "",
    val goal: String = "",
    val experience: String = "",
    val heightCm: Int = 175,
    val weeklyFrequency: String = ""
) {
    /** No-arg constructor required for Firestore deserialization */
    constructor() : this(uid = "")
}
