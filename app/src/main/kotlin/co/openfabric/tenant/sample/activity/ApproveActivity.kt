package co.openfabric.tenant.sample.activity

import android.animation.AnimatorInflater
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.openfabric.slice.apis.models.v1.apis.ClientTransactionResponse
import co.openfabric.slice.apis.models.v1.apis.FullCardDetails
import co.openfabric.slice.apis.models.v1.apis.Provider
import co.openfabric.tenant.sample.service.ApproveTransactionRequest
import co.openfabric.tenant.sample.service.ApproveTransactionResponse
import co.openfabric.tenant.sample.service.TenantApi
import co.openfabric.unilateral.sample.R
import co.openfabric.unilateral.sdk.Environment
import co.openfabric.unilateral.sdk.ErrorListener
import co.openfabric.unilateral.sdk.PartnerConfiguration
import co.openfabric.unilateral.sdk.TenantConfiguration
import co.openfabric.unilateral.sdk.TransactionListener
import co.openfabric.unilateral.sdk.UnilateralSDK
import co.openfabric.unilateral.sdk.models.apis.ClientTransactionRequest
import com.google.gson.GsonBuilder
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.text.DecimalFormat


class ApproveActivity : AppCompatActivity(), TransactionListener, ErrorListener {
    companion object {
        const val INTENT_TRANSACTION = "transaction"
        const val INTENT_PARTNER = "partner"
        val CURRENCY_FORMAT = DecimalFormat("#,###.00")
    }

    private val api = Retrofit.Builder()
        .baseUrl("https://of-test-1.samples.dev.openfabric.co")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
        .create(TenantApi::class.java)
    private lateinit var sdk: UnilateralSDK
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Review Payment"
        actionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.activity_approve)

        val transaction: ClientTransactionRequest = Json.decodeFromString(intent.getStringExtra(INTENT_TRANSACTION)!!)
        val partner = intent.getSerializableExtra(INTENT_PARTNER) as PartnerConfiguration

        sdk = UnilateralSDK.initialize(
            TenantConfiguration(
                "Flip",
                URL("https://storage.googleapis.com/dot-flip-dev/media-library/logo_lg_430b708631/logo_lg_430b708631.svg"),
                "Flip Indonesia"
            ),
            partner,
            Environment.DEV
        )
        sdk.setDebug(false)
//         sdk.setTestCard(FullCardDetails(
//             provider = Provider.mastercard,
//             card_reference_id = "",
//             card_number = "",
//             cvv = "",
//             expiry_year = "24",
//             expiry_month = "01",
//             message = "S",
//             redacted_number = ""
//         ))
        sdk.setTransactionListener(this)
        sdk.setErrorListener(this)

        if (sdk.partner.website == co.openfabric.unilateral.sdk.Website.SHOPEE) {
            findViewById<TextView>(R.id.partner_name).text = "Shopee"
        } else {
            findViewById<TextView>(R.id.partner_name).text = "Lazada"
        }

        findViewById<TextView>(R.id.textSubtotalValue).text =
            CURRENCY_FORMAT.format(transaction.transaction_details.original_amount) + " " + transaction.currency

        findViewById<TextView>(R.id.textDiscountValue).text =
            CURRENCY_FORMAT.format(transaction.transaction_details.shipping_amount) + " " + transaction.currency

        findViewById<TextView>(R.id.textTotalAmountValue).text =
            CURRENCY_FORMAT.format(transaction.amount) + " " + transaction.currency

        findViewById<Button>(R.id.btnConfirmPayment).setOnClickListener {
            sdk.createTransaction()

            // Show loading dialog, since it will take up to 10s to process the transaction
            showLoadingDialog()
        }
    }

    private fun showLoadingDialog() {
        val inflater = LayoutInflater.from(this)
        val loadingView = inflater.inflate(R.layout.layout_loading_indicator, null, false)

        if (sdk.partner.website == co.openfabric.unilateral.sdk.Website.SHOPEE) {
            loadingView.findViewById<ImageView>(R.id.imageLZD).setImageResource(R.drawable.shopee_horizontal)
        } else {
            loadingView.findViewById<ImageView>(R.id.imageLZD).setImageResource(R.drawable.lazada_horizontal)
        }

        loadingDialog = Dialog(this)
        loadingDialog.setContentView(loadingView)
        loadingDialog.setCancelable(false)
        loadingDialog.show()

        val dotIds = listOf(R.id.dot1, R.id.dot2, R.id.dot3, R.id.dot4, R.id.dot5)
        val dancingAnimators = List(5) { AnimatorInflater.loadAnimator(this, R.animator.dance_animation) }

        val handler = Handler()

        dotIds.forEachIndexed { index, dotId ->
            val dot = loadingDialog.findViewById<ImageView>(dotId)
            val animator = dancingAnimators[index]
            animator.setTarget(dot)
            handler.postDelayed({ animator.start() }, (index * 100).toLong())
        }
    }


    private fun dismissLoadingDialog() {
        loadingDialog.dismiss()
    }

    override fun onCardDetailsFilled() {
        runOnUiThread {
            dismissLoadingDialog()
            finish()
        }
    }

    override fun onCreateTransactionFailed(throwable: Throwable) {
        runOnUiThread {
            dismissLoadingDialog()
        }
    }

    override fun onCreateTransactionSuccess(response: ClientTransactionResponse) {
        api.approve(
            ApproveTransactionRequest(
                response.id,
                response.tenant_reference_id
            )
        ).enqueue(object : Callback<ApproveTransactionResponse> {
            override fun onResponse(
                call: Call<ApproveTransactionResponse>,
                response: Response<ApproveTransactionResponse>
            ) {
                sdk.onTransactionApproved()
            }

            override fun onFailure(call: Call<ApproveTransactionResponse>, t: Throwable) {
                sdk.cancelTransaction()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.single_action_cancel, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cancel -> {
                finish()
                true
            }

            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onError(throwable: Throwable) {
        runOnUiThread {
            Toast.makeText(this, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
//            finish()
        }
    }
}
