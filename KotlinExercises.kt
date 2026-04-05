import kotlin.math.sqrt

// =============================================================================
// Section 1.1 — Event Log Processing
// =============================================================================

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

fun demonstrateEventLog() {
    println("=== 1.1 Event Log Processing ===")
    val events = listOf(
        Event.Login("alice", 1000),
        Event.Purchase("alice", 49.99, 1100),
        Event.Login("bob", 1200),
        Event.Purchase("alice", 29.99, 1300),
        Event.Purchase("bob", 9.99, 1400),
        Event.Logout("alice", 1500),
        Event.Logout("bob", 1600)
    )

    // processEvents with a when handler
    processEvents(events) { event ->
        when (event) {
            is Event.Login    -> println("[LOGIN]    ${event.username} logged in at t=${event.timestamp}")
            is Event.Purchase -> println("[PURCHASE] ${event.username} spent \$${event.amount} at t=${event.timestamp}")
            is Event.Logout   -> println("[LOGOUT]   ${event.username} logged out at t=${event.timestamp}")
        }
    }

    println()
    val aliceEvents = events.filterByUser("alice")
    println("Alice's events: ${aliceEvents.size}")
    println("Alice total spent: \$${events.totalSpent("alice")}")
    println("Bob total spent: \$${events.totalSpent("bob")}")
}

// =============================================================================
// Section 1.2 — Generics: A Type-Safe In-Memory Cache
// =============================================================================

class Cache<K : Any, V : Any> {
    private val store: MutableMap<K, V> = mutableMapOf()

    fun put(key: K, value: V) {
        store[key] = value
    }

    fun get(key: K): V? = store[key]

    fun evict(key: K) {
        store.remove(key)
    }

    fun size(): Int = store.size

    fun getOrPut(key: K, default: () -> V): V {
        return store.getOrPut(key, default)
    }

    fun <R : Any> transform(transform: (V) -> R): Cache<K, R> {
        val newCache = Cache<K, R>()
        store.forEach { (k, v) -> newCache.put(k, transform(v)) }
        return newCache
    }

    fun snapshot(): Map<K, V> = store.toMap()

    // Challenge: filterValues
    fun filterValues(predicate: (V) -> Boolean): Map<K, V> =
        store.filter { (_, v) -> predicate(v) }
}

fun demonstrateCache() {
    println("\n=== 1.2 Type-Safe In-Memory Cache ===")
    val cache = Cache<String, Int>()
    cache.put("a", 1)
    cache.put("b", 2)
    cache.put("c", 3)
    println("Cache size: ${cache.size()}")
    println("Get 'a': ${cache.get("a")}")

    cache.evict("b")
    println("After evicting 'b', size: ${cache.size()}")

    val result = cache.getOrPut("d") { 42 }
    println("getOrPut 'd' (default 42): $result")
    println("getOrPut 'a' (already exists): ${cache.getOrPut("a") { 99 }}")

    val snapshot = cache.snapshot()
    println("Snapshot: $snapshot")

    val doubled = cache.transform { it * 2 }
    println("Transformed (doubled): ${doubled.snapshot()}")

    // Challenge
    val filtered = cache.filterValues { it > 2 }
    println("Filtered (value > 2): $filtered")
}

// =============================================================================
// Section 1.3 — Functions and Lambdas: A Configurable Data Pipeline
// =============================================================================

class Pipeline {
    private val stages: MutableList<Pair<String, (List<String>) -> List<String>>> = mutableListOf()

    fun addStage(name: String, transform: (List<String>) -> List<String>) {
        stages.add(name to transform)
    }

    fun execute(input: List<String>): List<String> =
        stages.fold(input) { acc, (_, transform) -> transform(acc) }

    fun describe(): String =
        stages.joinToString(" -> ") { (name, _) -> name }

    // Challenge: compose two pipelines
    fun compose(other: Pipeline): Pipeline {
        val composed = Pipeline()
        stages.forEach { (name, transform) -> composed.addStage(name, transform) }
        other.stages.forEach { (name, transform) -> composed.addStage(name, transform) }
        return composed
    }

    // Challenge: fork — execute both pipelines and return a Pair
    fun fork(other: Pipeline): (List<String>) -> Pair<List<String>, List<String>> =
        { input -> Pair(this.execute(input), other.execute(input)) }
}

fun buildPipeline(block: Pipeline.() -> Unit): Pipeline =
    Pipeline().apply(block)

fun demonstratePipeline() {
    println("\n=== 1.3 Configurable Data Pipeline ===")
    val rawLogs = listOf(
        "  ERROR: disk full  ",
        "  info: user logged in  ",
        "  ERROR: connection timeout  ",
        "  WARNING: low memory  ",
        "  error: file not found  "
    )

    val pipeline = buildPipeline {
        addStage("Trim") { lines -> lines.map { it.trim() } }
        addStage("Filter ERROR") { lines -> lines.filter { it.uppercase().startsWith("ERROR") } }
        addStage("Uppercase") { lines -> lines.map { it.uppercase() } }
        addStage("Add index") { lines -> lines.mapIndexed { i, line -> "${i + 1}. $line" } }
    }

    println("Pipeline: ${pipeline.describe()}")
    val result = pipeline.execute(rawLogs)
    println("Result:")
    result.forEach { println("  $it") }

    // Challenge: compose two pipelines
    println()
    val p1 = buildPipeline {
        addStage("Trim") { lines -> lines.map { it.trim() } }
    }
    val p2 = buildPipeline {
        addStage("Uppercase") { lines -> lines.map { it.uppercase() } }
    }
    val composed = p1.compose(p2)
    println("Composed: ${composed.describe()}")
    println("Composed result: ${composed.execute(listOf("  hello  ", "  world  "))}")

    // Challenge: fork
    val filterErrors = buildPipeline {
        addStage("Trim") { lines -> lines.map { it.trim() } }
        addStage("Filter ERROR") { lines -> lines.filter { it.uppercase().startsWith("ERROR") } }
    }
    val filterWarnings = buildPipeline {
        addStage("Trim") { lines -> lines.map { it.trim() } }
        addStage("Filter WARNING") { lines -> lines.filter { it.uppercase().startsWith("WARNING") } }
    }
    val forked = filterErrors.fork(filterWarnings)
    val (errors, warnings) = forked(rawLogs)
    println("\nForked:")
    println("  Errors: $errors")
    println("  Warnings: $warnings")
}

// =============================================================================
// Section 1.4 — Operator Overloading: A 2D Vector Library
// =============================================================================

data class Vec2(val x: Double, val y: Double) : Comparable<Vec2> {
    // Arithmetic operators
    operator fun plus(other: Vec2)  = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times(scalar: Double) = Vec2(x * scalar, y * scalar)
    operator fun unaryMinus() = Vec2(-x, -y)

    // Index operator: 0 -> x, 1 -> y
    operator fun get(index: Int): Double = when (index) {
        0    -> x
        1    -> y
        else -> throw IndexOutOfBoundsException("Vec2 index must be 0 or 1, got $index")
    }

    // compareTo by magnitude
    override fun compareTo(other: Vec2): Int =
        magnitude().compareTo(other.magnitude())

    // Challenge: component functions for destructuring
    operator fun component1() = x
    operator fun component2() = y

    // Vector functions
    fun magnitude(): Double = sqrt(x * x + y * y)

    fun dot(other: Vec2): Double = x * other.x + y * other.y

    fun normalized(): Vec2 {
        val mag = magnitude()
        require(mag != 0.0) { "Cannot normalize a zero vector" }
        return Vec2(x / mag, y / mag)
    }

    override fun toString(): String = "Vec2(%.2f, %.2f)".format(x, y)
}

// Challenge: left-hand scalar multiplication as extension on Double
operator fun Double.times(v: Vec2) = Vec2(this * v.x, this * v.y)
operator fun Int.times(v: Vec2) = Vec2(this * v.x, this * v.y)

fun demonstrateVec2() {
    println("\n=== 1.4 Operator Overloading: 2D Vector Library ===")
    val a = Vec2(3.0, 4.0)
    val b = Vec2(1.0, 2.0)

    println("a = $a")
    println("b = $b")
    println("a + b = ${a + b}")
    println("a - b = ${a - b}")
    println("a * 2.0 = ${a * 2.0}")
    println("2.0 * a = ${2.0 * a}")  // Challenge: left scalar
    println("-a = ${-a}")
    println("a[0] = ${a[0]}, a[1] = ${a[1]}")
    println("|a| = ${a.magnitude()}")
    println("a · b = ${a.dot(b)}")
    println("a.normalized() = ${a.normalized()}")
    println("a > b (by magnitude)? ${a > b}")
    println("a < b (by magnitude)? ${a < b}")

    // Challenge: destructuring
    val (x, y) = a
    println("Destructured a: x=$x, y=$y")
}

// =============================================================================
// Main — Test all exercises
// =============================================================================

fun main() {
    demonstrateEventLog()
    demonstrateCache()
    demonstratePipeline()
    demonstrateVec2()
}
