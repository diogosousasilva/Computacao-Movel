package com.diogo.replog.data.repository

import com.diogo.replog.data.model.Challenge
import com.diogo.replog.data.model.ChallengeStatus
import com.diogo.replog.data.model.Friendship
import com.diogo.replog.data.model.FriendshipStatus
import com.diogo.replog.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Repository for social features: friends, leaderboards, challenges.
 */
class SocialRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUid: String get() = auth.currentUser?.uid ?: throw Exception("Not authenticated")

    /**
     * Send a friend request to a user.
     */
    suspend fun sendFriendRequest(targetUid: String): Result<Unit> {
        return try {
            val friendship = Friendship(
                id = UUID.randomUUID().toString(),
                userIds = listOf(currentUid, targetUid).sorted(),
                status = FriendshipStatus.PENDING,
                requestedBy = currentUid
            )
            firestore.collection("friendships")
                .document(friendship.id)
                .set(friendship).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Accept or decline a friend request.
     */
    suspend fun respondToRequest(friendshipId: String, accept: Boolean): Result<Unit> {
        return try {
            val status = if (accept) FriendshipStatus.ACCEPTED else FriendshipStatus.DECLINED
            firestore.collection("friendships").document(friendshipId)
                .update("status", status.name).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get pending friend requests for the current user.
     */
    suspend fun getPendingRequests(): Result<List<Friendship>> {
        return try {
            val docs = firestore.collection("friendships")
                .whereArrayContains("userIds", currentUid)
                .whereEqualTo("status", FriendshipStatus.PENDING.name)
                .get().await()
            val requests = docs.mapNotNull { it.toObject(Friendship::class.java) }
                .filter { it.requestedBy != currentUid }  // Only incoming requests
            Result.success(requests)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all accepted friends.
     */
    suspend fun getFriends(): Result<List<User>> {
        return try {
            val friendships = firestore.collection("friendships")
                .whereArrayContains("userIds", currentUid)
                .whereEqualTo("status", FriendshipStatus.ACCEPTED.name)
                .get().await()
                .mapNotNull { it.toObject(Friendship::class.java) }

            val friendUids = friendships.flatMap { it.userIds }.filter { it != currentUid }
            val friends = friendUids.map { uid ->
                firestore.collection("users").document(uid).get().await()
                    .toObject(User::class.java) ?: User(uid = uid, displayName = "Unknown")
            }
            Result.success(friends)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Calculate delta leaderboard: % improvement in total volume over the last 4 weeks.
     */
    suspend fun getDeltaLeaderboard(friendUids: List<String>): Result<List<Pair<User, Double>>> {
        return try {
            val allUids = friendUids + currentUid
            val leaderboard = mutableListOf<Pair<User, Double>>()

            val fourWeeksAgo = Timestamp(
                Timestamp.now().seconds - (4 * 7 * 24 * 60 * 60), 0
            )
            val twoWeeksAgo = Timestamp(
                Timestamp.now().seconds - (2 * 7 * 24 * 60 * 60), 0
            )

            for (uid in allUids) {
                val user = firestore.collection("users").document(uid).get().await()
                    .toObject(User::class.java) ?: continue

                // Get workouts from weeks 2-4 (baseline)
                val baselineWorkouts = firestore.collection("users").document(uid)
                    .collection("workouts")
                    .whereGreaterThan("date", fourWeeksAgo)
                    .whereLessThan("date", twoWeeksAgo)
                    .get().await()
                    .mapNotNull { it.toObject(com.diogo.replog.data.model.Workout::class.java) }
                val baselineVolume = baselineWorkouts.sumOf { it.totalVolumeKg }

                // Get workouts from weeks 0-2 (recent)
                val recentWorkouts = firestore.collection("users").document(uid)
                    .collection("workouts")
                    .whereGreaterThan("date", twoWeeksAgo)
                    .get().await()
                    .mapNotNull { it.toObject(com.diogo.replog.data.model.Workout::class.java) }
                val recentVolume = recentWorkouts.sumOf { it.totalVolumeKg }

                val delta = if (baselineVolume > 0) {
                    ((recentVolume - baselineVolume) / baselineVolume) * 100
                } else {
                    0.0
                }

                leaderboard.add(user to delta)
            }

            Result.success(leaderboard.sortedByDescending { it.second })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Create a challenge between two PRO users.
     */
    suspend fun createChallenge(
        challengedUid: String,
        exerciseId: String,
        exerciseName: String,
        durationDays: Int = 30
    ): Result<String> {
        return try {
            val id = UUID.randomUUID().toString()
            val challenge = Challenge(
                id = id,
                challengerId = currentUid,
                challengedId = challengedUid,
                exerciseId = exerciseId,
                exerciseName = exerciseName,
                endDate = Timestamp(
                    Timestamp.now().seconds + (durationDays * 24 * 60 * 60), 0
                ),
                status = ChallengeStatus.PENDING
            )
            firestore.collection("challenges").document(id).set(challenge).await()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get active challenges for the current user.
     */
    suspend fun getActiveChallenges(): Result<List<Challenge>> {
        return try {
            val asChallenger = firestore.collection("challenges")
                .whereEqualTo("challengerId", currentUid)
                .whereEqualTo("status", ChallengeStatus.ACTIVE.name)
                .get().await()
                .mapNotNull { it.toObject(Challenge::class.java) }

            val asChallenged = firestore.collection("challenges")
                .whereEqualTo("challengedId", currentUid)
                .whereEqualTo("status", ChallengeStatus.ACTIVE.name)
                .get().await()
                .mapNotNull { it.toObject(Challenge::class.java) }

            Result.success(asChallenger + asChallenged)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
