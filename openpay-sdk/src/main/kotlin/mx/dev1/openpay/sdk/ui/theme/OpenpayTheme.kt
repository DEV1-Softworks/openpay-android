package mx.dev1.openpay.sdk.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OpenpayBlue = Color(0xFF0072CE)
private val OpenpayBlueDark = Color(0xFF00447C)
private val OpenpayTeal = Color(0xFF00B2A9)

private val OpenpayLightColorScheme = lightColorScheme(
    primary = OpenpayBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0E4FF),
    onPrimaryContainer = OpenpayBlueDark,
    secondary = OpenpayTeal,
    onSecondary = Color.White,
    error = Color(0xFFBA1A1A),
)

private val OpenpayDarkColorScheme = darkColorScheme(
    primary = Color(0xFF9CCAFF),
    onPrimary = OpenpayBlueDark,
    primaryContainer = Color(0xFF00335E),
    onPrimaryContainer = Color(0xFFD0E4FF),
    secondary = Color(0xFF54DBD2),
    onSecondary = Color(0xFF003734),
    error = Color(0xFFFFB4AB),
)

/**
 * Material 3 theme with the Openpay brand palette. Wrap SDK components with
 * it when the host app does not provide its own MaterialTheme.
 */
@Composable
fun OpenpayTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (useDarkTheme) OpenpayDarkColorScheme else OpenpayLightColorScheme,
        content = content,
    )
}
