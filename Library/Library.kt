class Library(val name: String) {
    private val books = mutableListOf<Book>()

    companion object {
        private var totalBooksCreated = 0
        fun getTotalBooksCreated() = totalBooksCreated
    }

    fun addBook(book: Book) {
        books.add(book)
        totalBooksCreated++
    }

    fun showBooks() {
        println("\n--- Catálogo: $name ---")
        books.forEach { println("${it} | ${it.getStorageInfo()}") }
    }

    fun borrowBook(title: String) {
        val book = books.find { it is PhysicalBook && it.title.equals(title, true) } as? PhysicalBook
        if (book != null && book.availableCopies > 0) {
            book.availableCopies--
            println("Empréstimo de '$title' concluído.")
        } else {
            println("Indisponível: '$title'.")
        }
    }

    fun searchByAuthor(author: String) {
        val results = books.filter { it.author.equals(author, true) }
        println("\nResultados para '$author':")
        results.forEach { println(it.title) }
    }
}