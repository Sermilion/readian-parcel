# 📦 Readian Parcel - Package Tracking App (parcelapp.net)

> A modern Android application for package tracking and delivery management, built with Jetpack Compose and Clean Architecture principles.

## 📱 Screenshots

<img width="267" height="553" alt="Screenshot 2025-09-01 at 09 43 48" src="https://github.com/user-attachments/assets/af20455c-629a-494e-9fa0-d684af6cccea" />
<img width="262" height="556" alt="Screenshot 2025-09-01 at 09 43 59" src="https://github.com/user-attachments/assets/e3202a57-dfce-4ba7-a381-58e0b03f7ce3" />
<img width="261" height="551" alt="Screenshot 2025-09-01 at 09 45 57" src="https://github.com/user-attachments/assets/b602a7d4-7581-46f5-974c-8c4058ea849c" />

## 🚀 Features

### Core Functionality
- **Package Tracking**: Track multiple packages with delivery status updates
- **Multi-Carrier Support**: Support for various shipping carriers via Parcel API (parcelapp.net)
- **Search & Filtering**: Filter packages by status (Active, Delivered, All) with real-time search
- **Offline Support**: Local Room database for offline data access
- **Rate Limiting**: Smart API quota management with DataStore persistence (20 requests/hour as per official API)

### Architecture Patterns
- **Clean Architecture**: Strict separation of concerns with data, domain, and presentation layers
- **MVVM**: Modern reactive architecture with StateFlow and unidirectional data flow
- **Repository Pattern**: Centralized data management with multiple data sources
- **Dependency Injection**: Full Hilt integration for testable, modular code
- **Feature-Based Modularization**: Scalable codebase organization

### Technology Stack

> **🚀 Recently Updated**: Project now uses **Kotlin 2.0.20** with the new **K2 compiler**, **Java 17** target, and **Android API 36** for cutting-edge performance and latest platform features.

#### **Core Framework**
- **Language**: Kotlin 2.0.20 with K2 compiler and latest language features
- **JVM Target**: Java 17 for optimal performance and compatibility
- **Minimum SDK**: API 26 (Android 8.0)
- **Target SDK**: API 36 (Android 16)
- **Compile SDK**: API 36

#### **UI & Presentation**
- **Jetpack Compose**: 100% Compose
- **Material Design 3**: Modern design system implementation
- **Navigation Component**: Type-safe navigation with Compose Navigation
- **ViewModel**: Lifecycle-aware state management
- **Compose Animation**: Smooth, performant animations throughout

#### **Architecture & DI**
- **Hilt**: Dependency injection framework (v2.55) with KSP support
- **Room**: Local database with coroutines support  
- **DataStore**: Modern preferences and proto data storage
- **Coroutines**: Async programming with structured concurrency
- **Flow**: Reactive data streams for real-time updates

#### **Networking & Data**
- **Retrofit**: HTTP client with Kotlin serialization
- **OkHttp**: HTTP interceptors for authentication and logging
- **Kotlin Serialization**: JSON parsing with compile-time safety
- **Protocol Buffers**: Efficient data serialization for local storage
- **Chucker**: Network debugging (debug builds only)

#### **Quality & Development**
- **KSP**: Kotlin Symbol Processing 2.0.20-1.0.25 for better compile times
- **Detekt**: Static code analysis and linting
- **Spotless**: Code formatting with ktlint integration
- **Timber**: Structured logging framework
- **Kotlin Compose Compiler**: New Kotlin 2.x integrated Compose compiler

#### **Security & Performance**
- **Security Crypto**: Encrypted preferences for sensitive data
- **ProGuard**: Code obfuscation and optimization

#### **Advanced Features**
- **Custom Build Logic**: Gradle convention plugins for build consistency
- **Multi-Flavor Support**: Google Play and alternative distribution channels
- **In-App Updates**: Seamless app update experience
- **Rate Limiting**: Intelligent API quota management with persistence

### Package Details
- Comprehensive delivery timeline
- Interactive event tracking
- Carrier information and branding
- Expected delivery date predictions

### User Authentication
- Secure API key management (parcelapp.net)
- Encrypted credential storage
- Seamless login/logout flow

### Quick Start
```bash
# Clone the repository
git clone https://github.com/your-username/readian-parcel.git
cd readian-parcel
```

### Build Variants
- **Debug**: Development build with Chucker network debugging
- **Release**: Production build with code obfuscation and optimization
- **Google Play**: Play Store distribution variant
- **Other**: Alternative distribution channels

## 📊 Performance Optimizations

### Runtime Performance  
- **K2 Compiler**: Faster compilation times and improved code generation with Kotlin 2.0
- **Coroutines**: Efficient async operations
- **Flow**: Backpressure-aware reactive streams
- **Room**: Optimized database queries
- **List Rendering**: Compose LazyColumn optimizations
- **Java 17**: Enhanced performance and modern JVM features

### Network Efficiency
- **Rate Limiting**: Intelligent API quota management with DataStore persistence
- **Offline Support**: Local-first data architecture with Room database
- **Manual Refresh**: User-initiated refresh with cooldown protection

## 🔒 Security Implementation

### Data Protection
- **Encrypted Storage**: Security Crypto for sensitive data
- **API Key Security**: Secure credential management
- **ProGuard**: Code obfuscation in release builds

### Privacy & Compliance
- **No User Tracking**: Privacy-focused design
- **Local Data Storage**: Minimal cloud dependencies
- **Secure Defaults**: Security-first configuration

## 🤝 Contributing

Android development practices including:

- **Clean Architecture**: Scalable, testable code organization
- **Modern UI**: Jetpack Compose with Material Design 3
- **Reactive Programming**: Coroutines and Flow throughout
- **Security**: Industry-standard security implementations
- **Performance**: Optimized for real-world usage
- **Documentation**: Production-ready documentation standards

## ⚠️ Disclaimer

**This is an unofficial, independent project and is not affiliated with, endorsed by, or connected to Parcel (parcelapp.net) in any way.**

### Important Notes:
- This app requires a **premium Parcel subscription** to function
- Users must obtain their own API key from [parcelapp.net](https://web.parcelapp.net/#copyAPIKey)
- This project provides only the client application code - it does not include access to the Parcel API
- The developer is not responsible for any API changes, service interruptions, or account issues
- Use of the Parcel API is subject to Parcel's own terms of service
- API rate limits (20 requests/hour) are enforced as per Parcel's official documentation

### Data & Privacy:
- Your API key is stored locally on your device using encrypted storage
- No user data is collected or transmitted to any third parties
- All communication is directly between your device and Parcel's servers

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

**Note**: This license applies only to the application source code. The Parcel API and service are owned by Parcel and subject to their own terms.

---

**Developer**: [@sermilion](https://github.com/sermilion)  
**Tech Stack**: Android API 36 • Kotlin 2.0.20 (K2 compiler) • Java 17 • Jetpack Compose • Clean Architecture • MVVM • Hilt 2.55 • Room • Retrofit • Material Design 3
