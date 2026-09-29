package mx.dev1.openpay.sdk.di

import mx.dev1.openpay.sdk.antifraud.DeviceSessionCollector
import mx.dev1.openpay.sdk.antifraud.DeviceSessionIdGenerator
import mx.dev1.openpay.sdk.antifraud.WebViewDeviceSessionCollector
import org.koin.dsl.module

/**
 * Koin definitions for the antifraud device fingerprint collection.
 */
internal fun openpayAntifraudModule() = module {
    factory { DeviceSessionIdGenerator() }

    single<DeviceSessionCollector> {
        WebViewDeviceSessionCollector(
            config = get(),
            sessionIdGenerator = get(),
        )
    }
}
