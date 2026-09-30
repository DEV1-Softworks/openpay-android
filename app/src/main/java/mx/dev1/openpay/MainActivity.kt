package mx.dev1.openpay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.dev1.openpay.sdk.ui.theme.OpenpayTheme
import mx.dev1.openpay.ui.addcard.AddCardScreen
import mx.dev1.openpay.ui.checkout.CheckoutScreen
import mx.dev1.openpay.ui.checkout.CheckoutViewModel
import org.koin.androidx.compose.koinViewModel

private object SampleDestinations {
    const val CHECKOUT = "checkout"
    const val ADD_CARD = "add-card"
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpenpayTheme {
                // Resolved at the activity level so both screens share the
                // same configuration and tokenization state.
                val sharedViewModel: CheckoutViewModel = koinViewModel()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = SampleDestinations.CHECKOUT,
                ) {
                    composable(SampleDestinations.CHECKOUT) {
                        CheckoutScreen(
                            onAddCardClick = {
                                navController.navigate(SampleDestinations.ADD_CARD)
                            },
                            viewModel = sharedViewModel,
                        )
                    }
                    composable(SampleDestinations.ADD_CARD) {
                        AddCardScreen(
                            onNavigateBack = { navController.popBackStack() },
                            viewModel = sharedViewModel,
                        )
                    }
                }
            }
        }
    }
}
