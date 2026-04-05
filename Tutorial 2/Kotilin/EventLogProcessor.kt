package com.diogo.coolweatherapp.exercises

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

sealed class Event {
    data class Login(val username: String, val timestamp: Long) : Event()
    data class Purchase(val username: String, val amount: Double, val timestamp: Long) : Event()
    data class Logout(val username: String, val timestamp: Long) : Event()
}

fun Long.toFormattedDate(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(this))
}

fun List<Event>.filterByUser(username: String): List<Event> =
    filter { event ->
        when (event) {
            is Event.Login    -> event.username == username
            is Event.Purchase -> event.username == username
            is Event.Logout   -> event.username == username
        }
    }

fun List<Event>.totalSpent(username: String): Double =
    filterIsInstance<Event.Purchase>()
        .filter { it.username == username }
        .sumOf { it.amount }

fun processEvents(events: List<Event>, handler: (Event) -> Unit) {
    events.forEach { handler(it) }
}

fun main() {
    val events = listOf(
        Event.Login("diogo", System.currentTimeMillis() - 100000),
        Event.Purchase("diogo", 10.0, System.currentTimeMillis()),
        Event.Logout("diogo", System.currentTimeMillis() + 50000)
    )
    
    println("--- Event Log ---")
    processEvents(events) { event ->
        val dateStr = event.timestamp.toFormattedDate()
        val desc = when (event) {
            is Event.Login -> "[LOGIN] User ${event.username} logged in."
            is Event.Purchase -> "[PURCHASE] User ${event.username} spent $${event.amount}."
            is Event.Logout -> "[LOGOUT] User ${event.username} logged out."
        }
        println("$dateStr: $desc")
    }

    println("\nTotal gasto por diogo: ${events.totalSpent("diogo")}")
}