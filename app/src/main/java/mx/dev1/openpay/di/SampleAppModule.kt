package mx.dev1.openpay.di

import mx.dev1.openpay.ui.checkout.CheckoutViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val sampleAppModule = module {
    viewModel { CheckoutViewModel() }
}
