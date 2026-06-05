package com.diogo.geminiimageapp

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data class representing a single history entry.
 * Extra feature: keeps track of all prompts and responses with timestamps.
 */
data class HistoryEntry(
    val prompt: String,
    val response: String,
    val timestamp: String
)

/**
 * GeminiViewModel — handles communication with the Gemini AI model.
 *
 * Uses Google's Generative AI SDK to send text + image prompts to the
 * Gemini model and receives text responses. Maintains a history of
 * all interactions as an extra feature.
 */
class GeminiViewModel : ViewModel() {

    // The Generative AI model instance
    // NOTE: Replace "YOUR_API_KEY" with your actual Gemini API key
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _response = MutableLiveData<String?>()
    val response: LiveData<String?> = _response

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Extra feature: response history
    private val _history = MutableLiveData<List<HistoryEntry>>(emptyList())
    val history: LiveData<List<HistoryEntry>> = _history

    private val historyList = mutableListOf<HistoryEntry>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    /**
     * Sends a text prompt along with an image to the Gemini model.
     *
     * @param prompt The user's text prompt (e.g., "What is the recipe for this cake?")
     * @param image The selected Bitmap image to analyze
     */
    fun sendPrompt(prompt: String, image: Bitmap) {
        _isLoading.value = true
        _error.value = null
        _response.value = null

        viewModelScope.launch {
            try {
                // Build the content with both text and image
                val inputContent = content {
                    image(image)
                    text(prompt)
                }

                // Generate response from Gemini
                val result = generativeModel.generateContent(inputContent)
                val responseText = result.text ?: "No response received"

                _response.value = responseText
                _isLoading.value = false

                // Add to history (extra feature)
                val entry = HistoryEntry(
                    prompt = prompt,
                    response = if (responseText.length > 150) responseText.substring(0, 150) + "..." else responseText,
                    timestamp = dateFormat.format(Date())
                )
                historyList.add(0, entry) // Add newest first
                _history.value = historyList.toList()

            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
                _isLoading.value = false
            }
        }
    }
}
