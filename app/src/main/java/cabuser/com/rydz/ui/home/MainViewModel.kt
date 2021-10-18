package cabuser.com.rydz.ui.home

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.profile.CheckPhoneStatusModel
import cabuser.com.rydz.ui.profile.User
import cabuser.com.rydz.ui.ride.ScheduleRideRequest
import cabuser.com.rydz.util.common.SingleLiveEvent
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.get
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel(application: Application) : AndroidViewModel(application), Callback<CheckPhoneStatusModel> {

    var mProgress: SingleLiveEvent<Boolean>? = null

    var checkPhoneStatus: MutableLiveData<CheckPhoneStatusModel>? = null


    override fun onFailure(call: Call<CheckPhoneStatusModel>, t: Throwable) {
        mProgress?.value = false


    }

    override fun onResponse(call: Call<CheckPhoneStatusModel>, response: Response<CheckPhoneStatusModel>) {

        mProgress?.value = false
        checkPhoneStatus?.value = response.body()


    }


    companion object {
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun ImageView.setImageUrl(url: String?) {
            if (url != null && !url.isEmpty()) {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.group)
                requestOptions.error(R.drawable.group)

                Glide.with(this.context).setDefaultRequestOptions(requestOptions).load(url).into(this)
            }

        }
    }

    var activity: Context = application.applicationContext
    var userName: ObservableField<String>? = null
    var userRating: ObservableField<String>? = null
    var userProfilePicUrl: ObservableField<String>? = null


    init {
        userName = ObservableField("")
        userRating = ObservableField("")
        userProfilePicUrl = ObservableField("")
        mProgress = SingleLiveEvent<Boolean>()
        checkPhoneStatus = MutableLiveData<CheckPhoneStatusModel>()
    }

    fun onNavMenuCreated() {

        val prefs = PreferenceHelper.defaultPrefs(activity)
        val user_String = prefs[PreferenceHelper.Key.REGISTEREDUSER, ""]
        val gson = Gson()

        if (prefs.contains(PreferenceHelper.Key.REGISTEREDUSER) && gson.fromJson(user_String.toString(), User::class.java) as User != null) {

            val obj: User = gson.fromJson(user_String.toString(), User::class.java) as User

            val currentUserName = obj.firstName + " " + obj.lastName

            if (!TextUtils.isEmpty(currentUserName)) {
                userName?.set(currentUserName)
            } else {
                userName?.set("Guest")
            }

            val currentUserRating = obj.rating

            if (currentUserRating != null) {
                userRating?.set(currentUserRating.toString())
            } else {
                userRating?.set("0")
            }

            val profilePicUrl = RydzApplication.BASEURLFORPHOTO + obj.profilePic
            if (profilePicUrl != null && !TextUtils.isEmpty(profilePicUrl)) {
                userProfilePicUrl?.set(profilePicUrl)
            }


        }
    }


    fun scheduleRideRequest(scheduleRideRequest: ScheduleRideRequest) {
        Log.e("128", Gson().toJson(scheduleRideRequest))
        RydzApplication.getRetroApiClient().onBookScheduleRide(scheduleRideRequest).enqueue(this)

    }


}