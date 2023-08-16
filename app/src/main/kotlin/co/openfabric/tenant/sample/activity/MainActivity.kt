package co.openfabric.tenant.sample.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.openfabric.tenant.sample.adapter.GridAdapter
import co.openfabric.tenant.sample.model.Merchant
import co.openfabric.tenant.sample.provider.NetworkProvider
import co.openfabric.tenant.sample.service.TenantApi
import co.openfabric.unilateral.sample.R
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GridAdapter
    private val items = mutableListOf<Merchant>()

    private val tenantApi = NetworkProvider.retrofit.create(TenantApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        fetchMerchants()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(this, 2) // 2 columns
        recyclerView.layoutManager = gridLayoutManager

        adapter = GridAdapter(this, items)
        recyclerView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchMerchants() {
        tenantApi.getMerchants().enqueue(object : Callback<List<Merchant>> {
            override fun onResponse(call: Call<List<Merchant>>, response: Response<List<Merchant>>) {
                if (response.isSuccessful) {
                    response.body()?.let { items.addAll(it) }
                    adapter.notifyDataSetChanged()
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