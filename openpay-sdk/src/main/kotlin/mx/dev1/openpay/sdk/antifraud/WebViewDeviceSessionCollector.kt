package mx.dev1.openpay.sdk.antifraud

import android.annotation.SuppressLint
import android.app.Activity
import android.webkit.WebView
import android.webkit.WebViewClient
import mx.dev1.openpay.sdk.core.OpenpayConfig

/**
 * Replicates the legacy antifraud collection: a short lived, invisible
 * WebView loads the Openpay fingerprint page passing the merchant id and a
 * fresh session id. The WebView is destroyed as soon as the page finishes.
 *
 * @param webViewProvider indirection over `WebView(activity)` so tests can
 * observe the WebView the collector drives.
 */
internal class WebViewDeviceSessionCollector(
    private val config: OpenpayConfig,
    private val sessionIdGenerator: DeviceSessionIdGenerator,
    private val webViewProvider: (Activity) -> WebView = ::WebView,
) : DeviceSessionCollector {

    @SuppressLint("SetJavaScriptEnabled")
    override fun collect(activity: Activity): String {
        val deviceSessionId = sessionIdGenerator.generate()
        val fingerprintUrl = buildFingerprintUrl(deviceSessionId)

        val fingerprintWebView = webViewProvider(activity)
        fingerprintWebView.settings.javaScriptEnabled = true
        fingerprintWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(finishedView: WebView, url: String) {
                finishedView.destroy()
            }
        }
        fingerprintWebView.loadUrl(fingerprintUrl)

        return deviceSessionId
    }

    private fun buildFingerprintUrl(deviceSessionId: String): String =
        "${config.baseUrl}/oa/logo.htm?m=${config.merchantId}&s=$deviceSessionId"
}
