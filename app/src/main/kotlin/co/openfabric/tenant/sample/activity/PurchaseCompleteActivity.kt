package co.openfabric.tenant.sample.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import co.openfabric.unilateral.sample.R

class PurchaseCompleteActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContentView(R.layout.purchase_completed)
        val button = findViewById<Button>(R.id.purchase_complete_btn)

        button.setOnClickListener {
            finish()
        }
    }
}