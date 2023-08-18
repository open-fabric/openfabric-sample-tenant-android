package co.openfabric.tenant.sample.activity

import android.os.Bundle
import android.widget.Button
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

    val api = Retrofit.Builder()
        .baseUrl("https://of-test-1.samples.dev.openfabric.co")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
        .create(TenantApi::class.java)
    var sdk: UnilateralSDK? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_dialog)

        val amount = intent.getDoubleExtra(INTENT_AMOUNT, 0.0)
        val currency = intent.getStringExtra(INTENT_CURRENCY)
        val partner = intent.getSerializableExtra(INTENT_PARTNER) as PartnerConfiguration

        sdk = UnilateralSDK.getInstance(partner)
        sdk!!.setTransactionListener(this)

        findViewById<TextView>(R.id.textAmountValue).text =
            CURRENCY_FORMAT.format(amount) + currency
        findViewById<Button>(R.id.btnConfirmPayment).setOnClickListener {
            sdk!!.createTransaction()
        }
    }

    override fun onCardDetailsFilled() {
        finish()
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
                sdk!!.onTransactionApproved(
                    response.body()!!.card_fetch_token
                )
            }

            override fun onFailure(call: Call<ApproveTransactionResponse>, t: Throwable) {
                sdk!!.cancelTransaction()
            }
        })
    }
}