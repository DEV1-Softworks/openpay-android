package mx.dev1.openpay.sdk.i18n

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/**
 * Language selection for every text the SDK shows.
 *
 * By default the SDK follows the device locale automatically (falling back
 * to English when the device language is not bundled). Assign [override] to
 * force one of the bundled languages regardless of the device settings:
 *
 * ```kotlin
 * OpenpayLocale.override = OpenpayLanguage.PORTUGUESE  // force Portuguese
 * OpenpayLocale.override = null                        // back to automatic
 * ```
 *
 * The override is observable Compose state, so SDK composables on screen
 * recompose immediately when it changes.
 */
object OpenpayLocale {

    /** Forced language, or null to follow the device locale. */
    var override: OpenpayLanguage? by mutableStateOf(null)

    /**
     * Returns [context] unchanged when no override is set, otherwise a
     * context whose resources resolve in the overridden language. Useful for
     * view-based apps; Compose content localizes itself automatically.
     */
    fun localize(context: Context): Context {
        val overrideLanguage = override ?: return context
        val overrideLocale = Locale.forLanguageTag(overrideLanguage.languageTag)
        val localizedConfiguration = Configuration(context.resources.configuration)
        localizedConfiguration.setLocale(overrideLocale)
        return context.createConfigurationContext(localizedConfiguration)
    }
}
