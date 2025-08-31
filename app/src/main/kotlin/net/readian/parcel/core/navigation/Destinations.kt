package net.readian.parcel.core.navigation

import kotlinx.serialization.Serializable

sealed interface ParcelDestination

@Serializable
data object LoginDestination : ParcelDestination

@Serializable
data object PackagesDestination : ParcelDestination

@Serializable
data class PackageDetailDestination(
  val packageId: String,
) : ParcelDestination
