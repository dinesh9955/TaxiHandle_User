package cabuser.com.rydz.ui.history


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class History() : Parcelable {

    @SerializedName("success")
    @Expose
    var success: Boolean? = null
    @SerializedName("bookingHistory")
    @Expose
    var bookingHistory: List<BookingHistory>? = null

    constructor(parcel: Parcel) : this() {
        success = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        bookingHistory = parcel.createTypedArrayList(BookingHistory)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(success)
        parcel.writeTypedList(bookingHistory)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<History> {
        override fun createFromParcel(parcel: Parcel): History {
            return History(parcel)
        }

        override fun newArray(size: Int): Array<History?> {
            return arrayOfNulls(size)
        }
    }


}




data class BookingHistory protected constructor(val parcel: Parcel): Parcelable  {

    @SerializedName("driverRating")
    @Expose
    var driverRating: Double? = null
    @SerializedName("userId")
    @Expose
    var userId: String? = null
    @SerializedName("driverId")
    @Expose
    var driverId: DriverId? = null
    @SerializedName("vehicleType")
    @Expose
    var vehicleType: VehicleType? = null
    @SerializedName("fare")
    @Expose
    var fare: Double? = null
     @SerializedName("subtotalFare")
    @Expose
    var subtotalFare: Double? = null
    @SerializedName("paymentMode")
    @Expose
    var paymentMode: String? = null
    @SerializedName("date")
    @Expose
    var date: Long? = null
    @SerializedName("__v")
    @Expose
    var v: Int? = null
    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null
    @SerializedName("status")
    @Expose
    var status: Int? = null
    @SerializedName("tripEndTime")
    @Expose
    var tripEndTime: Long? = null
     @SerializedName("mapImage")
     @Expose
     var mapImage:String?=null
    @SerializedName("tripStartTime")
    @Expose
    var tripStartTime: Long? = null
    @SerializedName("isPaid")
    @Expose
    var isPaid: Int? = null
    @SerializedName("tax")
    @Expose
    var tax: Double? = null
    @SerializedName("commission")
    @Expose
    var commission: Double? = null
    @SerializedName("stopPoints")
    @Expose
    var stopPoints: List<Any>? = null
    @SerializedName("destination")
    @Expose
    var destination: Destination? = null
     @SerializedName("refund")
    @Expose
    var refund: Refund? = null
    @SerializedName("source")
    @Expose
    var source: Source? = null
    @SerializedName("totalDriverRating")
    @Expose
    var totalDriverRating: Double = 0.0
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null
     @SerializedName("timeSlot")
     @Expose
     var timeSlot:String?=null
     @SerializedName("scheduleTime")
     @Expose
     var scheduleTime:Long?=null

    init {
        driverRating = parcel.readValue(Double::class.java.classLoader) as? Double
        userId = parcel.readString()
        driverId = parcel.readParcelable(DriverId::class.java.classLoader)
        vehicleType = parcel.readParcelable(VehicleType::class.java.classLoader)
        fare = parcel.readValue(Double::class.java.classLoader) as? Double
        subtotalFare = parcel.readValue(Double::class.java.classLoader) as? Double
        paymentMode = parcel.readString()
        date = parcel.readValue(Long::class.java.classLoader) as? Long
        v = parcel.readValue(Int::class.java.classLoader) as? Int
        updatedAt = parcel.readString()
        status = parcel.readValue(Int::class.java.classLoader) as? Int
        tripEndTime = parcel.readValue(Long::class.java.classLoader) as? Long
        mapImage = parcel.readString()
        tripStartTime = parcel.readValue(Long::class.java.classLoader) as? Long
        isPaid = parcel.readValue(Int::class.java.classLoader) as? Int
        tax = parcel.readValue(Double::class.java.classLoader) as? Double
        commission = parcel.readValue(Double::class.java.classLoader) as? Double
        destination = parcel.readParcelable(Destination::class.java.classLoader)
        refund = parcel.readParcelable(Refund::class.java.classLoader)
        source = parcel.readParcelable(Source::class.java.classLoader)
        totalDriverRating = parcel.readDouble()
        id = parcel.readString()
        createdAt = parcel.readString()
        timeSlot = parcel.readString()
        scheduleTime = parcel.readValue(Long::class.java.classLoader) as? Long
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(driverRating)
        parcel.writeString(userId)
        parcel.writeParcelable(driverId, flags)
        parcel.writeParcelable(vehicleType, flags)
        parcel.writeValue(fare)
        parcel.writeValue(subtotalFare)
        parcel.writeString(paymentMode)
        parcel.writeValue(date)
        parcel.writeValue(v)
        parcel.writeString(updatedAt)
        parcel.writeValue(status)
        parcel.writeValue(tripEndTime)
        parcel.writeString(mapImage)
        parcel.writeValue(tripStartTime)
        parcel.writeValue(isPaid)
        parcel.writeValue(tax)
        parcel.writeValue(commission)
        parcel.writeParcelable(destination, flags)
        parcel.writeParcelable(refund, flags)
        parcel.writeParcelable(source, flags)
        parcel.writeDouble(totalDriverRating)
        parcel.writeString(id)
        parcel.writeString(createdAt)
        parcel.writeString(timeSlot)
        parcel.writeValue(scheduleTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookingHistory> {
        override fun createFromParcel(parcel: Parcel): BookingHistory {
            return BookingHistory(parcel)
        }

        override fun newArray(size: Int): Array<BookingHistory?> {
            return arrayOfNulls(size)
        }
    }


}


data class Destination protected constructor(val `in`: Parcel) : Parcelable {

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null
    @SerializedName("latitude")
    @Expose
    var latitude: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null

    init {
        longitude = `in`.readString()
        latitude = `in`.readString()
        name = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(longitude)
        dest.writeString(latitude)
        dest.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<Destination> = object : Parcelable.Creator<Destination> {
            override fun createFromParcel(`in`: Parcel): Destination {
                return Destination(`in`)
            }

            override fun newArray(size: Int): Array<Destination?> {
                return arrayOfNulls(size)
            }
        }
    }

}


data class DriverId protected constructor(val `in`: Parcel) : Parcelable {

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null
    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null
    @SerializedName("firstName")
    @Expose
    var firstName: String? = null
    @SerializedName("lastName")
    @Expose
    var lastName: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("countryCode")
    @Expose
    var contryCode: String? = null
    @SerializedName("phone")
    @Expose
    var phone: String? = null
    @SerializedName("vehicleType")
    @Expose
    var vehicleType: String? = null
    @SerializedName("vehicleName")
    @Expose
    var vehicleName: String? = null
    @SerializedName("vehicleNumber")
    @Expose
    var vehicleNumber: String? = null
    @SerializedName("vehicleColor")
    @Expose
    var vehicleColor: String? = null
    @SerializedName("model")
    @Expose
    var model: String? = null
    @SerializedName("manufacturerName")
    @Expose
    var manufacturerName: String? = null
    @SerializedName("inviteCode")
    @Expose
    var inviteCode: String? = null
    @SerializedName("deviceType")
    @Expose
    var deviceType: String? = null
    @SerializedName("__v")
    @Expose
    var v: Int? = null
    @SerializedName("deviceId")
    @Expose
    var deviceId: String? = null
    @SerializedName("coordinates")
    @Expose
    var coordinates: List<Double>? = null
    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null
    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null
    @SerializedName("isAvailable")
    @Expose
    var isAvailable: Int? = null
    @SerializedName("isVerified")
    @Expose
    var isVerified: Int? = null
    @SerializedName("profilePic")
    @Expose
    var profilePic: String? = null
    @SerializedName("ratings")
    @Expose
    var ratings: List<Any>? = null
    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("updatedA t")
    @Expose
    var updatedAT: String? = null

    init {
        updatedAt = `in`.readString()
        createdAt = `in`.readString()
        firstName = `in`.readString()
        lastName = `in`.readString()
        email = `in`.readString()
        contryCode = `in`.readString()
        phone = `in`.readString()
        vehicleType = `in`.readString()
        vehicleName = `in`.readString()
        vehicleNumber = `in`.readString()
        vehicleColor = `in`.readString()
        model = `in`.readString()
        manufacturerName = `in`.readString()
        inviteCode = `in`.readString()
        deviceType = `in`.readString()
        if (`in`.readByte().toInt() == 0) {
            v = null
        } else {
            v = `in`.readInt()
        }
        deviceId = `in`.readString()
        if (`in`.readByte().toInt() == 0) {
            latitude = null
        } else {
            latitude = `in`.readDouble()
        }
        if (`in`.readByte().toInt() == 0) {
            longitude = null
        } else {
            longitude = `in`.readDouble()
        }
        if (`in`.readByte().toInt() == 0) {
            isAvailable = null
        } else {
            isAvailable = `in`.readInt()
        }
        if (`in`.readByte().toInt() == 0) {
            isVerified = null
        } else {
            isVerified = `in`.readInt()
        }
        profilePic = `in`.readString()
        id = `in`.readString()
        updatedAT = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(updatedAt)
        dest.writeString(createdAt)
        dest.writeString(firstName)
        dest.writeString(lastName)
        dest.writeString(email)
        dest.writeString(contryCode)
        dest.writeString(phone)
        dest.writeString(vehicleType)
        dest.writeString(vehicleName)
        dest.writeString(vehicleNumber)
        dest.writeString(vehicleColor)
        dest.writeString(model)
        dest.writeString(manufacturerName)
        dest.writeString(inviteCode)
        dest.writeString(deviceType)
        if (v == null) {
            dest.writeByte(0.toByte())
        } else {
            dest.writeByte(1.toByte())
            dest.writeInt(v!!)
        }
        dest.writeString(deviceId)
        if (latitude == null) {
            dest.writeByte(0.toByte())
        } else {
            dest.writeByte(1.toByte())
            dest.writeDouble(latitude!!)
        }
        if (longitude == null) {
            dest.writeByte(0.toByte())
        } else {
            dest.writeByte(1.toByte())
            dest.writeDouble(longitude!!)
        }
        if (isAvailable == null) {
            dest.writeByte(0.toByte())
        } else {
            dest.writeByte(1.toByte())
            dest.writeInt(isAvailable!!)
        }
        if (isVerified == null) {
            dest.writeByte(0.toByte())
        } else {
            dest.writeByte(1.toByte())
            dest.writeInt(isVerified!!)
        }
        dest.writeString(profilePic)
        dest.writeString(id)
        dest.writeString(updatedAT)
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<DriverId> = object : Parcelable.Creator<DriverId> {
            override fun createFromParcel(`in`: Parcel): DriverId {
                return DriverId(`in`)
            }

            override fun newArray(size: Int): Array<DriverId?> {
                return arrayOfNulls(size)
            }
        }
    }
}


data class DriverRate protected constructor(val `in`: Parcel) : Parcelable {

    @SerializedName("_id")
    @Expose
    var id: String? = null
    @SerializedName("bookingId")
    @Expose
    var bookingId: String? = null
    @SerializedName("rating")
    @Expose
    var rating: Int? = null

    init {
        id = `in`.readString()
        bookingId = `in`.readString()
        if (`in`.readByte().toInt() == 0) {
            rating = null
        } else {
            rating = `in`.readInt()
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(bookingId)
        if (rating == null) {
            dest.writeByte(0.toByte())
        } else {
            dest.writeByte(1.toByte())
            dest.writeInt(rating!!)
        }
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DriverRate> = object : Parcelable.Creator<DriverRate> {
            override fun createFromParcel(`in`: Parcel): DriverRate {
                return DriverRate(`in`)
            }

            override fun newArray(size: Int): Array<DriverRate?> {
                return arrayOfNulls(size)
            }
        }
    }
}


data class Source protected constructor(val `in`: Parcel) : Parcelable {

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null
    @SerializedName("latitude")
    @Expose
    var latitude: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null

    init {
        longitude = `in`.readString()
        latitude = `in`.readString()
        name = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(longitude)
        dest.writeString(latitude)
        dest.writeString(name)
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<Source> = object : Parcelable.Creator<Source> {
            override fun createFromParcel(`in`: Parcel): Source {
                return Source(`in`)
            }

            override fun newArray(size: Int): Array<Source?> {
                return arrayOfNulls(size)
            }
        }
    }
}


data class VehicleType protected constructor(val `in`: Parcel) : Parcelable {

    @SerializedName("_id")
    @Expose
    var id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null

    init {
        id = `in`.readString()
        name = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(name)
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<VehicleType> = object : Parcelable.Creator<VehicleType> {
            override fun createFromParcel(`in`: Parcel): VehicleType {
                return VehicleType(`in`)
            }

            override fun newArray(size: Int): Array<VehicleType?> {
                return arrayOfNulls(size)
            }
        }
    }



}




