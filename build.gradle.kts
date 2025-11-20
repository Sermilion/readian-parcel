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
  id("com.github.ben-manes.versions") version "0.52.0"
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
    ktlint().editorConfigOverride(
      mapOf(
        "indent_size" to "2"
      )
    )
  }
}

subprojects {
  apply(plugin = "com.diffplug.spotless")
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("${layout.buildDirectory.get()}/**/*.kt")
      targetExclude("bin/**/*.kt")
      ktlint().editorConfigOverride(
        mapOf(
          "indent_size" to "2",
          "ij_kotlin_allow_trailing_comma" to true,
          "ij_kotlin_allow_trailing_comma_on_call_site" to true,
          "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
        )
      )
    }
    kotlinGradle {
      target("*.gradle.kts")
      ktlint().editorConfigOverride(
        mapOf(
          "indent_size" to "2"
        )
      )
    }
  }


  apply(plugin = "io.gitlab.arturbosch.detekt")
  detekt {
    config.setFrom(files("$rootDir/detekt.yml"))
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
