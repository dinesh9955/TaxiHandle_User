package cabuser.com.rydz.ui.settings

import cabuser.com.rydz.ui.home.payment.addcard.PromoCode
import cabuser.com.rydz.ui.ride.PromoId
import com.google.gson.annotations.SerializedName

data class SupportResponse(
        @SerializedName("message")
        var message: String? = "", // Sent successfully
        @SerializedName("success")
        var success: Boolean? = false, // true
        @SerializedName("coupon")
        var coupon: PromoCode? = null // true
)