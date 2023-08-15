package co.openfabric.tenant.sample.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.openfabric.tenant.sample.adapter.GridAdapter
import co.openfabric.tenant.sample.model.Merchant
import co.openfabric.tenant.sample.provider.NetworkProvider
import co.openfabric.tenant.sample.service.ApiService
import co.openfabric.tenant.sample.service.ApiServiceImpl
import co.openfabric.unilateral.sample.R

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GridAdapter
    private val items = mutableListOf<Merchant>()
    private val apiService = NetworkProvider.retrofit.create(ApiService::class.java)
    private val apiServiceImpl = ApiServiceImpl(apiService)

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

    private fun fetchMerchants() {
        println("Start to fetch merchants")
        apiServiceImpl.fetchMerchants(
            onSuccess = { merchants ->
                items.addAll(merchants)
                adapter.notifyDataSetChanged()
            },
            onError = { error ->
                println(error)
            }
        )
    }
}