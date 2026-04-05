# 04 Architecture

## Architectural Pattern
The application will follow the **MVVM (Model-View-ViewModel)** architecture pattern, officially recommended by Google. 

### Presentation Layer (UI)
- **Activities/Fragments**: Handle user interaction and observe LiveData/Flow from the ViewModel.
- **XML Layouts**: Use Data Binding or View Binding to strictly separate logic from UI views.
- **State Handling**: A sealed class `UiState<T>` (Loading, Success, Error) to manage screen states robustly.

### Domain / ViewModel Layer
- **ViewModels**: Expose data streams to the UI. Act as mediators between the presentation and the repository. Coroutines are used via `viewModelScope`.

### Data Layer (Repository)
- **Repository Pattern**: A single source of truth (`DogRepository`). It decides whether to fetch from the Network (`DogApiService`) or the Local Database (`DogDao`).

### Libraries & Tech Stack
- **Networking**: Retrofit2 + OkHttp + Gson Converter
- **Local Persistence**: Room Database
- **Asynchrony**: Kotlin Coroutines & Flow
- **Image Loading**: Glide (or Coil)
- **Dependency Injection**: Optional (Manual DI or Hilt if time allows)
