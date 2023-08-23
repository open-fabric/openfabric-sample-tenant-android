package co.openfabric.tenant.sample.activity

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
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
import androidx.appcompat.app.AppCompatActivity
import co.openfabric.slice.apis.models.v1.apis.ClientTransactionResponse
import co.openfabric.tenant.sample.service.ApproveTransactionRequest
import co.openfabric.tenant.sample.service.ApproveTransactionResponse
import co.openfabric.tenant.sample.service.TenantApi
import co.openfabric.unilateral.sample.R
import co.openfabric.unilateral.sdk.PartnerConfiguration
import co.openfabric.unilateral.sdk.TransactionListener
import co.openfabric.unilateral.sdk.UnilateralSDK
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DecimalFormat


class ApproveActivity : AppCompatActivity(), TransactionListener {
    companion object {
        const val INTENT_AMOUNT = "amount"
        const val INTENT_CURRENCY = "currency"
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

        val amount = intent.getDoubleExtra(INTENT_AMOUNT, 0.0)
        val currency = intent.getStringExtra(INTENT_CURRENCY)
        val partner = intent.getSerializableExtra(INTENT_PARTNER) as PartnerConfiguration

        sdk = UnilateralSDK.getInstance(partner)
        sdk.setTransactionListener(this)

        findViewById<TextView>(R.id.textTotalAmountValue).text =
            CURRENCY_FORMAT.format(amount) + currency
        findViewById<Button>(R.id.btnConfirmPayment).setOnClickListener {
            sdk.createTransaction()

            // Show loading dialog, since it will take up to 10s to process the transaction
            showLoadingDialog()
        }
    }

    private fun showLoadingDialog() {
        val inflater = LayoutInflater.from(this)
        val loadingView = inflater.inflate(R.layout.layout_loading_indicator, null, false)

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
        TODO("Not yet implemented")
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
                sdk.onTransactionApproved(
                    response.body()!!.card_fetch_token
                )
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
}