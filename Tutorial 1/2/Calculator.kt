import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    println("--- Calculadora Kotlin ---")
    println("Operações suportadas: +, -, *, /, &&, ||, !, shl, shr")
    println("Para operações lógicas unárias, coloque um espaço após o operador (ex: ! 1)")
    println("Escreva 'sair' para terminar.")
    
    while (true) {
        try {
            print("\nIntroduza a operação: ")
            val input = scanner.nextLine().trim()
            
            if (input.lowercase() == "sair") break
            if (input.isEmpty()) continue
            
            val parts = input.split("\\s+".toRegex())
            
            if (parts.size == 2 && parts[0] == "!") {
                val num = parts[1].toInt()
                val result = (num == 0)
                printResults(if (result) 1 else 0)
                continue
            }
            
            if (parts.size != 3) {
                throw IllegalArgumentException("Formato inválido. Use: operando1 operador operando2")
            }
            
            val num1 = parts[0].toInt()
            val op = parts[1]
            val num2 = parts[2].toInt()
            
            val result = when (op.lowercase()) {
                "+" -> num1 + num2
                "-" -> num1 - num2
                "*" -> num1 * num2
                "/" -> {
                    if (num2 == 0) throw ArithmeticException("Divisão por zero.")
                    num1 / num2
                }
                "&&" -> {
                    val b1 = num1 != 0
                    val b2 = num2 != 0
                    if (b1 && b2) 1 else 0
                }
                "||" -> {
                    val b1 = num1 != 0
                    val b2 = num2 != 0
                    if (b1 || b2) 1 else 0
                }
                "shl" -> num1 shl num2
                "shr" -> num1 shr num2
                else -> throw IllegalArgumentException("Operador desconhecido: $op")
            }
            
            printResults(result)
            
        } catch (e: NumberFormatException) {
            println("Erro: Por favor insira números inteiros válidos.")
        } catch (e: Exception) {
            println("Erro: ${e.message}")
        }
    }
}

fun printResults(value: Int) {
    println("Resultado:")
    println("  Decimal: $value")
    println("  Hexadecimal: 0x${value.toString(16).uppercase()}")
    println("  Booleano: ${value != 0}")
}
