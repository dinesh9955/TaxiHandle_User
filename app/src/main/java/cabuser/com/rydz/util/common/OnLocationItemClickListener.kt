package cabuser.com.rydz.util.common

import cabuser.com.rydz.ui.home.SendBookResponse


interface OnLocationItemClickListener {

    fun onItemClick(name: String ,srcplace_obj: SendBookResponse.Booking.Source,place_obj : SendBookResponse.Booking.Destination)
}