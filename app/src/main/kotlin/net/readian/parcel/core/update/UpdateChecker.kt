package net.readian.parcel.core.update

import androidx.compose.runtime.Composable
import se.warting.inappupdate.compose.RequireLatestVersion

@Composable
fun UpdateChecker(content: @Composable () -> Unit) = RequireLatestVersion(content)
