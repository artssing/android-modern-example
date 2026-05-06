# Android Modern Example App

This project is a modern Android example demonstrating various Jetpack Compose and Material 3 features.

## Recent Changes (2026-5-6)

### TopAppBar (TitleBar) Implementation
- Added a `TopAppBar` to the main `Scaffold` in `MainActivity.kt`.
- Used `ExperimentalMaterial3Api` for Material 3 components.

### TopAppBar Customization
- Customized `TopAppBar` colors using `TopAppBarDefaults.topAppBarColors`.
- Set initial background color and title text color.

### Scroll Behavior
- Implemented `scrollBehavior` using `TopAppBarDefaults.pinnedScrollBehavior()`.
- Integrated `nestedScroll` with `Scaffold` to detect content scrolling.
- Configured `scrolledContainerColor` to change the `TopAppBar` background color dynamically when content is scrolled.

### UI Improvements
- Updated `HomeScreen` with scrollable content to demonstrate the TopAppBar color transition.
- Cleaned up imports and organized code structure in `MainActivity.kt`.
