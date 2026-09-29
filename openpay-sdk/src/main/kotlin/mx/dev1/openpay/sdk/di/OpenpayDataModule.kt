package mx.dev1.openpay.sdk.di

import java.time.Clock
import mx.dev1.openpay.sdk.data.repository.RemoteTokenRepository
import mx.dev1.openpay.sdk.domain.repository.TokenRepository
import mx.dev1.openpay.sdk.domain.usecase.CreateTokenUseCase
import org.koin.dsl.module

/**
 * Koin definitions for repositories and use cases.
 */
internal fun openpayDataModule() = module {
    single<Clock> { Clock.systemDefaultZone() }

    single<TokenRepository> {
        RemoteTokenRepository(
            httpClient = get(),
            responseHandler = get(),
            config = get(),
        )
    }

    factory {
        CreateTokenUseCase(
            tokenRepository = get(),
            clock = get(),
        )
    }
}
