@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.firebase.crashlytics) apply false
  alias(libs.plugins.firebase.perf) apply false
  alias(libs.plugins.hilt) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.spotless)
  alias(libs.plugins.detekt)
  alias(libs.plugins.org.jetbrains.kotlin.android) apply false
}

buildscript {
  repositories {
    google()
    mavenCentral()
  }
}


spotless {
  predeclareDeps()
}

configure<com.diffplug.gradle.spotless.SpotlessExtensionPredeclare> {
  kotlin {
    ktlint()
  }
}

subprojects {
  apply(plugin = "com.diffplug.spotless")
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("$buildDir/**/*.kt")
      targetExclude("bin/**/*.kt")
      ktlint().editorConfigOverride(
        mapOf(
          "ij_kotlin_allow_trailing_comma" to true,
          "ij_kotlin_allow_trailing_comma_on_call_site" to true
        )
      )
    }
    kotlinGradle {
      target("*.gradle.kts")
      ktlint()
    }
  }


  apply(plugin = "io.gitlab.arturbosch.detekt")
  detekt {
    config = files("$rootDir/detekt.yml")
    baseline = file("${rootProject.projectDir}/config/baseline.xml")
    buildUponDefaultConfig = true
  }


  dependencyLocking {
    // For now lets disable explicit locking
    // lockAllConfigurations()
    unlockAllConfigurations()
  }

  dependencies {
    detektPlugins("com.twitter.compose.rules:detekt:0.0.26")
  }
}
