import com.android.build.gradle.LibraryExtension
import io.readian.android.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("readian.android.library")
                apply("readian.android.hilt")
            }
            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner =
                        "io.readian.android.core.testing.ReadianTestRunner"
                }
            }

            dependencies {
//                add("implementation", project(":core:model"))
//                add("implementation", project(":core:ui"))
                add("implementation", project(":core:designsystem"))
                add("implementation", project(":core:data"))
                add("implementation", project(":core:common"))
                add("implementation", project(":core:navigation"))
//                add("implementation", project(":core:domain"))
//                add("implementation", project(":core:analytics"))

                add("implementation", libs.findLibrary("coil.kt").get())
                add("implementation", libs.findLibrary("coil.kt.compose").get())

                add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())

                add("implementation", libs.findLibrary("kotlinx.coroutines.android").get())
            }
        }
    }
}
