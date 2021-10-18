package cabuser.com.rydz.ui.profile

import android.app.Application
import android.content.Context
import android.widget.Toast
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

class EditNumberViewModel(application: Application) : AndroidViewModel(application), Callback<CheckPhoneStatusModel> {


    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    var countryCode: ObservableField<String>? = null
    var phoneNumber: ObservableField<String>? = null
    var mProgress: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<CheckPhoneStatusModel>? = null

    init {

        btnSelected = ObservableBoolean(false)
        mProgress = SingleLiveEvent()
        countryCode = ObservableField("")
        phoneNumber = ObservableField("")
        userLogin = MutableLiveData()



    }


    override fun onFailure(call: Call<CheckPhoneStatusModel>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<CheckPhoneStatusModel>, response: Response<CheckPhoneStatusModel>) {

        mProgress?.value = false
        userLogin?.value = response.body()


    }


    fun updateUserInfo() {
        if (validate()) {
            if (!("+"+countryCode!!.get().toString().trim() + phoneNumber!!.get().toString().trim()).equals((RydzApplication.user_obj!!.countryCode + RydzApplication.user_obj!!.phone), false)) {
                btnSelected = ObservableBoolean(true)
                mProgress?.value = true

                val editNumberRequest = EditNumberRequest(RydzApplication.adminId, RydzApplication.user_obj!!.id, countryCode!!.get().toString(), phoneNumber!!.get().toString())
                RydzApplication.getRetroApiClient().onCheckPhoneStatus(editNumberRequest).enqueue(this)
            } else {
                Toast.makeText(activity.applicationContext, activity.getString(R.string.diff_number), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validate(): Boolean {

        if (phoneNumber!!.get().toString().trim().isEmpty() || phoneNumber!!.get().toString().length < 8) {


            Utils.showMessage(activity.getString(R.string.phone_validation), activity.applicationContext)
            return false
        }
        return true

    }


}







