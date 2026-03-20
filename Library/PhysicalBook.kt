class PhysicalBook(
    title: String,
    author: String,
    publicationYear: Int,
    initialCopies: Int,
    val weight: Double,
    val hasHardCover: Boolean
) : Book(title, author, publicationYear) {

    var availableCopies: Int = initialCopies
        set(value)
        {
            if (value < 0)
            {
                field = 0
            }
            else
            {
                field = value
            }
        }

    override fun getStorageInfo(): String
    {
        val capa
        if (capa.hasHardCover)
        {
            capa = "Capa Dura"
        }
        else
        {
            capa = "Capa Mole"
        }
        return "Físico: ${weight}kg, $capa"
    }
}