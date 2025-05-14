<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# Media+ Android App

This is an Android application for playing video and audio files with a modern UI.

## App Structure

- MVVM architecture (Model-View-ViewModel)
- Room database for local persistence
- ExoPlayer for media playback
- Material Design UI components

## Key Components

- MediaRepository - Handles media file scanning and database operations
- PlaylistRepository - Manages playlists
- Video and Audio player activities - Handle media playback
- Fragment-based UI with navigation components

## Dependencies

- AndroidX libraries
- ExoPlayer for media playback
- Room for database
- Glide for image loading
- Material Components for UI
- Kotlin Coroutines for asynchronous operations
