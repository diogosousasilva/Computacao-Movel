class PhysicalBook(
    title: String,
    author: String,
    publicationYear: Int,
    initialCopies: Int,
    val weight: Double,
    val hasHardCover: Boolean
) : Book(title, author, publicationYear) {

    var availableCopies: Int = initialCopies
        set(value) {
            field = if (value < 0) 0 else value
        }

    override fun getStorageInfo(): String {
        val capa = if (hasHardCover) "Capa Dura" else "Capa Mole"
        return "Físico: ${weight}kg, $capa"
    }
}