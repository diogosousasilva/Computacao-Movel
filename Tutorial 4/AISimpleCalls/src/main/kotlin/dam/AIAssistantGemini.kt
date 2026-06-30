package dam

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * GeminiAIAssistant class provides an interface to communicate with Google's Gemini AI models.
 */
class AIAssistantGemini(override val properties: Properties) : AIAssistant {

    override fun getSystem() = "GEMINI"
    override val apiKeyName = "GEMINI_API_KEY"

    // Atualizado para o modelo oficial, rápido e ativo em 2026
    override var model = "gemini-2.5-flash"

    /**
     * Constructs and formats a structured request from the given input prompt.
     */
    override fun buildRequest(prompt: String): Request {
        // Estrutura padrão oficial exigida pelo endpoint v1 do Gemini
        val textPart = JSONObject().put("text", prompt)
        val partsArray = JSONArray().put(textPart)
        val contentObject = JSONObject().put("parts", partsArray)
        val contentsArray = JSONArray().put(contentObject)

        // Configurações de geração vindas do teu ficheiro de propriedades
        val generationConfig = JSONObject()
            .put("temperature", temperature)
            .put("maxOutputTokens", maxTokens)

        // Junta tudo no corpo do pedido JSON
        val requestBody = JSONObject()
            .put("contents", contentsArray)
            .put("generationConfig", generationConfig)
            .toString()

        // Monta o pedido HTTP com OkHttp apontando para o modelo correto
        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1/models/$model:generateContent?key=$apiKey")
            .addHeader("Content-Type", "application/json")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        return request
    }
}