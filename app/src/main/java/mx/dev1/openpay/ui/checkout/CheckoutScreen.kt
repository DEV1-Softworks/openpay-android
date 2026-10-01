package mx.dev1.openpay.ui.checkout

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import mx.dev1.openpay.R
import mx.dev1.openpay.sdk.core.OpenpayCountry
import mx.dev1.openpay.sdk.i18n.OpenpayLanguage
import org.koin.androidx.compose.koinViewModel

object CheckoutScreenTags {
    const val MERCHANT_ID_FIELD = "sample_merchant_id_field"
    const val PUBLIC_KEY_FIELD = "sample_public_key_field"
    const val DEVICE_SESSION_BUTTON = "sample_device_session_button"
    const val ADD_CARD_BUTTON = "sample_add_card_button"
    const val ERROR_MESSAGE = "sample_error_message"
}

/**
 * Configuration screen of the sample: merchant credentials, SDK language and
 * antifraud device session. The card form lives in its own screen, reached
 * through [onAddCardClick] once the configuration is complete.
 */
@Composable
fun CheckoutScreen(
    onAddCardClick: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val hostActivity = LocalContext.current as? Activity

    CheckoutScreenContent(
        uiState = uiState,
        onMerchantIdChange = viewModel::updateMerchantId,
        onPublicApiKeyChange = viewModel::updatePublicApiKey,
        onCountryChange = viewModel::updateCountry,
        onProductionModeChange = viewModel::updateProductionMode,
        onLanguageChange = viewModel::updateLanguage,
        onStartDeviceSession = { hostActivity?.let(viewModel::startDeviceSession) },
        onAddCardClick = onAddCardClick,
    )
}

/**
 * Stateless layout of the configuration screen, extracted so previews and
 * tests can render it with a fabricated [CheckoutUiState].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreenContent(
    uiState: CheckoutUiState,
    onMerchantIdChange: (String) -> Unit,
    onPublicApiKeyChange: (String) -> Unit,
    onCountryChange: (OpenpayCountry) -> Unit,
    onProductionModeChange: (Boolean) -> Unit,
    onLanguageChange: (OpenpayLanguage?) -> Unit,
    onStartDeviceSession: () -> Unit,
    onAddCardClick: () -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                // Shrinks the scrollable viewport when the keyboard opens so the
                // focused field can always scroll into view above the IME.
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MerchantConfigurationSection(
                uiState = uiState,
                onMerchantIdChange = onMerchantIdChange,
                onPublicApiKeyChange = onPublicApiKeyChange,
                onCountryChange = onCountryChange,
                onProductionModeChange = onProductionModeChange,
            )
            LanguageSection(
                selectedLanguage = uiState.selectedLanguage,
                onLanguageChange = onLanguageChange,
            )

            OutlinedButton(
                onClick = onStartDeviceSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CheckoutScreenTags.DEVICE_SESSION_BUTTON),
            ) {
                Text(stringResource(R.string.sample_start_device_session))
            }
            uiState.deviceSessionId?.let { deviceSessionId ->
                Text(
                    text = stringResource(R.string.sample_device_session_result, deviceSessionId),
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            Button(
                onClick = onAddCardClick,
                enabled = uiState.isConfigurationComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag(CheckoutScreenTags.ADD_CARD_BUTTON),
            ) {
                Text(stringResource(R.string.sample_add_card_button))
            }

            uiState.errorMessage?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag(CheckoutScreenTags.ERROR_MESSAGE),
                )
            }
        }
    }
}

@Composable
private fun MerchantConfigurationSection(
    uiState: CheckoutUiState,
    onMerchantIdChange: (String) -> Unit,
    onPublicApiKeyChange: (String) -> Unit,
    onCountryChange: (OpenpayCountry) -> Unit,
    onProductionModeChange: (Boolean) -> Unit,
) {
    Text(
        text = stringResource(R.string.sample_configuration_title),
        style = MaterialTheme.typography.titleMedium,
    )
    OutlinedTextField(
        value = uiState.merchantId,
        onValueChange = onMerchantIdChange,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CheckoutScreenTags.MERCHANT_ID_FIELD),
        label = { Text(stringResource(R.string.sample_merchant_id_label)) },
        singleLine = true,
    )
    OutlinedTextField(
        value = uiState.publicApiKey,
        onValueChange = onPublicApiKeyChange,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CheckoutScreenTags.PUBLIC_KEY_FIELD),
        label = { Text(stringResource(R.string.sample_public_key_label)) },
        singleLine = true,
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OpenpayCountry.entries.forEach { country ->
            FilterChip(
                selected = uiState.country == country,
                onClick = { onCountryChange(country) },
                label = { Text(country.name) },
            )
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Switch(
            checked = uiState.isProductionMode,
            onCheckedChange = onProductionModeChange,
        )
        Text(stringResource(R.string.sample_production_mode))
    }
}

@Composable
private fun LanguageSection(
    selectedLanguage: OpenpayLanguage?,
    onLanguageChange: (OpenpayLanguage?) -> Unit,
) {
    Text(
        text = stringResource(R.string.sample_language_title),
        style = MaterialTheme.typography.titleMedium,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selectedLanguage == null,
            onClick = { onLanguageChange(null) },
            label = { Text(stringResource(R.string.sample_language_automatic)) },
        )
        OpenpayLanguage.entries.forEach { language ->
            FilterChip(
                selected = selectedLanguage == language,
                onClick = { onLanguageChange(language) },
                label = { Text(language.languageTag.uppercase()) },
            )
        }
    }
}
