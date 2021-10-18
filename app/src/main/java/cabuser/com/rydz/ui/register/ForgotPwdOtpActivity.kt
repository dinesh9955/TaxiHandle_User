package cabuser.com.rydz.ui.register


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.FacebookEventObject
import cabuser.com.rydz.databinding.ActivityForgotpwdOtpBinding
import cabuser.com.rydz.ui.profile.ForgotPwdOTPViewModel
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import com.sinch.verification.*
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.header_back_onregister.*
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


class ForgotPwdOtpActivity : BaseActivity() {

    var binding: ActivityForgotpwdOtpBinding? = null
    var viewmodel: ForgotPwdOTPViewModel? = null
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

                if (viewmodel?.type.equals("KEY_EMAIL")) {


                    viewmodel!!.sendMailOtp(intent.getStringExtra("email").toString().trim())
                } else {
                    startTimer()
                    createVerification()
                }

            }
            R.id.fab_next -> {

            }

        }
    }


    private fun initObservables() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgotpwd_otp)
        viewmodel = ViewModelProviders.of(this).get(ForgotPwdOTPViewModel::class.java)

        binding?.viewmodel = viewmodel


        mProgress = CustomeProgressDialog(this)
        viewmodel?.otpView = otp_view
        viewmodel?.otpView!!.mOtpFourField.addTextChangedListener(PinTextWatcher(4))
        viewmodel?.type = intent.getStringExtra("type")!!

        // iv_back.visibility=View.GONE
        if (viewmodel?.type.equals("KEY_EMAIL")) {
            tv_counter.visibility = View.GONE
            tv_resendOtp.visibility = View.GONE
            viewmodel?.email = intent.getStringExtra("email")!!
            tv_numberValue.text = viewmodel?.email
            startTimer()
        } else {
            viewmodel?.str_phone = intent.getStringExtra("phonenumber")!!
            viewmodel?.CountryCode = intent.getStringExtra("countryCode")!!

            tv_numberValue.text = viewmodel?.str_phone
            createVerification()
            viewmodel?.fb_Objet = EventBus.getDefault().getStickyEvent(FacebookEventObject::class.java)
            startTimer()
        }


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

            if (viewmodel?.type!!.toString().equals("KEY_EMAIL", false)) {
                if (user.success!!) {

                    var intent = Intent(this@ForgotPwdOtpActivity, NewPasswordActivity::class.java)
                    intent.putExtra("type", "KEY_EMAIL")
                    intent.putExtra("email", viewmodel?.email)

                    startActivity(intent)


                }

            } else {
                if (user.success!!) {
                    viewmodel?.str_phone = intent.getStringExtra("phonenumber")!!
                    viewmodel?.CountryCode = intent.getStringExtra("countryCode")!!
                    var intent = Intent(this@ForgotPwdOtpActivity, NewPasswordActivity::class.java)
                    intent.putExtra("type", "KEY_PHONE")
                    intent.putExtra("phonenumber", viewmodel?.str_phone)
                    intent.putExtra("countryCode", viewmodel?.CountryCode)
                    startActivity(intent)
                }

            }
            showMessage(user.message.toString())
        })



        viewmodel?.otpResponse?.observe(this, Observer { user ->

            if (viewmodel?.type!!.toString().equals("KEY_EMAIL", false)) {

                if (user.success!!) {

                    startTimer()


                }


                try {
                    showMessage(user.message.toString())
                } catch (e: Exception) {
                }
            }

        }
        )


    }

    fun startTimer() {
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


        }

        override fun onInitiationFailed(exception: Exception) {
            // Log.e("onInitiationFailed", "Verification initialization failed: " + exception.getMessage());
            // hideProgressAndShowMessage(R.string.failed);

            if (exception is InvalidInputException) {
                // Incorrect number provided
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


                var intent = Intent(this@ForgotPwdOtpActivity, NewPasswordActivity::class.java)
                intent.putExtra("type", "KEY_PHONE")
                intent.putExtra("phonenumber", viewmodel?.str_phone)
                intent.putExtra("countryCode", viewmodel?.CountryCode)
                startActivity(intent)


            }


        }

        override fun onVerificationFailed(exception: Exception) {
            mProgress!!.dismiss()
            if (exception.message.toString().equals("The verification code is incorrect.", ignoreCase = true)) {
                Toast.makeText(this@ForgotPwdOtpActivity, "The verification code is incorrect.", Toast.LENGTH_SHORT).show()
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
                if (viewmodel?.type.equals("KEY_EMAIL")) {
                    viewmodel?.onOTPSuccessfulVerification()

                } else {

                    createVerification()

                }
            }


        }


    }


}


/*

import android.content.Intent
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
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.FacebookEventObject
import cabuser.com.rydz.databinding.ActivityForgotpwdOtpBinding
import cabuser.com.rydz.ui.home.SendOtpRequest
import cabuser.com.rydz.ui.profile.ForgotPwdOTPViewModel
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.common.Utils
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.header_back_onregister.*
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit


class ForgotPwdOtpActivity : BaseActivity() {

    var binding: ActivityForgotpwdOtpBinding? = null
    var viewmodel: ForgotPwdOTPViewModel? = null
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

                if (viewmodel?.type.equals("KEY_EMAIL")) {


                 //   viewmodel!!.sendMailOtp(intent.getStringExtra("email").trim())
                } else {
                    startTimer()
                    //createVerification()
                }

            }
            R.id.fab_next ->
            {
                if (viewmodel?.type!!.toString().equals("KEY_EMAIL", false)) {
                    var intent = Intent(this@ForgotPwdOtpActivity, NewPasswordActivity::class.java)
                    intent.putExtra("type", "KEY_EMAIL")
                    intent.putExtra("email", viewmodel?.email)

                    startActivity(intent)
                } else {
                    var intent = Intent(this@ForgotPwdOtpActivity, NewPasswordActivity::class.java)
                    intent.putExtra("type", "KEY_PHONE")
                    intent.putExtra("phonenumber", viewmodel?.str_phone)
                    intent.putExtra("countryCode", viewmodel?.CountryCode)
                    startActivity(intent)
                }




            }

        }
    }


    private fun initObservables() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgotpwd_otp)
        viewmodel = ViewModelProviders.of(this).get(ForgotPwdOTPViewModel::class.java)

        binding?.viewmodel = viewmodel


        mProgress = CustomeProgressDialog(this)
        viewmodel?.otpView = otp_view
        viewmodel?.otpView!!.mOtpFourField.addTextChangedListener(PinTextWatcher(4))
        viewmodel?.type = intent.getStringExtra("type")!!

        // iv_back.visibility=View.GONE
        if (viewmodel?.type.equals("KEY_EMAIL")) {
            tv_counter.visibility = View.GONE
            tv_resendOtp.visibility = View.GONE
            viewmodel?.email = intent.getStringExtra("email")!!
            tv_numberValue.text = viewmodel?.email
            startTimer()
        } else {
            viewmodel?.str_phone = intent.getStringExtra("phonenumber")!!
            viewmodel?.CountryCode = intent.getStringExtra("countryCode")!!

            tv_numberValue.text = viewmodel?.str_phone
            createVerification()
            viewmodel?.fb_Objet = EventBus.getDefault().getStickyEvent(FacebookEventObject::class.java)
            startTimer()
        }


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

            if (viewmodel?.type!!.toString().equals("KEY_EMAIL", false)) {
                if (user.success!!) {

                    var intent = Intent(this@ForgotPwdOtpActivity, NewPasswordActivity::class.java)
                    intent.putExtra("type", "KEY_EMAIL")
                    intent.putExtra("email", viewmodel?.email)

                    startActivity(intent)


                }
                showMessage(user.message.toString())
            } else {
                if (user.success!!) {
                    viewmodel?.str_phone = intent.getStringExtra("phonenumber")!!
                    viewmodel?.CountryCode = intent.getStringExtra("countryCode")!!

                    var intent = Intent(this@ForgotPwdOtpActivity, NewPasswordActivity::class.java)
                    intent.putExtra("type", "KEY_PHONE")
                      intent.putExtra("phonenumber",viewmodel?.str_phone)
                      intent.putExtra("countryCode",viewmodel?.CountryCode)
                    startActivity(intent)

                }
                showMessage(user.message.toString())
            }
        })



        viewmodel?.otpResponse?.observe(this, Observer { user ->

            if (viewmodel?.type!!.toString().equals("KEY_EMAIL", false)) {

                if (user.success!!) {

                    startTimer()


                }


                try {
                    showMessage(user.message.toString())
                } catch (e: Exception) {
                }
            }

        }
        )

        viewmodel?.phoneOtpResponse?.observe(this, Observer { obj ->

            Log.e("userLogin", "" + obj.success)



            if (obj.success!!) {

                var intent = Intent(this@ForgotPwdOtpActivity, NewPasswordActivity::class.java)
                intent.putExtra("type", "KEY_PHONE")
                intent.putExtra("phonenumber", viewmodel?.str_phone)
                intent.putExtra("countryCode", viewmodel?.CountryCode)
                startActivity(intent)
              */
/*  if (viewmodel?.str_NavigationFrom!!.toString().equals("facebookLogin", false)) {
                    viewmodel?.socialMediaLogin()
                } else {
                    viewmodel?.checkPhonenumberIsAlreadyRegistered(viewmodel?.CountryCode!!, viewmodel?.str_phone!!)
                }*//*

            } else {

                Utils.showMessage(obj.err,this)
            }
        })



    }

    fun startTimer() {
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
       */
/* // It is important to pass ApplicationContext to the Verification config builder as the
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
        viewmodel?.mVerification!!.initiate()*//*


        var sendOtpRequest = SendOtpRequest()
        sendOtpRequest.mobile =viewmodel?.CountryCode.toString().replace("+","")+viewmodel?.str_phone.toString().trim()
        viewmodel?.getSmsOtp(sendOtpRequest)
        startTimer()

    }

*/
/*
    internal inner class MyVerificationListener : VerificationListener {

        override fun onInitiated(result: InitiationResult) {


        }

        override fun onInitiationFailed(exception: Exception) {
            // Log.e("onInitiationFailed", "Verification initialization failed: " + exception.getMessage());
            // hideProgressAndShowMessage(R.string.failed);

            if (exception is InvalidInputException) {
                // Incorrect number provided
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
            if (viewmodel?.otpView!!.makeOTP().length == 4 && viewmodel?.mIsVerified!!) {


                var intent = Intent(this@ForgotPwdOtpActivity, NewPasswordActivity::class.java)
                intent.putExtra("type", "KEY_PHONE")
                intent.putExtra("phonenumber", viewmodel?.str_phone)
                intent.putExtra("countryCode", viewmodel?.CountryCode)
                startActivity(intent)


            }


        }

        override fun onVerificationFailed(exception: Exception) {
            mProgress!!.dismiss()
            if (exception.message.toString().equals("The verification code is incorrect.", ignoreCase = true)) {
                Toast.makeText(this@ForgotPwdOtpActivity, "The verification code is incorrect.", Toast.LENGTH_SHORT).show()
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
*//*



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
                if (viewmodel?.type!!.toString().equals("KEY_EMAIL", false))
                {
                    var intent = Intent(this@ForgotPwdOtpActivity, NewPasswordActivity::class.java)
                    intent.putExtra("type", "KEY_EMAIL")
                    intent.putExtra("email", viewmodel?.email)
                    startActivity(intent)
                }
                else
                {
                        viewmodel?.str_phone = intent.getStringExtra("phonenumber")!!
                        viewmodel?.CountryCode = intent.getStringExtra("countryCode")!!

                        var intent = Intent(this@ForgotPwdOtpActivity, NewPasswordActivity::class.java)
                        intent.putExtra("type", "KEY_PHONE")
                        intent.putExtra("phonenumber",viewmodel?.str_phone)
                        intent.putExtra("countryCode",viewmodel?.CountryCode)
                        startActivity(intent)

                    }


               */
/* if (viewmodel?.type.equals("KEY_EMAIL")) {*//*

                    //viewmodel?.onOTPSuccessfulVerification()

               */
/* } else {
                    viewmodel?.onOTPSuccessfulVerification()
                    //createVerification()

                }*//*

            }


        }


    }


}


*/
