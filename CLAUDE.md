# Development Notes

## JDK Compatibility Issue

This Android project requires OpenJDK 17 due to a known compatibility issue between newer JDK versions (21+) and the Android Gradle Plugin's JDK image transformation process.

### Solution

Use the provided wrapper script instead of the regular gradlew:

```bash
./gradlew-jdk17 app:assembleGooglePlayDebug
```

Or set JAVA_HOME explicitly:

```bash
JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.16/libexec/openjdk.jdk/Contents/Home ./gradlew <task>
```

### Build Commands

- **Debug build**: `./gradlew-jdk17 app:assembleGooglePlayDebug`  
- **Release build**: `./gradlew-jdk17 app:assembleGooglePlayRelease`
- **Clean**: `./gradlew-jdk17 clean`
- **Tests**: `./gradlew-jdk17 test`

### Error Details

Without JDK 17, you'll see this error:
```
Failed to transform core-for-system-modules.jar to match attributes {artifactType=_internal_android_jdk_image}
Error while executing process .../bin/jlink with arguments {...}
```

This occurs because jlink in JDK 21+ has breaking changes that are incompatible with Android Gradle Plugin 8.1.2.