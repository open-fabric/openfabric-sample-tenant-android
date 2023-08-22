package co.openfabric.tenant.sample.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.net.URL

class WebViewActivity : AppCompatActivity(), NavigationListener, ErrorListener {
    companion object {
        const val INTENT_MERCHANT = "merchant"
        const val INTENT_LABEL = "label"
    }

    private lateinit var sdk: UnilateralSDK
    private lateinit var fab: FloatingActionButton
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        WebView.setWebContentsDebuggingEnabled(true)

        val webView = findViewById<WebView>(R.id.webView)
        val merchant = intent.getSerializableExtra(INTENT_MERCHANT)!! as? Merchant

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.title = intent.getStringExtra(INTENT_LABEL)

        sdk = UnilateralSDK.initialize(
            TenantConfiguration(
                "Home Credit Qwarta",
                URL("https://chatbot.homecredit.ph/assets/visual/icons/smile-logo_outline.svg"),
                "Home Credit",
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

        setupFloatingActionButton()
    }

    override fun onEnterCheckoutPage(amount: Double, currency: String) {
        runOnUiThread {
            fab.setOnClickListener {
                val intent = Intent(this, ApproveActivity::class.java)
                intent.putExtra(INTENT_AMOUNT, amount)
                intent.putExtra(INTENT_CURRENCY, currency)
                intent.putExtra(INTENT_PARTNER, sdk!!.partner)
                startActivity(intent)
            }

            fab.visibility = View.VISIBLE
            fab.show()
        }
    }

    override fun onExitCheckoutPage() {
        runOnUiThread {
            fab.visibility = View.INVISIBLE
            fab.hide()
        }
    }

    private fun setupFloatingActionButton() {
        fab = findViewById(R.id.fab)
        fab.setImageResource(R.drawable.homecredit)
        fab.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                fab.layoutParams.width = fab.width * 2
                fab.layoutParams.height = fab.height * 2
                fab.requestLayout()
                fab.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
        fab.visibility = View.INVISIBLE
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
