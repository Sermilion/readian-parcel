# Development Notes

## Build Commands

- **Debug build**: `./gradlew app:assembleGooglePlayDebug`  
- **Release build**: `./gradlew app:assembleGooglePlayRelease`
- **Clean**: `./gradlew clean`
- **Tests**: `./gradlew test`

## Tech Stack

- **Kotlin**: 1.9.10
- **Android Gradle Plugin**: 8.1.2  
- **Java**: 11+ (using Java 11 target, Java 23 runtime available)
- **Compose BOM**: 2023.10.00

## Network Debugging

The app includes Chucker for network debugging in debug builds:
- Chucker will automatically show a notification when network requests are made
- Tap the notification to view detailed request/response information
- Chucker is only included in debug builds - it's automatically excluded from release builds
- Useful for debugging API issues, JSON parsing problems, and network errors