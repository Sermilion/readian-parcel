package net.readian.parcel.core.navigation

import kotlinx.serialization.Serializable

/**
 * Typed navigation destinations for the Parcel tracking app
 */
sealed interface ParcelDestination

@Serializable
data object LoginDestination : ParcelDestination

@Serializable
data object PackagesDestination : ParcelDestination

@Serializable
data object ProfileDestination : ParcelDestination

@Serializable
data class PackageDetailDestination(
    val packageId: String
) : ParcelDestination

/**
 * Top-level destinations for bottom navigation
 */
@Serializable
data object PackagesTab : ParcelDestination

@Serializable
data object ProfileTab : ParcelDestination