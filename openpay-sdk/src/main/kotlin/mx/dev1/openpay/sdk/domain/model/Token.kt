package mx.dev1.openpay.sdk.domain.model

/**
 * Result of tokenizing a card. The [tokenId] replaces the card data when
 * creating charges from the merchant's backend.
 */
data class Token(
    val tokenId: String,
    val card: TokenizedCard?,
)

/**
 * Card information echoed back by the Openpay API after tokenization.
 * The card number comes masked, only the last four digits are visible.
 */
data class TokenizedCard(
    val maskedCardNumber: String?,
    val holderName: String?,
    val expirationMonth: String?,
    val expirationYear: String?,
    val brand: String?,
    val cardType: String?,
    val bankName: String?,
    val bankCode: String?,
    val allowsCharges: Boolean?,
    val allowsPayouts: Boolean?,
    val address: Address?,
)
