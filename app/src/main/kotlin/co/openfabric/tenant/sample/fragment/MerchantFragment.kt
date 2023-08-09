package co.openfabric.tenant.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.openfabric.unilateral.sdk.TenantConfiguration
import co.openfabric.unilateral.sdk.UnilateralSDK
import java.net.URL


class MerchantFragment: Fragment() {
    private val sdk: UnilateralSDK by lazy {
        UnilateralSDK(
            TenantConfiguration(
                id = "ffb4313d-c03d-4aae-828a-abc51a9e3015",
                name = "Home Credit",
                logo = URL(""),
            )
        )
    }

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentGalleryBinding.inflate(inflater, container, false)
//        sdk!.configure(binding.webView, Merchant())
//
//        sdk!.setOnCheckoutListener { transaction ->
//            // expect Tenant to show approval prompt + call API to approve the transaction
//            // !!!TODO: decision point regarding the approval prompt
//
//            // After the approval is done, Tenant is expected to notify the SDK
//            sdk!.onTransactionApproved(transaction)
//        }
//
//        binding.webView.loadUrl(...)
//
//        return binding.root
//    }
}