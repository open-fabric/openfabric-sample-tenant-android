package co.openfabric.tenant.sample.activity

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import co.openfabric.unilateral.sample.R

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webView = findViewById<WebView>(R.id.webView)
        val url = intent.getStringExtra("url")

        if (url != null) {
            webView.apply {
                this.settings.loadsImagesAutomatically = true
                this.settings.javaScriptEnabled = true
                this.settings.setSupportZoom(true)
                this.settings.builtInZoomControls = true
                this.settings.displayZoomControls = false
                this.settings.useWideViewPort = true
                this.settings.loadWithOverviewMode = true
                this.settings.domStorageEnabled = true
                this.settings.databaseEnabled = true
                this.settings.javaScriptCanOpenWindowsAutomatically = true
                this.settings.setSupportMultipleWindows(true)
                this.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            }
            webView.webViewClient = WebViewClient()
            webView.loadUrl(url)
        }

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack() // Go back in WebView history
            } else {
                finish() // Finish the activity if WebView history is empty
            }
        }
    }
}
