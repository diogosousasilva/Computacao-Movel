package com.diogo.coolweatherapp.exercises

class Pipeline {
    private val stages: MutableList<Pair<String, (List<String>) -> List<String>>> = mutableListOf()

    fun addStage(name: String, transform: (List<String>) -> List<String>) {
        stages.add(name to transform)
    }

    fun execute(input: List<String>): List<String> =
        stages.fold(input) { acc, (_, transform) -> transform(acc) }

    fun describe(): String =
        stages.joinToString(" -> ") { (name, _) -> name }

    /** Combina este pipeline com outro, executando-os em sequência */
    fun compose(other: Pipeline): Pipeline {
        val composed = Pipeline()
        stages.forEach { (name, transform) -> composed.addStage(name, transform) }
        other.stages.forEach { (name, transform) -> composed.addStage(name, transform) }
        return composed
    }

    /** Executa dois pipelines em paralelo sobre o mesmo input */
    fun fork(other: Pipeline): (List<String>) -> Pair<List<String>, List<String>> =
        { input -> Pair(this.execute(input), other.execute(input)) }
}

fun buildPipeline(block: Pipeline.() -> Unit): Pipeline =
    Pipeline().apply(block)

fun main() {
    val pipeline = buildPipeline {
        addStage("Trim") { lines -> lines.map { it.trim() } }
        addStage("Filter Errors") { lines -> lines.filterNot { it.contains("ERROR") } }
        addStage("Uppercase") { lines -> lines.map { it.uppercase() } }
        addStage("Add Index") { lines -> lines.mapIndexed { index, line -> "${index + 1}: $line" } }
    }
    val result = pipeline.execute(listOf("  ola  ", "  erro: ERROR ocorreu  ", "  mundo  "))
    println("Pipeline: ${pipeline.describe()}")
    println("Resultado: $result")
}