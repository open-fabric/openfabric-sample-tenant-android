package co.openfabric.tenant.sample.service

import co.openfabric.tenant.sample.model.Merchant
import co.openfabric.tenant.sample.model.ApproveTransactionRequest
import co.openfabric.tenant.sample.model.ApproveTransactionResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TenantApi {
    @GET("/api/unilateral/partners")
    fun getMerchants(): Call<List<Merchant>>

    @POST("/api/orchestrated/approve")
    fun createTransaction(@Body request: ApproveTransactionRequest): Call<ApproveTransactionResponse>
}