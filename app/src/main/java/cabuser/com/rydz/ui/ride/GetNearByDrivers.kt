package cabuser.com.rydz.ui.ride

import com.google.gson.annotations.SerializedName

data class GetNearByDrivers(
        @SerializedName("cardList")
        var cardList: List<Any?>? = listOf(),
        @SerializedName("driversLocations")
        var driversLocations: List<DriversLocation?>? = listOf(),
        @SerializedName("success")
        var success: Boolean? = false, // true
        @SerializedName("vehicleTypeList")
        var vehicleTypeList: List<VehicleType?>? = listOf(),
        @SerializedName("message")
        var message: String? = "",
        @SerializedName("promo")
        var promo: Promo? = Promo(),
        @SerializedName("walletAmount")
        var walletAmount: Double? = 0.0 // 0
        , @SerializedName("rating")
        var rating: Double? = 0.0

) {
    data class DriversLocation(

            @SerializedName("id")
            var id: String? = "", // 5c74e622854c9c62e95a2fbf
            @SerializedName("latitude")
            var latitude: Double? = 0.0, // 30.7130246
            @SerializedName("longitude")
            var longitude: Double? = 0.0, // 76.7093696
            @SerializedName("ratings")
            var ratings: Any? = Any(), // null
            @SerializedName("vehicleType")
            var vehicleType: String? = "" // 5c500bca8f80ec6aee8ce945
    )

    data class VehicleType(
            @SerializedName("__v")
            var v: Int? = 0, // 0
            @SerializedName("_id")
            var id: String? = "", // 5c500bca8f80ec6aee8ce945
            @SerializedName("baseFare")
            var baseFare: Double? = 0.0, // 30
            @SerializedName("fareRate")
            var fareRate: Double? = 0.0, // 10
            @SerializedName("image")
            var image: String? = "",
            @SerializedName("name")
            var name: String? = "", // XX
            @SerializedName("persons")
            var persons: Int? = 0,// 4
            @SerializedName("selected")
            var isselected: Boolean? = false,
            @SerializedName("fareRateAfter")
            var fareRateAfter: Double? = 0.0, // 3,
            @SerializedName("fareChangekm")
            var fareChangekm: Double? = 0.0,
            @SerializedName("activeImage")
            var activeImage: String? = ""


    )


data class Promo(
        @SerializedName("__v")
        var v: Int? = 0, // 0
        @SerializedName("_id")
        var id: String? = "", // 5c8ca91a7cb11f21a22bbe79
        @SerializedName("applied")
        var applied: Int? = 0, // 1
        @SerializedName("promoId")
        var promoId: PromoId? = PromoId(),
        @SerializedName("validTill")
        var validTill: Long? = 0 // 1553154202689
) {
    data class PromoId(
            @SerializedName("__v")
            var v: Int? = 0, // 0
            @SerializedName("_id")
            var id: String? = "", // 5c77b3d1d5c5437cffb99a84
            @SerializedName("code")
            var code: String? = "", // aa
            @SerializedName("dayslimit")
            var dayslimit: Int? = 0, // 5
            @SerializedName("description")
            var description: String? = "", // test
            @SerializedName("discount")
            var discount: Double? = 0.0, // 12
            @SerializedName("discountType")
            var discountType: Double? = 0.0, // 1
            @SerializedName("enddate")
            var enddate: Long? = 0, // 1554057000000
            @SerializedName("image")
            var image: String? = "",
            @SerializedName("maxamount")
            var maxamount: Double? = 0.0, // 100
            @SerializedName("maxlimit")
            var maxlimit: Double? = 0.0, // 23
            @SerializedName("name")
            var name: String? = "" // aa
    )
}
}