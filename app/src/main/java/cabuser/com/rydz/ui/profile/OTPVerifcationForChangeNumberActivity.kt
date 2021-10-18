package cabuser.com.rydz.ui.profile


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.FacebookEventObject
import cabuser.com.rydz.databinding.ActivityChangenumberOtpverificationBinding
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import kotlinx.android.synthetic.main.activity_otp_verification.*
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.TimeUnit




import com.sinch.verification.*


class OTPVerifcationForChangeNumberActivity : BaseActivity(), View.OnClickListener {


    var binding: ActivityChangenumberOtpverificationBinding? = null
    var viewmodel: ChangeNumberOTPVerificationViewModel? = null
    lateinit var phoneNumber: String
    private lateinit var resendTimer: CountDownTimer
    //sinch verification
    private var mIsSmsVerification = true
    private var mShouldFallback = true

    // lateinit var bundle: Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObservables()


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {

                finish()
            }
            R.id.tv_resendOtp -> {

                startTimer()
                createVerification()

            }
        }
    }

    private fun initObservables() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_changenumber_otpverification)
        viewmodel = ViewModelProviders.of(this).get(ChangeNumberOTPVerificationViewModel::class.java)

        binding?.viewmodel = viewmodel
        mProgress = CustomeProgressDialog(this)
        viewmodel?.otpView = otp_view

        viewmodel?.strPhone = intent.getStringExtra("phonenumber")!!
        viewmodel?.CountryCode = intent.getStringExtra("countryCode")!!
        tv_numberValue.text = viewmodel?.strPhone

        viewmodel?.fbObjet = EventBus.getDefault().getStickyEvent(FacebookEventObject::class.java)
        startTimer()
        createVerification()
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

            if (user.success!!) {
                finish()
            }
            showMessage(user.message)


        })

    }


    private fun startTimer() {

        this.resendTimer = object : CountDownTimer(60000, 1000) {
            override fun onFinish() {

                tv_counter.visibility = View.GONE
                tv_resendOtp.visibility = View.VISIBLE


            }

            override fun onTick(millisUntilFinished: Long) {

                tv_counter.visibility = View.VISIBLE
                tv_resendOtp.visibility = View.GONE
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                val time = "0$minutes:$seconds"
                tv_counter.text = time

            }
        }

        resendTimer.start()
    }





    private fun createVerification() {
        // It is important to pass ApplicationContext to the Verification config builder as the
        // verification process might outlive the activity.
        val config = SinchVerification.config()
                .applicationKey(RydzApplication.SINCH_APP_KEY)
                .context(applicationContext)
                .build()


        val listener = MyVerificationListener()
        if(viewmodel?.CountryCode.toString().contains("+"))
        {
            viewmodel?.mVerification = SinchVerification.createSmsVerification(config, viewmodel?.CountryCode.toString().trim() + viewmodel?.strPhone.toString().trim(), listener)

        }
        else
        {
            viewmodel?.mVerification = SinchVerification.createSmsVerification(config,"+"+ viewmodel?.CountryCode.toString().trim() + viewmodel?.strPhone.toString().trim(), listener)

        }
        viewmodel?.mVerification!!.initiate()


    }

    internal inner class MyVerificationListener : VerificationListener {

        override fun onInitiated(result: InitiationResult) {
            // Log.d("onInitiated", "Initialized!");
            //   showProgress("Loading...");


        }

        override fun onInitiationFailed(exception: Exception) {
            // Log.e("onInitiationFailed", "Verification initialization failed: " + exception.getMessage());
            // hideProgressAndShowMessage(R.string.failed);

            when (exception) {
                is InvalidInputException -> {
                    // Incorrect number provided
                }
                is ServiceErrorException -> // Verification initiation aborted due to early reject feature,
                    // client callback denial, or some other Sinch service error.
                    // Fallback to other verification method here.
                    fallbackIfNecessary()
                else -> {
                    // Other system error, such as UnknownHostException in case of network error
                }
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


            if ( viewmodel?.otpView!!.makeOTP().length == 4 &&  viewmodel?.mIsVerified!!) {
                viewmodel?.changePhoneNumber()

            }
        }

        override fun onVerificationFailed(exception: Exception) {
            //hideProgress()
            mProgress!!.dismiss()
            if (exception.message.toString().equals("The verification code is incorrect.", ignoreCase = true)) {
                Toast.makeText(this@OTPVerifcationForChangeNumberActivity, "The verification code is incorrect.", Toast.LENGTH_SHORT).show()
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




/*

class OTPVerifcationForChangeNumberActivity : BaseActivity(), View.OnClickListener {


    var binding: ActivityChangenumberOtpverificationBinding? = null
    var viewmodel: ChangeNumberOTPVerificationViewModel? = null
    lateinit var phoneNumber: String
    private lateinit var resendTimer: CountDownTimer
    //sinch verification
    private var mIsSmsVerification = true
    private var mShouldFallback = true

    // lateinit var bundle: Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObservables()


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {

                finish()
            }
            R.id.tv_resendOtp -> {

                startTimer()
                createVerification()

            }
        }
    }

    private fun initObservables() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_changenumber_otpverification)
        viewmodel = ViewModelProviders.of(this).get(ChangeNumberOTPVerificationViewModel::class.java)

        binding?.viewmodel = viewmodel
        mProgress = CustomeProgressDialog(this)
        viewmodel?.otpView = otp_view
        viewmodel?.otpView!!.mOtpFourField.addTextChangedListener(PinTextWatcher(4))

        viewmodel?.strPhone = intent.getStringExtra("phonenumber")!!


        viewmodel?.CountryCode = intent.getStringExtra("countryCode")!!
        tv_numberValue.text = viewmodel?.strPhone

        viewmodel?.fbObjet = EventBus.getDefault().getStickyEvent(FacebookEventObject::class.java)
        startTimer()
        createVerification()
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

            if (user.success!!) {
                finish()
            }
            //showMessage(user.message)


        })

        viewmodel?.otpResponse?.observe(this, Observer { obj ->

            Log.e("userLogin", "" + obj.success)



            if (obj.success!!) {
                viewmodel?.changePhoneNumber()


            } else {

                Utils.showMessage(obj.err,this)
            }
        })

    }


    private fun startTimer() {

        this.resendTimer = object : CountDownTimer(60000, 1000) {
            override fun onFinish() {

                tv_counter.visibility = View.GONE
                tv_resendOtp.visibility = View.VISIBLE


            }

            override fun onTick(millisUntilFinished: Long) {

                tv_counter.visibility = View.VISIBLE
                tv_resendOtp.visibility = View.GONE
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                val time = "0$minutes:$seconds"
                tv_counter.text = time

            }
        }

        resendTimer.start()
    }





    private fun createVerification() {
      */
/*  // It is important to pass ApplicationContext to the Verification config builder as the
        // verification process might outlive the activity.
        val config = SinchVerification.config()
                .applicationKey(CapyApplication.SINCH_APP_KEY)
                .context(applicationContext)
                .build()


        val listener = MyVerificationListener()
        if(viewmodel?.CountryCode.toString().contains("+"))
        {
            viewmodel?.mVerification = SinchVerification.createSmsVerification(config, viewmodel?.CountryCode.toString().trim() + viewmodel?.strPhone.toString().trim(), listener)

        }
        else
        {
            viewmodel?.mVerification = SinchVerification.createSmsVerification(config,"+"+ viewmodel?.CountryCode.toString().trim() + viewmodel?.strPhone.toString().trim(), listener)
*//*



        var sendOtpRequest = SendOtpRequest()
        sendOtpRequest.mobile =viewmodel?.CountryCode.toString().replace("+","")+viewmodel?.strPhone.toString().trim()
        viewmodel?.getSmsOtp(sendOtpRequest)


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
              */
/*  if (viewmodel?.type.equals("KEY_EMAIL")) {
                    viewmodel?.onOTPSuccessfulVerification()

                } else {*//*


                    //createVerification()

                //}

                viewmodel?.onOTPSuccessfulVerification()
            }


        }


    }


}




*/

