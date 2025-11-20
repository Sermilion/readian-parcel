import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import io.readian.android.ReadianBuildType
plugins {
  alias(libs.plugins.protobuf)
  alias(libs.plugins.ksp)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.readian.android.application)
  alias(libs.plugins.readian.android.application.compose)
  alias(libs.plugins.readian.android.application.flavors)
  alias(libs.plugins.readian.android.hilt)
  alias(libs.plugins.readian.android.room)
  // alias(libs.plugins.readian.android.application.firebase) // Disabled until Firebase is properly configured
}

val localProperties by lazy {
  gradleLocalProperties(rootDir, providers)
}

android {
  namespace = "net.readian.parcel"

  defaultConfig {
    applicationId = "net.readian.parcel"
    versionCode = localProperties.getProperty("version.code", "9999").toInt()
    versionName = localProperties.getProperty("version.name", "DEV")

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    vectorDrawables {
      useSupportLibrary = true
    }

    val apiBaseUrl = localProperties.getProperty("api.baseUrl", "https://api.parcel.app/")
    buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
    buildFeatures {
      buildConfig = true
    }
  }

  buildTypes {
    debug {
      applicationIdSuffix = ReadianBuildType.DEBUG.applicationIdSuffix
    }
    val release by getting {
      isMinifyEnabled = true
      applicationIdSuffix = ReadianBuildType.RELEASE.applicationIdSuffix
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

      signingConfig = try {
        signingConfigs.getByName("release")
      } catch (e: UnknownDomainObjectException) {
        signingConfigs.getByName("debug")
      }
    }
  }

  packaging {
    resources {
      excludes.add("/META-INF/{AL2.0,LGPL2.1}")
      excludes.add("META-INF/versions/**")
    }
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
      all {
        it.useJUnitPlatform()
      }
    }
  }
}

protobuf {
  protoc {
    artifact = libs.protobuf.protoc.get().toString()
  }
  generateProtoTasks {
    all().forEach { task ->
      task.builtins {
        val java by registering {
          option("lite")
        }
        val kotlin by registering {
          option("lite")
        }
      }
    }
  }
}

dependencies {
  implementation(libs.androidx.material3.android)
  implementation(libs.androidx.hilt.common)
  coreLibraryDesugaring(libs.android.desugarJdkLibs)

  androidTestImplementation(kotlin("test"))
  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.androidx.compose.ui.test)
  debugImplementation(libs.androidx.compose.ui.testManifest)

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.androidx.lifecycle.process)
  implementation(libs.androidx.compose.runtime.tracing)
  implementation(libs.androidx.compose.material3.windowSizeClass)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.window.manager)
  implementation(libs.androidx.profileinstaller)
  implementation(libs.kotlinx.coroutines.guava)
  implementation(libs.coil.kt)

  implementation(libs.compose.material.icons)
  implementation(libs.compose.constraintlayout)

  implementation(libs.kotlinx.collections.immutable)

  implementation(libs.okhttp)
  implementation(libs.retrofit)
  implementation(libs.retrofit.kotlin.serialization)

  implementation(libs.timber)

  // DataStore for persisting rate limiting state and user preferences
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.datastore)
  implementation(libs.protobuf.kotlin.lite)

  // Security
  implementation(libs.tink.android)

  // JSON serialization
  implementation(libs.kotlinx.serialization.json)

  // WorkManager not used currently; removed to reduce footprint

  // Chucker for network debugging
  debugImplementation("com.github.chuckerteam.chucker:library:4.2.0")
  releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.2.0")

  // Core functions
  testImplementation(libs.androidx.navigation.testing)
  testImplementation(kotlin("test"))
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.mockk)

  // Kotest
  testImplementation(libs.kotest.runner.junit5)
  testImplementation(libs.kotest.assertions.core)
  testImplementation(libs.kotest.property)

  // Turbine for Flow testing
  testImplementation(libs.turbine)

  // Robolectric for Android unit tests
  testImplementation(libs.robolectric)
  testImplementation("androidx.test:core:1.6.1")
}
