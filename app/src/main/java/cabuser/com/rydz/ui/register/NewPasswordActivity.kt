package cabuser.com.rydz.ui.register

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import cabuser.com.rydz.R
import cabuser.com.rydz.databinding.ActivityNewPasswordBinding
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import kotlinx.android.synthetic.main.header_with_back.*

class NewPasswordActivity : BaseActivity() {


    var binding: ActivityNewPasswordBinding? = null
    var viewmodel: NewPasswordViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initObservables()
        initViews()

    }

    private fun initViews() {
        cl_headermain.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun initObservables() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_password)
        viewmodel = ViewModelProviders.of(this).get(NewPasswordViewModel::class.java)
        binding?.viewmodel = viewmodel
        tv_title.visibility=View.VISIBLE
        tv_title.setText(R.string.setpassword)
        mProgress = CustomeProgressDialog(this)

        viewmodel?.type=intent.getStringExtra("type").toString()
        if(viewmodel?.type.equals("KEY_PHONE"))
        {
            viewmodel?.phoneNumber= intent.getStringExtra("phonenumber").toString()
            viewmodel?.countryCode=intent.getStringExtra("countryCode").toString()
        }
        else
        {
            viewmodel?.email=intent.getStringExtra("email").toString()
        }
        viewmodel?.mProgress?.observe(this, Observer {
            if (it!!) {
                mProgress!!.show();
            } else
                mProgress!!.dismiss()
        })



        viewmodel?.userLogin?.observe(this, Observer { user ->

            if (user.success!!) {
                showMessage(user.message.toString())
                startActivity(Intent(this@NewPasswordActivity,PhoneLoginActivity::class.java))
                finishAffinity()
            } else {
                showMessage(user.message.toString())
            }
        })
    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when(v!!.id)
        {
            R.id.iv_back->
            {
                finish()
            }
            R.id.fab_next->
            {

                    viewmodel?.changePassword()

            }
        }
    }


}
