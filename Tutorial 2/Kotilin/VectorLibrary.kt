package com.diogo.coolweatherapp.exercises

import kotlin.math.sqrt

// O uso de "data class" gera automaticamente as funções component1 e component2
data class Vec2(val x: Double, val y: Double) : Comparable<Vec2> {

    // Operadores Aritméticos
    operator fun plus(other: Vec2)  = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(scalar: Double) = Vec2(x * scalar, y * scalar)
    operator fun unaryMinus() = Vec2(-x, -y)

    // Acesso por índice: v[0] -> x, v[1] -> y
    operator fun get(index: Int): Double = when (index) {
        0    -> x
        1    -> y
        else -> throw IndexOutOfBoundsException("Vec2 index must be 0 or 1")
    }

    // Comparação por magnitude (tamanho do vetor)
    override fun compareTo(other: Vec2): Int =
        magnitude().compareTo(other.magnitude())

    // NOTA: As funções component1() e component2() foram removidas
    // porque o Kotlin já as cria automaticamente em "data classes".

    fun magnitude(): Double = sqrt(x * x + y * y)

    fun dot(other: Vec2): Double = x * other.x + y * other.y

    fun normalized(): Vec2 {
        val mag = magnitude()
        require(mag != 0.0) { "Cannot normalize a zero vector" }
        return Vec2(x / mag, y / mag)
    }

    override fun toString(): String = "Vec2(%.2f, %.2f)".format(x, y)
}

// Extensões para permitir multiplicação escalar à esquerda (Ex: 2.0 * vetor)
operator fun Double.times(v: Vec2) = Vec2(this * v.x, this * v.y)

fun main() {
    val v1 = Vec2(3.0, 4.0)
    val v2 = Vec2(1.0, 2.0)

    println("=== Teste Secção 1.4 ===")
    println("Vetor 1: $v1")
    println("Magnitude de v1: ${v1.magnitude()}") // Deve dar 5.0
    println("Soma v1 + v2: ${v1 + v2}")
    println("Multiplicação (2.0 * v1): ${2.0 * v1}")
}