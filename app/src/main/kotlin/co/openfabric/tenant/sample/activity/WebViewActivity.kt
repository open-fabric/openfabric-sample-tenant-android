package co.openfabric.tenant.sample.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import co.openfabric.tenant.sample.activity.ApproveActivity.Companion.INTENT_AMOUNT
import co.openfabric.tenant.sample.activity.ApproveActivity.Companion.INTENT_CURRENCY
import co.openfabric.tenant.sample.activity.ApproveActivity.Companion.INTENT_PARTNER
import co.openfabric.tenant.sample.model.Merchant
import co.openfabric.unilateral.sample.R
import co.openfabric.unilateral.sdk.Environment
import co.openfabric.unilateral.sdk.ErrorListener
import co.openfabric.unilateral.sdk.NavigationListener
import co.openfabric.unilateral.sdk.PartnerConfiguration
import co.openfabric.unilateral.sdk.TenantConfiguration
import co.openfabric.unilateral.sdk.UnilateralSDK
import co.openfabric.unilateral.sdk.Website
import com.google.android.material.snackbar.Snackbar
import java.net.URL

class WebViewActivity : AppCompatActivity(), NavigationListener, ErrorListener {
    companion object {
        const val INTENT_MERCHANT = "merchant"
        const val INTENT_LABEL = "label"
    }

    private lateinit var sdk: UnilateralSDK
    private lateinit var overlayLayout: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_webview)

        title = "Lazada"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val webView = findViewById<WebView>(R.id.webView)
        val merchant = intent.getSerializableExtra(INTENT_MERCHANT)!! as? Merchant

        sdk = UnilateralSDK.initialize(
            TenantConfiguration(
                "Home Credit Qwarta",
                URL("https://chatbot.homecredit.ph/assets/visual/icons/smile-logo_outline.svg"),
            ),
            PartnerConfiguration(
                merchant!!.accessToken,
                Website.LAZADA
            ),
            webView,
            Environment.DEV
        )
//        sdk.setDebug(true)
        sdk.setNavigationListener(this)

        webView.loadUrl(merchant.url.toString())

        setupOverlayButton()
    }

    override fun onEnterCheckoutPage(amount: Double, currency: String) {
        runOnUiThread {
            overlayLayout = findViewById(R.id.overlay_button)
            overlayLayout.setOnClickListener {
                val intent = Intent(this, ApproveActivity::class.java)
                intent.putExtra(INTENT_AMOUNT, amount)
                intent.putExtra(INTENT_CURRENCY, currency)
                intent.putExtra(INTENT_PARTNER, sdk!!.partner)
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
            Snackbar.make(
                findViewById(android.R.id.content),
                throwable.localizedMessage!!,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}
