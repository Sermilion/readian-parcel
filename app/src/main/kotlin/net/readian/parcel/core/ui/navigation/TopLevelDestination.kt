package net.readian.parcel.core.ui.navigation

import androidx.annotation.StringRes
import net.readian.parcel.core.designsystem.icon.Icon
import net.readian.parcel.core.navigation.ParcelDestination

data class TopLevelDestination(
    val destination: ParcelDestination,
    val selectedIcon: Icon,
    @StringRes val iconTextResource: Int,
)
