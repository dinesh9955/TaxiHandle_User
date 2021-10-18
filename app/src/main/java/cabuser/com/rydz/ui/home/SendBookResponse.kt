package cabuser.com.rydz.ui.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SendBookResponse(
        @SerializedName("booking")
        var booking: Booking? = Booking(),
        @SerializedName("message")
        var message: String? = "",
        @SerializedName("status")
        var status: Int? = 0, // 0
        @SerializedName("success")
        var success: Boolean? = false // true
) {
    data class Booking(
            @SerializedName("__v")
            var v: Int? = 0, // 0
            @SerializedName("_id")
            var id: String? = "", // 5c7674fdb6368d25ee5334eb
            @SerializedName("destination")
            var destination: Destination? = Destination(),
            @SerializedName("driverId")
            var driverId: DriverId? = DriverId(),
            @SerializedName("fare")
            var fare: Double? = 0.0, // 30
            @SerializedName("subtotalFare")
            var subtotalFare: Double? = 0.0, // 30
            @SerializedName("isPaid")
            var isPaid: Int? = 0, // 0
            @SerializedName("paymentMode")
            var paymentMode: String? = "", // cash
            @SerializedName("source")
            var source: Source? = Source(),
            @SerializedName("status")
            var status: Int? = 0, // 0
            @SerializedName("stopPoints")
            var stopPoints: List<Any?>? = listOf(),
            @SerializedName("tax")
            var tax: Double? = 0.0, // 0
            @SerializedName("tripEndTime")
            var tripEndTime: Long? = 0, // 0
            @SerializedName("tripStartTime")
            var tripStartTime: Long? = 0, // 0
            @SerializedName("userId")
            var userId: UserId? = UserId(),
            @SerializedName("vehicleType")
            var vehicleType: String? = "",
            @SerializedName("date")
            @Expose
            var date: Long? = 0,
            @SerializedName("driverDocuments")
            var driverDocuments: List<DriverDocument> = listOf()
    ) {
        data class Destination(

                @SerializedName("latitude")
                var latitude: String? = "", // 30.7096333
                @SerializedName("longitude")
                var longitude: String? = "", // 76.71992329999999
                @SerializedName("name")
                var name: String? = "" // Phase 3B-2, Phase 3B-2, Sector 60, Sahibzada Ajit Singh Nagar, Punjab, India
        )

        data class Source(
                @SerializedName("latitude")
                var latitude: String? = "", // 30.713023
                @SerializedName("longitude")
                var longitude: String? = "", // 76.709291
                @SerializedName("name")
                var name: String? = "" // Apptunix, C127, Phase 8, Industrial Area, Phase 8, Industrial Area, Sector 73, Sahibzada Ajit Singh Nagar, Punjab 160071, India
        )

        data class UserId(
                @SerializedName("__v")
                var v: Int? = 0, // 0
                @SerializedName("countryCode")
                var contryCode: String? = "", // +91
                @SerializedName("createdAt")
                var createdAt: String? = "", // 2019-02-25T06:32:41.755Z
                @SerializedName("email")
                var email: String? = "", // honey.sadiora@apptunix.com
                @SerializedName("firstName")
                var firstName: String? = "", // honey
                @SerializedName("id")
                var id: String? = "", // 5c738c09eb7bb22afbaef7ba
                @SerializedName("inviteCode")
                var inviteCode: String? = "", // 6PZVFOM
                @SerializedName("isFullUrl")
                var isFullUrl: Int? = 0, // 0
                @SerializedName("lastName")
                var lastName: String? = "", // sadiora
                @SerializedName("phone")
                var phone: String? = "", // 9914567515
                @SerializedName("profilePic")
                var profilePic: String? = "",
                @SerializedName("ratings")
                var ratings: Any? = Any(), // null
                @SerializedName("updatedAt")
                var updatedAt: String? = "" // 2019-02-25T06:32:41.763Z
        )

        data class DriverId(
                @SerializedName("__v")
                var v: Int? = 0, // 0
                @SerializedName("totalDrivingRating")
                var totalDrivingRating: Double? = 0.0, // 0
                @SerializedName("bio")
                var bio: String? = "", // testing
                @SerializedName("countryCode")
                var contryCode: String? = "", // +91
                @SerializedName("coordinates")
                var coordinates: List<Double?>? = listOf(),
                @SerializedName("createdAt")
                var createdAt: String? = "", // 2019-01-29T08:16:55.954Z
                @SerializedName("deviceId")
                var deviceId: String? = "", // 11111112
                @SerializedName("deviceType")
                var deviceType: String? = "", // iOS
                @SerializedName("dob")
                var dob: Long? = 0, // 1546300800000
                @SerializedName("email")
                var email: String? = "", // pank.sharma@apptunix.com
                @SerializedName("firstName")
                var firstName: String? = "", // N
                @SerializedName("gender")
                var gender: String? = "", // Male
                @SerializedName("id")
                var id: String? = "", // 5c500bf78f80ec6aee8ce947
                @SerializedName("isAvailable")
                var isAvailable: Int? = 0, // 1
                @SerializedName("isVerified")
                var isVerified: Int? = 0, // 1
                @SerializedName("lastName")
                var lastName: String? = "", // new 2
                @SerializedName("latitude")
                var latitude: Double? = 0.0, // 30.713000335735522
                @SerializedName("longitude")
                var longitude: Double? = 0.0, // 76.70947288428115
                @SerializedName("manufacturerName")
                var manufacturerName: String? = "", // Maruti
                @SerializedName("model")
                var model: String? = "", // swift
                @SerializedName("phone")
                var phone: String? = "", // 9416051592
                @SerializedName("profilePic")
                var profilePic: String? = "", // static/drivers/pic_1551182490503_pic.png
                @SerializedName("ratings")
                var ratings: Any? = Any(), // null
                @SerializedName("updatedAt")
                var updatedAt: String? = "", // 2019-02-27T11:31:03.013Z
                @SerializedName("vehicleColor")
                var vehicleColor: String? = "", // black
                @SerializedName("vehicleNumber")
                var vehicleNumber: String? = "", // 1234
                @SerializedName("vehicleType")
                var vehicleType: String? = "", // 5c500bc68f80ec6aee8ce944
                @SerializedName("vehicleName")
                var vehicleName: String? = ""

        )
    }
}