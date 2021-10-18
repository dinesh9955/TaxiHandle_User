package cabuser.com.rydz.ui.ride

import com.google.gson.annotations.SerializedName

data class  TrackStatusResponse(
        @SerializedName("location")
        var location: Location? = Location(),
        @SerializedName("success")
        var success: Boolean? = false // true
) {
    data class Location(
            @SerializedName("bearing")
            var bearing: Double? = 0.0, // 0.0
            @SerializedName("bookingId")
            var bookingId: String? = "", // 5c7900a0ee177b5191101ccc
            @SerializedName("driverId")
            var driverId: String? = "", // 5c784ae069a9f12a9b135c14
            @SerializedName("latitude")
            var latitude: Double? = 0.0, // 30.7130125
            @SerializedName("longitude")
            var longitude: Double? = 0.0, // 76.7093687
            @SerializedName("status")
            var status: Int? = 0 // 1
    )
}