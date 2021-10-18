package cabuser.com.rydz.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.util.common.OnItemClickListener
import cabuser.com.rydz.util.common.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class HistoryAdapter(private val pastTripsList: List<BookingHistory>, internal var context: Context, val itemClick : OnItemClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_rowitem, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val hc = pastTripsList[position]
        holder.carName.setText(hc.driverId!!.vehicleName )
        holder.date.setText(Utils.getDate(hc.date!!))

        if(hc.tripStartTime!!.compareTo(0)!=0)
        holder.time.setText(Utils.getTime(hc.tripStartTime!!)+ "-"+Utils.getTime(hc.tripEndTime!!))
        else
            holder.time.visibility=View.GONE

        if (!hc.mapImage.isNullOrEmpty()) {
            try
            {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.logo)
            requestOptions.error(R.drawable.logo)
            Glide.with(this.context).setDefaultRequestOptions(requestOptions).load(RydzApplication.BASEURLFORPHOTO +""+ hc.mapImage).into(holder.iv_map)
            }catch (e:Exception)
            {

            }
        }

        holder.price.setText("$ " + String.format("%1$.2f", hc.subtotalFare).replace(",", ".").toDouble())
        if (hc.status == 11) {
            holder.tv_ridestatus.setText(context.getString(R.string.canceled))
        } else
            holder.tv_ridestatus.setText(context.getString(R.string.completed))

        holder.cv_history.setOnClickListener {
            itemClick.onItemClick(position)

        }
    }

    override fun getItemCount(): Int {
        return pastTripsList.size
    }

    inner class MyViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val carName: TextView
        val date: TextView
        val time: TextView
        val price: TextView
        val cv_history: androidx.cardview.widget.CardView
        val tv_ridestatus: TextView
        val iv_map: ImageView

        init {
            carName = view.findViewById<View>(R.id.tv_carName) as TextView
            date = view.findViewById<View>(R.id.tv_date) as TextView
            time = view.findViewById<View>(R.id.tv_time) as TextView
            price = view.findViewById<View>(R.id.tv_cost) as TextView
            cv_history = view.findViewById<View>(R.id.cv_history) as androidx.cardview.widget.CardView
            tv_ridestatus = view.findViewById<View>(R.id.tv_ridestatus) as TextView
            iv_map = view.findViewById<View>(R.id.iv_map) as ImageView
        }
    }
}