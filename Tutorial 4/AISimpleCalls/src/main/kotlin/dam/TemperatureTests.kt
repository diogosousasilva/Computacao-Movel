package dam

import kotlinx.coroutines.runBlocking

/**
 * Temperature Tests — Task 3
 *
 * This file provides two test cases that demonstrate how changes in the
 * temperature value lead to noticeably different outputs from the LLM.
 *
 * Test Case 1: Creative writing prompt — shows how high temperature produces
 *              more varied, creative text while low temperature is predictable.
 *
 * Test Case 2: Factual question — shows how low temperature sticks to facts
 *              while high temperature may add embellishments or variations.
 *
 * Run this file directly to execute the tests.
 */
fun main() = runBlocking {
    println("\n🧪 Temperature Tests — Demonstrating temperature effects on LLM output\n")

    // Get configuration properties and create assistant
    val properties = getProperties()
    configureLogging(properties)

    val assistant: AIAssistant = AIAssistantFactory.createAssistant(properties)
    println("✨ Using: ${assistant.getSystem()} ${assistant.model}\n")

    // ════════════════════════════════════════════════════════════════
    // Test Case 1: Creative Writing
    // A creative prompt where temperature differences are very visible
    // ════════════════════════════════════════════════════════════════
    println("═══════════════════════════════════════════════════════════")
    println("🧪 TEST CASE 1: Creative Writing")
    println("   Prompt: \"Write a short 2-sentence story about a robot discovering emotions.\"")
    println("═══════════════════════════════════════════════════════════\n")

    val creativePrompt = "Write a short 2-sentence story about a robot discovering emotions. Be creative."

    // Low temperature (0.1) — deterministic, predictable
    properties.setProperty("TEMPERATURE", "0.1")
    println("🌡️ Temperature: 0.1 (LOW — deterministic, predictable)")
    println("─────────────────────────────────────────────────────")
    val lowTempCreative = assistant.processInput(creativePrompt)
    println("📝 Response:\n$lowTempCreative\n")

    // High temperature (0.9) — creative, varied
    properties.setProperty("TEMPERATURE", "0.9")
    println("🌡️ Temperature: 0.9 (HIGH — creative, varied)")
    println("─────────────────────────────────────────────────────")
    val highTempCreative = assistant.processInput(creativePrompt)
    println("📝 Response:\n$highTempCreative\n")

    // ════════════════════════════════════════════════════════════════
    // Test Case 2: Factual Question
    // A factual prompt where temperature affects the phrasing style
    // ════════════════════════════════════════════════════════════════
    println("\n═══════════════════════════════════════════════════════════")
    println("🧪 TEST CASE 2: Factual Explanation")
    println("   Prompt: \"Explain what Kotlin coroutines are in exactly one paragraph.\"")
    println("═══════════════════════════════════════════════════════════\n")

    val factualPrompt = "Explain what Kotlin coroutines are in exactly one paragraph."

    // Low temperature (0.1) — precise, factual
    properties.setProperty("TEMPERATURE", "0.1")
    println("🌡️ Temperature: 0.1 (LOW — precise, factual)")
    println("─────────────────────────────────────────────────────")
    val lowTempFactual = assistant.processInput(factualPrompt)
    println("📝 Response:\n$lowTempFactual\n")

    // High temperature (0.9) — more expressive, varied wording
    properties.setProperty("TEMPERATURE", "0.9")
    println("🌡️ Temperature: 0.9 (HIGH — expressive, varied wording)")
    println("─────────────────────────────────────────────────────")
    val highTempFactual = assistant.processInput(factualPrompt)
    println("📝 Response:\n$highTempFactual\n")

    // Summary
    println("\n═══════════════════════════════════════════════════════════")
    println("📊 SUMMARY")
    println("═══════════════════════════════════════════════════════════")
    println("Low temperature (0.1):")
    println("  • Produces more consistent, predictable responses")
    println("  • Sticks closely to the most probable word choices")
    println("  • Ideal for factual Q&A, code generation, documentation")
    println()
    println("High temperature (0.9):")
    println("  • Produces more diverse, creative responses")
    println("  • May use unexpected metaphors or varied phrasing")
    println("  • Ideal for creative writing, brainstorming, storytelling")
    println("═══════════════════════════════════════════════════════════\n")
}
