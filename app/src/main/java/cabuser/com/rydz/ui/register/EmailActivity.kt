package cabuser.com.rydz.ui.register

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.Utils
import kotlinx.android.synthetic.main.activity_email.*
import kotlinx.android.synthetic.main.header_back_onregister.*

/**
 * this class to used to get email from user during registration
 */
class EmailActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        initView()
    }

    private fun initView() {
        cl_headermain.setBackgroundColor(Color.TRANSPARENT)
        tv_haveYour.text= RydzApplication.tempuser_obj!!.firstName+", "+getString(R.string.let_us_have_your)
    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.iv_back -> {
                finish()
            }
            R.id.fab_next -> {
                if (!edt_email.getText().toString().isEmpty()) {
                    if (Utils.isEmailValid(edt_email.getText().toString())) {
                        val intent = Intent(this, PasswordActivity::class.java)
                        RydzApplication.tempuser_obj!!.email = edt_email.text.toString().trim()
                        startActivity(intent)
                    } else {
                        showMessage(R.string.email_validation)
                    }

                } else {
                    val intent = Intent(this, PasswordActivity::class.java)
                    RydzApplication.tempuser_obj!!.email = edt_email.text.toString().trim()
                    startActivity(intent)
                }

            }


        }
    }

}
