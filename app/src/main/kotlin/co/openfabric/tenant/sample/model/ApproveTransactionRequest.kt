package co.openfabric.tenant.sample.model

data class ApproveTransactionRequest(
    val approved_amount: Double,
    val approved_currency: String,
    val account_reference_id: String
)

