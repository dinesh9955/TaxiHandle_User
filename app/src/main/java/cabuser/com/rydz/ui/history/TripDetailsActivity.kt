package cabuser.com.rydz.ui.history

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.RatingEvent
import cabuser.com.rydz.ui.settings.HelpActivity
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.common.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_trip_details.*
import kotlinx.android.synthetic.main.header_with_back.*
import org.greenrobot.eventbus.EventBus

/**
 * this class  shows trip details
 */
class TripDetailsActivity : BaseActivity() {

    private lateinit var bookingHistory: BookingHistory
    var viewmodel: TripRatingViewModel? = null
   var img=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_details)
        inits()
    }


    private fun inits() {

        tv_title.visibility = View.VISIBLE
        tv_title.setText(R.string.details)

        mProgress = CustomeProgressDialog(this@TripDetailsActivity)


        //Get parcelable data
        viewmodel = ViewModelProviders.of(this@TripDetailsActivity).get(TripRatingViewModel::class.java)



        viewmodel?.ratingResponse?.observe(this, Observer { user ->

            mProgress!!.dismiss()
            if (user.success!!) {

                ratingBar.rating = user.rating.rating.toFloat()
                if (user.rating.rating == 0.0)
                    ll_rateYourDriver.visibility = View.VISIBLE
                else
                    ll_rateYourDriver.visibility = View.GONE
                try {
                    if (user.rating != null) {
                        val ratingEvent: RatingEvent = RatingEvent(user.rating!!)
                        EventBus.getDefault().postSticky(ratingEvent)
                    }
                } catch (e: Exception) {

                }

            } else {

            }


        })

        bookingHistory = intent.getParcelableExtra("history")!! //auto converts using Generics
        tv_youRated.text = "${bookingHistory.driverId!!.firstName} ${bookingHistory.driverId!!.lastName}"
        tv_carName.text = "${bookingHistory.driverId!!.vehicleName}, ${bookingHistory.driverId!!.vehicleColor}"
        tv_date.text = Utils.getDate(bookingHistory.date!!)

        if(bookingHistory.tripStartTime!!.compareTo(0)!=0)
        tv_time.text = Utils.getTime(bookingHistory.tripStartTime!!) + " - " + Utils.getTime(bookingHistory.tripEndTime!!)
        else
        tv_time.visibility=View.GONE

        tv_cost.setText("$ " + String.format("%1$.2f", bookingHistory.subtotalFare!!).replace(",", ".").toDouble())
        tv_startPlace.text = bookingHistory.source!!.name
        tv_endPlace.text = bookingHistory.destination!!.name

        tv_tripFareValue.text = "$ " + String.format("%1$.2f", (bookingHistory.subtotalFare!! -  bookingHistory.tax!!)).replace(",", ".").toDouble()
        tv_taxValue.text = "$ " + String.format("%1$.2f", bookingHistory.tax!!).replace(",", ".").toDouble()
        tv_totalValue.text = "$ " + String.format("%1$.2f", bookingHistory.subtotalFare!!).replace(",", ".").toDouble()





        ratingBar.rating = bookingHistory.driverRating!!.toFloat()
        if ((RydzApplication.BASEURLFORPHOTO + bookingHistory.driverId!!.profilePic!!).isNotEmpty()) {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.group)
            requestOptions.error(R.drawable.group)

try {

            Glide.with(this@TripDetailsActivity).setDefaultRequestOptions(requestOptions).load(RydzApplication.BASEURLFORPHOTO + bookingHistory.driverId!!.profilePic).into(iv_profilePhoto)
}catch (e:Exception)
{

}
        }




        if (!bookingHistory.mapImage.isNullOrEmpty()) {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.logo)
            requestOptions.error(R.drawable.logo)
            img=RydzApplication.BASEURLFORPHOTO + bookingHistory.mapImage
            try {


            Glide.with(this).setDefaultRequestOptions(requestOptions).load(RydzApplication.BASEURLFORPHOTO + bookingHistory.mapImage).into(iv_map)
            }catch (e:Exception)
            {

            }
        }


        if (bookingHistory.status == 11) {
            tv_rideStatus.text = getString(R.string.canceled)
        }
        else {
            if (bookingHistory.driverRating!! == 0.0)
                ll_rateYourDriver.visibility = View.VISIBLE
            else
                ll_rateYourDriver.visibility = View.GONE
            tv_rideStatus.text = getString(R.string.completed)

            if(bookingHistory.refund!= null && bookingHistory?.refund?.status.equals("noRequest"))
            {
                tv_refund.visibility=View.VISIBLE
            }
            else
            {
                tv_refund.visibility=View.GONE
                tv_refundstatus.visibility=View.VISIBLE
                tv_refundstatus.text=" Refund Requested ("+bookingHistory?.refund?.status+")"
            }
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()

            R.id.tv_help -> {
                startActivity(Intent(this, HelpActivity::class.java))
            }
            R.id.ll_rateYourDriver -> {
                ratingPopUp()
            }
            R.id.iv_map -> {
                showZoomableImage(img)
            }
            R.id.tv_refund -> {
                startActivityForResult(Intent(this, RefundActvity::class.java).apply {  putExtra("bookingId",bookingHistory.id ) },15)
            }
        }
    }


    /**
     * To rate  driver
     */
    private fun ratingPopUp() {


        val ratetDial = Dialog(this@TripDetailsActivity)
        ratetDial.requestWindowFeature(Window.FEATURE_NO_TITLE)
        ratetDial.setContentView(R.layout.rating_dialog)
        val btn_skipRating = ratetDial.findViewById<Button>(R.id.btn_skipRating)
        val btn_submit = ratetDial.findViewById<Button>(R.id.btn_submit)
        val tvDrivername = ratetDial.findViewById<TextView>(R.id.tv_driverName)
        val tv_driverRating = ratetDial.findViewById<TextView>(R.id.tv_driverRating)
        val tv_rideDate = ratetDial.findViewById<TextView>(R.id.tv_rideDate)
        val tv_rideCost = ratetDial.findViewById<TextView>(R.id.tv_rideCost)
        val ratingBar = ratetDial.findViewById<RatingBar>(R.id.ratingBar)
        val iv_driverPhoto = ratetDial.findViewById<ImageView>(R.id.iv_driverPhoto)
        val edt_review = ratetDial.findViewById<EditText>(R.id.edt_review)
        tvDrivername.text = bookingHistory.driverId!!.firstName + " " + bookingHistory.driverId!!.lastName

        if (bookingHistory.driverRating == 0.0)
            tv_driverRating.visibility = View.GONE
        else {
            tv_driverRating.visibility = View.VISIBLE
            tv_driverRating.text = (bookingHistory.driverRating.toString())
        }


        tv_rideDate.text = Utils.getTime(bookingHistory.tripStartTime!!)
        tv_rideCost.text = "$ " +  String.format("%1$.2f", bookingHistory.subtotalFare)
        try {


        val requestOptions = RequestOptions()
        requestOptions.placeholder(R.drawable.group)
        requestOptions.error(R.drawable.group)
        Glide.with(this@TripDetailsActivity).setDefaultRequestOptions(requestOptions).load(RydzApplication.BASEURLFORPHOTO + bookingHistory.driverId!!.profilePic).into(iv_driverPhoto)
        }catch (e:Exception)
        {

        }
        btn_skipRating.setOnClickListener { ratetDial.dismiss() }
        btn_submit.setOnClickListener {
            mProgress!!.show()
            viewmodel!!.giveRating(edt_review.text.toString().trim(), ratingBar.rating, bookingHistory.driverId!!.id!!, bookingHistory.id!!)
            ratetDial.dismiss()

        }
        ratetDial.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==15 && resultCode == Activity.RESULT_OK)
        {
            tv_refund.visibility=View.GONE
            tv_refundstatus.visibility=View.VISIBLE
            tv_refundstatus.text= "Refund Requested"
        }
    }
}
