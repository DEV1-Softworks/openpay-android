package mx.dev1.openpay.sdk.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import mx.dev1.openpay.sdk.R
import mx.dev1.openpay.sdk.i18n.OpenpayLocalized
import mx.dev1.openpay.sdk.ui.input.CardNumberVisualTransformation
import mx.dev1.openpay.sdk.ui.input.ExpirationVisualTransformation

object OpenpayFormTags {
    const val CARD_PREVIEW = "openpay_card_preview"
    const val HOLDER_NAME_FIELD = "openpay_holder_name_field"
    const val CARD_NUMBER_FIELD = "openpay_card_number_field"
    const val EXPIRATION_FIELD = "openpay_expiration_field"
    const val SECURITY_CODE_FIELD = "openpay_security_code_field"
    const val CARD_BRAND_LOGO = "openpay_card_brand_logo"
    const val CARD_PREVIEW_BRAND_LOGO = "openpay_card_preview_brand_logo"
    const val SUBMIT_BUTTON = "openpay_submit_button"
}

@Composable
fun OpenpayHolderNameField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    OpenpayLocalized {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .testTag(OpenpayFormTags.HOLDER_NAME_FIELD),
            label = { Text(stringResource(R.string.openpay_holder_name_label)) },
            isError = isError,
            supportingText = errorSupportingText(isError, R.string.openpay_error_holder_name),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
    }
}

@Composable
fun OpenpayCardNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    OpenpayLocalized {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .testTag(OpenpayFormTags.CARD_NUMBER_FIELD),
            label = { Text(stringResource(R.string.openpay_card_number_label)) },
            isError = isError,
            supportingText = errorSupportingText(isError, R.string.openpay_error_card_number),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = CardNumberVisualTransformation(),
        )
    }
}

@Composable
fun OpenpayExpirationField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    OpenpayLocalized {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .testTag(OpenpayFormTags.EXPIRATION_FIELD),
            label = { Text(stringResource(R.string.openpay_expiration_label)) },
            placeholder = { Text(stringResource(R.string.openpay_expiration_placeholder)) },
            isError = isError,
            supportingText = errorSupportingText(isError, R.string.openpay_error_expiration),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            visualTransformation = ExpirationVisualTransformation(),
        )
    }
}

@Composable
fun OpenpaySecurityCodeField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    OpenpayLocalized {
        val hiddenCodeDescription = stringResource(R.string.openpay_security_code_hidden_description)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .testTag(OpenpayFormTags.SECURITY_CODE_FIELD)
                .semantics { contentDescription = hiddenCodeDescription },
            label = { Text(stringResource(R.string.openpay_security_code_label)) },
            isError = isError,
            supportingText = errorSupportingText(isError, R.string.openpay_error_security_code),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = PasswordVisualTransformation(),
        )
    }
}

@Composable
private fun errorSupportingText(isError: Boolean, errorTextId: Int): (@Composable () -> Unit)? =
    if (isError) {
        { Text(stringResource(errorTextId)) }
    } else {
        null
    }
