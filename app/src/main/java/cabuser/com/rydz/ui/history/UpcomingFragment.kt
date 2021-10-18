package cabuser.com.rydz.ui.history


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import kotlinx.android.synthetic.main.fragment_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpcomingFragment : Fragment(), Callback<History> {

    private var pastTripsList: List<BookingHistory> = java.util.ArrayList()
    private var hcAdapter: ScheduleAdapter? = null

    override fun onFailure(call: Call<History>, t: Throwable) {
        t.message?.let { Log.e("upComing Failiure", it) }
    }

    override fun onResponse(call: Call<History>, response: Response<History>) {
        try {
            Log.e("upComing Failiure", "cvzcbc")

            if (response.body()!!.success!! && response.body()!!.bookingHistory != null) {
                pastTripsList = response.body()!!.bookingHistory!!
                if (pastTripsList.isNotEmpty()) {
                    hcAdapter = ScheduleAdapter(pastTripsList, activity!!)
                    recycler_view!!.adapter = hcAdapter

                } else {
                    recycler_view.visibility = View.GONE
                    ll_placeholder.visibility = View.VISIBLE
                }
            } else {
                recycler_view.visibility = View.GONE
                ll_placeholder.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val recyclerView = view!!.findViewById<RecyclerView>(R.id.recycler_view)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()

        return view
    }

    override fun onResume() {
        super.onResume()
        RydzApplication.getRetroApiClient().getscheduleTrips(RydzApplication.user_obj!!.id!!).enqueue(this)
    }
}
