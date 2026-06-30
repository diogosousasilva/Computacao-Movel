package com.diogo.replog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diogo.replog.data.model.Challenge
import com.diogo.replog.data.model.Friendship
import com.diogo.replog.data.model.User
import com.diogo.replog.data.repository.SocialRepository
import com.diogo.replog.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SocialState(
    val friends: List<User> = emptyList(),
    val pendingRequests: List<Friendship> = emptyList(),
    val leaderboard: List<Pair<User, Double>> = emptyList(),
    val challenges: List<Challenge> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val searchResult: User? = null,
    val searchError: String? = null,
    val requestSent: Boolean = false,
)

/**
 * ViewModel for Friends & Leaderboard screen.
 */
class SocialViewModel : ViewModel() {
    private val socialRepository = SocialRepository()
    private val userRepository = UserRepository()

    private val _state = MutableStateFlow(SocialState())
    val state: StateFlow<SocialState> = _state.asStateFlow()

    init {
        loadSocialData()
    }

    fun loadSocialData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val friends = socialRepository.getFriends().getOrDefault(emptyList())
                val pending = socialRepository.getPendingRequests().getOrDefault(emptyList())
                val challenges = socialRepository.getActiveChallenges().getOrDefault(emptyList())

                val friendUids = friends.map { it.uid }
                val leaderboard = if (friendUids.isNotEmpty()) {
                    socialRepository.getDeltaLeaderboard(friendUids).getOrDefault(emptyList())
                } else emptyList()

                _state.value = SocialState(
                    friends = friends,
                    pendingRequests = pending,
                    leaderboard = leaderboard,
                    challenges = challenges,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.localizedMessage)
            }
        }
    }

    fun searchFriend(email: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(searchResult = null, searchError = null, requestSent = false)
            userRepository.searchUserByEmail(email).fold(
                onSuccess = { _state.value = _state.value.copy(searchResult = it) },
            ) { _state.value = _state.value.copy(searchError = "User not found") }
        }
    }

    fun sendFriendRequest(uid: String) {
        viewModelScope.launch {
            socialRepository.sendFriendRequest(uid).fold(
                onSuccess = { _state.value = _state.value.copy(requestSent = true, searchResult = null) },
                onFailure = { _state.value = _state.value.copy(searchError = it.localizedMessage) }
            )
        }
    }

    fun respondToRequest(friendshipId: String, accept: Boolean) {
        viewModelScope.launch {
            socialRepository.respondToRequest(friendshipId, accept)
            loadSocialData()
        }
    }

    fun clearSearch() {
        _state.value = _state.value.copy(searchResult = null, searchError = null, requestSent = false)
    }
}
