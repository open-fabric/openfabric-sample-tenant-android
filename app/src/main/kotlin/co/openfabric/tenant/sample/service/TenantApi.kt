package co.openfabric.tenant.sample.service

import co.openfabric.tenant.sample.model.Merchant
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.UUID


data class ApproveTransactionRequest(
    val id: UUID,
    val account_reference_id: String,
    val amount: Double? = null,
    val currency: String? = null
)

data class ApproveTransactionResponse(
    val card_fetch_token: String,
)

interface TenantApi {
    @GET("/api/unilateral/partners")
    fun getMerchants(): Call<List<Merchant>>

    @POST("/api/orchestrated/approve")
    fun approve(@Body request: ApproveTransactionRequest): Call<ApproveTransactionResponse>
}