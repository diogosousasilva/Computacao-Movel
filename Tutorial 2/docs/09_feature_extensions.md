# 09. Feature Extensions

This document outlines potential future extensions for the Image Explorer (MIP-2) application, detailing the technical approach and required modifications for each new feature.

## Extension 1: Dog Breed Filtering

### Description
Allow users to select a specific dog breed from a dropdown or search bar to fetch images exclusively of that breed, rather than completely random dogs.

### Architecture & Implementation
- **API Update**: Use the `https://dog.ceo/api/breeds/list/all` endpoint to fetch available breeds and `https://dog.ceo/api/breed/{breed}/images/random` to fetch images of a specific breed.
- **UI Update**: Add a `Spinner` or an `AutoCompleteTextView` to the top of `HomeFragment`.
- **ViewModel**: `HomeViewModel` will hold a `StateFlow<String?>` for the selected breed. The `fetchNewDog()` method will conditionally use the specific breed endpoint if a breed is selected.

## Extension 2: Native Image Sharing

### Description
Allow users to share a funny or cute dog image directly to other applications (like WhatsApp, Instagram, or Messages) using Android's native share intent.

### Architecture & Implementation
- **UI Update**: Add a "Share" floating action button (FAB) or an `ImageButton` overlay on the `item_dog_image.xml` layout and the main image view in `HomeFragment`.
- **Logic**: When clicked, the app will create an `Intent(Intent.ACTION_SEND)`.
- **Data Handling**: Since we only have the image URL, the `Intent.EXTRA_TEXT` will contain the URL string. For a more advanced implementation, Glide could be used to download the `Bitmap`, save it to a local `FileProvider` cache, and share the actual image binary using `Intent.EXTRA_STREAM`.

## Extension 3: Infinite Scrolling Gallery

### Description
Instead of displaying a single image at a time that must be refreshed manually, convert the `HomeFragment` into an infinite scrolling gallery of dog images.

### Architecture & Implementation
- **API Update**: Use the multiple random images endpoint: `https://dog.ceo/api/breeds/image/random/10` to fetch images in batches.
- **UI Update**: Replace the single `ImageView` in `HomeFragment` with a `RecyclerView` using a `StaggeredGridLayoutManager`.
- **Logic**: Implement a scroll listener on the `RecyclerView`. When the user scrolls near the bottom of the list, trigger the `HomeViewModel` to fetch another batch of 10 images and append them to the existing list.
