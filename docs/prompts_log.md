# Prompts Log

This document records the exact prompts fed to AntiGravity AI during the conceptualization of the Image Explorer App.

### Prompt 1: Project Scaffolding
> **User**: "I need to build an Android app called Doggo that fetches images of dogs from the public dog.ceo/api/. The app needs to save favorite images to a Room database and show them in a grid. I need you to act as a Senior Android Developer and plan the architecture using MVVM and Material 3 design. Can we start by designing the Data layer?"

### Prompt 2: Data Layer Request
> **User**: "Based on the api documentation, design the `FavoriteDog` entity and the Room `FavoriteDogDao`. Also, design the Retrofit service to fetch random images."

### Prompt 3: UI Implementation
> **User**: "Now that the data layer is ready, generate the `MainViewModel` observing the flow of favorites from Room, and the XML layout using a RecyclerView with ViewBinding to display the fetched images."

### Prompt 4: Finishing Touches
> **User**: "Generate an implementation plan for adding dark mode dynamically."
