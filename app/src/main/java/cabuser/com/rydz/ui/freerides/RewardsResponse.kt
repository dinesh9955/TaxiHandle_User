package cabuser.com.rydz.ui.freerides

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RewardsResponse(
        @SerializedName("message")
        var message: String? = "", // Successful
        @SerializedName("success")
        var success: Boolean? = false, // true
        @SerializedName("userRewards")
        var userRewards: Int? = 0 ,
        @SerializedName("wallet")
         var wallet: Double? = 0.0) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Double::class.java.classLoader) as? Double) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(message)
        parcel.writeValue(success)
        parcel.writeValue(userRewards)
        parcel.writeValue(wallet)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RewardsResponse> {
        override fun createFromParcel(parcel: Parcel): RewardsResponse {
            return RewardsResponse(parcel)
        }

        override fun newArray(size: Int): Array<RewardsResponse?> {
            return arrayOfNulls(size)
        }
    }
}