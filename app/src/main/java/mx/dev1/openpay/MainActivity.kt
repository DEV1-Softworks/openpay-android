package mx.dev1.openpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import mx.dev1.openpay.ui.checkout.CheckoutScreen
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenpayTheme {
                CheckoutScreen()
            }
        }
    }
}
