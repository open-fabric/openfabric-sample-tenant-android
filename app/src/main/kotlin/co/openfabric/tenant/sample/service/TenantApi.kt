package co.openfabric.tenant.sample.service

import co.openfabric.tenant.sample.model.Merchant
import co.openfabric.tenant.sample.model.TransactionRequest
import co.openfabric.tenant.sample.model.TransactionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface TenantApi {
    @GET("/api/unilateral/partners")
    fun getMerchants(): Call<List<Merchant>>

    @POST("/api/unilateral/transactions")
    fun createTransaction(request: TransactionRequest): Call<TransactionResponse>
}