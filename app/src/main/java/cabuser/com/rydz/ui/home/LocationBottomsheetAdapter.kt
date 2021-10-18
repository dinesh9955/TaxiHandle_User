package cabuser.com.rydz.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.PlacesEventObject
import cabuser.com.rydz.util.common.OnLocationItemClickListener
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.location_rowitem.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*


class LocationBottomsheetAdapter(private val placesClient: PlacesClient, val context: Context, private val placesList: List<AutocompletePrediction>, val onItemClickListener: OnLocationItemClickListener, val placeType: String) : androidx.recyclerview.widget.RecyclerView.Adapter<LocationBottomsheetAdapter.ViewHolder>() {


    var srcObject: SendBookResponse.Booking.Source? = null
    var desObject: SendBookResponse.Booking.Destination? = null

    override fun onBindViewHolder(holder: LocationBottomsheetAdapter.ViewHolder, position: Int) {
        holder.tv_locname.text = placesList.get(position).getPrimaryText(null).toString()
        holder.tv_locAddress.text = placesList.get(position).getSecondaryText(null).toString()
        var place: com.google.android.libraries.places.api.model.Place?
        holder.rl_location.setOnClickListener {
            (context as LocationSetActivity).progress.visibility = View.VISIBLE
            if ( placesList.size > 0) {
                val placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.NAME, com.google.android.libraries.places.api.model.Place.Field.LAT_LNG, com.google.android.libraries.places.api.model.Place.Field.ADDRESS)
                val request = FetchPlaceRequest.builder(placesList.get(position).placeId, placeFields).build()
                placesClient.fetchPlace(request).addOnSuccessListener({ response ->
                    place = response.place


                    if (placeType.equals("source")) {
                        srcObject = SendBookResponse.Booking.Source(place!!.latLng!!.latitude.toString(), place!!.latLng!!.longitude.toString(), /*place!!.name + ", " +*/ place!!.address)
                    } else {
                        desObject = SendBookResponse.Booking.Destination(place!!.latLng!!.latitude.toString(), place!!.latLng!!.longitude.toString(), /*place!!.name + ", " +*/ place!!.address)
                    }

                    if (srcObject != null && desObject != null) {
                        onItemClickListener.onItemClick("location name", srcObject!!, desObject!!)
                    } else if (srcObject != null && desObject == null) {
                        if (EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java) != null) {
                            desObject = EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java).destination
                        } else {
                            desObject = SendBookResponse.Booking.Destination()
                        }
                        onItemClickListener.onItemClick("location name", srcObject!!, desObject!!)
                    } else if (srcObject == null && desObject != null) {
                        if (EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java) != null) {
                            srcObject = EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java).source
                        } else {
                            srcObject = SendBookResponse.Booking.Source()
                        }
                        onItemClickListener.onItemClick("location name", srcObject!!, desObject!!)
                    }

                }).addOnFailureListener({

                })
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.location_rowitem, viewGroup, false))
    }


    override fun getItemCount(): Int {
        return placesList.size
    }


    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        var tv_locname: TextView = view.tv_locname
        var tv_locAddress: TextView = view.tv_locAddress
        var rl_location: RelativeLayout = view.rl_location


    }

}