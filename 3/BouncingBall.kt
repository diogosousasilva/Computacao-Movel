import java.util.Locale

fun main() {
    val initialHeight = 100.0
    val bounceFactor = 0.6
    
    val bounces = generateSequence(initialHeight) { height ->
        height * bounceFactor
    }
    
    val validBounces = bounces
        .filter { it >= 1.0 }
        .take(15)
        .toList()
        
    println("--- Altura dos Saltos da Bola ---")
    validBounces.forEachIndexed { index, height ->
        val formattedHeight = String.format(Locale.US, "%.2f", height)
        if (index == 0) {
            println("Altura Inicial: $formattedHeight m")
        } else {
            println("Salto $index: $formattedHeight m")
        }
    }
}
