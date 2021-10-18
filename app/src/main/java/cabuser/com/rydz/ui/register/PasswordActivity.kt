package cabuser.com.rydz.ui.register

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.databinding.ActivityPasswordBinding
import cabuser.com.rydz.ui.home.MainActivity
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import kotlinx.android.synthetic.main.activity_password.*
import kotlinx.android.synthetic.main.header_with_back.*

/**
 * PasswordActivity is used to get password and hit  registarion api
 */
class PasswordActivity : BaseActivity(), View.OnClickListener {


    var binding: ActivityPasswordBinding? = null
    var viewmodel: RegisterViewModel? = null

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
            R.id.fab_next->
            {
                if(edt_pwd.text.toString().length>=8)
                {
                viewmodel?.registerUser()
            }
            else
            {
                Toast.makeText(this, R.string.strongpwd_validation,Toast.LENGTH_SHORT).show()
            }
            }


        }
    }


    private fun initObservables() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_password)
        viewmodel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        binding?.viewmodel = viewmodel





        mProgress = CustomeProgressDialog(this)

        viewmodel?.mProgress?.observe(this, Observer {
            if (it!!) {
                mProgress!!.show();
            } else
                mProgress!!.dismiss()
        })

        viewmodel?.userLogin?.observe(this, Observer { user ->

            if (user.success!!) {

                RydzApplication.prefs= PreferenceHelper.defaultPrefs(this)
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


}
