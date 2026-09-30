package mx.dev1.openpay.ui.addcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import mx.dev1.openpay.R
import mx.dev1.openpay.sdk.domain.model.Card as OpenpayCard
import mx.dev1.openpay.sdk.domain.validation.CardBrand
import mx.dev1.openpay.sdk.ui.components.OpenpayCardBrandLogo
import mx.dev1.openpay.sdk.ui.components.OpenpayCardForm
import mx.dev1.openpay.ui.checkout.CheckoutUiState
import mx.dev1.openpay.ui.checkout.CheckoutViewModel
import org.koin.androidx.compose.koinViewModel

object AddCardScreenTags {
    const val BACK_BUTTON = "sample_add_card_back_button"
    const val TOKEN_RESULT = "sample_token_result"
    const val ERROR_MESSAGE = "sample_add_card_error_message"
}

/**
 * Independent screen hosting the SDK card form, separated from the merchant
 * configuration so the sample mirrors a real checkout flow: configure once,
 * then capture cards on a dedicated screen.
 */
@Composable
fun AddCardScreen(
    onNavigateBack: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    AddCardScreenContent(
        uiState = uiState,
        onCardValidated = viewModel::tokenize,
        onNavigateBack = onNavigateBack,
    )
}

/**
 * Stateless layout of the add-card screen, extracted so previews and tests
 * can render it with a fabricated [CheckoutUiState].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCardScreenContent(
    uiState: CheckoutUiState,
    onCardValidated: (OpenpayCard) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.sample_add_card_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag(AddCardScreenTags.BACK_BUTTON),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.sample_back),
                        )
                    }
                },
            )
        },
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
            OpenpayCardForm(onCardValidated = onCardValidated)

            if (uiState.isProcessing) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            uiState.createdToken?.let { token ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(AddCardScreenTags.TOKEN_RESULT),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(stringResource(R.string.sample_token_created, token.tokenId))
                        token.card?.let { tokenizedCard ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                OpenpayCardBrandLogo(
                                    brand = CardBrand.fromBrandName(tokenizedCard.brand),
                                )
                                Text(
                                    text = "${tokenizedCard.brand ?: ""} ${tokenizedCard.maskedCardNumber ?: ""}".trim(),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }

            uiState.errorMessage?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag(AddCardScreenTags.ERROR_MESSAGE),
                )
            }
        }
    }
}
