package cabuser.com.rydz.ui.register

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.FacebookEventObject
import cabuser.com.rydz.ui.home.MainActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.common.Utils.Companion.showMessage
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.getClient
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.header_back_onregister.*
import org.greenrobot.eventbus.EventBus
import java.util.*

class SelectSocialAccount : AppCompatActivity() , View.OnClickListener{


    var viewmodel: SelectSocialAccountViewModel? = null
    var mProgress: CustomeProgressDialog? = null
    private var str_firstname: String? = null
    private var str_lastname: String? = null
    private var str_email = ""
    private var str_id = ""
    var jsonObject: FacebookEventObject? = null
    lateinit var gso : GoogleSignInOptions
    var account: GoogleSignInAccount?=null
    var mGoogleSignInClient : GoogleSignInClient?=null
    val RC_SIGN_IN : Int = 234

    lateinit var callbackManager: CallbackManager

    companion object{
        var isGoogle:Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_social_account)

        FacebookSdk.sdkInitialize(applicationContext)

        gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleSignInClient = getClient(this, gso)

        //account = GoogleSignIn.getLastSignedInAccount(this@SelectSocialAccount)!!

        viewmodel = ViewModelProviders.of(this).get(SelectSocialAccountViewModel::class.java)
        initView()
        callbackManager = CallbackManager.Factory.create()
        mProgress = CustomeProgressDialog(this)
        initObservables()

    }

    private fun initView() {
        cl_headermain.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.iv_back -> {
                finish()
            }

            R.id.tv_fbLogin -> {
                Log.e("fb","fb")
                facebookLogin()
            }

            R.id.tv_googleLogin -> {
                Log.e("google","google")
                googleSignIn()
            }
        }
    }

    private fun googleSignIn() {
        val signInIntent : Intent  = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if the requestCode is the Google Sign In code that we defined at starting
        if (requestCode == RC_SIGN_IN) {

            //Getting the GoogleSignIn Task
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                Log.e("call", "12222")
                //Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                Log.e("call", "acoount email--->>>   " + account!!.email!!)


                 isGoogle =true

                try {
                    str_firstname = account.givenName
                } catch (e: Exception) {
                    str_firstname = ""
                }

                try {
                    str_lastname = account.familyName
                } catch (e: Exception) {
                    str_lastname = ""
                }
                try {
                    str_email = account.email!!
                } catch (e: Exception) {
                    str_email = ""
                }
                try {
                    str_id = account.id!!
                } catch (e: Exception) {
                    str_id = ""
                }

                jsonObject = FacebookEventObject(str_firstname!!, str_lastname!!, str_id, str_email, "", "")

                // viewModel.googleLoginUser(account.email, "", account.givenName, account.familyName, account.id)
                if (jsonObject != null && str_id.length > 0)
                     viewmodel?.checkGoogleAccountAlreadyRegistered(str_id,str_email)


                GoogleSignIn.getClient(this@SelectSocialAccount,gso).signOut()

                //authenticating with firebase
                //firebaseAuthWithGoogle(account);
            } catch (e: ApiException) {
                Log.e("call", "google response--> " + e.message)
                Toast.makeText(this@SelectSocialAccount, e.message, Toast.LENGTH_SHORT).show()
            }
        }else{
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    //get info from facebook and hit api
    private fun facebookLogin() {
        LoginManager.getInstance().logInWithReadPermissions(this@SelectSocialAccount, Arrays.asList("public_profile,email"))

        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val request = GraphRequest.newMeRequest(loginResult.accessToken
                ) { `object`, response ->

                    isGoogle =false

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

                    jsonObject = FacebookEventObject(str_firstname!!, str_lastname!!, str_id, str_email, "", "")

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
                Toast.makeText(this@SelectSocialAccount, e.message.toString(), Toast.LENGTH_SHORT).show()
                Log.e("5656",e.message.toString())
                AccessToken.setCurrentAccessToken(null)
            }
        })
    }

    private fun initObservables() {

        viewmodel?.mProgress?.observe(this, androidx.lifecycle.Observer {
            if(it!!){
                mProgress!!.show()
            }
            else{
                mProgress!!.dismiss()
            }
        })

        viewmodel?.userLogin?.observe(this, androidx.lifecycle.Observer { user_obj ->

            if (user_obj?.success!!) {
                if (user_obj?.isUser == 1) //if user is already registered through facebook
                {
                    if (user_obj?.user != null) {
                        val prefs = PreferenceHelper.defaultPrefs(this)
                        prefs[PreferenceHelper.Key.REGISTEREDUSER] = user_obj?.user //setter
                        RydzApplication.prefs=PreferenceHelper.defaultPrefs(this)
                        RydzApplication.prefs[PreferenceHelper.Key.LOGINTYPE] = "phoneLogin"
                        RydzApplication.user_obj = user_obj?.user!!

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    }
                    else {
                        if (jsonObject != null) {
                            EventBus.getDefault().postSticky(jsonObject)
                            val intent = Intent(this@SelectSocialAccount, ConfirmActivity::class.java)
                            startActivity(intent)
                        }
                        else if(isGoogle){
                            EventBus.getDefault().postSticky(jsonObject)
                            val intent = Intent(this@SelectSocialAccount, ConfirmActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
                else {
                    if (jsonObject != null) {
                        EventBus.getDefault().postSticky(jsonObject)
                        val intent = Intent(this@SelectSocialAccount, ConfirmActivity::class.java)
                        startActivity(intent)
                    }
                    else if(isGoogle){
                        EventBus.getDefault().postSticky(jsonObject)
                        val intent = Intent(this@SelectSocialAccount, ConfirmActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else {
                if (jsonObject != null) {
                    EventBus.getDefault().postSticky(jsonObject)
                    val intent = Intent(this@SelectSocialAccount, ConfirmActivity::class.java)
                    startActivity(intent)
                } else {
                    showMessage(getString(R.string.str_wrong),this@SelectSocialAccount)
                }
            }
        })
    }

}
