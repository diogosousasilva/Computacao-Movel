package com.diogo.coolweatherapp.exercises

sealed class Event {
    data class Login(val username: String, val timestamp: Long) : Event()
    data class Purchase(val username: String, val amount: Double, val timestamp: Long) : Event()
    data class Logout(val username: String, val timestamp: Long) : Event()
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
    val events = listOf(Event.Purchase("diogo", 10.0, 100))
    println("Total gasto: ${events.totalSpent("diogo")}")
}