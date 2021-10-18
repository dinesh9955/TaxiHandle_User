package cabuser.com.rydz.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.FacebookEventObject
import cabuser.com.rydz.ui.home.MainActivity
import cabuser.com.rydz.ui.login.EmailLoginActivity
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.activity_phonelogin.*
import org.greenrobot.eventbus.EventBus
import java.util.*


//This class contains the functionality to get user phone number
class PhoneLoginActivity : BaseActivity(), View.OnClickListener {


    var viewmodel: PhoneLoginViewModel? = null
    private var str_firstname: String? = null
    private var str_lastname: String? = null
    private var str_email = ""
    private var str_id = ""
    var jsonObject: FacebookEventObject? = null


    lateinit var callbackManager: CallbackManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_phonelogin)
        viewmodel = ViewModelProviders.of(this).get(PhoneLoginViewModel::class.java)
        callbackManager = CallbackManager.Factory.create()
        init()
        mProgress = CustomeProgressDialog(this)
        initObservables()

    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.iv_back -> {
                finish()
            }
            R.id.fab_next -> {

                if (edt_phonenumber.text!!.toString().trim().length >= 8) {
                    val intent = Intent(this, OtpVerificationActivity::class.java)
                    intent.putExtra("phonenumber", edt_phonenumber.text!!.toString().trim())
                    intent.putExtra("countryCode", "+" + ccp.selectedCountryCode.toString())
                    intent.putExtra("from", "phoneLogin")
                    startActivity(intent)
                } else {
                    showMessage(getString(R.string.contact_validation))
                }

            }

            R.id.ll_fb -> {

                val intent = Intent(this, SelectSocialAccount::class.java)
                startActivity(intent)

            }

            R.id.tv_loginWithEmail -> {

                val intent = Intent(this, EmailLoginActivity::class.java)
                startActivity(intent)

            }
            R.id.tv_forgotpsswd -> {
                startActivity(Intent(this@PhoneLoginActivity, ForgotActivity::class.java))
            }


        }
    }

    //intialization
    private fun init() {


        ccp.setAutoDetectedCountry(true)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    //get info from facebook and hit api
    private fun facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this@PhoneLoginActivity, Arrays.asList("public_profile,email"))

        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(loginResult.accessToken
                ) { `object`, response ->

                    try {
                        str_firstname = `object`.getString("first_name")
                    } catch (e: Exception) {
                        str_firstname = ""
                    }

                    try {
                        str_lastname = `object`.getString("last_name")
                    } catch (e: Exception) {
                        str_lastname = ""
                    }
                    try {
                        str_email = `object`.getString("email")
                    } catch (e: Exception) {
                        str_email = ""
                    }
                    try {
                        str_id = `object`.getString("id")
                    } catch (e: Exception) {
                        str_id = ""
                    }

                    jsonObject = FacebookEventObject(str_firstname!!, str_lastname!!, str_id, str_email, ccp.selectedCountryCode, "")

                    if (jsonObject != null && str_id.length > 0)
                        viewmodel?.checkFacebookAccountAlreadyRegistered(str_id, str_email)


                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,first_name,last_name,gender")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                if (AccessToken.getCurrentAccessToken() == null) {
                    return
                    // already logged out
                }
                GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, object : GraphRequest.Callback {
                    override fun onCompleted(graphResponse: GraphResponse) {
                        LoginManager.getInstance().logOut()
                        facebookLogin()
                    }
                }).executeAsync()

            }

            override fun onError(e: FacebookException) {

                Toast.makeText(this@PhoneLoginActivity, e.message.toString(), Toast.LENGTH_SHORT).show()

                AccessToken.setCurrentAccessToken(null)
            }
        })
    }


    private fun initObservables() {

        viewmodel?.mProgress?.observe(this, Observer {
            if (it!!) {
                mProgress!!.show()
            } else
                mProgress!!.dismiss()
        })

        viewmodel?.userLogin?.observe(this, Observer { user_obj ->

            if (user_obj?.success!!) {
                if (user_obj.isUser == 1) //if user is already registered through facebook
                {
                    if (user_obj.user != null) {
                        val prefs = PreferenceHelper.defaultPrefs(this)
                        prefs[PreferenceHelper.Key.REGISTEREDUSER] = user_obj.user //setter
                        RydzApplication.prefs = PreferenceHelper.defaultPrefs(this)
                        RydzApplication.prefs[PreferenceHelper.Key.LOGINTYPE] = "phoneLogin"
                        RydzApplication.user_obj = user_obj.user!!

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    } else {
                        if (jsonObject != null) {
                            EventBus.getDefault().postSticky(jsonObject)
                            val intent = Intent(this@PhoneLoginActivity, ConfirmActivity::class.java)
                            startActivity(intent)
                        }
                    }
                } else {
                    if (jsonObject != null) {
                        EventBus.getDefault().postSticky(jsonObject)
                        val intent = Intent(this@PhoneLoginActivity, ConfirmActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                if (jsonObject != null) {
                    EventBus.getDefault().postSticky(jsonObject)
                    val intent = Intent(this@PhoneLoginActivity, ConfirmActivity::class.java)
                    startActivity(intent)
                } else {
                    showMessage(getString(R.string.str_wrong))
                }
            }


        })
    }


}
