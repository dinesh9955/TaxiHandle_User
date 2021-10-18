package cabuser.com.rydz.ui.home.payment.addcard

data class Card(
	val stripePaymentMethod: String = "",
	val expiryDate: String = "",
	val isDefault: Boolean = false,
	val isDeleted: Boolean = false,
	val _V: Int = 0,
	val isBlocked: Boolean = false,
	val _id: String = "",
	val stripeCustomerId: String = "",
	val brand: String = "",
	val userId: String = "",
	val last4Digits: String = "",
	var isSelected : Boolean = false
)
