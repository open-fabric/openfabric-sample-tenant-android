package co.openfabric.tenant.sample.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.openfabric.tenant.sample.adapter.GridAdapter
import co.openfabric.tenant.sample.model.Merchant
import co.openfabric.tenant.sample.service.TenantApi
import co.openfabric.unilateral.sample.R
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var toolbar: Toolbar

    private val items = mutableListOf<Merchant>()

    val api = Retrofit.Builder()
        .baseUrl("https://of-test-1.samples.dev.openfabric.co")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()
        .create(TenantApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        loadingIndicator = findViewById(R.id.loadingIndicator)
        loadingIndicator.visibility = View.VISIBLE

        val gridLayoutManager = GridLayoutManager(this, 2) // 2 columns
        recyclerView.layoutManager = gridLayoutManager

        adapter = GridAdapter(this, items)
        recyclerView.adapter = adapter

        // Show loading indicator
        loadingIndicator.visibility = View.VISIBLE

        // Simulate a loading delay of 5 seconds
        Handler().postDelayed({
            fetchMerchants()
            loadingIndicator.visibility = View.GONE
        }, 3000) // Delay for 5000 milliseconds (5 seconds)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchMerchants() {
        api.getMerchants().enqueue(object : Callback<List<Merchant>> {
            override fun onResponse(
                call: Call<List<Merchant>>,
                response: Response<List<Merchant>>
            ) {

                if (response.isSuccessful) {
                    response.body()?.let {
                        items.addAll(it)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    displayError(RuntimeException("Error response: ${response.code()}"))
                }
            }

            override fun onFailure(call: Call<List<Merchant>>, t: Throwable) {
                displayError(t)
            }
        })
    }

    private fun displayError(throwable: Throwable) {
        Snackbar.make(
            findViewById(android.R.id.content),
            throwable.localizedMessage!!,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
