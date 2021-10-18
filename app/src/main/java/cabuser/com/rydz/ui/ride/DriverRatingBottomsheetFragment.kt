package cabuser.com.rydz.ui.ride

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.home.MainActivity
import cabuser.com.rydz.util.common.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_trip_details.*
import kotlinx.android.synthetic.main.rating_dialog.*
import kotlinx.android.synthetic.main.rating_dialog.ratingBar

/**
 * This class main purpose is to show driver's rating dialog
 */
class DriverRatingBottomsheetFragment : BottomSheetDialogFragment() {


    var style = androidx.fragment.app.DialogFragment.STYLE_NO_TITLE
    var theme1 = R.style.MyDialog
    var viewmodel: RatingViewModel? = null
    var TAG="DriverRatingBottomsheetFragment"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(style, theme1);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return     inflater.inflate( R.layout.rating_dialog, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        inits()
    }

    //intialization of views & clicks
    private fun inits() {

        viewmodel = ViewModelProviders.of(activity!!).get(RatingViewModel::class.java)
        Log.e(TAG,"outside inits")



        if ((activity as MainActivity).sendBookResponse != null && (activity as MainActivity).sendBookResponse!!.booking!=null) {
            viewmodel!!.driverId = (activity as MainActivity).sendBookResponse!!.booking!!.driverId!!.id.toString()
            viewmodel!!.bookingId = (activity as MainActivity).sendBookResponse!!.booking!!.id.toString()
            try {
                tv_driverName.text=((activity as MainActivity).sendBookResponse!!.booking!!.driverId!!.firstName) +" "+((activity as MainActivity).sendBookResponse!!.booking!!.driverId!!.lastName)

            }catch (e:Exception)
            {

            }
            try {
                if((activity as MainActivity).sendBookResponse!!.booking!!.driverId!!.totalDrivingRating!=0.0)
                tv_driverRating.text=((activity as MainActivity).sendBookResponse!!.booking!!.driverId!!.totalDrivingRating.toString())
                    else
                    tv_driverRating.text=""

            }catch (e:Exception)
            {

            }


            try {


                tv_rideCost.text="$ "+   String.format("%1$.2f", (activity as MainActivity).sendBookResponse!!.booking!!.subtotalFare).replace(",", ".").toDouble()

            }catch (e:Exception)
            {

            }
            try {
                tv_date.setText(Utils.getDate((activity as MainActivity).sendBookResponse!!.booking!!.date!!))
            }catch (e:Exception)
            {

            }
            try {
                tv_time.setText(Utils.getTime((activity as MainActivity).sendBookResponse!!.booking!!.tripStartTime!!))
            }
            catch (e:java.lang.Exception)
            {

            }
            try {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.group)
                requestOptions.error(R.drawable.group)
                Glide.with(activity!!).setDefaultRequestOptions(requestOptions).load(RydzApplication.BASEURLFORPHOTO + (activity as MainActivity).sendBookResponse!!.booking!!.driverId!!.profilePic).into(iv_driverPhoto)

            }catch (e:java.lang.Exception)
            {

            }
               }



        ratingBar.setOnRatingBarChangeListener { ratingBar: RatingBar, fl: Float, b: Boolean ->
            viewmodel?.rating = fl
        }

        btn_skipRating.setOnClickListener {
            (activity as MainActivity).userSkipRating_socket(viewmodel!!.bookingId.toString())
            (activity as MainActivity).isbottomsheetFragment=false
            dismiss()

        }

        btn_submit.setOnClickListener { v ->
            viewmodel!!.giveRating(edt_review.text.toString().trim())
            dismiss()
        }

        viewmodel?.userLogin?.observe(this, Observer { user ->
            Log.e("130",user.message)
            if (user.success!!) {

                Utils.showMessage(user.message, activity!!)
            } else {
                Utils.showMessage(user.message, activity!!)
            }
            (activity as MainActivity).isbottomsheetFragment=false

            (activity as MainActivity).clearViews()

        })
    }


    override fun onDestroy() {
        super.onDestroy()

        (activity as MainActivity).isbottomsheetFragment=false


        (activity as MainActivity).checkRideAfterRating()
    }

}