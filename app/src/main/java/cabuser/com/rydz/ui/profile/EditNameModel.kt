package cabuser.com.rydz.ui.profile

import android.app.Application
import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.util.common.SingleLiveEvent
import cabuser.com.rydz.util.common.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditNameModel(application: Application) : AndroidViewModel(application), Callback<ProfileResponse> {


    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    var firstName: ObservableField<String>? = null
    var lastName: ObservableField<String>? = null
    var mProgress: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<ProfileResponse>? = null


    init {

        btnSelected = ObservableBoolean(false)
        mProgress = SingleLiveEvent<Boolean>()
        firstName = ObservableField("")
        lastName = ObservableField("")
        userLogin = MutableLiveData<ProfileResponse>()

    }


    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
        mProgress?.value = false
        userLogin?.value = response.body()
    }


    fun updateUserInfo() {
        if (validate()) {
            btnSelected = ObservableBoolean(true)
            mProgress?.value = true
            val updateProfileRequest: UpdateProfileRequest = UpdateProfileRequest(RydzApplication.adminId, RydzApplication.user_obj!!.id, firstName!!.get().toString(), lastName!!.get().toString(), ""
                    , "")
            RydzApplication.getRetroApiClient().onUpdateProfile(updateProfileRequest).enqueue(this)
        }


    }


    private fun validate(): Boolean {

        if (firstName!!.get().toString().isEmpty()) {


            Utils.showMessage(activity.getString(R.string.firstname_validation), activity.applicationContext)
            return false
        } else if (lastName!!.get().toString().isEmpty()) {
            Utils.showMessage(activity.getString(R.string.lastname_validation), activity.applicationContext)
            return false
        }
        return true

    }


}







