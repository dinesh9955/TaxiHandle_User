package cabuser.com.rydz.ui.login

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.databinding.ActivityEmailLoginBinding
import cabuser.com.rydz.ui.home.MainActivity
import cabuser.com.rydz.ui.register.ForgotActivity
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.Key.REMEMBERME
import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import kotlinx.android.synthetic.main.activity_email_login.*

/**
 * This class is used for login with email and password
 */
class EmailLoginActivity : BaseActivity() {


    var binding: ActivityEmailLoginBinding? = null
    var viewmodel: EmailLoginViewModel? = null
    private var mEditor: SharedPreferences.Editor?=null
    private var mPrefs: SharedPreferences?=null
    private var rememberMe : Boolean=false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_email_login)
        viewmodel = ViewModelProviders.of(this).get(EmailLoginViewModel::class.java)
        binding?.viewmodel = viewmodel
        mProgress = CustomeProgressDialog(this@EmailLoginActivity)
        mEditor = getSharedPreferences(REMEMBERME, MODE_PRIVATE).edit()

        initObservables()

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back ->
            {
                finish()
            }

            R.id.tv_forgotpsswd->
            {
                startActivity(Intent(this@EmailLoginActivity,ForgotActivity::class.java))
            }

            R.id.tv_rememberMe ->{
                if(rememberMe){
                    rememberMe=false
                    checkbox.isChecked=false
                }else{
                    rememberMe=true
                    checkbox.isChecked=true
                }
            }

        }
    }


    private fun initObservables() {



        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            //Toast.makeText(this,isChecked.toString(),Toast.LENGTH_SHORT).show()
            rememberMe = isChecked
        }
        mPrefs = getSharedPreferences(REMEMBERME, MODE_PRIVATE)

        if(mPrefs!!.contains(REMEMBERME) && mPrefs!!.getString(PreferenceHelper.Key.EMAIL, "")?.isNotEmpty()!! && mPrefs!!.getString("password", "")!!.isNotEmpty()){
            viewmodel!!.email!!.set(mPrefs!!.getString("email", ""))
            viewmodel!!.password!!.set(mPrefs!!.getString("password", ""))
            rememberMe = mPrefs!!.getBoolean(REMEMBERME, false)
            checkbox.isChecked = rememberMe
        }

        viewmodel?.mProgress?.observe(this, Observer {
            if (it!!) {
                mProgress!!.show()
            } else
                mProgress!!.dismiss()
        })

        viewmodel?.userLogin?.observe(this, Observer { user_obj ->

            if (user_obj?.success!!) {
                if (user_obj.user != null) {
                   /* val prefs = PreferenceHelper.defaultPrefs(this)
                    prefs[PreferenceHelper.Key.REGISTEREDUSER] = user_obj?.user //setter*/

                    RydzApplication.prefs = PreferenceHelper.defaultPrefs(this)
                    RydzApplication.prefs[PreferenceHelper.Key.LOGINTYPE] = "phoneLogin"
                    RydzApplication.prefs[PreferenceHelper.Key.REGISTEREDUSER] = user_obj.user //setter
                    RydzApplication.user_obj = user_obj.user!!

                    if(rememberMe) {

                        mEditor!!.putString(PreferenceHelper.Key.EMAIL, viewmodel!!.email!!.get())
                        mEditor!!.putString(PreferenceHelper.Key.PASSWORD, viewmodel!!.password!!.get())
                        mEditor!!.putBoolean(REMEMBERME, rememberMe)

                    }
                    else{
                        mEditor!!.remove(REMEMBERME)

                    }
                    mEditor!!.commit()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                } else {
                    showMessage(getString(R.string.str_wrong))
                }


            } else {
                showMessage(user_obj.message)
            }


        })
    }


}
