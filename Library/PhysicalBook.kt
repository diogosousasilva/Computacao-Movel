class PhysicalBook(
    title: String,
    author: String,
    publicationYear: Int,
    initialCopies: Int,
    val weight: Double,
    val hasHardCover: Boolean
) : Book(title, author, publicationYear) {

    private var _availableCopies: Int = initialCopies

    var availableCopies: Int
        get() = _availableCopies
        set(value) {_availableCopies = if (value < 0) 0 else value}

    override fun getStorageInfo(): String {
        val tipoCapa = if (hasHardCover) "Capa Dura" else "Capa Mole"
        return "Físico: ${weight}kg, $tipoCapa"
    }
}