package cabuser.com.rydz.ui.ride

import cabuser.com.rydz.ui.home.SendBookResponse
import com.google.gson.annotations.SerializedName

data class RideStatusChangeResponse(
        @SerializedName("message")
        var message: String? = "",
        @SerializedName("status")
        var status: String? = "", // 1
        @SerializedName("success")
        var success: Boolean? = false, // true
        @SerializedName("booking")
var booking: SendBookResponse.Booking? = SendBookResponse.Booking()
)