package mx.dev1.openpay.sdk.antifraud

import android.app.Activity
import android.webkit.WebView
import mx.dev1.openpay.sdk.core.OpenpayConfig
import mx.dev1.openpay.sdk.core.OpenpayCountry
import mx.dev1.openpay.sdk.core.OpenpayEnvironment
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@RunWith(RobolectricTestRunner::class)
class WebViewDeviceSessionCollectorTest {

    private val config = OpenpayConfig(
        merchantId = "merchant-id",
        publicApiKey = "pk_test",
        country = OpenpayCountry.MEXICO,
        environment = OpenpayEnvironment.SANDBOX,
    )

    private var createdWebView: WebView? = null

    private val collector = WebViewDeviceSessionCollector(
        config = config,
        sessionIdGenerator = DeviceSessionIdGenerator(),
        webViewProvider = { activity ->
            WebView(activity).also { createdWebView = it }
        },
    )

    private fun launchActivity(): Activity =
        Robolectric.buildActivity(Activity::class.java).setup().get()

    @Test
    fun `collect returns a 32 character session id`() {
        val deviceSessionId = collector.collect(launchActivity())

        assertEquals(32, deviceSessionId.length)
        assertTrue(deviceSessionId.all { character -> character in "0123456789abcdef" })
    }

    @Test
    fun `collect loads the fingerprint url with merchant and session id`() {
        val deviceSessionId = collector.collect(launchActivity())

        val fingerprintWebView = checkNotNull(createdWebView)
        assertEquals(
            "https://sandbox-api.openpay.mx/oa/logo.htm?m=merchant-id&s=$deviceSessionId",
            shadowOf(fingerprintWebView).lastLoadedUrl,
        )
    }

    @Test
    fun `collect enables javascript for the fingerprint page`() {
        collector.collect(launchActivity())

        val fingerprintWebView = checkNotNull(createdWebView)
        assertTrue(fingerprintWebView.settings.javaScriptEnabled)
    }

    @Test
    fun `each collection uses a fresh session id`() {
        val activity = launchActivity()

        val firstSessionId = collector.collect(activity)
        val secondSessionId = collector.collect(activity)

        assertNotNull(firstSessionId)
        assertTrue(firstSessionId != secondSessionId)
    }
}
