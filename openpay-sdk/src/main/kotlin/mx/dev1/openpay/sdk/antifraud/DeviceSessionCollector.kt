package mx.dev1.openpay.sdk.antifraud

import android.app.Activity

/**
 * Collects the device fingerprint Openpay uses for antifraud analysis.
 * The returned session id must be sent to the merchant backend together
 * with the token when creating a charge.
 */
interface DeviceSessionCollector {

    /**
     * Starts the fingerprint collection and returns the device session id.
     * Must be called from the main thread.
     */
    fun collect(activity: Activity): String
}
