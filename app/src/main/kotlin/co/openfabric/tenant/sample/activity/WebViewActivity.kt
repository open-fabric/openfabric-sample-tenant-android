package co.openfabric.tenant.sample.activity

import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import co.openfabric.tenant.sample.model.Merchant
import co.openfabric.unilateral.sample.R
import co.openfabric.unilateral.sdk.PartnerConfiguration
import co.openfabric.unilateral.sdk.TenantConfiguration
import co.openfabric.unilateral.sdk.UnilateralSDK
import java.net.URL
import java.util.UUID

class WebViewActivity : AppCompatActivity() {
    companion object {
        const val INTENT_MERCHANT = "merchant"
    }

    private val sdk = UnilateralSDK(
        TenantConfiguration(
            UUID.fromString("ffb4313d-c03d-4aae-828a-abc51a9e3015"),
            "Home Credit",
            URL("https://homecredit.vn/img/logo-hc-main.png"),
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webView = findViewById<WebView>(R.id.webView)
        val merchant = intent.getSerializableExtra(INTENT_MERCHANT)!! as? Merchant

        sdk.configure(webView, PartnerConfiguration(
            merchant!!.accessToken,
            merchant.url
        ))

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack() // Go back in WebView history
            } else {
                finish() // Finish the activity if WebView history is empty
            }
        }

        webView.loadUrl(merchant.url.toString())
    }
}
