package mx.dev1.openpay.ui.checkout

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import mx.dev1.openpay.sdk.ui.components.OpenpayCardForm
import org.koin.androidx.compose.koinViewModel

object CheckoutScreenTags {
    const val MERCHANT_ID_FIELD = "sample_merchant_id_field"
    const val PUBLIC_KEY_FIELD = "sample_public_key_field"
    const val DEVICE_SESSION_BUTTON = "sample_device_session_button"
    const val TOKEN_RESULT = "sample_token_result"
    const val ERROR_MESSAGE = "sample_error_message"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(viewModel: CheckoutViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val hostActivity = LocalContext.current as? Activity

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MerchantConfigurationSection(uiState, viewModel)
            LanguageSection(uiState, viewModel)

            OutlinedButton(
                onClick = { hostActivity?.let(viewModel::startDeviceSession) },
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

            OpenpayCardForm(onCardValidated = viewModel::tokenize)

            if (uiState.isProcessing) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            uiState.createdToken?.let { token ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .testTag(CheckoutScreenTags.TOKEN_RESULT)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(stringResource(R.string.sample_token_created, token.tokenId))
                        token.card?.let { tokenizedCard ->
                            Text(
                                text = "${tokenizedCard.brand ?: ""} ${tokenizedCard.maskedCardNumber ?: ""}".trim(),
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
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
    viewModel: CheckoutViewModel,
) {
    Text(
        text = stringResource(R.string.sample_configuration_title),
        style = MaterialTheme.typography.titleMedium,
    )
    OutlinedTextField(
        value = uiState.merchantId,
        onValueChange = viewModel::updateMerchantId,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(CheckoutScreenTags.MERCHANT_ID_FIELD),
        label = { Text(stringResource(R.string.sample_merchant_id_label)) },
        singleLine = true,
    )
    OutlinedTextField(
        value = uiState.publicApiKey,
        onValueChange = viewModel::updatePublicApiKey,
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
                onClick = { viewModel.updateCountry(country) },
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
            onCheckedChange = viewModel::updateProductionMode,
        )
        Text(stringResource(R.string.sample_production_mode))
    }
}

@Composable
private fun LanguageSection(
    uiState: CheckoutUiState,
    viewModel: CheckoutViewModel,
) {
    Text(
        text = stringResource(R.string.sample_language_title),
        style = MaterialTheme.typography.titleMedium,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = uiState.selectedLanguage == null,
            onClick = { viewModel.updateLanguage(null) },
            label = { Text(stringResource(R.string.sample_language_automatic)) },
        )
        OpenpayLanguage.entries.forEach { language ->
            FilterChip(
                selected = uiState.selectedLanguage == language,
                onClick = { viewModel.updateLanguage(language) },
                label = { Text(language.languageTag.uppercase()) },
            )
        }
    }
}
