package co.openfabric.unilateral.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.openfabric.unilateral.sdk.TenantConfiguration
import co.openfabric.unilateral.sdk.UnilateralSDK
import java.net.URL

class MainActivity : AppCompatActivity() {
    val sdk = UnilateralSDK(
        TenantConfiguration(
            id = "ffb4313d-c03d-4aae-828a-abc51a9e3015",
            name = "Home Credit",
            logo = URL(""),
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}