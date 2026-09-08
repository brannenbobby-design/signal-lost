# Signal Lost

A native Android retro music-video channel-surfing app using YouTube's embedded player.

## Current prototype

- Native Kotlin + Jetpack Compose shell
- Full-screen YouTube IFrame playback inside Android WebView
- Channel Up / Channel Down controls
- Automatic next-video behavior when a video ends
- Retro channel bug: `CH 01 • 90s ROCK`
- Seven starter channel slots
- GitHub Actions workflow that builds a debug APK

## Important

Signal Lost uses YouTube's embedded player. It does not extract, download, or separately stream YouTube audio/video files.

The starter video IDs are placeholders for testing channel behavior. The catalog will be curated and cleaned up during development.

## Build locally

Open the project in Android Studio or run:

```bash
gradle :app:assembleDebug
```

APK output:

`app/build/outputs/apk/debug/app-debug.apk`
