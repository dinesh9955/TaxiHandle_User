package cabuser.com.rydz.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.databinding.ObservableBoolean
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.util.common.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhoneLoginViewModel(application: Application) : AndroidViewModel(application), Callback<RegistrationResponse> {

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

    override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {

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



    override fun onCleared() {
        super.onCleared()
    }


}