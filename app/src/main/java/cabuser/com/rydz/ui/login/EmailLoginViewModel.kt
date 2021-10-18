package cabuser.com.rydz.ui.login


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.ui.profile.ProfileResponse
import cabuser.com.rydz.util.common.SingleLiveEvent
import cabuser.com.rydz.util.common.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailLoginViewModel(application: Application) : AndroidViewModel(application), Callback<ProfileResponse> {
    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
        mProgress?.value = false
        userLogin?.value = response.body()    }

    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    var email: ObservableField<String>? = null
    var password: ObservableField<String>? = null
    var mProgress: SingleLiveEvent<Boolean>? = null

    var userLogin: MutableLiveData<ProfileResponse>? = null


    init {

        btnSelected = ObservableBoolean(false)
        mProgress = SingleLiveEvent<Boolean>()
        email = ObservableField("")
        password = ObservableField("")
        userLogin = MutableLiveData<ProfileResponse>()

    }


    override fun onCleared() {
        super.onCleared()
    }

     fun onNext()
    {
        if(validate())
        {
            mProgress?.value = true
            RydzApplication.getRetroApiClient().onEmailLogin(RydzApplication.adminId,email!!.get().toString(),password!!.get().toString(),RydzApplication.deviceToken!!,"Android").enqueue(this)
        }
    }



    private fun validate(): Boolean {

        if (email!!.get().toString().trim().isEmpty() || !Utils.isEmailValid(email!!.get().toString()) ) {

            Utils.showMessage(activity.getString(R.string.email_validation), activity.applicationContext)
            return false

        }else  if (password!!.get().toString().trim().isEmpty() ) {

            Utils.showMessage(activity.getString(R.string.pwd_validation), activity.applicationContext)
            return false

        }

        return true

    }


}