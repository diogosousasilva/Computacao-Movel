package com.diogo.coolweatherapp.exercises

class Cache<K : Any, V : Any> {
    private val store: MutableMap<K, V> = mutableMapOf()

    fun put(key: K, value: V) {
        store[key] = value
    }

    fun get(key: K): V? = store[key]

    fun evict(key: K) {
        store.remove(key)
    }

    fun clear() {
        store.clear()
    }

    val size: Int
        get() = store.size

    fun getOrDefault(key: K, default: () -> V): V {
        return store.getOrPut(key, default)
    }

    /** Cria uma nova cache aplicando uma transformação a todos os valores existentes */
    fun <R : Any> transform(transform: (V) -> R): Cache<K, R> {
        val newCache = Cache<K, R>()
        store.forEach { (k, v) -> newCache.put(k, transform(v)) }
        return newCache
    }

    fun snapshot(): Map<K, V> = store.toMap()

    fun filterValues(predicate: (V) -> Boolean): Map<K, V> =
        store.filter { (_, v) -> predicate(v) }
}

fun main() {
    val cache = Cache<String, String>()
    cache.put("ID", "12345")
    println("Valor na Cache: ${cache.get("ID")}")
    println("Tamanho atual: ${cache.size}")
    
    val name = cache.getOrDefault("Name") { "Sem Nome" }
    println("Nome: $name")
    
    cache.clear()
    println("Tamanho apos clear: ${cache.size}")
}