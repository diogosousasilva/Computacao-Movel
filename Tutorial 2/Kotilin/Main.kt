package com.diogo.coolweatherapp.exercises

fun main() {
    println("--- EXERCÍCIOS SECÇÃO 1 ---")

    // 1.1 Event Log
    val events = listOf(
        Event.Login("diogo", 1000),
        Event.Purchase("diogo", 25.5, 1100)
    )
    println("Total gasto: ${events.totalSpent("diogo")}")

    // 1.2 Cache
    val cache = Cache<String, String>()
    cache.put("aula", "Computação Móvel")
    println("Cache: ${cache.get("aula")}")

    // 1.4 Vectores
    val v1 = Vec2(3.0, 4.0)
    println("Magnitude do vetor $v1: ${v1.magnitude}")
}