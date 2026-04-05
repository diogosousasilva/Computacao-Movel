# 08 Implementation Plan

## Phase 1: Project Setup
1. Generate standard Android template with empty Activity.
2. Add dependencies (`retrofit`, `gson`, `room-runtime`, `room-compiler`, `room-ktx`, `glide`, `lifecycle-viewmodel-ktx`, `coroutines`).
3. Set up ViewBinding in `build.gradle.kts`.

## Phase 2: Data Layer (Offline First)
1. Create models (`FavoriteDog.kt`, `RandomDogResponse.kt`).
2. Implement Room: Create `FavoriteDogDao.kt` and `AppDatabase.kt`.
3. Implement Retrofit: Create `DogApiService.kt` and `RetrofitClient.kt`.
4. Create single source of truth `DogRepository.kt` combining both sources.

## Phase 3: Domain & ViewModels
1. Create `HomeViewModel.kt` to load arrays of `DogImage`.
2. Create `FavoritesViewModel.kt` to observe the `Flow<List<FavoriteDog>>` from Room.
3. Handle UI States (Loading, Error, Success).

## Phase 4: User Interface (UI)
1. Create `activity_main.xml` with Bottom Navigation.
2. Create `fragment_home.xml` and `fragment_favorites.xml` with Recycler Views.
3. Create `item_dog_image.xml` for the grid cells.
4. Implement `DogAdapter.kt` using `DiffUtil`.
5. Connect ViewModels to the UI and ensure images load via Glide.

## Phase 5: Polish & Testing
- Validate Landscape Mode layout.
- Review dark theme colors constraints.
- Fix memory leaks by clearing adapter references.
