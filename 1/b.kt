fun main()
{
    val numeros = 1..50

    val quadrados = numeros.map { it * it}

    println(quadrados)
}