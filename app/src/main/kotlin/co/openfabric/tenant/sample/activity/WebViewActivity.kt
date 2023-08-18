package co.openfabric.tenant.sample.activity

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import co.openfabric.slice.apis.models.v1.apis.ClientTransactionRequest
import co.openfabric.slice.apis.models.v1.apis.ClientTransactionResponse
import co.openfabric.tenant.sample.model.ApproveTransactionRequest
import co.openfabric.tenant.sample.model.ApproveTransactionResponse
import co.openfabric.tenant.sample.model.Merchant
import co.openfabric.tenant.sample.provider.NetworkProvider
import co.openfabric.tenant.sample.service.TenantApi
import co.openfabric.unilateral.sample.R
import co.openfabric.unilateral.sdk.Environment
import co.openfabric.unilateral.sdk.PartnerConfiguration
import co.openfabric.unilateral.sdk.TenantConfiguration
import co.openfabric.unilateral.sdk.UnilateralSDK
import co.openfabric.unilateral.sdk.UnilateralSDKListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.util.concurrent.Future

class WebViewActivity : AppCompatActivity() {
    companion object {
        const val INTENT_MERCHANT = "merchant"
    }

    private val sdk = UnilateralSDK(
        TenantConfiguration(
            "Home Credit",
            URL("https://homecredit.vn/img/logo-hc-main.png"),
        ),
        Environment.DEV
    )

    private val OVERLAY_PERMISSION_REQUEST_CODE = 123

    private lateinit var fab: FloatingActionButton
    private val tenantApi = NetworkProvider.retrofit.create(TenantApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        val view = this
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        WebView.setWebContentsDebuggingEnabled(true)

        val webView = findViewById<WebView>(R.id.webView)
        val merchant = intent.getSerializableExtra(INTENT_MERCHANT)!! as? Merchant

        sdk.configure(webView, PartnerConfiguration(
            merchant!!.accessToken,
            merchant.url
        ))
        sdk.setListener(object : UnilateralSDKListener {
            override fun onCheckoutPage() {
                showOverlayButton()
            }

            override fun offCheckoutPage() {
                hideOverlayButton()
            }

            override fun onTransactionApprovalRequest(request: ClientTransactionRequest, response: Future<ClientTransactionResponse>) {
                var currency = request.currency
                var currencySymbol = "$"
                intent.putExtra("currency",  currency)
                intent.putExtra("currencySymbol",  currencySymbol)
                intent.putExtra("amount",  request.amount)
                Log.d("", "${request}")
                showOverlayDialog(intent)
            }

//            override fun onTransactionRequest(request: ClientTransactionRequest) {
//                TODO("Not yet implemented")
//            }
        })

        webView.loadUrl(merchant.url.toString())

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack() // Go back in WebView history
            } else {
                finish() // Finish the activity if WebView history is empty
            }
        }

        setupFloatingActionButton()
    }

    fun showOverlayButton() {
        fab.visibility = View.VISIBLE
        fab.show()
    }

    fun hideOverlayButton() {
        fab.visibility = View.INVISIBLE
        fab.hide()
    }

    private fun setupFloatingActionButton() {
        fab = findViewById(R.id.fab)
        fab.setImageResource(R.drawable.homecredit)
        fab.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                fab.layoutParams.width = fab.width * 2
                fab.layoutParams.height = fab.height * 2
                fab.requestLayout()
                fab.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

//        fab.setOnTouchListener(FloatingActionButtonTouchListener())
        fab.setOnClickListener { view ->
            sdk.startTransaction()
        }
        fab.visibility = View.INVISIBLE
    }

    private fun showOverlayDialog(intent: Intent) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_dialog)

        val amount = intent.getDoubleExtra("amount", 0.0)
        val currency = intent.getStringExtra("currency", )

        val textAmountValue = dialog.findViewById<TextView>(R.id.textAmountValue)
        textAmountValue.text = "${amount}${currency}"

        val btnConfirmPayment = dialog.findViewById<Button>(R.id.btnConfirmPayment)
        val approveTransactionRequest: ApproveTransactionRequest = ApproveTransactionRequest(
            amount,
            currency!!,
            "")

        btnConfirmPayment.setOnClickListener {
            tenantApi.createTransaction(approveTransactionRequest).enqueue(object: Callback<ApproveTransactionResponse> {
                override fun onResponse(
                    call: Call<ApproveTransactionResponse>,
                    response: Response<ApproveTransactionResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val approveTransactionResponse = ApproveTransactionResponse(
                                it.account_reference_id,
                                it.transaction_id,
                                it.amount,
                                it.currency,
                                it.status
                            )
                        }
                        dialog.hide()
                    } else {
                        displayError(RuntimeException("Error response: ${response.code()}"))
                    }
                }

                override fun onFailure(call: Call<ApproveTransactionResponse>, t: Throwable) {
                    displayError(t)
                }
            })
        }

        dialog.setCancelable(true)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        dialog.show()
    }

    private fun displayError(throwable: Throwable) {
        Snackbar.make(
            findViewById(android.R.id.content),
            throwable.localizedMessage!!,
            Snackbar.LENGTH_LONG
        ).show()
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
}
