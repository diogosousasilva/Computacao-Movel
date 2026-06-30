package dam

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("\n🤖 Starting LLM Assistant application... 😀😀😀😀😀\n")

    val properties = getProperties()
    configureLogging(properties)
    println()

    println("✨ Using AI_LLM: ${properties.getProperty("AI_LLM")}")

    val assistant: AIAssistant = AIAssistantFactory.createAssistant(properties)
    println()

    println("✨ Using: ${assistant.getSystem()} ${assistant.model}")

    val tempSource = if (properties.getProperty("TEMPERATURE") != null) "config" else "default"
    val tokensSource = if (properties.getProperty("MAX_TOKENS") != null) "config" else "default"
    println("🌡️ Temperature: ${assistant.temperature} ($tempSource)")
    println("📏 Max Tokens: ${assistant.maxTokens} ($tokensSource)\n")

    println("📋 Select processing mode:")
    println("   1️⃣  Simple input processing")
    println("   2️⃣  Sentiment analysis")
    print("\n👉 Your choice (1 or 2): ")
    val modeInput = readlnOrNull()?.trim() ?: "1"
    val sentimentMode = modeInput == "2"

    if (sentimentMode) {
        println("\n🔍 Mode: SENTIMENT ANALYSIS")
        println("💬 Type text to analyze its sentiment. Press Ctrl+D (Unix/Mac) or Ctrl+Z (Windows) to exit.\n")
    } else {
        println("\n💬 Mode: SIMPLE INPUT PROCESSING")
        println("💬 Type your questions and press Enter to chat with the AI.")
        println("💬 Press Ctrl+D (Unix/Mac) or Ctrl+Z (Windows) to exit.\n")
    }

    while (true) {
        println("➖➖➖➖➖➖➖➖➖➖")

        if (sentimentMode) {
            print("📝 Text to analyze: ")
        } else {
            print("🧠 Your question: ")
        }

        val input = readlnOrNull() ?: break

        if (input.isBlank()) {
            println("⚠️ Please enter text or press Ctrl+D to exit.")
            continue
        }

        if (sentimentMode) {
            val output = assistant.processSentiment(input)
            println("\n🔍 Sentiment Analysis Result:\n$output\n\n")
        } else {
            val output = assistant.processInput(input)
            println("\n🤖 Answer: $output\n\n")
        }
    }

    println("\n👋 Thank you for using LLM Assistant. Goodbye!")
}