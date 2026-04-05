abstract class Book(
    val title: String,
    val author: String,
    val publicationYear: Int
) {
    fun getPublicationCategory(): String
    {
        return when
        {
            publicationYear < 1980 -> "Clássico"
            publicationYear in 1980..2010 -> "Moderno"
            else -> "Contemporâneo"
        }
    }

    abstract fun getStorageInfo(): String

    override fun toString(): String
    {
        return "Título: $title, Autor: $author, Época: ${getPublicationCategory()}"
    }
}