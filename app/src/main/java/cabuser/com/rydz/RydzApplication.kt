package cabuser.com.rydz

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import cabuser.com.rydz.api.ApiClient
import cabuser.com.rydz.api.ApiInterface
import cabuser.com.rydz.socket.AppSocketListener
import cabuser.com.rydz.ui.profile.User
import cabuser.com.rydz.util.common.ConstVariables.Companion.STRIPEPUBLISHKEY
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.get
import com.google.gson.Gson
import com.stripe.android.PaymentConfiguration

/**
 * CapyApplication contains the baseurl of Apis, adminid of app and sinch key
 */

open class RydzApplication : Application() {

    companion object {
        lateinit var prefs: SharedPreferences
        lateinit var gson: Gson
        var ctx: Context? = null
        var deviceToken: String? = ""


        //Apis Base url & adminkey of app
      /*  var BASEURL = "https://appgrowthcompany.com:7080/v1/"
        var BASEURLFORPHOTO = "https://appgrowthcompany.com:7080/"
        var adminId = "5f69f4fac158265871588117";*/
          var BASEURL = "https://api.rydzgroup.com/v1/"
        var BASEURLFORPHOTO = "https://api.rydzgroup.com/"
       var adminId = "5f7aed891b111e4b680552a4"




        var poolingType = "5d39a46d71df2e1fc8c7f000"

        //for sinch
        val SINCH_APP_KEY = "95c3e8ef-1a32-43fa-8030-743ab6c9f333"
//        val SINCH_APP_KEY = "98820ee9-bde3-4143-ae93-275c6276876b"
        var user_obj: User? = null
        var tempuser_obj: User? = null

        fun getRetroApiClient(): ApiInterface {
            return ApiClient.getClient().create(ApiInterface::class.java)
        }

        fun getContext(): Context? {
            return ctx
        }
    }

    override fun onCreate() {
        super.onCreate()
        ctx = applicationContext
        prefs = PreferenceHelper.defaultPrefs(applicationContext)

        gson = Gson()
        if (prefs.contains(PreferenceHelper.Key.REGISTEREDUSER)) { // if data is stored in shared prefrences
            val gson_String = prefs[PreferenceHelper.Key.REGISTEREDUSER, ""]
            user_obj = gson.fromJson(gson_String.toString(), User::class.java) as User
        } else {
            user_obj = User()
        }
        deviceToken = prefs[PreferenceHelper.Key.FCMTOKEN, ""] as String?

        initializeSocket(applicationContext)
        PaymentConfiguration.init(
                applicationContext,
                STRIPEPUBLISHKEY
        )
    }

    fun initializeSocket(applicationContext: Context) {
        try {
            AppSocketListener.getInstance().initialize(applicationContext)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    fun destroySocketListener() {
        try {
            AppSocketListener.getInstance().destroy()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    override fun onTerminate() {
        super.onTerminate()
        destroySocketListener()
    }


}