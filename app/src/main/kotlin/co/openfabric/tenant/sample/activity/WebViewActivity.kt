package co.openfabric.tenant.sample.activity

import android.os.Bundle
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
            webView.settings.javaScriptEnabled = true
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