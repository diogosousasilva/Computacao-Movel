# 02 Features

## Core Functional Requirements
1. **Browse Random Dogs**: Fetches and displays a grid/list of random dog images.
2. **View by Breed**: Allows users to select a specific breed and view images belonging to that breed.
3. **Favorite Images**: Users can tap a "heart" icon to save a specific dog image locally.
4. **Favorites Gallery**: A dedicated screen outlining all locally saved, offline-accessible favorite dogs.
5. **Remove from Favorites**: Users can delete an image from their favorites collection.

## Non-Functional Requirements
- **Performance**: Use Glide or Coil for image caching and asynchronous loading to prevent UI stuttering.
- **Offline Reliability**: The "Favorites" gallery must work fully offline (the images might be cached, but the URLs/references must be stored in Room).
- **Responsiveness**: Support for Portrait and Landscape modes using appropriate constraints.
- **Support**: Minimum Android SDK API 26.

## Future / Out of Scope
- User authentication and cloud-syncing for favorites.
- Uploading photos of the user's personal dogs.
