class DigitalBook(
    title: String,
    author: String,
    publicationYear: Int,
    val fileSize: Double,
    val format: String
) : Book(title, author, publicationYear) {

    override fun getStorageInfo(): String
    {
        return "Digital: $fileSize MB, Formato: $format"
    }
}