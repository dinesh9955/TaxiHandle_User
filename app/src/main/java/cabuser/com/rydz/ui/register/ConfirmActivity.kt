package cabuser.com.rydz.ui.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.FacebookEventObject
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.Utils
import kotlinx.android.synthetic.main.activity_confirm.*
import kotlinx.android.synthetic.main.header_with_back.*
import org.greenrobot.eventbus.EventBus


/**
 * This class serves as registarion page when user first time login through facebook
 */
class ConfirmActivity : BaseActivity() {


    var str_id: String? = null
    var countrycode: String? = null
    var fb_Objet: FacebookEventObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        initViews()
    }


    private fun initViews() {

        ccp.setAutoDetectedCountry(true)
        cl_headermain.setBackgroundColor(Color.TRANSPARENT)
        countrycode = "+" + ccp.selectedCountryCode.toString()
        fb_Objet = EventBus.getDefault().getStickyEvent(FacebookEventObject::class.java)
        str_id = fb_Objet!!.fbId
        etv_first.setText(fb_Objet!!.firstName)
        etv_last.setText(fb_Objet!!.lastName)
        edt_email.setText(fb_Objet!!.email)
        edt_phonenumber.setText(fb_Objet!!.phonenumber)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.iv_back -> {
                finish()
            }
            R.id.fab_next -> {
                if (validate()) {
                    var jsonObject = FacebookEventObject(etv_first.text.toString(), etv_last.text.toString(), str_id!!, edt_email.text.toString(), countrycode!!, edt_phonenumber.text.toString())

                    EventBus.getDefault().postSticky(jsonObject)

                    var intent = Intent(this@ConfirmActivity, OtpVerificationActivity::class.java)
                    intent.putExtra("phonenumber", edt_phonenumber.text.toString())
                    intent.putExtra("countryCode", "+" + ccp.selectedCountryCode.toString())
                    intent.putExtra("from", "facebookLogin")
                    startActivity(intent)
                }
            }
        }
    }


    private fun validate(): Boolean {

        if (etv_first.text.toString().isEmpty()) {

            showMessage(R.string.firstname_validation)
            return false
        }
        if (etv_last.text.toString().isEmpty()) {
            showMessage(R.string.lastname_validation)
            return false
        }
        if (!edt_email.text.toString().isEmpty() && !Utils.isEmailValid(edt_email.text.toString())) {
            showMessage(R.string.email_validation)
            return false
        }
        if (edt_phonenumber.text!!.toString().trim().length < 8) {
            showMessage(R.string.phone_validation)
            return false
        }
        return true
    }


}


