package co.openfabric.tenant.sample.service

import co.openfabric.tenant.sample.model.Merchant
import co.openfabric.tenant.sample.model.TransactionRequest
import co.openfabric.tenant.sample.model.TransactionResponse
import co.openfabric.tenant.sample.provider.NetworkProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiServiceImpl(private val apiService: ApiService) {

    fun fetchMerchants(
        onSuccess: (List<Merchant>) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.getMerchants().enqueue(object : Callback<List<Merchant>> {
            override fun onResponse(call: Call<List<Merchant>>, response: Response<List<Merchant>>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        println("API call succeed: ${body}")
                        onSuccess(body)
                    }
                } else {
                    println("Error response: ${response.code()}")
                    onError("Error response: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Merchant>>, t: Throwable) {
                println("API call failed: ${t.message}")
                onError("API call failed: ${t.message}")
            }
        })
    }

    fun createTransaction(
        request: TransactionRequest,
        onSuccess: (TransactionResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.createTransaction(request).enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        println("Transaction created: $body")
                        onSuccess(body)
                    }
                } else {
                    println("Error response: ${response.code()}")
                    onError("Error response: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                println("API call failed: ${t.message}")
                onError("API call failed: ${t.message}")
            }
        })
    }
}