package cabuser.com.rydz.ui.register


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import cabuser.com.rydz.R
import cabuser.com.rydz.databinding.ActivityForgotBinding
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.common.Utils
import kotlinx.android.synthetic.main.activity_forgot.*
import kotlinx.android.synthetic.main.activity_forgot.ccp
import kotlinx.android.synthetic.main.header_with_back.cl_headermain
import kotlinx.android.synthetic.main.header_with_back.tv_title


/**
 * This class contains the functinality of forgot password with email and phone options
 */
class ForgotActivity : BaseActivity() {


    var binding: ActivityForgotBinding? = null
    var viewmodel: ForgotViewModel? = null
    var APPTAG: String = ForgotActivity::class.java.name

    var type: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot)
        viewmodel = ViewModelProviders.of(this).get(ForgotViewModel::class.java)
        binding?.viewmodel = viewmodel

        initViewS()


    }


    private fun initViewS() {


        tv_title.text = getString(R.string.forgot_pwd)
        tv_title.visibility = View.VISIBLE
        ccp.setAutoDetectedCountry(true)

        cl_headermain.setBackgroundColor(Color.TRANSPARENT)
        mProgress = CustomeProgressDialog(this)
        et_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.length > 0) {
                    et_phonenumber.setText("")
                }
            }


            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        et_phonenumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.length > 0) {
                    et_email.setText("")
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })



        mProgress = CustomeProgressDialog(this)

        viewmodel?.mProgress?.observe(this, Observer {
            if (it!!) {
                mProgress!!.show();
            } else
                mProgress!!.dismiss()
        })

        viewmodel?.checkPhoneStatus?.observe(this, Observer { user ->

            if (type.equals("KEY_EMAIL")) {
                if (user.success!!) {
                    var intent = Intent(this@ForgotActivity, ForgotPwdOtpActivity::class.java)
                    intent.putExtra("type", type)
                    intent.putExtra("email", et_email.text.toString().trim())
                    startActivity(intent)
                } else
                    showMessage(user.message.toString())
            } else {
                if (!user.success!!) {
                    var intent = Intent(this@ForgotActivity, ForgotPwdOtpActivity::class.java)
                    intent.putExtra("type", type)
                    intent.putExtra("phonenumber", et_phonenumber.text.toString())
                    intent.putExtra("countryCode", ccp.selectedCountryCode.toString())
                    startActivity(intent)


                } else {
                    showMessage("This phone number is not associated with any account.")
                }

            }
        })
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.btn_next -> {

                if (et_phonenumber.text.toString().trim().isEmpty()) {
                    if (!Utils.isValidEmail(et_email.text.toString().trim())) {
                        showAlert(getString(R.string.email_validation))
                        return
                    } else {



                        type = "KEY_EMAIL"
                       viewmodel!!.sendMailOtp(et_email.text.toString().trim())
                      /*  var intent = Intent(this@ForgotActivity, ForgotPwdOtpActivity::class.java)
                        intent.putExtra("type", type)
                        intent.putExtra("email", et_email.text.toString().trim())
                        startActivity(intent)*/
                        return
                    }
                }
                if (et_email.text.toString().isEmpty()) {
                    if (et_phonenumber.text.toString().length < 8) {
                        showAlert(getString(R.string.phone_validation))
                        return
                    } else {


                        type = "KEY_PHONE"
                        viewmodel!!.checkPhonenumbeIsRegistered("+"+ccp.selectedCountryCode.toString(), et_phonenumber.text.toString())

                        return

                    }
                }


            }

            R.id.iv_back -> {

                finish()
            }

            R.id.et_email -> {
                showKeyboard(et_email)
            }

            R.id.et_phonenumber -> {
                showKeyboard(et_phonenumber)
            }

        }
    }




}