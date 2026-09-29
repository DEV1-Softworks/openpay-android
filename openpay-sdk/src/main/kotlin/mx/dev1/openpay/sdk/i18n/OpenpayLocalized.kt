package mx.dev1.openpay.sdk.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

/**
 * Applies the [OpenpayLocale.override] to the composition so every
 * stringResource call inside resolves in the overridden language.
 * With no override the content composes untouched.
 */
@Composable
internal fun OpenpayLocalized(content: @Composable () -> Unit) {
    val overrideLanguage = OpenpayLocale.override
    if (overrideLanguage == null) {
        content()
        return
    }

    val baseContext = LocalContext.current
    val localizedContext = remember(overrideLanguage, baseContext) {
        OpenpayLocale.localize(baseContext)
    }
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedContext.resources.configuration,
        content = content,
    )
}
