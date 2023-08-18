package co.openfabric.tenant.sample.model

data class ApproveTransactionResponse(
    val account_reference_id: String,
    val transaction_id: String,
    val amount: Double,
    val currency: String,
    val status: String
)