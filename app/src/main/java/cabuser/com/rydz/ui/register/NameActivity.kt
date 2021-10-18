package cabuser.com.rydz.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.ui.profile.User
import cabuser.com.rydz.util.base.BaseActivity
import kotlinx.android.synthetic.main.activity_name.*

class NameActivity : BaseActivity(), View.OnClickListener {


    lateinit var bundle: Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)

    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.iv_back -> {
                finish()
            }
            R.id.fab_next -> {

                if(validate()) {
                    val next_intent = Intent(this, EmailActivity::class.java)
                    RydzApplication.tempuser_obj = User()
                    RydzApplication.tempuser_obj!!.countryCode = intent.getStringExtra("countryCode")
                    RydzApplication.tempuser_obj!!.phone = intent.getStringExtra("phonenumber")
                    RydzApplication.tempuser_obj!!.firstName = etv_first.text.toString().trim()
                    RydzApplication.tempuser_obj!!.lastName = etv_last.text.toString().trim()
                    startActivity(next_intent)
                }


            }

        }
    }


    private fun validate(): Boolean {
        if (etv_first.getText().toString().trim().isEmpty()) {

          showMessage(R.string.firstname_validation)
            return false
        } else if (etv_last.getText().toString().trim().isEmpty()) {
            showMessage( R.string.lastname_validation)
            return false
        }
        return true
    }


}


