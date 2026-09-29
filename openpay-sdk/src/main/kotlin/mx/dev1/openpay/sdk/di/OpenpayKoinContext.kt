package mx.dev1.openpay.sdk.di

import mx.dev1.openpay.sdk.core.OpenpayConfig
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

/**
 * Isolated Koin container for the SDK. Keeping the SDK graph in its own
 * [KoinApplication] guarantees it never clashes with a host application
 * that also uses Koin globally.
 */
internal object OpenpayKoinContext {

    private var application: KoinApplication? = null

    val koin: Koin
        get() = checkNotNull(application) {
            "The Openpay SDK has not been started. Create an Openpay instance first."
        }.koin

    fun start(config: OpenpayConfig, extraModules: List<Module> = emptyList()) {
        stop()
        application = koinApplication {
            modules(listOf(openpayCoreModule(config)) + extraModules)
        }
    }

    fun stop() {
        application?.close()
        application = null
    }

    val isStarted: Boolean
        get() = application != null
}
