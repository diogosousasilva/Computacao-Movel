fun main()
{
    var numeros = IntArray(50)

    for (i in 1..50)
    {
        numeros[i-1] = i*i
    }

    println(numeros.contentToString())
}