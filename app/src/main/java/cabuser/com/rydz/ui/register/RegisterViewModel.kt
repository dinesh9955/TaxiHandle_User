package cabuser.com.rydz.ui.register


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.util.common.SingleLiveEvent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(application: Application) : AndroidViewModel(application), Callback<RegistrationResponse> {


    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    var password: ObservableField<String>? = null
    var mProgress: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<RegistrationResponse>? = null


    init {

        btnSelected = ObservableBoolean(false)
        mProgress = SingleLiveEvent<Boolean>()
        password = ObservableField("")
        userLogin = MutableLiveData<RegistrationResponse>()
    }


    override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
        mProgress?.value = false
        userLogin?.value = response.body()


    }


    fun registerUser() {

        /*btnSelected = ObservableBoolean(true)
        if(btnSelected!!.get()) {*/
            if (RydzApplication.tempuser_obj != null) {

                mProgress?.value = true
                var registrationRequest = RegistrationRequest(RydzApplication.adminId, RydzApplication.tempuser_obj!!.firstName, RydzApplication.tempuser_obj!!.lastName, RydzApplication.tempuser_obj!!.email, RydzApplication.tempuser_obj!!.password!!, RydzApplication.tempuser_obj!!.countryCode!!, RydzApplication.tempuser_obj!!.phone!!, RydzApplication.deviceToken.toString(), "Android",RydzApplication.tempuser_obj!!.isSubscribed,RydzApplication.tempuser_obj!!.company)
                RydzApplication.getRetroApiClient().onRegisteration(registrationRequest).enqueue(this)
            }
       /* }
        else
        {
            Toast.makeText(activity, R.string.strongpwd_validation,Toast.LENGTH_SHORT).show()
        }*/

    }


    override fun onCleared() {
        super.onCleared()
    }
}







