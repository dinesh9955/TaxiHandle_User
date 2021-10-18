package cabuser.com.rydz.ui.profile


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.util.common.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import cabuser.com.rydz.util.common.SingleLiveEvent as CommonSingleLiveEvent

class EditEmailViewModel(application: Application) : AndroidViewModel(application), Callback<ProfileResponse> {


    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    var email: ObservableField<String>? = null
    var mProgress: CommonSingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<ProfileResponse>? = null


    init
    {
        btnSelected = ObservableBoolean(false)
        mProgress = CommonSingleLiveEvent<Boolean>()
        email = ObservableField("")
        userLogin = MutableLiveData<ProfileResponse>()
    }


    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
        mProgress?.value = false
        userLogin?.value = response.body()


    }


    fun updateUserEmail() = if (!email!!.get().toString().isEmpty()) {
        if (Utils.isEmailValid(email!!.get().toString())) {
            btnSelected = ObservableBoolean(true)
            mProgress?.value = true
            val editEmailRequest:EditEmailRequest = EditEmailRequest(RydzApplication.adminId, RydzApplication.user_obj!!.id, email!!.get().toString())
            RydzApplication.getRetroApiClient().onUpdateEmail(editEmailRequest).enqueue(this)
        } else {
            Utils.showMessage(activity.getString(R.string.email_validation),activity.applicationContext)
        }

    } else {
        btnSelected = ObservableBoolean(true)
        mProgress?.value = true
        val editEmailRequest = EditEmailRequest(RydzApplication.adminId, RydzApplication.user_obj!!.id, email!!.get().toString())
        RydzApplication.getRetroApiClient().onUpdateEmail(editEmailRequest).enqueue(this)
    }

}







