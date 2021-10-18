package cabuser.com.rydz.ui.history


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.common.OnItemClickListener
import kotlinx.android.synthetic.main.fragment_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HistoryFragment : Fragment(), Callback<History>, OnItemClickListener {

    private var pastTripsList: List<BookingHistory> = java.util.ArrayList()
    private var hcAdapter: HistoryAdapter? = null
    private var page: Int = 0
    private var selectedPos = -1
    var mProgress: CustomeProgressDialog? = null


    override fun onFailure(call: Call<History>, t: Throwable) {
        mProgress!!.dismiss()

    }

    override fun onResponse(call: Call<History>, response: Response<History>) {
        try {
            mProgress!!.dismiss()
            if (response.body()!!.success!! && response.body()!!.bookingHistory != null) {


                pastTripsList = response.body()!!.bookingHistory!!
                // Log.e("45", pastTripsList[0].mapImage)
                if (pastTripsList.isNotEmpty()) {
                    hcAdapter = HistoryAdapter(pastTripsList, activity!!, this)
                    recycler_view!!.adapter = hcAdapter

                } else {
                    recycler_view.visibility = View.GONE
                    ll_placeholder.visibility = View.VISIBLE
                }
            } else {
                recycler_view.visibility = View.GONE
                ll_placeholder.visibility = View.VISIBLE
            }
        }catch ( e : Exception)
        {

        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recycler_view)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        mProgress = CustomeProgressDialog(context!!)
        mProgress!!.show()
        return view
    }


    override fun onItemClick(pos: Int) {
        selectedPos = pos
        val historyIntent = Intent(activity, TripDetailsActivity::class.java).apply {
            putExtra("history", pastTripsList[pos])
        }
        activity!!.startActivity(historyIntent)
    }


    override fun onResume() {
        super.onResume()
        RydzApplication.getRetroApiClient().getPastTrips(RydzApplication.user_obj!!.id!!, page).enqueue(this)

        /* try {
             if (EventBus.getDefault().getStickyEvent(RatingEvent::class.java) != null) {
                 if (selectedPos != -1) {
                     if (hcAdapter != null) {
                         pastTripsList[selectedPos].driverRating = EventBus.getDefault().getStickyEvent(RatingEvent::class.java).rating.rating
                         hcAdapter!!.notifyDataSetChanged()
                     }
                 }
             }
         } catch (e: Exception) {

         }
         selectedPos = -1
         try {
             EventBus.getDefault().removeStickyEvent(RatingEvent::class.java)
         } catch (e: Exception) {

         }*/
    }


}
