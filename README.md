# Computação Móvel — Trabalho Prático 1

## Assignment Context

This project was developed as part of the first tutorial assignment (**Tutorial 1**) of the **Computação Móvel** (Mobile Computing) course at ENIDH. The objective was to learn the fundamentals of the Kotlin programming language and apply them progressively — starting with basic Kotlin syntax and data structures, moving on to object-oriented programming concepts, and finally building a complete Android application that consumes a REST API.

## Project Description

The repository is organised into three progressive exercises that follow the tutorial structure:

1. **Kotlin Basics** — small programs that practise arrays, ranges, and functional transformations in Kotlin.
2. **Library System** — a console-based library management system that demonstrates object-oriented programming with abstract classes, inheritance, data classes, companion objects, and custom property setters.
3. **CountryInfo** — a full Android application that allows the user to search for a country by name and displays its flag, capital, population, and region. The data is fetched in real time from the [REST Countries API](https://restcountries.com/).

## Features

### Kotlin Basics (`1/`)
- Generate an array of the squares of numbers 1 to 50 using three different approaches:
  - Imperative approach with `IntArray` and a `for` loop.
  - Functional approach using `map` on a range.
  - Declarative approach using the `Array` constructor with a lambda.

### Library System (`Library/`)
- Abstract `Book` base class with title, author, publication year, and an era-based category (Classic / Modern / Contemporary).
- `PhysicalBook` subclass with weight, cover type, and available copies (with a safe custom setter).
- `DigitalBook` subclass with file size and format.
- `Library` class that supports adding books, listing the catalogue, borrowing physical books, and searching by author.
- `LibraryMember` data class representing a member with borrowed books.
- `companion object` to track the total number of books created across all libraries.

### CountryInfo App (`CountryInfo/`)
- Search for any country by name.
- Display the country's flag (loaded from URL), capital, population (formatted with locale), and region.
- Material Design 3 UI with a toolbar, outlined text input, Material Cards, and vector icons.
- Loading indicator while the API call is in progress.
- User-friendly error messages for network failures, server errors, and not-found responses.
- Welcome/idle state with a prompt and globe icon.
- Keyboard-driven search (IME action "Search").
- Dark theme support (separate `values-night` theme resources).

## Technologies Used

| Technology | Purpose |
|---|---|
| **Kotlin** | Primary programming language |
| **Android SDK (API 24–35)** | Target platform |
| **Android Studio** | IDE / Build system |
| **Gradle (Kotlin DSL)** | Build & dependency management |
| **Retrofit 2 + Gson** | REST API communication & JSON parsing |
| **OkHttp Logging Interceptor** | HTTP request/response logging |
| **Glide 4** | Image loading and caching (flag images) |
| **Material Design 3** | UI components & theming |
| **Jetpack ViewModel + LiveData** | MVVM architecture & reactive UI |
| **Kotlin Coroutines** | Asynchronous network calls |
| **View Binding** | Type-safe access to XML views |
| **ConstraintLayout** | Responsive layout |

## Project Structure

```
Computacao-Movel/
├── Enunciados/
│   └── ENIDH_CM_Tutorial1_2026.pdf   # Assignment statement (PDF)
│
├── 1/                                 # Exercise 1 — Kotlin Basics
│   ├── a.kt                          # Squares with IntArray + for loop
│   ├── b.kt                          # Squares with map on a range
│   └── c.kt                          # Squares with Array constructor
│
├── Library/                           # Exercise 2 — OOP Library System
│   ├── Book.kt                       # Abstract base class
│   ├── PhysicalBook.kt               # Physical book subclass
│   ├── DigitalBook.kt                # Digital book subclass
│   ├── Library.kt                    # Library manager class
│   ├── LibraryMember.kt              # Data class for members
│   └── main.kt                       # Entry point / demo
│
├── CountryInfo/                       # Exercise 3 — Android Application
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/diogo/countryinfo/
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/CountryApiService.kt       # Retrofit API interface
│   │   │   │   │   ├── model/CountryResponse.kt       # Data models (Country, Name, Flags)
│   │   │   │   │   └── repository/CountryRepository.kt # Repository with error handling
│   │   │   │   ├── ui/
│   │   │   │   │   ├── MainActivity.kt                # Main Activity (search + display)
│   │   │   │   │   └── CountryViewModel.kt            # ViewModel (MVVM)
│   │   │   │   └── util/
│   │   │   │       └── NetworkResult.kt               # Sealed class (Idle/Loading/Success/Error)
│   │   │   └── res/
│   │   │       ├── layout/activity_main.xml            # UI layout
│   │   │       ├── drawable/                           # Vector icons (globe, search, error, etc.)
│   │   │       ├── values/colors.xml, strings.xml, themes.xml
│   │   │       └── values-night/themes.xml             # Dark theme
│   │   └── build.gradle.kts                           # App-level dependencies
│   ├── build.gradle.kts                               # Project-level Gradle config
│   └── settings.gradle.kts                            # Module settings
│
└── README.md                          # This file
```

## How to Run the Project

### Kotlin Basics (`1/`) and Library System (`Library/`)

1. Install the [Kotlin compiler](https://kotlinlang.org/docs/command-line.html) or use an IDE like IntelliJ IDEA.
2. To run an individual file (e.g. `a.kt`):
   ```bash
   kotlinc a.kt -include-runtime -d a.jar && java -jar a.jar
   ```
3. To run the Library system, compile all files together:
   ```bash
   kotlinc Book.kt PhysicalBook.kt DigitalBook.kt Library.kt LibraryMember.kt main.kt -include-runtime -d library.jar && java -jar library.jar
   ```

### CountryInfo Android App

1. Open **Android Studio** (Arctic Fox or later recommended).
2. Select **File → Open** and navigate to the `CountryInfo/` folder.
3. Wait for Gradle to sync and download dependencies.
4. Connect an Android device (or start an emulator) with **API 24** or higher.
5. Click **Run ▶** to build and install the app.
6. Type a country name in the search field and press the **Search** button (or the keyboard search action) to see the results.

> **Note:** An active internet connection is required for the app to fetch data from the REST Countries API.

## Implementation Explanation

### Exercise 1 — Kotlin Basics

Three small programs demonstrate different ways to create an array of squares (1² to 50²):

- **`a.kt`** uses a traditional `for` loop to fill an `IntArray` imperatively.
- **`b.kt`** creates a range `1..50` and uses the `.map` higher-order function to transform each element.
- **`c.kt`** uses the `Array(size) { lambda }` constructor to generate the array declaratively in a single expression.

### Exercise 2 — Library System

The library system demonstrates core OOP concepts in Kotlin:

- **`Book`** is an `abstract class` with shared properties (`title`, `author`, `publicationYear`) and a method `getPublicationCategory()` that classifies books as *Clássico*, *Moderno*, or *Contemporâneo* based on their year using a `when` expression. It declares an abstract method `getStorageInfo()`.
- **`PhysicalBook`** extends `Book` and adds `weight`, `hasHardCover`, and `availableCopies`. The `availableCopies` property uses a **custom setter** that prevents negative values.
- **`DigitalBook`** extends `Book` and adds `fileSize` and `format`.
- **`Library`** manages a collection of books and provides operations to add books, display the catalogue, borrow physical books (decrementing available copies), and search by author. A **companion object** tracks the total number of books created.
- **`LibraryMember`** is a **data class** representing a library member.
- **`main.kt`** creates a library, adds books, and demonstrates borrowing and searching.

### Exercise 3 — CountryInfo Android App

The app follows the **MVVM (Model–View–ViewModel)** architecture:

- **Model layer** — `CountryApiService` defines the Retrofit endpoint (`GET v3.1/name/{name}`). `CountryResponse`, `Name`, and `Flags` are data classes that map the JSON response. `CountryRepository` builds a Retrofit instance with an OkHttp client (logging interceptor, 15-second timeouts) and exposes a `searchCountry()` suspend function that wraps results in a `NetworkResult` sealed class.
- **ViewModel layer** — `CountryViewModel` holds a `LiveData<NetworkResult>` that the UI observes. It validates input and launches a coroutine in `viewModelScope` to call the repository.
- **View layer** — `MainActivity` uses **View Binding** to access UI elements. It sets up a search button listener, a keyboard IME action listener, and observes the ViewModel's LiveData. Depending on the state (`Idle`, `Loading`, `Success`, `Error`), it shows/hides the appropriate views. The country flag is loaded using **Glide** with a crossfade transition. The population number is formatted with `NumberFormat` for locale-aware display.
- **Error handling** — The repository catches `HttpException` (404 → "not found", other codes → server error), `IOException` (network error), and generic exceptions, returning user-friendly messages.
- **UI** — Built entirely with **Material Design 3** components: `MaterialToolbar`, `TextInputLayout` (outlined style), `MaterialButton`, `MaterialCardView`, `CircularProgressIndicator`, and custom vector drawable icons.

## Conclusion

This project successfully covers the three stages proposed in the tutorial assignment: learning Kotlin fundamentals, applying object-oriented programming principles, and building a functional Android application. The CountryInfo app demonstrates a clean MVVM architecture, proper API consumption with Retrofit, image loading with Glide, and a polished Material Design 3 interface with appropriate error handling and loading states. All code is written in Kotlin and follows modern Android development practices.