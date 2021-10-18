package cabuser.com.rydz.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.databinding.ObservableField
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.util.common.SingleLiveEvent
import cabuser.com.rydz.util.common.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditPasswordViewModel(application: Application) : AndroidViewModel(application), Callback<ProfileResponse> {


    var activity: Context = application.applicationContext
    var oldPassword: ObservableField<String>? = null
    var confirmPassword: ObservableField<String>? = null
    var newPassword: ObservableField<String>? = null
    var mProgress: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<ProfileResponse>? = null


    init
    {

        mProgress = SingleLiveEvent<Boolean>()
        oldPassword = ObservableField("")
        confirmPassword = ObservableField("")
        newPassword = ObservableField("")
        userLogin = MutableLiveData<ProfileResponse>()
    }


    override fun onFailure(call: Call<ProfileResponse>, t: Throwable)
    {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>)
    {
        mProgress?.value = false
        userLogin?.value = response.body()

    }





    fun changePassword()
    {
        if(validate()) {

            mProgress?.value = true
            val editPasswordRequest:EditPasswordRequest = EditPasswordRequest(RydzApplication.user_obj!!.id, oldPassword!!.get().toString(), newPassword!!.get().toString())
            RydzApplication.getRetroApiClient().onChangePassword(editPasswordRequest).enqueue(this)

        } }
    override fun onCleared() {
        super.onCleared()
    }

    private fun validate(): Boolean {

        if (oldPassword!!.get().toString().trim().isEmpty() ) {

            Utils.showMessage(activity.getString(R.string.oldpwd_validation), activity.applicationContext)
            return false

        }else  if (newPassword!!.get().toString().trim().isEmpty() || newPassword!!.get().toString().trim().length<8 ) {

            Utils.showMessage(activity.getString(R.string.newpwd_validation), activity.applicationContext)
            return false

        } else  if (confirmPassword!!.get().toString().trim().isEmpty() || confirmPassword!!.get().toString().trim().length<8 ) {

            Utils.showMessage(activity.getString(R.string.confirmpwd_validation), activity.applicationContext)
            return false

        }
        else  if (confirmPassword!!.get().toString().trim().isEmpty() || confirmPassword!!.get().toString().trim().length<8 || !newPassword!!.get().toString().equals(confirmPassword!!.get().toString())) {

            Utils.showMessage(activity.getString(R.string.matchpwd_validation), activity.applicationContext)
            return false

        }

        return true

    }


}







