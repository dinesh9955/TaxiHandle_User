package cabuser.com.rydz.ui.home.payment.addcard

import cabuser.com.rydz.ui.ride.GetNearByDrivers

data class CardsListResponse(
	val messages: String = "",
	val status: Boolean = false,
	var  Card: List<Card>,
	var  promo: List<PromoCode>,
	var walletAmount : Double = 0.0
)
