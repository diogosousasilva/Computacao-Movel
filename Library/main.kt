fun main()
{
    val myLibrary = Library("Biblioteca do Diogo")

    val b1 = PhysicalBook("1984", "George Orwell", 1949, 2, 0.5, false)
    val b2 = DigitalBook("Kotlin Mobile", "JetBrains", 2024, 1.2, "PDF")

    myLibrary.addBook(b1)
    myLibrary.addBook(b2)

    myLibrary.showBooks()
    myLibrary.borrowBook("1984")
    myLibrary.searchByAuthor("George Orwell")

    println("\nTotal de livros no sistema: ${Library.getTotalBooksCreated()}")
}