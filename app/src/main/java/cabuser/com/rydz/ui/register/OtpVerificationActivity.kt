package cabuser.com.rydz.ui.register



import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.FacebookEventObject
import cabuser.com.rydz.databinding.ActivityOtpVerificationBinding
import cabuser.com.rydz.ui.home.MainActivity
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.common.Utils
import cabuser.com.rydz.util.prefrences.PreferenceHelper

import com.sinch.verification.*
import kotlinx.android.synthetic.main.activity_otp_verification.*


import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import com.sinch.verification.SinchVerification
import kotlinx.android.synthetic.main.header_back_onregister.*
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

/**
 * OtpVerificationActivity class is used  for otp verification
 */

class OtpVerificationActivity : BaseActivity(), View.OnClickListener {

    var binding: ActivityOtpVerificationBinding? = null
    var viewmodel: OTPVerificationViewModel? = null
    lateinit var phoneNumber: String

    //sinch verification
    private var mIsSmsVerification = true
    private var mShouldFallback = true
    internal var isVerify: Boolean? = false
    lateinit var resendTimer: CountDownTimer

    // lateinit var bundle: Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObservables()

        initViews()

    }

    private fun initViews() {
        cl_headermain.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.tv_resendOtp -> {

                createVerification()
            }

            R.id.tv_numberValue -> {
                finish()
            }
        }
    }

    private fun initObservables() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verification)
        viewmodel = ViewModelProviders.of(this).get(OTPVerificationViewModel::class.java)

        binding?.viewmodel = viewmodel
        mProgress = CustomeProgressDialog(this)
        viewmodel?.otpView = otp_view
        viewmodel?.str_NavigationFrom = intent.getStringExtra("from")!!
        viewmodel?.str_phone = intent.getStringExtra("phonenumber")!!
        viewmodel?.CountryCode = intent.getStringExtra("countryCode")!!

        tv_numberValue.text = viewmodel?.CountryCode + viewmodel?.str_phone

        createVerification()
        viewmodel?.fb_Objet = EventBus.getDefault().getStickyEvent(FacebookEventObject::class.java)

        viewmodel?.mProgress?.observe(this, Observer {
            try {
                if (it!!) {
                    mProgress!!.show()
                } else
                    mProgress!!.dismiss()
            } catch (e: Exception) {

            }
        })

        viewmodel?.userLogin?.observe(this, Observer { user ->

            Log.e("userLogin", "" + user.success)

            if (viewmodel?.str_NavigationFrom!!.toString().equals("facebookLogin", false)) {
                if (user.success!!) {
                    RydzApplication.prefs = PreferenceHelper.defaultPrefs(applicationContext)
                    RydzApplication.prefs[PreferenceHelper.Key.LOGINTYPE] = "socialLogin"
                    goToMainScreen(user)
                    finishAffinity()
                } else {
                    showMessage(user.message)
                }
            } else {
                if (user.success!!) {
                    if (user.isUser == 1) {
                        RydzApplication.prefs = PreferenceHelper.defaultPrefs(applicationContext)
                        RydzApplication.prefs[PreferenceHelper.Key.LOGINTYPE] = "phoneLogin"
                        goToMainScreen(user)
                        finishAffinity()
                    } else {
                        val intent = Intent(this, ProfileSetupActivity::class.java)
                        intent.putExtra("phonenumber", viewmodel?.str_phone)
                        intent.putExtra("countryCode", viewmodel?.CountryCode)
                        startActivity(intent)
                        finishAffinity()
                    }

                } else {
                    showMessage(user.message)
                }
            }
        })


        viewmodel?.otpResponse?.observe(this, Observer { obj ->

            Log.e("userLogin", "" + obj.success)



            if (obj.success!!) {

                if (viewmodel?.str_NavigationFrom!!.toString().equals("facebookLogin", false)) {
                    viewmodel?.socialMediaLogin()
                } else {
                    viewmodel?.checkPhonenumberIsAlreadyRegistered(viewmodel?.CountryCode!!, viewmodel?.str_phone!!)
                }
            } else {

                Utils.showMessage(obj.message,this)
            }
        })


    }

    private fun goToMainScreen(user: RegistrationResponse) {

        val prefs = PreferenceHelper.defaultPrefs(this)
        prefs[PreferenceHelper.Key.REGISTEREDUSER] = user.user //setter
        RydzApplication.user_obj = user.user!!
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()

    }

    fun startTimer(mode: String) {
        resendTimer = object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                tv_counter.visibility = View.GONE
                tv_resendOtp.visibility = View.VISIBLE
            }

            override fun onTick(millisUntilFinished: Long) {

                tv_counter.visibility = View.VISIBLE
                tv_resendOtp.visibility = View.GONE

                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                var time = "0" + minutes.toString() + ":" + seconds.toString()
                tv_counter.text = time

            }
        }

        resendTimer.start()
    }


    private fun showErrorMsg() {
        showMessage(getString(R.string.otp_validation))
    }


    private fun createVerification() {

        startTimer("start")

        // It is important to pass ApplicationContext to the Verification config builder as the
        // verification process might outlive the activity.
        val config = SinchVerification.config()
                .applicationKey(RydzApplication.SINCH_APP_KEY)
                .context(applicationContext)
                .build()


        val listener = MyVerificationListener()
        if (viewmodel?.CountryCode.toString().contains("+")) {
            viewmodel?.mVerification = SinchVerification.createSmsVerification(config, viewmodel?.CountryCode.toString().trim() + viewmodel?.str_phone.toString().trim(), listener)

        } else {
            viewmodel?.mVerification = SinchVerification.createSmsVerification(config, "+" + viewmodel?.CountryCode.toString().trim() + viewmodel?.str_phone.toString().trim(), listener)

        }
        viewmodel?.mVerification!!.initiate()


    }

    internal inner class MyVerificationListener : VerificationListener {

        override fun onInitiated(result: InitiationResult) {

            Log.e("Sinch Verification : ", "Started")
        }

        override fun onInitiationFailed(exception: Exception) {
            // Log.e("onInitiationFailed", "Verification initialization failed: " + exception.getMessage());
            // hideProgressAndShowMessage(R.string.failed);
            Log.e("203", exception.message!!)
            if (exception is InvalidInputException) {
                // Incorrect number provided
                try {
                    Toast.makeText(this@OtpVerificationActivity, exception.message, Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else if (exception is CodeInterceptionException) {
                // Incorrect number provided
                try {
                    Toast.makeText(this@OtpVerificationActivity, exception.message, Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (exception is ServiceErrorException) {
                // Verification initiation aborted due to early reject feature,
                // client callback denial, or some other Sinch service error.
                // Fallback to other verification method here.
                fallbackIfNecessary()
            } else {
                // Other system error, such as UnknownHostException in case of network error
            }
        }

        override fun onVerificationFallback() {
            fallbackIfNecessary()
        }

        private fun fallbackIfNecessary() {
            if (mShouldFallback) {
                mIsSmsVerification = !mIsSmsVerification
                if (mIsSmsVerification) {
                    // Log.i("fallbackIfNecessary", "Falling back to sms verification.");
                }
                mShouldFallback = false
            }
        }

        override fun onVerified() {

            viewmodel?.mIsVerified = true
            isVerify = true
            if (viewmodel?.mIsVerified!!) {

                if (viewmodel?.str_NavigationFrom.equals("facebookLogin")) {
                    viewmodel?.socialMediaLogin()
                } else {
                    viewmodel?.checkPhonenumberIsAlreadyRegistered(viewmodel?.CountryCode!!, viewmodel?.str_phone!!)
                }
            }

        }

        override fun onVerificationFailed(exception: Exception) {
            mProgress!!.dismiss()
            if (exception.message.toString().equals("The verification code is incorrect.", ignoreCase = true)) {
                Toast.makeText(this@OtpVerificationActivity, "The verification code is incorrect.", Toast.LENGTH_SHORT).show()
            } else {
                //  Toast.makeText(this@OtpVerificationActivity, exception.message, Toast.LENGTH_SHORT).show()
            }
            if (viewmodel?.mIsVerified!!) {
                return
            }


            if (exception is CodeInterceptionException) {
                // Automatic code interception failed, probably due to missing permissions.
                // Let the user try and enter the code manually.

            } else {

            }


        }
    }


}





/*import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.CapyApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.FacebookEventObject
import cabuser.com.rydz.databinding.ActivityOtpVerificationBinding
import cabuser.com.rydz.ui.home.MainActivity
import cabuser.com.rydz.ui.home.SendOtpRequest
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import com.sinch.verification.SinchVerification
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.header_back_onregister.*
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit

*//**
 * OtpVerificationActivity class is used  for otp verification
 *//*

class OtpVerificationActivity : BaseActivity(), View.OnClickListener {

    var binding: ActivityOtpVerificationBinding? = null
    var viewmodel: OTPVerificationViewModel? = null
    lateinit var phoneNumber: String

    //sinch verification
    private var mIsSmsVerification = true
    private var mShouldFallback = true
    internal var isVerify: Boolean? = false
    lateinit var resendTimer: CountDownTimer

    // lateinit var bundle: Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObservables()

        initViews()

    }

    private fun initViews() {
        cl_headermain.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.tv_resendOtp -> {
               startTimer("start")
                createVerification()
            }

            R.id.tv_numberValue -> {
                finish()
            }
        }
    }

    private fun initObservables() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp_verification)
        viewmodel = ViewModelProviders.of(this).get(OTPVerificationViewModel::class.java)

        binding?.viewmodel = viewmodel
        mProgress = CustomeProgressDialog(this)
        viewmodel?.otpView = otp_view
        viewmodel?.otpView!!.mOtpFourField.addTextChangedListener(PinTextWatcher(4))

        viewmodel?.str_NavigationFrom = intent.getStringExtra("from")!!
        viewmodel?.str_phone = intent.getStringExtra("phonenumber")!!
        viewmodel?.CountryCode = intent.getStringExtra("countryCode")!!
        tv_numberValue.text = viewmodel?.CountryCode + viewmodel?.str_phone
        startTimer("start")
       // createVerification()
        viewmodel?.fb_Objet = EventBus.getDefault().getStickyEvent(FacebookEventObject::class.java)

        viewmodel?.mProgress?.observe(this, Observer {
            try {
                if (it!!) {
                    mProgress!!.show()
                } else
                    mProgress!!.dismiss()
            } catch (e: Exception) {

            }
        })

        viewmodel?.userLogin?.observe(this, Observer { user ->

            Log.e("userLogin", "" + user.success)

            if (viewmodel?.str_NavigationFrom!!.toString().equals("facebookLogin", false)) {
                if (user.success!!) {
                    CapyApplication.prefs = PreferenceHelper.defaultPrefs(applicationContext)
                    CapyApplication.prefs[PreferenceHelper.Key.LOGINTYPE] = "socialLogin"
                    goToMainScreen(user)
                    finishAffinity()
                } else {
                    showMessage(user.message)
                }
            } else {
                if (user.success!!) {
                    if (user.isUser == 1) {
                        CapyApplication.prefs = PreferenceHelper.defaultPrefs(applicationContext)
                        CapyApplication.prefs[PreferenceHelper.Key.LOGINTYPE] = "phoneLogin"
                        goToMainScreen(user)
                        finishAffinity()
                    } else {
                        val intent = Intent(this, ProfileSetupActivity::class.java)
                        intent.putExtra("phonenumber", viewmodel?.str_phone)
                        intent.putExtra("countryCode", viewmodel?.CountryCode)
                        startActivity(intent)
                        finishAffinity()
                    }

                } else {
                    showMessage(user.message)
                }
            }
        })


 *//*       viewmodel?.otpResponse?.observe(this, Observer { obj ->

            Log.e("userLogin", "" + obj.success)



                if (obj.success!!) {

                    if (viewmodel?.str_NavigationFrom!!.toString().equals("facebookLogin", false)) {
                        viewmodel?.socialMediaLogin()
                } else {
                    viewmodel?.checkPhonenumberIsAlreadyRegistered(viewmodel?.CountryCode!!, viewmodel?.str_phone!!)
                }
            } else {

                    Utils.showMessage(obj.err,this)
            }
        })




*//*



    }

    private fun goToMainScreen(user: RegistrationResponse) {

        val prefs = PreferenceHelper.defaultPrefs(this)
        prefs[PreferenceHelper.Key.REGISTEREDUSER] = user.user //setter
        CapyApplication.user_obj = user.user!!
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()

    }

    fun startTimer(mode: String) {
        resendTimer = object : CountDownTimer(60000, 1000) {
            override fun onFinish() {
                tv_counter.visibility = View.GONE
                tv_resendOtp.visibility = View.VISIBLE
            }

            override fun onTick(millisUntilFinished: Long) {

                tv_counter.visibility = View.VISIBLE
                tv_resendOtp.visibility = View.GONE

                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                var time = "0" + minutes.toString() + ":" + seconds.toString()
                tv_counter.text = time

            }
        }

        resendTimer.start()
    }


    private fun showErrorMsg() {
        showMessage(getString(R.string.otp_validation))
    }


    private fun createVerification() {
         // It is important to pass ApplicationContext to the Verification config builder as the
              // verification process might outlive the activity.
              val config = SinchVerification.config()
                      .applicationKey(CapyApplication.SINCH_APP_KEY)
                      .context(applicationContext)
                      .build()


              val listener = MyVerificationListener()
              if (viewmodel?.CountryCode.toString().contains("+")) {
                  viewmodel?.mVerification = SinchVerification.createSmsVerification(config, viewmodel?.CountryCode.toString().trim() + viewmodel?.str_phone.toString().trim(), listener)

              } else {
                  viewmodel?.mVerification = SinchVerification.createSmsVerification(config, "+" + viewmodel?.CountryCode.toString().trim() + viewmodel?.str_phone.toString().trim(), listener)

              }
              viewmodel?.mVerification!!.initiate()

     *//*   var sendOtpRequest = SendOtpRequest()
        sendOtpRequest.mobile =viewmodel?.CountryCode.toString().replace("+","")+viewmodel?.str_phone.toString().trim()
        viewmodel?.getSmsOtp(sendOtpRequest)*//*




    }

    inner class PinTextWatcher internal constructor(private val currentIndex: Int) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        private var isFirst = false
        private var isLast = false
        private var newTypedString = ""


        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            newTypedString = s.subSequence(start, start + count).toString().trim { it <= ' ' }




            if (viewmodel?.otpView!!.mOtpFourField.hasFocus() && !viewmodel?.otpView!!.mOtpOneField.text.toString().isEmpty() && !viewmodel?.otpView!!.mOtpTwoField.text.toString().isEmpty() && !viewmodel?.otpView!!.mOtpThreeField.text.toString().isEmpty() && !viewmodel?.otpView!!.mOtpFourField.text.toString().isEmpty()) {
                viewmodel?.onLoginSuccess()


            }


        }


    }


}*/


