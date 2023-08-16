package co.openfabric.tenant.sample.service

import co.openfabric.tenant.sample.model.Merchant
import retrofit2.Call
import retrofit2.http.GET

interface TenantApi {
    @GET("/api/unilateral/partners")
    fun getMerchants(): Call<List<Merchant>>
}