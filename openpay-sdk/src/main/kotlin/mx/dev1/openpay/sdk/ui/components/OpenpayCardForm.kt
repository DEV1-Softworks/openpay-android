package mx.dev1.openpay.sdk.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import mx.dev1.openpay.sdk.R
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.domain.validation.CardField
import mx.dev1.openpay.sdk.i18n.OpenpayLocalized
import mx.dev1.openpay.sdk.ui.state.OpenpayCardFormState
import mx.dev1.openpay.sdk.ui.state.rememberOpenpayCardFormState

/**
 * Complete card capture form: holder name, card number with live brand
 * detection, expiration and security code, plus a submit button.
 *
 * Accessibility: every field exposes its label and error text through
 * semantics, the security code stays masked, touch targets keep at least
 * 48dp and the detected brand is announced via content description.
 *
 * @param onCardValidated invoked with a valid [Card] when the user submits
 * and every field passes validation.
 * @param submitButtonText optional replacement for the submit button label;
 * when null the localized SDK text is used.
 */
@Composable
fun OpenpayCardForm(
    onCardValidated: (Card) -> Unit,
    modifier: Modifier = Modifier,
    state: OpenpayCardFormState = rememberOpenpayCardFormState(),
    submitButtonText: String? = null,
) = OpenpayLocalized {
    val formDescription = stringResource(R.string.openpay_form_description)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = formDescription },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OpenpayHolderNameField(
            value = state.holderName,
            onValueChange = state::updateHolderName,
            isError = state.isFieldInvalid(CardField.HOLDER_NAME),
        )

        OpenpayCardNumberField(
            value = state.cardNumber,
            onValueChange = state::updateCardNumber,
            brand = state.detectedBrand,
            isError = state.isFieldInvalid(CardField.CARD_NUMBER),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OpenpayExpirationField(
                value = state.expiration,
                onValueChange = state::updateExpiration,
                isError = state.isFieldInvalid(CardField.EXPIRATION),
                modifier = Modifier.weight(1f),
            )
            OpenpaySecurityCodeField(
                value = state.securityCode,
                onValueChange = state::updateSecurityCode,
                isError = state.isFieldInvalid(CardField.SECURITY_CODE),
                modifier = Modifier.weight(1f),
            )
        }

        Button(
            onClick = {
                state.validate()?.let(onCardValidated)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .testTag(OpenpayFormTags.SUBMIT_BUTTON),
        ) {
            Text(submitButtonText ?: stringResource(R.string.openpay_pay_button))
        }
    }
}
