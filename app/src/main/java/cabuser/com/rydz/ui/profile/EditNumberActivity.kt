package cabuser.com.rydz.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.R
import cabuser.com.rydz.databinding.ActivityEditNumberBinding
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import kotlinx.android.synthetic.main.activity_edit_number.*

class EditNumberActivity : BaseActivity() {


    var binding: ActivityEditNumberBinding? = null
    var viewmodel: EditNumberViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* setContentView(R.layout.activity_edit_number)*/
        inits()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()

        }
    }


    private fun inits() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_number)
        viewmodel = ViewModelProviders.of(this).get(EditNumberViewModel::class.java)
        binding?.viewmodel = viewmodel
        tv_title.setText(R.string.changeNumber)
        mProgress = CustomeProgressDialog(this)
        ccp.setAutoDetectedCountry(true)
        viewmodel?.countryCode!!.set(intent.getStringExtra("countrycode").toString())
        viewmodel?.phoneNumber!!.set(intent.getStringExtra("phoneNumber").toString())
        ccp.setCountryForPhoneCode(intent.getStringExtra("countrycode").toString().toInt())

        viewmodel?.mProgress?.observe(this, Observer {
            if (it!!) {
                mProgress!!.show()
            } else
                mProgress!!.dismiss()
        })



        viewmodel?.userLogin?.observe(this, Observer { user ->

            if (user.success!!) {
                //  showMessage(user.message)
                viewmodel?.countryCode!!.set(ccp.selectedCountryCode.toString())
                val nameIntent = Intent(this@EditNumberActivity, OTPVerifcationForChangeNumberActivity::class.java)
                nameIntent.putExtra("phonenumber", edt_number.text.toString().trim())
                nameIntent.putExtra("countryCode", ccp.selectedCountryCode.toString())
                startActivity(nameIntent)
                finish()
            } else {
                showMessage(user.message.toString())
            }
        })


    }




}
