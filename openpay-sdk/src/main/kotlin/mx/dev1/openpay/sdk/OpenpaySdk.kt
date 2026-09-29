package mx.dev1.openpay.sdk

/**
 * Global information about the Openpay SDK artifact.
 */
object OpenpaySdk {

    /** Version reported to the Openpay API through the user agent. */
    const val VERSION: String = "1.0.0"

    /** User agent sent on every HTTP request made by the SDK. */
    val userAgent: String = buildUserAgent(VERSION)

    internal fun buildUserAgent(version: String): String {
        require(version.isNotBlank()) { "The SDK version must not be blank" }
        return "openpay-android/$version"
    }
}
