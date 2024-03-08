package co.openfabric.tenant.sample.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

    private var onPurchaseCompleteActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        webView.destroy()
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setContentView(R.layout.activity_webview)

        actionBar?.setDisplayHomeAsUpEnabled(true)
        val partner = intent.getSerializableExtra(INTENT_PARTNER)!! as? Partner
        title = partner!!.name

        webView = findViewById(R.id.webView)
        WebView.setWebContentsDebuggingEnabled(true)
        sdk = UnilateralSDK.initialize(
            TenantConfiguration(
                "Flash Pay",
                URL("https://svgshare.com/i/13f5.svg"),
                "PH",
                "Flash Pay"
            ),
            PartnerConfiguration(
                partner.accessToken,
                "lazada"
            ),
            Environment.DEV
        )
        sdk.configure(this, webView)
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

    override fun onCheckoutPageFinished(transaction: ClientTransactionRequest) {
        runOnUiThread {
            overlayLayout = findViewById(R.id.overlay_button)

            overlayLayout.setOnClickListener {
                val intent = Intent(this, ApproveActivity::class.java)
                intent.putExtra(ApproveActivity.INTENT_PARTNER, sdk.partner)
                intent.putExtra(ApproveActivity.INTENT_TRANSACTION, Json.encodeToString(transaction))
                startActivity(intent)
            }
            overlayLayout.alpha = 0f
            overlayLayout.visibility = View.VISIBLE
            overlayLayout.animate().alpha(1.0f).setDuration(700)
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

    override fun onEnterCheckoutPage() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_loader)
        progressBar.visibility = View.VISIBLE
    }
    override fun onPageFinished() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_loader)
        progressBar.visibility = View.INVISIBLE
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.removeItem(android.R.id.home)
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.custom_back_button, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
                R.id.custom_back_button -> {
                    webView.destroy()
                    finish()
                    true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPurchaseCompletePage() {
        val purchaseCompleteIntent = Intent(this, PurchaseCompleteActivity::class.java)
        onPurchaseCompleteActivityLauncher.launch(purchaseCompleteIntent)
    }
}
