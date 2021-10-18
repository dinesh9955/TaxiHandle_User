package cabuser.com.rydz.ui.splash

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.widget.Toast
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.home.MainActivity
import cabuser.com.rydz.ui.profile.User
import cabuser.com.rydz.ui.register.PhoneLoginActivity
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.get
import com.google.gson.Gson
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : BaseActivity() {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds

    lateinit var prefs: SharedPreferences


    lateinit var gson: Gson


    //handler to start next activity
    internal val mRunnable: Runnable = Runnable {

        if (!isFinishing) {

            prefs = PreferenceHelper.defaultPrefs(applicationContext)
            gson = Gson()


            if (prefs.contains(PreferenceHelper.Key.FCMTOKEN)) {
                RydzApplication.deviceToken = prefs.getString(PreferenceHelper.Key.FCMTOKEN, "")
            }
            if (prefs.contains(PreferenceHelper.Key.REGISTEREDUSER)) {

                val gson_String = prefs[PreferenceHelper.Key.REGISTEREDUSER, ""]
                RydzApplication.user_obj = gson.fromJson(gson_String.toString(), User::class.java) as User
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                val intent = Intent(applicationContext, PhoneLoginActivity::class.java)
                intent.putExtra("navigate", "phonelogin")
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
      // getKeyHash()
        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }


    fun getKeyHash() {
        val info: PackageInfo
        try {
            info = packageManager.getPackageInfo("cabuser.com.rydz", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something = String(Base64.encode(md.digest(), 0))
                //String something = new String(Base64.encodeBytes(md.digest()));
                Toast.makeText(this, something ,Toast.LENGTH_SHORT).show()
                Log.e("hash key", something!!)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }

    }


}
