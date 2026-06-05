# Computação Móvel — Trabalhos Práticos

## Assignment Context

This project was developed as part of the **Computação Móvel** (Mobile Computing) course at ENIDH, covering **Tutorial 1**, **Tutorial 2**, and **Tutorial 3**. The objective is to progressively master Kotlin and Android development — starting from the language fundamentals, advancing through sealed classes, generics, functional programming, operator overloading, and culminating in full Android weather applications with MVVM architecture, GPS, REST API integration, and modern UI toolkits like Jetpack Compose.

## Project Description

The repository is organised into three tutorial folders that follow the assignment structure:

**Tutorial 1** covers Kotlin basics and the first Android application:
1. **Kotlin Basics** — small programs demonstrating arrays, ranges, `generateSequence`, string formatting, and functional transformations in Kotlin (Exercises 1, 2, and 3).
2. **Library System** — a console-based library management system demonstrating OOP with abstract classes, inheritance, data classes, companion objects, and custom property setters.
3. **CountryInfo** — a full Android application that searches for a country by name and shows its flag, capital, population, and region, fetched from the [REST Countries API](https://restcountries.com/).

**Tutorial 2** covers advanced Kotlin, the weather Android application, and AI Assisted Project Planning:
4. **Kotlin Exercises** — advanced Kotlin: sealed classes, generics, higher-order functions/lambdas, and operator overloading — each implemented in a separate file.
5. **CoolWeatherApp** — an Android weather application using the Open-Meteo API, with MVVM architecture, GPS location, Day/Night theming, and XML-driven WMO weather codes.
6. **AI Assisted Development (MIP-2)** — AI-guided planning and generation of a new Android application (Image Explorer), resulting in a complete `Tutorial 2/docs/` spec folder and a fully functional Android project in `Tutorial 2/ImageExplorer/`.

**Tutorial 3** introduces Kotlin Annotation Processing and modern Android UI development with Jetpack Compose:
7. **Annotation Processors** — creating compile-time code generators using `kapt` and `KotlinPoet` to build wrapper classes and implement regex-based data extractors.
8. **CoolJetpackWeatherApp** — a modern rewrite of the weather application utilizing **Jetpack Compose** for a declarative UI, **Ktor** for networking, Kotlin Serialization, and a Google Maps integration for location picking.

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

### AI Assisted Development & Image Explorer (MIP-2) (`Tutorial 2/ImageExplorer/` & `docs/`)
- **Documentation:** Comprehensive Markdown documentation (`docs/`) generated alongside an AI agent acting as a Senior Android Developer, covering the design and architecture of an "Image Explorer" app sourcing data from the public Dog CEO API.
- **Generated App:** A fully functional Android application generated automatically based on the AI documentation.
- **Features:** Fetches random dog images from the Dog CEO API and displays them in a grid.
- **Offline Favorites:** Users can favorite images, which are saved persistently using **Room Database** for offline viewing.
- **Architecture:** MVVM architecture with `HomeViewModel` and `FavoritesViewModel`, leveraging `Retrofit` for networking, `Glide` for images, and Kotlin Coroutines/Flow for reactive data streams.

### Annotation Processors (`Tutorial 3/GreetingProcessorProject/`)
- **`@Greeting` Processor:** A custom annotation processor utilizing `kapt` and `KotlinPoet`. When applied to a method, it generates a wrapper class that logs the greeting message before delegating execution to the original method using Composition.
- **`@Extract` Regex Challenge:** Generates a robust `DataProcessorExtractor` class. The processor scans abstract methods annotated with `@Extract(regex = "...")` and automatically generates method implementations to extract regex group matches from a string input.

### CoolJetpackWeatherApp (`Tutorial 3/CoolJetpackWeatherApp/`)
- **Jetpack Compose UI:** 100% declarative UI architecture using composables like `WeatherUI`, `CoordinatesCard`, and `WeatherCard`. Replaces traditional XML layouts.
- **Ktor Networking:** Uses `HttpClient` and `io.ktor` for asynchronous weather data fetching from Open-Meteo, replacing Retrofit.
- **StateFlow & ViewModel:** Employs modern Kotlin Coroutines `StateFlow` to hoist UI state (`WeatherUIState`) efficiently from the ViewModel to Compose components.
- **Responsive Layouts:** Employs conditional Compose rendering for seamless adaptations between Portrait and Landscape orientations.
- **WMO Weather Code Display:** Translates standard WMO weather codes into user-friendly descriptions with emoji icons (e.g., ☀️ Clear sky, 🌧️ Rain, ⛈️ Thunderstorm).
- **Loading & Error States:** Displays a `CircularProgressIndicator` while fetching data and a styled error message on failure.
- **Full Localization (i18n):** All UI strings use `stringResource()` with complete translations in English (`values/strings.xml`) and Portuguese (`values-pt/strings.xml`).
- **Google Maps Integration:** A dedicated `LocationPickerActivity` relying on `com.google.maps.android:maps-compose` allows users to visually select geographic coordinates on a world map. Selected coordinates are returned to the main screen via `ActivityResultContracts` and automatically trigger a weather data refresh.

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
| **Jetpack Compose** | Declarative UI framework (Tutorial 3) |
| **Ktor Client** | Modern networking client (Tutorial 3) |
| **Kotlin Serialization** | Native JSON parsing for Ktor (Tutorial 3) |
| **KotlinPoet & Kapt** | Annotation processing and code generation (Tutorial 3) |
| **Google Maps Compose** | Interactive map integration (Tutorial 3) |

## Project Structure

```
Computacao-Movel/
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
│   ├── CoolWeatherApp/                    # Section 2 — Android Weather App
│   │   ├── app/
│   │   │   ├── src/main/
│   │   │   │   ├── AndroidManifest.xml
│   │   │   │   ├── java/com/diogo/coolweatherapp/
│   │   │   │   │   ├── data/
│   │   │   │   │   │   ├── model/WeatherData.kt        # WeatherData, CurrentWeather, Hourly
│   │   │   │   │   │   └── repository/WeatherRepository.kt  # Gson + URL.readText()
│   │   │   │   │   └── ui/
│   │   │   │   │       ├── MainActivity.kt             # Observer pattern, GPS, UI updates
│   │   │   │   │       └── WeatherViewModel.kt         # MVVM ViewModel + LiveData<WeatherState>
│   │   │   │   └── res/
│   │   │   │       ├── layout/activity_main.xml        # Portrait layout
│   │   │   │       ├── layout-land/activity_main.xml   # Landscape layout
│   │   │   │       ├── drawable/ic_weather_*.xml       # Weather condition icons
│   │   │   │       ├── mipmap-anydpi-v26/              # Adaptive launcher icon
│   │   │   │       ├── values/                     
│   │   │   │       │   ├── strings.xml                 # Base English labels & mappings
│   │   │   │       │   ├── colors.xml                  # Day/Night color palette
│   │   │   │       │   └── themes.xml                  # Theme.Day + Theme.Night
│   │   │   │       └── values-pt/                      # Portuguese Localization
│   │   │   │           └── strings.xml                 # Portuguese translations
│   │   │   ├── build.gradle.kts
│   │   │   └── proguard-rules.pro
│   │   ├── gradle/libs.versions.toml
│   │   ├── build.gradle.kts
│   │   └── settings.gradle.kts
│   │
│   ├── docs/                              # Section 3 (MIP-2) — AI Assisted Planning
│   │   ├── 01_overview.md                 # Idea and problem statement
│   │   ├── 02_features.md                 # Core & non-functional requirements
│   │   ├── 03_ui_design.md                # Material 3 & UI plans
│   │   ├── 04_architecture.md             # MVVM & Flow structure
│   │   ├── 05_data_models.md              # Room Entity & Retrofit Models
│   │   ├── 06_database.md                 # DAO definition
│   │   ├── 07_api_usage.md                # Endpoints for Dog CEO API
│   │   ├── 08_implementation_plan.md      # 5-Phase rollout plan
│   │   ├── agents.md                      # Guidance used to prime the AI Assistant
│   │   └── prompts_log.md                 # Raw prompts used for generation
│   │
│   └── ImageExplorer/                     # Section 3 (MIP-2) — Generated Android App
│       ├── app/src/main/java/com/diogo/imageexplorer/
│       │   ├── data/                      # Room Database (Favorites) & Retrofit (Dog CEO)
│       │   ├── ui/                        # MVVM ViewModels, Fragments (Home, Favorites), Adapter
│       │   └── MainActivity.kt
│       └── build.gradle.kts
│
├── Tutorial 3/
│   ├── GreetingProcessorProject/          # Section 1 & 2 — Annotation Processing
│   │   ├── annotations/                   # Defines @Greeting and @Extract
│   │   ├── processor/                     # kapt processor using KotlinPoet
│   │   └── app/                           # Demonstrates generated code usage
│   │
│   └── CoolJetpackWeatherApp/             # Section 3 — Jetpack Compose Weather App
│       ├── app/src/main/
│       │   ├── java/com/example/cooljetpackweatherapp/
│       │   │   ├── data/                  # Ktor API Client & Serializable Models
│       │   │   ├── ui/                    # Compose UI components and Google Maps picker
│       │   │   └── viewmodel/             # StateFlow-driven WeatherViewModel
│       │   └── AndroidManifest.xml
│       └── build.gradle.kts
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

### Image Explorer (MIP-2) (`Tutorial 2/ImageExplorer/`)

1. Open **Android Studio** and select **File → Open → `Tutorial 2/ImageExplorer/`**.
2. Wait for Gradle to sync and download dependencies (Retrofit, Room, Glide).
3. Connect a device or emulator with **API 26+**.
4. Click **Run ▶**.
5. Browse random dogs on the Home tab, favorite them, and view your saved dogs on the Favorites tab (available offline).

### Tutorial 3 (`Tutorial 3/GreetingProcessorProject/` & `CoolJetpackWeatherApp/`)

1. To run the annotation processors, open **IntelliJ IDEA** and load the `GreetingProcessorProject`.
2. Ensure you have `Load Gradle Changes`. 
3. Run the `main()` function in `app/src/main/kotlin/com/example/app/Main.kt`. The output will display the execution of the generated wrappers and regex extractors.
4. To run the Jetpack Compose app, open **Android Studio** and load `Tutorial 3/CoolJetpackWeatherApp/`.
5. Sync Gradle and click **Run ▶** on an API 26+ device. A valid Google Maps API Key is already configured in the Manifest for the Location Picker. Tap the location icon to open the map, navigate to a location, and press **Confirm** — the selected coordinates will be sent back and the weather will refresh automatically.

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

### AI Assisted Development & Image Explorer (MIP-2)

- **Scenario Simulation**: The user defines constraints with AntiGravity to brainstorm an Image Explorer Application for querying Dog imagery (via Dog CEO API).
- **Component Engineering**: Strict guidelines (`agents.md`) enforce clean architecture and generation of robust data models, an interface for persistent offline favorites via Room DAO implementations (`06_database.md`), and HTTP fetching schemas (`07_api_usage.md`). 
- **Code Generation**: The AI agent successfully executed the planned architecture, creating the complete Android project from scratch with Jetpack Navigation, ViewBinding, Retrofit, Glide, and Room.

### Tutorial 3 Implementation

- **Compile-Time Metaprogramming** — The `GreetingProcessorProject` utilizes `javax.annotation.processing.AbstractProcessor` integrated tightly with Gradle via `kapt`. It uses `KotlinPoet` to dynamically scaffold `TypeSpec` and `FunSpec` components, emitting clean, type-safe Kotlin source files during the compilation phase, drastically reducing boilerplate code in runtime.
- **Jetpack Compose Paradigm** — The rewrite from XML transforms the UI layer entirely. Everything from the structural layout to the text fields acts as a pure reactive function dependent on `WeatherUIState`. The UI renders three distinct states — loading (`CircularProgressIndicator`), error (styled error text), and success (weather data cards) — driven purely by the `WeatherUIState` data class.
- **Ktor Networking** — Shifting away from Retrofit and standard `URL.readText()`, the application now takes advantage of `Ktor`'s robust HttpClient framework and integrates strictly with `kotlinx.serialization` for seamless JSON extraction, demonstrating a more idiomatic Kotlin networking stack.
- **State Hoisting** — ViewModel responsibilities are sharpened using Kotlin's `StateFlow`. As synchronous updates occur (like input changes or network returns), Compose intelligently re-evaluates the component tree naturally without requiring manual view lookups or complex observation bindings.
- **WMO Weather Codes** — The `WeatherCard` composable maps standard WMO weather codes to human-readable descriptions with emoji icons via a `when` expression, covering all major categories (clear, cloudy, fog, drizzle, rain, snow, thunderstorms).
- **Location Picker Round-Trip** — The `LocationPickerActivity` uses `setResult()` to send the map's camera position (latitude/longitude) back to the main screen. `WeatherUI` registers a `rememberLauncherForActivityResult` that receives the coordinates, updates the ViewModel, and triggers an automatic weather fetch — completing a full round-trip flow.

## Conclusion

This project covers Tutorial 1, Tutorial 2, and Tutorial 3: fundamental Kotlin programming, object-oriented design, advanced language features (sealed classes, generics, operator overloading, compile-time annotation processing with kapt), and Android application development. The CoolWeatherApps demonstrate clean MVVM architecture, real-time REST API consumption using Retrofit and Ktor, GPS integration, Google Maps integration, responsive layouts across both XML and Jetpack Compose toolkits — all written following modern Android development practices.