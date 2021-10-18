package cabuser.com.rydz.ui.home.payment.addcard

import android.os.Parcel
import android.os.Parcelable

data class PromoCode(
	val image: String = "",
	val maxlimit: Int= 0,
	val code: String = "",
	val description: String = "",
	val enddate: Long = 0,
	val maxamount: Double = 0.0,
	val V: Int = 0,
	val name: String = "",
	val discount: Double = 0.0,
	val discountType: Int = 0,
	val dayslimit: Int = 0,
	val _id: String = "",
	val startdate: Long= 0
) : Parcelable {
	constructor(parcel: Parcel) : this(
			parcel.readString()!!,
			parcel.readInt(),
			parcel.readString()!!,
			parcel.readString()!!,
			parcel.readLong(),
			parcel.readDouble()!!,
			parcel.readInt(),
			parcel.readString()!!,
			parcel.readDouble(),
			parcel.readInt(),
			parcel.readInt(),
			parcel.readString()!!,
			parcel.readLong()) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(image)
		parcel.writeInt(maxlimit)
		parcel.writeString(code)
		parcel.writeString(description)
		parcel.writeLong(enddate)
		parcel.writeDouble(maxamount)
		parcel.writeInt(V)
		parcel.writeString(name)
		parcel.writeDouble(discount)
		parcel.writeInt(discountType)
		parcel.writeInt(dayslimit)
		parcel.writeString(_id)
		parcel.writeLong(startdate)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<PromoCode> {
		override fun createFromParcel(parcel: Parcel): PromoCode {
			return PromoCode(parcel)
		}

		override fun newArray(size: Int): Array<PromoCode?> {
			return arrayOfNulls(size)
		}
	}
}
