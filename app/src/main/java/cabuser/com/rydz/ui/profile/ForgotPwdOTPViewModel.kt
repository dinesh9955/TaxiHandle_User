package cabuser.com.rydz.ui.profile


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Context
import androidx.databinding.ObservableBoolean
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.data.eventbus.FacebookEventObject
import cabuser.com.rydz.ui.home.VerifySmsOtpResponse
import cabuser.com.rydz.util.common.OtpView
import cabuser.com.rydz.util.common.SingleLiveEvent
import cabuser.com.rydz.util.common.Utils
import com.sinch.verification.Verification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPwdOTPViewModel(application: Application) : AndroidViewModel(application), Callback<CheckPhoneStatusModel> {

    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    var fb_Objet: FacebookEventObject? = null
    var mProgress: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<CheckPhoneStatusModel>? = null
    var otpResponse: MutableLiveData<CheckPhoneStatusModel>? = null
    var phoneOtpResponse: MutableLiveData<VerifySmsOtpResponse>? = null


    var otp = ""
    var str_phone = ""
    var email = ""
    var CountryCode = ""
    var type = ""

    //sinch verification
    public var mIsVerified: Boolean = false
    lateinit var otpView: OtpView
    public var mVerification: Verification? = null

    init {
        btnSelected = ObservableBoolean(false)
        mProgress = SingleLiveEvent<Boolean>()
        userLogin = MutableLiveData<CheckPhoneStatusModel>()
        otpResponse = MutableLiveData<CheckPhoneStatusModel>()
        phoneOtpResponse= MutableLiveData<VerifySmsOtpResponse>()
    }


    override fun onFailure(call: Call<CheckPhoneStatusModel>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<CheckPhoneStatusModel>, response: Response<CheckPhoneStatusModel>) {

        mProgress?.value = false
        userLogin?.value = response.body()


    }


   public fun onOTPSuccessfulVerification() {


        if (otpView.makeOTP().length == 4) {


            if (!otpView.makeOTP().isEmpty()) {

                if (!type.equals("KEY_EMAIL")) {

                    if (mVerification != null) {
                        mProgress?.value = true
                        mVerification!!.verify(otpView.makeOTP())

                    }


                } else {
                    RydzApplication.getRetroApiClient().onVerifyOtp(RydzApplication.adminId, email,otpView.makeOTP()).enqueue(this)
                }

            }


        } else {
            Utils.showMessage(activity.getString(R.string.otp_validation), activity); }

    }


    //to check user's phone account is already registered
    fun sendMailOtp(email: String) {
        RydzApplication.getRetroApiClient().onSendmailOtp(RydzApplication.adminId, email).enqueue(object :Callback<CheckPhoneStatusModel>
        {
            override fun onFailure(call: Call<CheckPhoneStatusModel>, t: Throwable) {
                mProgress?.value = false
            }

            override fun onResponse(call: Call<CheckPhoneStatusModel>, response: Response<CheckPhoneStatusModel>) {
                mProgress?.value = false
                otpResponse?.value = response.body()
            }

        })

    }




}
