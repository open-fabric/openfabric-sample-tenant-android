package co.openfabric.tenant.sample.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.openfabric.tenant.sample.adapter.GridAdapter
import co.openfabric.tenant.sample.service.Partner
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

    private val items = mutableListOf<Partner>()

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
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = GridAdapter(this, items)
        recyclerView.adapter = adapter

        fetchPartners()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchPartners() {
        api.getPartners().enqueue(object : Callback<List<Partner>> {
            override fun onResponse(
                call: Call<List<Partner>>,
                response: Response<List<Partner>>
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

            override fun onFailure(call: Call<List<Partner>>, t: Throwable) {
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
