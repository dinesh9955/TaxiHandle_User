package cabuser.com.rydz.ui.ride


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import android.widget.ImageView
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RatingViewModel(application: Application) : AndroidViewModel(application), Callback<DriverRatingResponse> {


    companion object {
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun ImageView.setImageUrl(url: String?) {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.group)
            requestOptions.error(R.drawable.group)

            Glide.with(this.context).setDefaultRequestOptions(requestOptions).load(url).into(this)


        }
    }

    var activity: Context = application.applicationContext
    var review: ObservableField<String>? = null
    var driverName: ObservableField<String>? = null
    var driverRating: ObservableField<String>? = null
    var profilePhoto: ObservableField<String>?=null

    var rating: Float = 5.0f
    var driverId:String =""
    var bookingId:String =""

    var userLogin: MutableLiveData<DriverRatingResponse>? = null

    init {

        review = ObservableField("")
        driverName= ObservableField("")
        driverRating= ObservableField("")
        profilePhoto=ObservableField("")
        userLogin = MutableLiveData<DriverRatingResponse>()
    }


    override fun onFailure(call: Call<DriverRatingResponse>, t: Throwable) {

    }

    override fun onResponse(call: Call<DriverRatingResponse>, response: Response<DriverRatingResponse>) {

        userLogin?.value = response.body()
    }


    fun giveRating(review:String) {
//driver id is static for time being
        RydzApplication.getRetroApiClient().onRateDriver(driverId, RydzApplication.user_obj!!.id, bookingId, rating.toString(), review).enqueue(this);
    }




}







