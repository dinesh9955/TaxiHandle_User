package cabuser.com.rydz.ui.ride

import android.os.Parcel
import android.os.Parcelable

data class PromoId
(
        var __v: Int = 0, // 0
        var _id: String = "", // 5dc1058c413f93256114d54e
        var code: String = "", // NewCode30
        var dayslimit: Int = 0, // 1
        var discount: Int = 0, // 10
        var discountType: Int = 0, // 2
        var enddate: Long = 0, // 1573756200000
        var image: String = "",
        var maxamount: Int = 0, // 10
        var maxlimit: Int = 0, // 15
        var name: String = "", // New Year
        var startdate: Long = 0 // 1572892200000

) :Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readLong())


    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeValue(__v)
        parcel.writeValue(_id)
        parcel.writeValue(code)
        parcel.writeValue(dayslimit)
        parcel.writeValue(discount)
        parcel.writeValue(discountType)
        parcel.writeValue(enddate)
        parcel.writeValue(image)
        parcel.writeValue(maxamount)
        parcel.writeValue(maxlimit)
        parcel.writeValue(name)
        parcel.writeValue(startdate)
    }

    override fun describeContents(): Int {
       return 0
    }

    companion object CREATOR : Parcelable.Creator<PromoId> {
        override fun createFromParcel(parcel: Parcel): PromoId {
            return PromoId(parcel)
        }

        override fun newArray(size: Int): Array<PromoId?> {
            return arrayOfNulls(size)
        }
    }
}