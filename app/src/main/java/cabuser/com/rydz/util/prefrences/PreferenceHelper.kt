package cabuser.com.rydz.util.prefrences

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import cabuser.com.rydz.ui.home.payment.addcard.Card
import cabuser.com.rydz.ui.profile.User
import com.google.gson.Gson

//shared prefrence helper class is used to save ,,edit and get data from shared prefrences
object PreferenceHelper {


    object Key {

        val REGISTEREDUSER = "registereduser"
        val FCMTOKEN = "fcm_token"
        val LOGINTYPE = "login_type" //for facebook or phone login
        val APPLANGUAGE = "app_language"
        val EMAIL="email"
        val PASSWORD="password"
        val REMEMBERME="rememberme"
        val WALLETSELECTED = "isWalletSelected"
        val PAYMENTMODE="paymentmode"
        val CARD="card"
        val CASH="cash"
        val SAVEDCARD="savedcard"
        val WALLET="wallet"



    }


    fun defaultPrefs(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)


    inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = this.edit()
        operation(editor)
        editor.apply()
    }

    /**
     * puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key]
     */
    operator fun SharedPreferences.set(key: String, value: Any?) {
        when (value) {
            is String? -> edit({ it.putString(key, value) })
            is Int -> edit({ it.putInt(key, value) })
            is Boolean -> edit({ it.putBoolean(key, value) })
            is Float -> edit({ it.putFloat(key, value) })
            is Long -> edit({ it.putLong(key, value) })
            is User -> {  //Register User object
                val gson = Gson()
                val json = gson.toJson(value)
                edit({ it.putString(key, json) })
            }
            is Card ->
            {
                val gson = Gson()
                val json = gson.toJson(value)
                edit({ it.putString(key, json) })
            }
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }

    /**
     * finds value on given key.
     * [T] is the type of value
     * @param defaultValue optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
     */
    operator inline fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): Any? {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String) as T?
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
            Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?


            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }


}