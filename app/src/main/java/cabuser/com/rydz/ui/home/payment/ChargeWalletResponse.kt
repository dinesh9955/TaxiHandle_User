package cabuser.com.rydz.ui.home.payment

data class ChargeWalletResponse(
	val Status: Boolean = false,
	val messages: String = "",
	val walletAmount: Double =0.0
)
