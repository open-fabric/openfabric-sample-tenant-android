package co.openfabric.tenant.sample.model

import java.io.Serializable
import java.net.URL

data class Merchant(
    val name: String,
    val url: URL,
    val accessToken: String
): Serializable
