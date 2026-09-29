package mx.dev1.openpay.sdk.antifraud

import java.util.UUID

/**
 * Generates the random session identifiers that link a device fingerprint
 * with a tokenization request.
 */
internal class DeviceSessionIdGenerator {

    /** Returns a 32 character hexadecimal identifier. */
    fun generate(): String = UUID.randomUUID().toString().replace("-", "")
}
