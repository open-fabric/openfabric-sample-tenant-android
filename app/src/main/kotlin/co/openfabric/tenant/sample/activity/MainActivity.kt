package co.openfabric.tenant.sample.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.openfabric.tenant.sample.adapter.GridAdapter
import co.openfabric.tenant.sample.service.Partner
import co.openfabric.tenant.sample.service.TenantApi
import co.openfabric.unilateral.sample.R
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GridAdapter

    private val items = mutableListOf<Partner>()

    private val api: TenantApi = Retrofit.Builder()
        .baseUrl("https://of-test-1.samples.dev.openfabric.co")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
        .create(TenantApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.acitivity_main_app)

        adapter = GridAdapter(this, items)

        fetchPartners(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchPartners(context: Context) {
        api.getPartners().enqueue(object : Callback<List<Partner>> {
            override fun onResponse(
                call: Call<List<Partner>>,
                response: Response<List<Partner>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        items.addAll(it)
                        val lazadaButton = findViewById<ImageView>(R.id.lazada_button)
                        val lazada = it.find { partner ->  partner.name == "Lazada" }
                        lazadaButton.setOnClickListener {
                            val webViewIntent = Intent(context, WebViewActivity::class.java)
                            webViewIntent.putExtra(WebViewActivity.INTENT_PARTNER, lazada)
                            context.startActivity(webViewIntent)
                        }

                        val shopeeButton = findViewById<ImageView>(R.id.shopee_button)
                        val shopee = it.find { partner -> partner.name == "Shopee" }

                        shopeeButton.setOnClickListener {
                            val webViewIntent = Intent(context, WebViewActivity::class.java)
                            webViewIntent.putExtra(WebViewActivity.INTENT_PARTNER, shopee)
                            context.startActivity(webViewIntent)
                        }
                    }
                } else {
                    displayError(RuntimeException("Error response: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<Partner>>, t: Throwable) {
                displayError(t)
            }
        })
    }

    private fun displayError(throwable: Throwable) {
        Toast.makeText(this, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
    }
}
