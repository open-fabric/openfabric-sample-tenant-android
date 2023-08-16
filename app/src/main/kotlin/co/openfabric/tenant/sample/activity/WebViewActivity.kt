package co.openfabric.tenant.sample.activity

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
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

    private val OVERLAY_PERMISSION_REQUEST_CODE = 123

    private val sdk: UnilateralSDK? = UnilateralSDK(
        this,
        TenantConfiguration(
            id = "ffb4313d-c03d-4aae-828a-abc51a9e3015",
            name = "Home Credit",
            logo = URL("https://homecredit.vn/img/logo-hc-main.png"),
        )
    )

    private lateinit var closeButton: AppCompatButton
    private lateinit var fab: FloatingActionButton
    private val apiService = NetworkProvider.retrofit.create(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webView = findViewById<WebView>(R.id.webView)
        val merchant = intent.getSerializableExtra(INTENT_MERCHANT)!! as? Merchant

        sdk.configure(webView, PartnerConfiguration(
            merchant!!.accessToken,
            merchant.url
        ))

        closeButton.setOnClickListener {
            finish() // Close the activity when the button is clicked
        }

        setupFloatingActionButton()
    }

    private fun setupFloatingActionButton() {
        fab.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                fab.layoutParams.width = fab.width * 2
                fab.layoutParams.height = fab.height * 2
                fab.requestLayout()
                fab.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

//        fab.setOnTouchListener(FloatingActionButtonTouchListener())
        fab.setOnClickListener {
            if (hasOverlayPermission()) {
                showOverlayDialog()
            } else {
                requestOverlayPermission()
            }
        }

        webView.loadUrl(merchant.url.toString())
    }

    private inner class FloatingActionButtonTouchListener : View.OnTouchListener {
        private var dx = 0
        private var dy = 0

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dx = view.x.toInt() - event.rawX.toInt()
                    dy = view.y.toInt() - event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX + dx
                    val newY = event.rawY + dy

                    val maxX = (view.parent as View).width - view.width
                    val maxY = (view.parent as View).height - view.height

                    view.animate()
                        .x(newX.coerceIn(0f, maxX.toFloat()))
                        .y(newY.coerceIn(0f, maxY.toFloat()))
                        .setDuration(0)
                        .start()
                }
            }
            return true
        }
    }

    private fun hasOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(this)
    }

    private fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    }

    private fun showOverlayDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_dialog)

        dialog.setCancelable(true)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        dialog.show()
    }
}
