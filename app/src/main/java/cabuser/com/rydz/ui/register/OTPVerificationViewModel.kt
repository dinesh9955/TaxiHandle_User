package cabuser.com.rydz.ui.register

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.FacebookEventObject
import cabuser.com.rydz.ui.home.SendOtpRequest
import cabuser.com.rydz.ui.home.SendSmsOtpResponse
import cabuser.com.rydz.ui.home.VerifyOtpRequest
import cabuser.com.rydz.ui.home.VerifySmsOtpResponse
import cabuser.com.rydz.util.common.OtpView
import cabuser.com.rydz.util.common.SingleLiveEvent
import cabuser.com.rydz.util.common.Utils
import com.google.gson.Gson
import com.sinch.verification.Verification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTPVerificationViewModel(application: Application) : AndroidViewModel(application), Callback<RegistrationResponse> {

    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    var fb_Objet: FacebookEventObject? = null
    var mProgress: SingleLiveEvent<Boolean>? = null
    var userLogin: MutableLiveData<RegistrationResponse>? = null
    var otpResponse: MutableLiveData<VerifySmsOtpResponse>? = null

    var otp = ""
    var str_NavigationFrom = ""
    var str_phone = ""
    var CountryCode = ""


    //sinch verification
    var mIsVerified: Boolean = false
    lateinit var otpView: OtpView
    var mVerification: Verification? = null

    init {
        btnSelected = ObservableBoolean(false)
        mProgress = SingleLiveEvent<Boolean>()
        userLogin = MutableLiveData<RegistrationResponse>()
        otpResponse = MutableLiveData<VerifySmsOtpResponse>()

    }


    override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
        Log.e("loginResponse ViewMOdel", "" + t.message)

        mProgress?.value = false
    }

    override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
        Log.e("loginResponse ViewMOdel", Gson().toJson(response.body()))
        mProgress?.value = false
        userLogin?.value = response.body()
    }


    fun onLoginSuccess() {

        if (otpView.makeOTP().length == 4) {

            if (!otpView.makeOTP().isEmpty()) {

                mProgress?.value = true

                var verifyOtpRequest = VerifyOtpRequest()

                if (CountryCode.toString().contains("+")) {
                    verifyOtpRequest.countryCode = CountryCode.toString().trim()

                } else {
                    verifyOtpRequest.countryCode = "+" + CountryCode.toString().trim()

                }
                verifyOtpRequest.phone = str_phone.toString().trim()
                verifyOtpRequest.otp = otpView.makeOTP()
                verifyOtpRequest.role = "user"
                // verifySmsOtp(verifyOtpRequest)


                mVerification!!.verify(otpView!!.makeOTP())

            }

        } else {

            Utils.showMessage(activity.getString(R.string.otp_validation), activity)
        }



    }

    //to check user's phone account is already registered
    fun checkPhonenumberIsAlreadyRegistered(countryCode: String, phone: String) {

        Log.e("INSIDE", "checkPhonenumberIsAlreadyRegistered")

        var userCheckRequest = UserCheckRequest(RydzApplication.adminId, countryCode, phone, RydzApplication.deviceToken.toString(), "Android")
        RydzApplication.getRetroApiClient().onPhoneLogin(userCheckRequest).enqueue(this)
    }


    fun socialMediaLogin() {
        mProgress?.value = true

        if (SelectSocialAccount.isGoogle) {

            val request: SocialMedia_GOOGLE_RegistrationRequest = SocialMedia_GOOGLE_RegistrationRequest(RydzApplication.adminId, fb_Objet!!.firstName, fb_Objet!!.lastName, fb_Objet!!.email
                    , fb_Objet!!.fbId, RydzApplication.deviceToken.toString(), "Android", fb_Objet!!.phonenumber, fb_Objet!!.countrycode, "", "1")

            RydzApplication.getRetroApiClient().onSocialMedia_GOOGLE_Registeration(request).enqueue(this)

        } else {
            val request: SocialMedia_FB_RegistrationRequest = SocialMedia_FB_RegistrationRequest(RydzApplication.adminId, fb_Objet!!.firstName, fb_Objet!!.lastName, fb_Objet!!.email
                    , fb_Objet!!.fbId, RydzApplication.deviceToken.toString(), "Android", fb_Objet!!.phonenumber, fb_Objet!!.countrycode, "", "1")

            RydzApplication.getRetroApiClient().onSocialMedia_FB_Registeration(request).enqueue(this)
        }
    }


    fun getSmsOtp(sendOtpRequest: SendOtpRequest) {

        Log.e("139",sendOtpRequest.toString())
        RydzApplication.getRetroApiClient().sendOtp(sendOtpRequest).enqueue(object : Callback<SendSmsOtpResponse> {
            override fun onFailure(call: Call<SendSmsOtpResponse>, t: Throwable) {
                mProgress?.value = false
                Utils.showMessage(activity.getString(R.string.str_wrong), activity)

                Log.e("139","139")
            }

            override fun onResponse(call: Call<SendSmsOtpResponse>, response: Response<SendSmsOtpResponse>) {

                mProgress?.value = false
                Log.e("139",response.toString())



            }
        })

    }


    fun verifySmsOtp(verifyOtpRequest: VerifyOtpRequest) {

        RydzApplication.getRetroApiClient().verifyOtp(verifyOtpRequest).enqueue(object : Callback<VerifySmsOtpResponse> {
            override fun onFailure(call: Call<VerifySmsOtpResponse>, t: Throwable) {
                mProgress?.value = false
                Utils.showMessage(activity.getString(R.string.str_wrong), activity)

            }

            override fun onResponse(call: Call<VerifySmsOtpResponse>, response: Response<VerifySmsOtpResponse>) {

                mProgress?.value = false
                otpResponse?.value = response.body()

            }
        })

    }


}