package co.openfabric.tenant.sample.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import co.openfabric.tenant.sample.service.Partner
import co.openfabric.unilateral.sample.R
import co.openfabric.unilateral.sdk.Environment
import co.openfabric.unilateral.sdk.ErrorListener
import co.openfabric.unilateral.sdk.NavigationListener
import co.openfabric.unilateral.sdk.PartnerConfiguration
import co.openfabric.unilateral.sdk.TenantConfiguration
import co.openfabric.unilateral.sdk.UnilateralSDK
import co.openfabric.unilateral.sdk.Website
import co.openfabric.unilateral.sdk.models.apis.ClientTransactionRequest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URL

class WebViewActivity : AppCompatActivity(), NavigationListener, ErrorListener {
    companion object {
        const val INTENT_PARTNER = "partner"
    }

    private lateinit var sdk: UnilateralSDK
    private lateinit var webView: WebView
    private lateinit var overlayLayout: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_webview)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        val partner = intent.getSerializableExtra(INTENT_PARTNER)!! as? Partner
        title = partner!!.name

        webView = findViewById(R.id.webView)

        sdk = UnilateralSDK.initialize(
            TenantConfiguration(
                "Home Credit Qwarta",
                URL("https://chatbot.homecredit.ph/assets/visual/icons/smile-logo_outline.svg"),
                "Home Credit"
            ),
            PartnerConfiguration(
                partner.accessToken,
                Website.valueOf(partner.name.uppercase())
            ),
            Environment.DEV
        )
        sdk.configure(webView)
        sdk.setDebug(false)
        sdk.setNavigationListener(this)
        sdk.setErrorListener(this)

        when (partner.name) {
            "Lazada" -> webView.loadUrl("https://www.lazada.com.ph/")
            "Shopee" -> webView.loadUrl("https://shopee.ph")
        }

        setupOverlayButton()
    }

    override fun onResume() {
        super.onResume()
        sdk.setNavigationListener(this)
        sdk.setErrorListener(this)
    }

    override fun onEnterCheckoutPage(transaction: ClientTransactionRequest) {
        runOnUiThread {
            overlayLayout = findViewById(R.id.overlay_button)
            overlayLayout.setOnClickListener {
                val intent = Intent(this, ApproveActivity::class.java)
                intent.putExtra(ApproveActivity.INTENT_PARTNER, sdk.partner)
                intent.putExtra(ApproveActivity.INTENT_TRANSACTION, Json.encodeToString(transaction))
                startActivity(intent)
            }

            overlayLayout.visibility = View.VISIBLE
        }
    }

    override fun onExitCheckoutPage() {
        runOnUiThread {
            overlayLayout.visibility = View.INVISIBLE
        }
    }

    private fun setupOverlayButton() {
        overlayLayout = findViewById(R.id.overlay_button)
        overlayLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                overlayLayout.layoutParams.width = overlayLayout.width * 2
                overlayLayout.layoutParams.height = overlayLayout.height * 2
                overlayLayout.requestLayout()
                overlayLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        overlayLayout.visibility = View.INVISIBLE
    }
    override fun onError(throwable: Throwable) {
        runOnUiThread {
            Toast.makeText(this, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
//            if (webView.canGoBack()) {
//                webView.goBack()
//            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
