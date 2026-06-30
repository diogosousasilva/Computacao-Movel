package com.diogo.replog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diogo.replog.BuildConfig
import com.diogo.replog.data.remote.AiCoachClient
import com.diogo.replog.data.repository.UserRepository
import com.diogo.replog.data.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)

data class CoachState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val trainingHistorySummary: String = ""
)

/**
 * ViewModel for the AI Coach screen (PRO feature).
 * All network calls run on Dispatchers.IO to keep the UI thread free.
 */
class CoachViewModel : ViewModel() {
    private val aiCoachClient = AiCoachClient()
    private val workoutRepository = WorkoutRepository()
    private val userRepository = UserRepository()

    private val _state = MutableStateFlow(CoachState())
    val state: StateFlow<CoachState> = _state.asStateFlow()

    init {
        loadTrainingHistory()
    }

    private fun loadTrainingHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val workouts = workoutRepository.getRecentWorkouts(30).getOrDefault(emptyList())
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val summary = buildString {
                    if (workouts.isEmpty()) {
                        append("No training data available yet.")
                    } else {
                        workouts.forEach { workout ->
                            appendLine("Date: ${dateFormat.format(workout.date.toDate())}")
                            appendLine("  Duration: ${workout.durationMinutes} min")
                            appendLine("  Volume: ${String.format(Locale.getDefault(), "%.1f", workout.totalVolumeKg)} kg")
                            appendLine("  Exercises: ${workout.exerciseCount}")
                            appendLine()
                        }
                    }
                }
                _state.value = _state.value.copy(trainingHistorySummary = summary)
            } catch (_: Exception) {
                // Training history is optional — silently ignore
            }
        }
    }

    fun sendMessage(userMessage: String) {
        if (userMessage.isBlank()) return

        // Validate API key before attempting a call
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || (apiKey == "YOUR_GEMINI_API_KEY_HERE")) {
            _state.value = _state.value.copy(
                messages = _state.value.messages + ChatMessage(
                    text = "⚠️ AI Coach is not configured. Please set your Gemini API key in build.gradle.",
                    isUser = false
                )
            )
            return
        }

        val userChat = ChatMessage(text = userMessage, isUser = true)
        _state.value = _state.value.copy(
            messages = _state.value.messages + userChat,
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val user = userRepository.getCurrentUser().getOrNull()
                // Network call on IO dispatcher — never blocks Main thread
                val result = withContext(Dispatchers.IO) {
                    aiCoachClient.getCoachAdvice(
                        trainingHistory = _state.value.trainingHistorySummary,
                        userMessage = userMessage,
                        user = user
                    )
                }

                result.fold(
                    onSuccess = { response ->
                        _state.value = _state.value.copy(
                            messages = _state.value.messages + ChatMessage(
                                text = response,
                                isUser = false
                            ),
                            isLoading = false
                        )
                    },
                ) { e ->
                    val errorMsg = when {
                        e.message?.contains("401") == true ->
                            "❌ Invalid API key. Check your Gemini API key configuration."
                        e.message?.contains("429") == true ->
                            "⏳ Rate limit reached. Please wait a moment before trying again."
                        e.message?.contains("UnknownHostException") == true ||
                        e.message?.contains("ConnectException") == true ->
                            "🌐 No internet connection. Check your network and try again."
                        else -> "❌ Coach unavailable: ${e.localizedMessage ?: "Unknown error"}"
                    }
                    _state.value = _state.value.copy(
                        messages = _state.value.messages + ChatMessage(
                            text = errorMsg,
                            isUser = false
                        ),
                        isLoading = false,
                        error = null // Shown inline in chat, not as separate error state
                    )
                }
            } catch (e: Exception) {
                // Catch-all for unexpected coroutine errors
                _state.value = _state.value.copy(
                    messages = _state.value.messages + ChatMessage(
                        text = "❌ Unexpected error: ${e.localizedMessage ?: "Please try again."}",
                        isUser = false
                    ),
                    isLoading = false
                )
            }
        }
    }

    fun requestInitialAnalysis() {
        sendMessage("Analyze my training history and give me a personalized weekly progression plan.")
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
