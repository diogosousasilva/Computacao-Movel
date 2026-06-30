package com.diogo.replog.data.remote

import com.diogo.replog.BuildConfig
import com.diogo.replog.data.model.User
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * AI Coach client using the official Google Gemini Android SDK.
 * Sends training history to the LLM and receives structured coaching advice.
 */
class AiCoachClient {

    private fun getModel(user: User?): GenerativeModel {
        val weight = user?.weightKg ?: 70f
        val height = user?.heightCm ?: 175
        val frequency = user?.weeklyFrequency?.ifBlank { "3-4x" } ?: "3-4x"
        val gender = user?.gender?.ifBlank { "não especificado" } ?: "não especificado"
        val goal = user?.goal?.ifBlank { "geral" } ?: "geral"
        val experience = user?.experience?.ifBlank { "beginner" } ?: "beginner"

        return GenerativeModel(
            modelName = "gemini-2.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY,
            systemInstruction = content {
                text("Tu és um Treinador de Ginásio IA focado em ajudar o utilizador a progredir cargas de forma realista. O utilizador atualmente pesa ${weight}kg, tem ${height}cm de altura, treina ${frequency} vezes por semana, identificou-se como género ${gender}, tem o objetivo de ${goal} e o seu nível de experiência é ${experience}. Tem isto em consideração nas tuas análises e responde sempre em português de Portugal, exceto no nome dos exercícios, que deves manter sempre em inglês.")
            }
        )
    }

    /**
     * Send a coaching request to Gemini API using the official Google Gemini SDK.
     */
    suspend fun getCoachAdvice(
        trainingHistory: String,
        userMessage: String? = null,
        user: User? = null,
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = buildPrompt(trainingHistory, userMessage)
            val model = getModel(user)
            val response = model.generateContent(prompt)
            val text = response.text ?: "No response generated."
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildPrompt(trainingHistory: String, userMessage: String?): String {
        val base = """
            TRAINING HISTORY (last 4 weeks):
            $trainingHistory
            
            Based on this data:
            1. Identify any stagnation or plateau patterns
            2. Suggest specific load/rep adjustments for each exercise
            3. Recommend a progression strategy for the next week
            4. Provide motivational feedback on their progress
            
            Be concise, practical, and encouraging. Format your response clearly with sections.
        """.trimIndent()

        return if (userMessage != null) {
            "$base\n\nUSER QUESTION: $userMessage\n\nAnswer the user's question while considering their training history."
        } else {
            base
        }
    }
}
