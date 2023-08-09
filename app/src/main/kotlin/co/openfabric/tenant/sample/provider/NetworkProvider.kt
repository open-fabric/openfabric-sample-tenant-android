package co.openfabric.tenant.sample.provider;

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder

object NetworkProvider {
    @Volatile
    private var instance: NetworkProvider? = null

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://of-test-1.samples.dev.openfabric.co")
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    fun self(): NetworkProvider {
        if (instance == null) {
            synchronized(this) {
                if (instance == null) {
                    instance = NetworkProvider
                }
            }
        }
        return instance!!
    }
}

