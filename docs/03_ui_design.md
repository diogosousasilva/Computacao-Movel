# 03 UI Design

## Design System
- **Theme**: Material Design 3 (Dynamic Color Support)
- **Palette**: 
  - Light Mode: Warm, organic tones (Browns, Creams) prioritizing friendly aesthetics.
  - Dark Mode: Dark gray backgrounds with bright amber accents.
- **Typography**: Roboto + standard Material Typography scales.

## Core Screens

### 1. Home / Feed Screen
- **Toolbar**: App Title and an action to filter by breed.
- **Content**: A `RecyclerView` using a `GridLayoutManager` (span count 2 in portrait, 3-4 in landscape) displaying cached image thumbnails.
- **Action**: Clicking an image opens the Detail Screen.

### 2. Detail Screen
- **Content**: A high-resolution image taking up most of the screen.
- **Actions**:
  - Share Button.
  - Heart Toggle (Add/Remove from favorites).
  - Back Navigation.

### 3. Favorites Screen
- **Toolbar**: "My Favorites".
- **Content**: Similar `RecyclerView` to the Home screen, but pulling exclusively from the local Room database using `Flow` or `LiveData`.
- **Empty State**: Friendly illustration of a dog asking the user to favorite some images if the database is empty.
