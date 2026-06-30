package com.diogo.replog.data.model

import com.google.firebase.Timestamp

/**
 * Represents a bidirectional friendship between two users.
 * Stored in Firestore at: friendships/{friendshipId}
 */
data class Friendship(
    val id: String = "",
    val userIds: List<String> = emptyList(),   // Two UIDs
    val status: FriendshipStatus = FriendshipStatus.PENDING,
    val requestedBy: String = "",               // UID of who sent the request
    val createdAt: Timestamp = Timestamp.now()
) {
    constructor() : this(id = "")
}

enum class FriendshipStatus {
    PENDING,
    ACCEPTED,
    DECLINED
}

/**
 * Represents a head-to-head improvement challenge between two PRO users.
 * Stored in Firestore at: challenges/{challengeId}
 */
data class Challenge(
    val id: String = "",
    val challengerId: String = "",
    val challengedId: String = "",
    val exerciseId: String = "",
    val exerciseName: String = "",
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp = Timestamp.now(),
    val status: ChallengeStatus = ChallengeStatus.PENDING,
    val challengerDelta: Double = 0.0,     // % improvement
    val challengedDelta: Double = 0.0,
    val winnerId: String? = null
) {
    constructor() : this(id = "")
}

enum class ChallengeStatus {
    PENDING,
    ACTIVE,
    COMPLETED,
    DECLINED
}
