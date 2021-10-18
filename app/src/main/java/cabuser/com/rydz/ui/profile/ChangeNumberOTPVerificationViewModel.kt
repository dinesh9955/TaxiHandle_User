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

class ChangeNumberOTPVerificationViewModel(application: Application) : AndroidViewModel(application), Callback<ProfileResponse> {

    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    var fbObjet: FacebookEventObject? = null
    var mProgress: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<ProfileResponse>? = null
    var otpResponse: MutableLiveData<VerifySmsOtpResponse>? = null

    var strPhone = ""
    var CountryCode = ""

    //sinch verification
    public var mIsVerified: Boolean = false
    lateinit var otpView: OtpView
    var mVerification: Verification? = null

    init {
        btnSelected = ObservableBoolean(false)
        mProgress = SingleLiveEvent<Boolean>()
        userLogin = MutableLiveData<ProfileResponse>()
        otpResponse = MutableLiveData<VerifySmsOtpResponse>()

    }


    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
        mProgress?.value = false
        userLogin?.value = response.body()


    }


    fun onOTPSuccessfulVerification() {


        if (otpView.makeOTP().length == 4) {


            if (!otpView.makeOTP().isEmpty()) {
                if (mVerification != null) {
                    mProgress?.value = true
                    mVerification!!.verify(otpView.makeOTP())


                }
            }

        } else {
            Utils.showMessage(activity.getString(R.string.otp_validation), activity); }

    }

    //to check user's phone account is already registered
    fun changePhoneNumber() {


        if(!CountryCode.contains("+"))
        {
            CountryCode="+"+CountryCode

        }
        val updateProfileRequest = UpdateProfileRequest(RydzApplication.adminId, RydzApplication.user_obj!!.id, "", "", CountryCode, strPhone)
        RydzApplication.getRetroApiClient().onUpdateProfile(updateProfileRequest).enqueue(this)

    }





}