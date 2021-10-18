package cabuser.com.rydz.ui.history


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.ui.ride.DriverRatingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TripRatingViewModel(application: Application) : AndroidViewModel(application), Callback<DriverRatingResponse> {


     var ratingResponse: MutableLiveData<DriverRatingResponse>? = null
     var refundResponse: MutableLiveData<RefundResponse>? = null

    init {
        ratingResponse = MutableLiveData<DriverRatingResponse>()
        refundResponse = MutableLiveData<RefundResponse>()
    }


    override fun onFailure(call: Call<DriverRatingResponse>, t: Throwable) {

    }

    override fun onResponse(call: Call<DriverRatingResponse>, response: Response<DriverRatingResponse>) {

        ratingResponse?.value = response.body()
    }


    fun giveRating(review: String, rating: Float, driverId: String, bookingId: String) {
        RydzApplication.getRetroApiClient().onRateDriver(driverId, RydzApplication.user_obj!!.id, bookingId, rating.toString(), review).enqueue(this)
    }



    fun onRefundFare(booking : String,reason: String , desc : String) {

        RydzApplication.getRetroApiClient().refundRequest(booking,reason,desc).enqueue(object : Callback<RefundResponse> {
            override fun onFailure(call: Call<RefundResponse>, t: Throwable) {


            }

            override fun onResponse(call: Call<RefundResponse>, response: Response<RefundResponse>) {


                try {
                    refundResponse?.value = response.body()
                } catch (e: Exception) {

                }

            }
        })

    }


}


