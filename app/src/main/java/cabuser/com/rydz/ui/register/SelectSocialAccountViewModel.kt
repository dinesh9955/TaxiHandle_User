package cabuser.com.rydz.ui.register

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.util.common.SingleLiveEvent
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectSocialAccountViewModel (application: Application) : AndroidViewModel(application), Callback<RegistrationResponse> {

    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    var mProgress: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<RegistrationResponse>? = null


    init {

        btnSelected = ObservableBoolean(false)
        mProgress = SingleLiveEvent<Boolean>()
        userLogin = MutableLiveData<RegistrationResponse>()

    }


    override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(calpixabayl: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {

        val ggg:Gson = Gson()
        Log.e("social response", ggg.toJson(response.body()))
        mProgress?.value = false
        userLogin?.value = response.body()

    }


    //to check user's facebook account is already registered
    fun checkFacebookAccountAlreadyRegistered( facebookId:String, email:String)
    {
        mProgress?.value = true
        RydzApplication.getRetroApiClient().onSocialMediaLogin(RydzApplication.adminId, "", "", email
                , facebookId, RydzApplication.deviceToken.toString(), "Android", "","","", "1").enqueue(this)

    }

    //to check user's Google account is already registered
    fun checkGoogleAccountAlreadyRegistered( googleId:String, email:String)
    {
        mProgress?.value = true
        RydzApplication.getRetroApiClient().onGoogleLogin(RydzApplication.adminId, "", "", email
                , googleId, RydzApplication.deviceToken.toString(), "Android", "","","", "1").enqueue(this)

    }



    override fun onCleared() {
        super.onCleared()
    }


}