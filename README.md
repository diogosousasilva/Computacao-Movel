# Computação Móvel — Trabalhos Práticos

## Assignment Context

This project was developed as part of the **Computação Móvel** (Mobile Computing) course at ENIDH, covering **Tutorial 1** and **Tutorial 2**. The objective is to progressively master Kotlin and Android development — starting from the language fundamentals, advancing through sealed classes, generics, functional programming, and operator overloading, and culminating in a full Android weather application with MVVM architecture, GPS, and REST API integration.

## Project Description

The repository is organised into two tutorial folders that follow the assignment structure:

**Tutorial 1** covers Kotlin basics and the first Android application:
1. **Kotlin Basics** — small programs demonstrating arrays, ranges, `generateSequence`, string formatting, and functional transformations in Kotlin (Exercises 1, 2, and 3).
2. **Library System** — a console-based library management system demonstrating OOP with abstract classes, inheritance, data classes, companion objects, and custom property setters.
3. **CountryInfo** — a full Android application that searches for a country by name and shows its flag, capital, population, and region, fetched from the [REST Countries API](https://restcountries.com/).

**Tutorial 2** covers advanced Kotlin, the weather Android application, and AI Assisted Project Planning:
4. **Kotlin Exercises** — advanced Kotlin: sealed classes, generics, higher-order functions/lambdas, and operator overloading — each implemented in a separate file.
5. **CoolWeatherApp** — an Android weather application using the Open-Meteo API, with MVVM architecture, GPS location, Day/Night theming, and XML-driven WMO weather codes.
6. **AI Assisted Development (MIP-2)** — AI-guided planning and documentation for a new Android application (Image Explorer), resulting in a complete `docs/` spec folder.

## Features

### Kotlin Basics (`Tutorial 1/1/`, `Tutorial 1/2/`, `Tutorial 1/3/`)
- **Exercise 1 (`1/`)**: Generate an array of the squares of numbers 1 to 50 using three different approaches:
  - Imperative approach with `IntArray` and a `for` loop.
  - Functional approach using `map` on a range.
  - Declarative approach using the `Array` constructor with a lambda.
- **Exercise 2 (`2/`)**: Console-based calculator supporting arithmetic (`+`, `-`, `*`, `/`), boolean (`&&`, `||`, `!`), and bitwise shift (`shl`, `shr`) operations. Handles errors using exceptions and outputs in Decimal, Hexadecimal, and Boolean formats.
- **Exercise 3 (`3/`)**: Simulates a bouncing ball using `generateSequence`. Filters for heights >= 1m, limits to 15 bounces, and prints values formatted to two decimal places.

### Library System (`Tutorial 1/Library/`)
- Abstract `Book` base class with title, author, publication year, and an era-based category (Classic / Modern / Contemporary).
- `PhysicalBook` subclass with weight, cover type, and available copies (with a safe custom setter).
- `DigitalBook` subclass with file size and format.
- `Library` class that supports adding books, listing the catalogue, borrowing physical books, and searching by author.
- `LibraryMember` data class representing a member with borrowed books.
- Instance-level `totalBooksInThisLibrary` property (with a `private set`) to track how many books have been added to each library.

### CountryInfo App (`Tutorial 1/CountryInfo/`)
- Search for any country by name.
- Display the country's flag (loaded from URL), capital, population (formatted with locale), and region.
- Material Design 3 UI with a toolbar, outlined text input, Material Cards, and vector icons.
- Loading indicator while the API call is in progress.
- User-friendly error messages for network failures, server errors, and not-found responses.
- Welcome/idle state with a prompt and globe icon.
- Keyboard-driven search (IME action "Search").
- Dark theme support (separate `values-night` theme resources).

### Kotlin Exercises (`Tutorial 2/Kotilin/`)
- **Section 1.1 — Event Log Processing (`EventLogProcessor.kt`):** A `sealed class Event` with `Login`, `Purchase`, and `Logout` subclasses. Extension functions `filterByUser` and `totalSpent` (using `sumOf`), and a `processEvents` higher-order function.
- **Section 1.2 — Generic In-Memory Cache (`TypeSafeCache.kt`):** A generic `Cache<K: Any, V: Any>` class with `put`, `get`, `evict`, `size`, `getOrPut`, `transform`, `snapshot`, and a `filterValues` challenge.
- **Section 1.3 — Configurable Data Pipeline (`DataPipeline.kt`):** A `Pipeline` class with `addStage`, `execute`, `describe`, a `buildPipeline` DSL builder, and `compose`/`fork` challenges.
- **Section 1.4 — 2D Vector Library (`VectorLibrary.kt`):** A `Vec2` data class with operator overloading (`+`, `-`, `*`, unary `-`, `[]`, `compareTo`), vector functions (`magnitude`, `dot`, `normalized`), and left-hand scalar multiplication + destructuring (via `data class` auto-generated `component1`/`component2`).

### CoolWeatherApp (`Tutorial 2/CoolWeatherApp/`)
- Fetches real-time weather data from the [Open-Meteo API](https://api.open-meteo.com/).
- Displays temperature, wind speed and direction (e.g., 14.5 km/h (220°)), pressure, weather description, and icon for the current WMO weather code.
- Dynamic **Day** (light blue) and **Night** (dark) themes applied programmatically based on the current hour.
- **Localization:** Full language support for both English (`values/strings.xml`) and Portuguese (`values-pt/strings.xml`).
- Portrait and landscape layouts (`layout/` and `layout-land/`).
- **GPS Integration:** On startup, requests location permissions and uses `FusedLocationProviderClient` to display the device's real coordinates.
- **XML WMO Resources:** Weather code mappings (codes, descriptions, icon names) stored in `strings.xml` as `<integer-array>` and `<string-array>` resources — no hardcoded enums.

### AI Assisted Development (MIP-2) (`docs/`)
- Comprehensive Markdown documentation generated alongside an AI agent acting as a Senior Android Developer.
- Extensively covers the design and architecture of an "Image Explorer" app sourcing data from the public Dog CEO API.
- Contains implementation plans, MVVM architecture rules, database and response models, UI planning, and prompt logs.
- **MVVM Architecture:** `WeatherViewModel` handles all API calls and exposes a `LiveData<WeatherState>` sealed class. `MainActivity` observes it reactively with zero business logic.

## Technologies Used

| Technology | Purpose |
|---|---|
| **Kotlin** | Primary programming language |
| **Android SDK (API 26–35)** | Target platform |
| **Android Studio** | IDE / Build system |
| **Gradle (Kotlin DSL) + Version Catalog** | Build & dependency management |
| **Gson** | JSON parsing (Open-Meteo response) |
| **Retrofit 2 + Gson (CountryInfo)** | REST API communication & JSON parsing |
| **OkHttp Logging Interceptor** | HTTP request/response logging |
| **Glide 4** | Image loading and caching (flag images) |
| **Material Design 3** | UI components & theming |
| **Jetpack ViewModel + LiveData** | MVVM architecture & reactive UI |
| **Google Play Services Location** | FusedLocationProviderClient for GPS |
| **Kotlin Coroutines** | Asynchronous network calls (CountryInfo) |
| **View Binding** | Type-safe access to XML views |
| **ConstraintLayout** | Responsive layout |

## Project Structure

```
Computacao-Movel/
├── docs/                                  # Section 3 (MIP-2) — AI Assisted Planning
│   ├── 01_overview.md                     # Idea and problem statement
│   ├── 02_features.md                     # Core & non-functional requirements
│   ├── 03_ui_design.md                    # Material 3 & UI plans
│   ├── 04_architecture.md                 # MVVM & Flow structure
│   ├── 05_data_models.md                  # Room Entity & Retrofit Models
│   ├── 06_database.md                     # DAO definition
│   ├── 07_api_usage.md                    # Endpoints for Dog CEO API
│   ├── 08_implementation_plan.md          # 5-Phase rollout plan
│   ├── agents.md                          # Guidance used to prime the AI Assistant
│   └── prompts_log.md                     # Raw prompts used for generation
│
├── Enunciados/
│   ├── ENIDH_CM_Tutorial1_2026.pdf
│   └── ENIDH_CM_Tutorial2_2026.pdf
│
├── Tutorial 1/
│   ├── 1/                                 # Exercise 1 — Kotlin Basics (Arrays)
│   │   ├── a.kt
│   │   ├── b.kt
│   │   └── c.kt
│   │
│   ├── 2/                                 # Exercise 2 — Calculator
│   │   └── Calculator.kt
│   │
│   ├── 3/                                 # Exercise 3 — Bouncing Ball
│   │   └── BouncingBall.kt
│   │
│   ├── Library/                           # Exercise — OOP Library System
│   │   ├── Book.kt
│   │   ├── PhysicalBook.kt
│   │   ├── DigitalBook.kt
│   │   ├── Library.kt
│   │   ├── LibraryMember.kt
│   │   └── main.kt
│   │
│   └── CountryInfo/                       # Android App — Country Search
│       └── app/src/main/
│           ├── AndroidManifest.xml
│           ├── java/com/diogo/countryinfo/
│           │   ├── data/api/CountryApiService.kt
│           │   ├── data/model/CountryResponse.kt
│           │   ├── data/repository/CountryRepository.kt
│           │   ├── ui/CountryViewModel.kt
│           │   ├── ui/MainActivity.kt
│           │   └── util/NetworkResult.kt
│           └── res/
│               ├── layout/activity_main.xml
│               ├── drawable/ic_*.xml
│               ├── values/colors.xml
│               ├── values/strings.xml
│               ├── values/themes.xml
│               └── values-night/themes.xml
│
├── Tutorial 2/
│   ├── Kotilin/                           # Section 1 — Advanced Kotlin Exercises
│   │   ├── EventLogProcessor.kt           # 1.1 — Sealed classes & higher-order functions
│   │   ├── TypeSafeCache.kt               # 1.2 — Generic in-memory cache
│   │   ├── DataPipeline.kt                # 1.3 — Configurable data pipeline
│   │   ├── VectorLibrary.kt               # 1.4 — 2D Vector with operator overloading
│   │   └── Main.kt                        # Entry point / demo runner
│   │
│   └── CoolWeatherApp/                    # Section 2 — Android Weather App
│       ├── app/
│       │   ├── src/main/
│       │   │   ├── AndroidManifest.xml
│       │   │   ├── java/com/diogo/coolweatherapp/
│       │   │   │   ├── data/
│       │   │   │   │   ├── model/WeatherData.kt        # WeatherData, CurrentWeather, Hourly
│       │   │   │   │   └── repository/WeatherRepository.kt  # Gson + URL.readText()
│       │   │   │   └── ui/
│       │   │   │       ├── MainActivity.kt             # Observer pattern, GPS, UI updates
│       │   │   │       └── WeatherViewModel.kt         # MVVM ViewModel + LiveData<WeatherState>
│       │   │   └── res/
│       │   │       ├── layout/activity_main.xml        # Portrait layout
│       │   │       ├── layout-land/activity_main.xml   # Landscape layout
│       │   │       ├── drawable/ic_weather_*.xml       # Weather condition icons
│       │   │       ├── mipmap-anydpi-v26/              # Adaptive launcher icon
│       │   │       ├── values/                     
│       │   │       │   ├── strings.xml                 # Base English labels & mappings
│       │   │       │   ├── colors.xml                  # Day/Night color palette
│       │   │       │   └── themes.xml                  # Theme.Day + Theme.Night
│       │   │       └── values-pt/                      # Portuguese Localization
│       │   │           └── strings.xml                 # Portuguese translations
│       │   ├── build.gradle.kts
│       │   └── proguard-rules.pro
│       ├── gradle/libs.versions.toml
│       ├── build.gradle.kts
│       └── settings.gradle.kts
│
└── README.md
```

## How to Run the Project

### Kotlin Basics (`Tutorial 1/1/`, `Tutorial 1/2/`, `Tutorial 1/3/`) and Library System (`Tutorial 1/Library/`)

1. Install the [Kotlin compiler](https://kotlinlang.org/docs/command-line.html) or use an IDE like IntelliJ IDEA.
2. To run an individual file:
   ```bash
   kotlinc Main.kt -include-runtime -d exercises.jar && java -jar exercises.jar
   ```
3. To run the Library system:
   ```bash
   kotlinc Book.kt PhysicalBook.kt DigitalBook.kt Library.kt LibraryMember.kt main.kt -include-runtime -d library.jar && java -jar library.jar
   ```

### Kotlin Exercises (`Tutorial 2/Kotilin/`)

Each exercise file has its own `main()` function for standalone testing. Run them from IntelliJ IDEA or with the Kotlin compiler:
```bash
kotlinc EventLogProcessor.kt -include-runtime -d event.jar && java -jar event.jar
kotlinc TypeSafeCache.kt -include-runtime -d cache.jar && java -jar cache.jar
kotlinc DataPipeline.kt -include-runtime -d pipeline.jar && java -jar pipeline.jar
kotlinc VectorLibrary.kt -include-runtime -d vector.jar && java -jar vector.jar
```

### CountryInfo Android App (`Tutorial 1/CountryInfo/`)

1. Open **Android Studio** (Arctic Fox or later recommended).
2. Select **File → Open** and navigate to the `Tutorial 1/CountryInfo/` folder.
3. Wait for Gradle to sync and download dependencies.
4. Connect an Android device (or start an emulator) with **API 26** or higher.
5. Click **Run ▶** to build and install the app.

> **Note:** An active internet connection is required.

### CoolWeatherApp (`Tutorial 2/CoolWeatherApp/`)

1. Open **Android Studio** and select **File → Open → `Tutorial 2/CoolWeatherApp/`**.
2. Wait for Gradle to sync.
3. Connect a device or emulator with **API 26+**.
4. Click **Run ▶**. On first launch, grant the location permission for GPS coordinates.
5. The app shows real-time weather for your current location. Type `lat,lon` in the field and tap **Update** to change coordinates.

## Implementation Explanation

### Kotlin Exercises (`Tutorial 2/Kotilin/`)

- **Section 1.1 — Event Log Processing (`EventLogProcessor.kt`):** A `sealed class Event` with three subclasses (`Login`, `Purchase`, `Logout`) serves as the event type system. Extension function `filterByUser` leverages a `when` expression for exhaustive pattern matching. `totalSpent` uses `filterIsInstance<Event.Purchase>()` and `sumOf`. `processEvents` is a higher-order function accepting a `(Event) -> Unit` handler.
- **Section 1.2 — Generic Cache (`TypeSafeCache.kt`):** `Cache<K: Any, V: Any>` wraps a `MutableMap` and exposes a clean API. `getOrPut` accepts a `default: () -> V` lambda. The `transform<R>` function returns a new `Cache` with transformed values. The challenge `filterValues` applies a predicate directly on the internal map.
- **Section 1.3 — Configurable Pipeline (`DataPipeline.kt`):** Each stage is a named `(List<String>) -> List<String>` function stored in a list. `buildPipeline` is a DSL builder using an extension lambda on `Pipeline`. The challenge `compose` joins two pipelines by combining their stage lists. `fork` returns a function that runs both pipelines and returns a `Pair<List<String>, List<String>>`.
- **Section 1.4 — Vec2 Library (`VectorLibrary.kt`):** `Vec2` is a `data class` implementing `Comparable<Vec2>` (compared by `magnitude()`). All binary operators (`+`, `-`, `*`) and unary minus are overloaded with `operator fun`. `get(index)` provides indexed access (0 → x, 1 → y). Left-hand scalar multiplication is added as a `Double.times(Vec2)` extension. Destructuring (`val (x, y) = vec`) works automatically because `Vec2` is a `data class`, which generates `component1`/`component2` for free.

### CoolWeatherApp (`Tutorial 2/CoolWeatherApp/`)

- **MVVM Architecture** — `WeatherViewModel` holds a `MutableLiveData<WeatherState>` (sealed class: `Loading`, `Success`, `Error`). It starts a `Thread` for the network call and uses `postValue` to push results. `MainActivity` observes via `viewModel.weatherState.observe(this) { ... }` — all UI update logic lives in `handleWeatherState()`.
- **Networking** — `WeatherRepository` builds the Open-Meteo URL and calls `URL(url).readText()`. The JSON response is parsed with `Gson.fromJson()` into `WeatherData` / `CurrentWeather` / `Hourly` data classes.
- **GPS Integration** — `MainActivity` registers an `ActivityResultContracts.RequestMultiplePermissions` launcher. On grant, it calls `FusedLocationProviderClient.lastLocation` and passes the real coordinates into the ViewModel. Falls back to Lisbon (38.76, −9.12) if denied.
- **XML WMO Resources** — WMO codes, descriptions, and icon drawable names are stored as three parallel arrays in `strings.xml` (`wmo_codes`, `wmo_descriptions`, `wmo_icon_names`). `MainActivity.getWeatherCodeInfo()` resolves them at runtime using `resources.getIntArray` / `getStringArray` / `getIdentifier`.
- **Localization & Data Expansion** — In addition to the base English `strings.xml`, a `values-pt/strings.xml` serves up Portuguese translations on supported devices. The Wind reading interpolates two variables (speed and direction layout-safe formatting).
- **Day/Night Theme** — `applyDayNightTheme()` in `MainActivity.onCreate` reads `Calendar.HOUR_OF_DAY` and calls `setTheme(R.style.Theme_Day)` or `setTheme(R.style.Theme_Night)` **before** `super.onCreate()` to ensure the theme is applied correctly. `Theme.Day` uses a light blue background; `Theme.Night` uses a dark navy/black background.
- **Portrait & Landscape** — Two separate XML layouts: `layout/activity_main.xml` (vertical scroll, stacked) and `layout-land/activity_main.xml` (two-column `ConstraintLayout` with a `Guideline`).

### AI Assisted Development (MIP-2)

- **Scenario Simulation**: The user defines constraints with AntiGravity to brainstorm an Image Explorer Application for querying Dog imagery (via Dog CEO API).
- **Component Engineering**: Strict guidelines (`agents.md`) enforce clean architecture and generation of robust data models, an interface for persistent offline favorites via DAO implementations (`06_database.md`), and HTTP fetching schemas (`07_api_usage.md`). 
- **Delivery**: Fully Markdown-annotated documentation ready for direct implementation.

## Conclusion

This project covers both Tutorial 1 and Tutorial 2: fundamental Kotlin programming, object-oriented design, advanced language features (sealed classes, generics, higher-order functions, operator overloading), and Android application development. The CoolWeatherApp demonstrates clean MVVM architecture, real-time REST API consumption with Gson, GPS integration, programmatic theming, responsive portrait/landscape layouts, and resource-driven WMO weather code mapping — all written in Kotlin following modern Android development practices.