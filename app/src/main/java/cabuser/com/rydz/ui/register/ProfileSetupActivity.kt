package cabuser.com.rydz.ui.register

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.databinding.ActivityProfileSetupBinding
import cabuser.com.rydz.ui.home.MainActivity
import cabuser.com.rydz.ui.profile.User
import cabuser.com.rydz.ui.settings.PrivacyPolicyActivity
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.common.Utils
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import kotlinx.android.synthetic.main.activity_profile_setup.*


class ProfileSetupActivity : BaseActivity(), View.OnClickListener {


    var binding: ActivityProfileSetupBinding? = null
    var viewmodel: RegisterViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initObservables()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {


            R.id.fab_next -> {
                if (validate()) {
                    RydzApplication.tempuser_obj = User()
                    RydzApplication.tempuser_obj!!.countryCode = intent.getStringExtra("countryCode")
                    RydzApplication.tempuser_obj!!.phone = intent.getStringExtra("phonenumber")
                    RydzApplication.tempuser_obj!!.firstName = etv_first.text.toString().trim()
                    RydzApplication.tempuser_obj!!.lastName = etv_last.text.toString().trim()
                    RydzApplication.tempuser_obj!!.email = edt_email.text.toString().trim()
                    RydzApplication.tempuser_obj!!.password = edt_pwd.text.toString().trim()
                    RydzApplication.tempuser_obj!!.company = edt_company.text.toString().trim()
                    if( cb_updates.isChecked)
                    RydzApplication.tempuser_obj!!.isSubscribed =1
                    else
                        RydzApplication.tempuser_obj!!.isSubscribed =0


                    viewmodel?.registerUser()
                }

            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun initObservables() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_setup)
        viewmodel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        binding?.viewmodel = viewmodel

        edt_phonenumber.setText(intent.getStringExtra("phonenumber"))
        intent.getStringExtra("countryCode")?.toInt()?.let { ccp.setCountryForPhoneCode(it) }

        mProgress = CustomeProgressDialog(this)

        val prefixInfoText = SpannableString(getString(R.string.by_clicking_i_agree_to_let_rydz_store_the_information_i_provide_in_order_to_provide_and_improve_its_services_you_may_delete_your_account_at_anytime))

        prefixInfoText.setSpan(ForegroundColorSpan(getColor(R.color._b78830)), 153, prefixInfoText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        cb_information.text = prefixInfoText

        val prefixText = SpannableString(getString(R.string.string_terms))

        prefixText.setSpan(ForegroundColorSpan(getColor(R.color._e4c755)), 59, 79, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        prefixText.setSpan(ForegroundColorSpan(getColor(R.color._e4c755)), 84, 99, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        prefixText.setSpan(ForegroundColorSpan(getColor(R.color._b78830)), 99, prefixText.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@ProfileSetupActivity, PrivacyPolicyActivity::class.java).apply {
                    putExtra("show", getString(R.string.terms))
                })
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        val privacyclickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@ProfileSetupActivity, PrivacyPolicyActivity::class.java).apply {
                    putExtra("show", getString(R.string.privacy_policy))
                })
            }


            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }

        prefixText.setSpan(clickableSpan, 59, 79, 0)
        prefixText.setSpan(privacyclickableSpan, 84, 99, 0)
        tv_terms.movementMethod = LinkMovementMethod.getInstance()


        tv_terms.text = prefixText








        viewmodel?.mProgress?.observe(this, Observer {
            if (it!!) {
                mProgress!!.show()
            } else
                mProgress!!.dismiss()
        })

        viewmodel?.userLogin?.observe(this, Observer { user ->

            if (user.success!!) {

                RydzApplication.prefs = PreferenceHelper.defaultPrefs(this)
                RydzApplication.prefs[PreferenceHelper.Key.REGISTEREDUSER] = user.user //setter
                RydzApplication.user_obj = user.user!!
                RydzApplication.prefs[PreferenceHelper.Key.LOGINTYPE] = "phoneLogin"
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()

            } else {
                showMessage(user.message)
            }
        })
    }


    private fun validate(): Boolean {
        if (etv_first.text.toString().trim().isEmpty()) {

            showMessage(R.string.firstname_validation)
            return false
        } else if (etv_last.text.toString().trim().isEmpty()) {
            showMessage(R.string.lastname_validation)
            return false
        } else if (edt_email.text.toString().isEmpty()) {

            showMessage(R.string.email_req_validation)
            return false
        } else if (!Utils.isEmailValid(edt_email.text.toString())) {


            showMessage(R.string.email_validation)
            return false
        }/* else if (edt_address.text.toString().trim().isEmpty()) {


            showMessage(R.string.address_validation)
            return false
        }*/ else if (edt_pwd.text.toString().trim().isEmpty() || edt_pwd.text.toString().trim().length < 8) {

            showMessage(R.string.strongpwd_validation)
            return false

        } else if (edt_confirm_pwd.text.toString().trim().isEmpty() || edt_confirm_pwd.text.toString().trim().length < 8) {

            showMessage(R.string.confirmpwd_validation)
            return false

        } else if (edt_confirm_pwd.text.toString().trim().isEmpty() || edt_confirm_pwd.text.toString().trim().length < 8 || !edt_pwd.text.toString().equals(edt_confirm_pwd.text.toString())) {

            showMessage(R.string.matchpwd_validation)
            return false

        } else if (cb_terms.isChecked && cb_information.isChecked) {
            return true
        } else {
            showMessage(R.string.terms_conditions)
            return false
        }



        return true
    }


}
