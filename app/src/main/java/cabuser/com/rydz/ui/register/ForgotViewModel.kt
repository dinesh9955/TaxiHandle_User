package cabuser.com.rydz.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.databinding.ObservableBoolean
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.data.eventbus.FacebookEventObject
import cabuser.com.rydz.ui.profile.CheckPhoneStatusModel
import cabuser.com.rydz.util.common.OtpView
import cabuser.com.rydz.util.common.SingleLiveEvent
import com.sinch.verification.Verification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotViewModel(application: Application) : AndroidViewModel(application), Callback<CheckPhoneStatusModel> {

    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    var fb_Objet: FacebookEventObject? = null
    var mProgress: SingleLiveEvent<Boolean>? = null
    var checkPhoneStatus: MutableLiveData<CheckPhoneStatusModel>? = null
    var otp = ""



    //sinch verification
    public var mIsVerified: Boolean = false
    lateinit var otpView: OtpView
    public var mVerification: Verification? = null

    init {
        btnSelected = ObservableBoolean(false)
        mProgress = SingleLiveEvent<Boolean>()
        checkPhoneStatus = MutableLiveData<CheckPhoneStatusModel>()
    }


    override fun onFailure(call: Call<CheckPhoneStatusModel>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<CheckPhoneStatusModel>, response: Response<CheckPhoneStatusModel>) {
        mProgress?.value = false
        checkPhoneStatus?.value = response.body()


    }




    //to check user's phone account is already registered
    fun checkPhonenumbeIsRegistered(countryCode: String, phone: String) {
        RydzApplication.getRetroApiClient().onChkregstatus(RydzApplication.adminId, "",countryCode,phone!!.toString()).enqueue(this)

    }


    fun sendMailOtp(email: String) {
        RydzApplication.getRetroApiClient().onSendmailOtp(RydzApplication.adminId, email).enqueue(this)

    }




    override fun onCleared() {
        super.onCleared()
    }
}