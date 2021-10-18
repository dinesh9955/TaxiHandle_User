package cabuser.com.rydz.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.profile.CheckPhoneStatusModel
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.common.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ScheduleAdapter(private var pastTripsList: List<BookingHistory>, internal var context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<ScheduleAdapter.MyViewHolder>(), Callback<CheckPhoneStatusModel> {

    private var positionCancel = 0
    var mProgress: CustomeProgressDialog? = null


    override fun onFailure(call: Call<CheckPhoneStatusModel>, t: Throwable) {
        mProgress!!.dismiss()
        Toast.makeText(context, "Sorry some error occured", Toast.LENGTH_SHORT).show()
    }

    override fun onResponse(call: Call<CheckPhoneStatusModel>, response: Response<CheckPhoneStatusModel>) {
        if (response.isSuccessful) {
            mProgress!!.dismiss()
            if (response.body()!!.success) {
                Toast.makeText(context, context.getString(R.string.ride_cancelled), Toast.LENGTH_SHORT).show()
                //pastTripsList.drop(positionCancel)
                pastTripsList = pastTripsList.minusElement(pastTripsList[positionCancel])
                this.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.upcoming_rowitem, parent, false)
        mProgress = CustomeProgressDialog(context)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val hc = pastTripsList[position]
        holder.time?.text = "" + hc.timeSlot.toString()
        holder.price?.text = "$ " +  String.format("%1$.2f", hc.fare).replace(",", ".").toDouble()
        holder.sourceLoc?.text = hc.source!!.name
        holder.destinationLoc?.text = hc.destination!!.name
        holder.date?.text = Utils.getDate(hc.scheduleTime!!)
        holder.cancelRide?.setOnClickListener { v ->
            positionCancel = position
            mProgress!!.show()
            RydzApplication.getRetroApiClient().cancelRide(hc.id!!).enqueue(this)
        }

    }

    override fun getItemCount(): Int {
        return pastTripsList.size
    }

    inner class MyViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val date: TextView? = view.findViewById<View>(R.id.tv_day) as? TextView
        val time: TextView?
        val price: TextView?
        private val cv_history: androidx.cardview.widget.CardView?
        val sourceLoc: TextView?
        val destinationLoc: TextView?
        val cancelRide: LinearLayout?

        init {
            time = view.findViewById<View>(R.id.tv_timeDuration) as? TextView
            price = view.findViewById<View>(R.id.tv_expectedPrice) as? TextView
            cv_history = view.findViewById<View>(R.id.cv_history) as? androidx.cardview.widget.CardView
            sourceLoc = view.findViewById<View>(R.id.tv_startPlace) as? TextView
            destinationLoc = view.findViewById<View>(R.id.tv_endPlace) as? TextView
            cancelRide = view.findViewById<View>(R.id.ll_cancel) as? LinearLayout
        }


    }
}