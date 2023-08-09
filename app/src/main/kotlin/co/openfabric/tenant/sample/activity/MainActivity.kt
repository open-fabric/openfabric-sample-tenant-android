package co.openfabric.tenant.sample.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.openfabric.tenant.sample.adapter.GridAdapter
import co.openfabric.tenant.sample.model.Merchant
import co.openfabric.unilateral.sample.R
import co.openfabric.unilateral.sdk.TenantConfiguration
import co.openfabric.unilateral.sdk.UnilateralSDK
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Replace the following list with your data source
        val items = listOf(
            Merchant("Lazada", R.drawable.lazada, "https://www.lazada.vn/"),
            Merchant("Shopee", R.drawable.shopee, "https://shopee.vn/")
        )

        val gridLayoutManager = GridLayoutManager(this, 2) // 2 columns
        recyclerView.layoutManager = gridLayoutManager

        val adapter = GridAdapter(this, items)
        recyclerView.adapter = adapter
    }
}