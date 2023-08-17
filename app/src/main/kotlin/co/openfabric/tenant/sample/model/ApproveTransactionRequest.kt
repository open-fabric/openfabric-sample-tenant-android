package co.openfabric.tenant.sample.model

data class ApproveTransactionRequest(
    val accountReferenceId: String,
    val transactionId: String
)

