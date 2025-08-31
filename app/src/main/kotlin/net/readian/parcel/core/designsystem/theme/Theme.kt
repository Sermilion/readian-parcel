package net.readian.parcel.core.designsystem.theme

import android.os.Build
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@VisibleForTesting
val LightDefaultColorScheme = lightColorScheme(
    primary = ReadianBlue,
    onPrimary = Light1,
    primaryContainer = Light3,
    onPrimaryContainer = ReadianBlueDark,
    secondary = Light8,
    onSecondary = Light1,
    secondaryContainer = Light3,
    onSecondaryContainer = Dark1,
    tertiary = Light7,
    onTertiary = Light1,
    tertiaryContainer = Light3,
    onTertiaryContainer = Dark1,
    error = Red40,
    onError = Light1,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = Light2,
    onBackground = Dark2,
    surface = Light1,
    onSurface = Dark3,
    surfaceVariant = Light4,
    onSurfaceVariant = Dark4,
    outline = Light6,
)

/**
 * Dark default theme color scheme
 */
@VisibleForTesting
val DarkDefaultColorScheme = darkColorScheme(
    primary = ReadianBlue,
    onPrimary = Dark1,
    primaryContainer = ReadianBlueDark,
    onPrimaryContainer = Light1,
    secondary = Dark7,
    onSecondary = Dark1,
    secondaryContainer = Dark5,
    onSecondaryContainer = Light1,
    tertiary = Dark7,
    onTertiary = Dark1,
    tertiaryContainer = Dark4,
    onTertiaryContainer = Light1,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = Dark2,
    onBackground = Light1,
    surface = Dark3,
    onSurface = Light1,
    surfaceVariant = Dark6,
    onSurfaceVariant = Dark7,
    outline = Dark6,
)

/**
 * Light Android theme color scheme
 */
@VisibleForTesting
val LightAndroidColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = DarkGreen40,
    onSecondary = Color.White,
    secondaryContainer = DarkGreen90,
    onSecondaryContainer = DarkGreen10,
    tertiary = Teal40,
    onTertiary = Color.White,
    tertiaryContainer = Teal90,
    onTertiaryContainer = Teal10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = DarkGreenGray99,
    onBackground = DarkGreenGray10,
    surface = DarkGreenGray99,
    onSurface = DarkGreenGray10,
    surfaceVariant = GreenGray90,
    onSurfaceVariant = GreenGray30,
    outline = GreenGray50,
)

/**
 * Dark Android theme color scheme
 */
@VisibleForTesting
val DarkAndroidColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    secondary = DarkGreen80,
    onSecondary = DarkGreen20,
    secondaryContainer = DarkGreen30,
    onSecondaryContainer = DarkGreen90,
    tertiary = Teal80,
    onTertiary = Teal20,
    tertiaryContainer = Teal30,
    onTertiaryContainer = Teal90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = DarkGreenGray10,
    onBackground = DarkGreenGray90,
    surface = DarkGreenGray10,
    onSurface = DarkGreenGray90,
    surfaceVariant = GreenGray30,
    onSurfaceVariant = GreenGray80,
    outline = GreenGray60,
)

val LightDefaultGradientColors = GradientColors(
    primary = ReadianBlue,
    secondary = ReadianBlueDisabled,
    tertiary = Light4,
    neutral = Light3,
)

val LightAndroidBackgroundTheme =
    BackgroundTheme(color = DarkGreenGray95)
val DarkAndroidBackgroundTheme =
    BackgroundTheme(color = Color.Black)

@Composable
fun StarterAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    androidTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = getColorScheme(dynamicColor, darkTheme, androidTheme)

    val defaultGradientColors = GradientColors()
    val gradientColors = when {
        dynamicColor -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                defaultGradientColors
            } else {
                if (darkTheme) defaultGradientColors else LightDefaultGradientColors
            }
        }

        androidTheme -> defaultGradientColors
        else -> if (darkTheme) defaultGradientColors else LightDefaultGradientColors
    }

    val defaultBackgroundTheme = BackgroundTheme(
        color = colorScheme.background,
        tonalElevation = 0.dp,
    )
    val backgroundTheme = when {
        dynamicColor -> defaultBackgroundTheme
        androidTheme -> if (darkTheme) DarkAndroidBackgroundTheme else LightAndroidBackgroundTheme
        else -> defaultBackgroundTheme
    }

    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides backgroundTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = ReadianTypography,
            content = content,
        )
    }
}

@Composable
private fun getColorScheme(
    dynamicColor: Boolean,
    darkTheme: Boolean,
    androidTheme: Boolean,
) = when {
    dynamicColor -> getDynamicColors(darkTheme)
    androidTheme -> if (darkTheme) DarkAndroidColorScheme else LightAndroidColorScheme
    else -> if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
}

@Composable
private fun getDynamicColors(darkTheme: Boolean): ColorScheme {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
    }
}
