package co.openfabric.tenant.sample.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.io.Serializable
import java.net.URL
import java.util.UUID

data class Partner(
    val name: String,
    val url: URL,
    val accessToken: String
): Serializable

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
    fun getMerchants(): Call<List<Partner>>

    @POST("/api/orchestrated/approve")
    fun approve(@Body request: ApproveTransactionRequest): Call<ApproveTransactionResponse>
}