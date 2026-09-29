package mx.dev1.openpay.sdk.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import mx.dev1.openpay.sdk.domain.model.Card
import mx.dev1.openpay.sdk.ui.components.OpenpayCardForm
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme

/**
 * XML-friendly wrapper around [OpenpayCardForm] for host apps that still
 * use view-based layouts:
 *
 * ```xml
 * <mx.dev1.openpay.sdk.ui.view.OpenpayCardFormView
 *     android:id="@+id/openpay_card_form"
 *     android:layout_width="match_parent"
 *     android:layout_height="wrap_content" />
 * ```
 *
 * Set [onCardValidated] to receive the validated card.
 */
class OpenpayCardFormView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AbstractComposeView(context, attrs, defStyleAttr) {

    /** Called with the validated card when the user submits the form. */
    var onCardValidated: (Card) -> Unit = {}

    @Composable
    override fun Content() {
        OpenpayTheme {
            OpenpayCardForm(
                onCardValidated = { validatedCard -> onCardValidated(validatedCard) },
            )
        }
    }
}
