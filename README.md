# Thmanyah Demo

A modern Android application showcasing the latest Android development practices and libraries.

## Features

- Modern Android Architecture Components
- Jetpack Compose UI
- MVI (Model-View-Intent) with MVVM
- Dependency Injection with Hilt
- Kotlin Coroutines for asynchronous operations
- Retrofit for network calls
- Material 3 Design
- Clean Architecture
- Unit Testing

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVI + MVVM + Clean Architecture
- **UI**: Jetpack Compose
- **DI**: Hilt
- **Async**: Coroutines + Flow
- **Network**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Testing**: JUnit, Mockk

## Project Structure

```
app/
├── data/           # Data layer
│   ├── api/       # API interfaces and models
│   └── repositoryImpl/ # Repository implementations
├── domain/        # Domain layer
│   ├── model/     # Domain models
│   ├── repository/# Repository interfaces
│   └── usecase/   # Use cases
└── ui/            # Presentation layer
    ├── common/    # Common UI components
    ├── home/      # Home screen
    ├── search/    # Search screen
    └── theme/     # App theme
```

## Testing

The project includes comprehensive testing at different levels:

### Unit Tests
- Located in `src/test/java`
- Tests for ViewModels, UseCases
- Uses JUnit, Mockk, and Turbine

### Running Tests
```bash
# Run unit tests only
./gradlew testDebugUnitTest

```

## Getting Started

1. Clone the repository
2. Open the project in Android Studio
3. Sync the project with Gradle files
4. Run the app on an emulator or physical device

## Requirements

- Android Studio 
- JDK 17
- Android SDK 34
- Kotlin 1.8.0

## Dependencies

- Jetpack Compose BOM: 2024.02.00
- Hilt: 2.50
- Retrofit: 2.9.0
- Room: 2.6.1
- Coil: 2.5.0
- Navigation Compose: 2.7.7
- Material3: 1.2.0
- Testing:
- JUnit: 4.13.2
- Mockk: 1.13.9

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Key Libraries

- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt](https://dagger.dev/hilt/)
- [Coil](https://coil-kt.github.io/coil/compose/)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/flow.html)

## Paging Home

- Home sections are loaded with infinite scroll.
- When the user scrolls near the end, the next page is fetched and appended.
- If loading the next page fails, a toast is shown and the current list stays visible (no full-screen error).

## Search

- Search input uses debounce (200ms) to avoid unnecessary API calls.
- Results are only shown after the user types.

## Architecture

The app uses **MVI (Model-View-Intent)** together with **MVVM** and Clean Architecture:

### MVI + MVVM

- **Model (State)**  
  A single immutable state object (e.g. `HomeState`) holds everything the screen needs: content, loading flags, pagination, and errors. The UI is a direct function of this state.

- **View**  
  Composable screens observe the state via `StateFlow` and render it. User actions are sent as **Intents** (e.g. `LoadInitial`, `LoadNextPage`) instead of calling multiple ViewModel methods.

- **Intent**  
  User actions are represented as sealed types (e.g. `HomeIntent`). The View only sends intents; the ViewModel processes them and runs side effects (e.g. use cases, API calls).

- **Reducer**  
  A pure function `reduce(state, result) -> newState` handles all state updates. Results come from side effects (e.g. `Success`, `Error`, `LoadMoreError`). This keeps transitions predictable and testable.

- **MVVM**  
  ViewModels remain the same: they own state, handle intents, and expose a single `StateFlow<State>`. The “Intent” layer is an extra step that makes the data flow one-way (View → Intent → ViewModel → State → View).

### Other

- **Hilt** for dependency injection.
- **StateFlow** for reactive UI state.
- **Channel** for intent delivery (single consumer, each intent processed once).
