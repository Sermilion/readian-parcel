package io.readian.android

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

enum class FlavorDimension {
    store
}

// The Application can either be distributed to the Playstore or another source.
// Depending on the target certain functionality may not be present, for example:
// - playstore offers api's for inappupdates wich are innaccessible by others
enum class ReadianFlavor(val dimension: FlavorDimension, val applicationIdSuffix: String? = null) {
    googlePlay(FlavorDimension.store),
    other(FlavorDimension.store)
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: ReadianFlavor) -> Unit = {}
) {
    commonExtension.apply {
        flavorDimensions += FlavorDimension.store.name
        productFlavors {
            ReadianFlavor.values().forEach {
                create(it.name) {
                    dimension = it.dimension.name
                    flavorConfigurationBlock(this, it)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (it.applicationIdSuffix != null) {
                            applicationIdSuffix = it.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}