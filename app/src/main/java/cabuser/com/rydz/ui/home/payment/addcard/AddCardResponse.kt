package cabuser.com.rydz.ui.home.payment.addcard

data class AddCardResponse(
	val messagechargeStrips: String = "",
	val status: Boolean = false,
	var card: Card
)
