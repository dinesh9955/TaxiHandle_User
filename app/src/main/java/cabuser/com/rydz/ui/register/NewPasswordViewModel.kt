package cabuser.com.rydz.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.databinding.ObservableField
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.profile.CheckPhoneStatusModel
import cabuser.com.rydz.util.common.SingleLiveEvent
import cabuser.com.rydz.util.common.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewPasswordViewModel(application: Application) : AndroidViewModel(application), Callback<CheckPhoneStatusModel> {


    var activity: Context = application.applicationContext
    var confirmPassword: ObservableField<String>? = null
    var newPassword: ObservableField<String>? = null
    var mProgress: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<CheckPhoneStatusModel>? = null

    var countryCode = ""
    var phoneNumber = ""
    var email = ""
    var type = ""

    init {

        mProgress = SingleLiveEvent<Boolean>()
        confirmPassword = ObservableField("")
        newPassword = ObservableField("")
        userLogin = MutableLiveData<CheckPhoneStatusModel>()
    }


    override fun onFailure(call: Call<CheckPhoneStatusModel>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<CheckPhoneStatusModel>, response: Response<CheckPhoneStatusModel>) {
        mProgress?.value = false
        userLogin?.value = response.body()

    }


    fun changePassword() {

        if (validate()) {

            mProgress?.value = true
            if (type.equals("KEY_PHONE"))
                RydzApplication.getRetroApiClient().onForgotChangePassword(RydzApplication.adminId, "", "+"+countryCode, newPassword!!.get().toString(), phoneNumber).enqueue(this)
            else
                RydzApplication.getRetroApiClient().onForgotChangePassword(RydzApplication.adminId, email, "", newPassword!!.get().toString(), "").enqueue(this)

        }
    }

    override fun onCleared() {
        super.onCleared()
    }


    private fun validate(): Boolean {
        if (newPassword!!.get().toString().trim().isEmpty() || newPassword!!.get().toString().trim().length < 8) {

            Utils.showMessage(activity.getString(R.string.newpwd_validation), activity.applicationContext)
            return false

        } else if (confirmPassword!!.get().toString().trim().isEmpty() || confirmPassword!!.get().toString().trim().length < 8) {

            Utils.showMessage(activity.getString(R.string.confirmpwd_validation), activity.applicationContext)
            return false

        } else if (confirmPassword!!.get().toString().trim().isEmpty() || confirmPassword!!.get().toString().trim().length < 8 || !newPassword!!.get().toString().equals(confirmPassword!!.get().toString())) {

            Utils.showMessage(activity.getString(R.string.matchpwd_validation), activity.applicationContext)
            return false

        }

        return true

    }


}







