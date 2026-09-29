package mx.dev1.openpay.sdk

import mx.dev1.openpay.sdk.core.OpenpayException

/**
 * Callback for consumers that do not use coroutines. Both methods are
 * invoked on the Android main thread.
 */
interface OpenpayCallback<ResultType> {

    fun onSuccess(result: ResultType)

    /**
     * Called with the failure cause:
     * [OpenpayException.ValidationError] for local validation failures,
     * [OpenpayException.ServiceError] for API rejections and
     * [OpenpayException.ConnectionError] for connectivity problems.
     */
    fun onError(exception: OpenpayException)
}
