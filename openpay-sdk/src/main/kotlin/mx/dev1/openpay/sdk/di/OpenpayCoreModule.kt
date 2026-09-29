package mx.dev1.openpay.sdk.di

import kotlinx.serialization.json.Json
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.data.remote.OpenpayHttpClientFactory
import mx.dev1.openpay.sdk.data.remote.OpenpayResponseHandler
import org.koin.dsl.module

/**
 * Koin definitions for the SDK core: JSON codec, HTTP client and response handling.
 * The [OpenpayConfig] instance is provided by the SDK entry point at start time.
 */
internal fun openpayCoreModule(config: OpenpayConfig) = module {
    single { config }

    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            encodeDefaults = false
        }
    }

    single { OpenpayHttpClientFactory(config = get(), json = get()) }

    single { get<OpenpayHttpClientFactory>().create() }

    single { OpenpayResponseHandler(json = get()) }
}
