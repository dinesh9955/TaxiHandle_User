package cabuser.com.rydz.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.history.History
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.header_with_back.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * HelpActivity class shows info about last trip and option to report issue
 */
class HelpActivity : BaseActivity(), Callback<History> {

    var bookingId=""
    override fun onFailure(call: Call<History>, t: Throwable) {
        hideProgress()

    }

    override fun onResponse(call: Call<History>, response: Response<History>) {


        hideProgress()

        if (response != null && response.body()!!.success!!) {
            if (response.body()!!.bookingHistory != null && response.body()!!.bookingHistory!!.size > 0) {
                sv_help.visibility = View.VISIBLE

                val hc = response.body()!!.bookingHistory!![0]
                bookingId=hc.id!!
                tv_name.text = hc.driverId!!.manufacturerName + " " + hc.driverId!!.vehicleName
                tv_date.text = Utils.getDate(hc.date!!)
                tv_time.text = Utils.getTime(hc.tripStartTime!!
                )
                tv_cost.text = "$ " + String.format("%1$.2f", hc.fare).replace(",", ".").toDouble()
                try {
                    if ((RydzApplication.BASEURL + hc.driverId!!.profilePic) != null && !(RydzApplication.BASEURL + hc.driverId!!.profilePic).isEmpty()) {
                        val requestOptions = RequestOptions()
                        requestOptions.placeholder(R.drawable.group)
                        requestOptions.error(R.drawable.group)
                        Glide.with(this@HelpActivity).setDefaultRequestOptions(requestOptions).load(RydzApplication.BASEURLFORPHOTO + hc.driverId!!.profilePic).into(iv_profile)
                    }

                    if (!hc.mapImage.isNullOrEmpty()) {
                        val requestOptions = RequestOptions()
                        requestOptions.placeholder(R.drawable.logo)
                        requestOptions.error(R.drawable.logo)
                        Glide.with(this@HelpActivity).setDefaultRequestOptions(requestOptions).load(RydzApplication.BASEURLFORPHOTO + hc.mapImage).into(iv_map)
                    }
                } catch (e: Exception) {

                }


            }

        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        inits()

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish() }
            R.id.tv_reportIssue -> {
                startActivity(Intent(this, SupportActivity::class.java).apply {
                  putExtra("bookingId",bookingId)
                })
            }
        }
    }


    private fun inits() {

        tv_title.visibility = View.VISIBLE
        tv_title.setText(R.string.help)

        showProgress("")
        RydzApplication.getRetroApiClient().getPastTrips(RydzApplication.user_obj!!.id!!, 0).enqueue(this)
    }


}
