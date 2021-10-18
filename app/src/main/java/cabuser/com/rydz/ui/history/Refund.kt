package cabuser.com.rydz.ui.history

import android.os.Parcel
import android.os.Parcelable

data class Refund(
	val reason: String? = null,
	val description: String? = null,
	val refundAmount: Double = 0.0,
	val status: String? = null
) : Parcelable {
	constructor(parcel: Parcel) : this(
			parcel.readString(),
			parcel.readString(),
			parcel.readDouble(),
			parcel.readString()) {
	}

	override fun writeToParcel(p0: Parcel, p1: Int) {
		p0.writeString(reason)
		p0.writeString(description)
		p0.writeDouble(refundAmount)
		p0.writeString(status)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<Refund> {
		override fun createFromParcel(parcel: Parcel): Refund {
			return Refund(parcel)
		}

		override fun newArray(size: Int): Array<Refund?> {
			return arrayOfNulls(size)
		}
	}
}
