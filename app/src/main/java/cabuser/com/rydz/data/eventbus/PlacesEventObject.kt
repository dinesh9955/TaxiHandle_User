package cabuser.com.rydz.data.eventbus;

import cabuser.com.rydz.ui.home.SendBookResponse


public class PlacesEventObject(source: SendBookResponse.Booking.Source, destination:SendBookResponse.Booking.Destination,isPin:Boolean,isFocusOnSource:Boolean) {


    val source: SendBookResponse.Booking.Source = source
    val destination: SendBookResponse.Booking.Destination = destination
    val isPin:Boolean=isPin
    val isFocusOnSource:Boolean=isFocusOnSource


}
