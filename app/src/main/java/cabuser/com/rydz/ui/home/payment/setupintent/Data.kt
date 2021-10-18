package cabuser.com.rydz.ui.home.payment.setupintent

data class Data(
	val mandate: Any? = null,
	val livemode: Boolean = false,
	val onBehalfOf: Any? = null,
	val created: Long = 0,
	val paymentMethodTypes: List<String?>? = null,
	val usage:String = "",


	val id:String = "",
	val client_secret:String = "",
	val paymentMethodOptions: PaymentMethodOptions? = null,

	val status:String = ""
)
