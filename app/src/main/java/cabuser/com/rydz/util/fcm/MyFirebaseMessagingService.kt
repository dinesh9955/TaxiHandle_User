package cabuser.com.rydz.util.fcm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.ui.chat.ChatActivity
import cabuser.com.rydz.ui.home.MainActivity
import cabuser.com.rydz.ui.register.PhoneLoginActivity
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private var mChannel: NotificationChannel? = null
    private var notifManager: NotificationManager? = null
    lateinit var prefs: SharedPreferences
    private val notificationIcon: Int
        //notification icon
        get() {
            val icon = R.drawable.logo
            val useWhiteIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
            return if (useWhiteIcon) icon else icon
        }


    override fun onNewToken(token: String) {


        Log.e("token ", "Token : ${token}")

        if (token != null && token.length > 0) {
            val prefs = PreferenceHelper.defaultPrefs(this)
            prefs[PreferenceHelper.Key.FCMTOKEN] = token
            RydzApplication.deviceToken = token

            val editor = prefs.edit()
            editor.putString(PreferenceHelper.Key.FCMTOKEN, token)
            editor.commit()
        }
    }




    //notification types: 10= Admin panel notification(will be shown on foreground an background), 11=chat notifications and otherwise ride notifications
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        try {
            prefs = PreferenceHelper.defaultPrefs(applicationContext)
            Log.e("remotedata : ", remoteMessage!!.data.toString())
            if (prefs.contains(PreferenceHelper.Key.REGISTEREDUSER)) {
                if (remoteMessage.data.size > 0) {
                    val jsonObject = JSONObject(remoteMessage.data as Map<*, *>)
                    Log.e("jsonObject", jsonObject.toString())
                    displayCustomNotification(jsonObject)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }


    //to set notification content
    @SuppressLint("WrongConstant")
    private fun displayCustomNotification(jsonObject: JSONObject?) {
        clearNotifications(this)
        Log.e("DATA : ", jsonObject!!.toString() + "")
        try {

            if (notifManager == null) {
                notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            var title = jsonObject.get("title").toString() /*getString(R.string.you_have_new_message)*/
            val type = jsonObject.get("type").toString()
            var message = ""
            if (type == "11") {
                val myData = JSONObject(jsonObject.get("notiData").toString())
                val newMessageObject = JSONObject(myData.get("driver").toString())
                title = getString(R.string.you_have_new_message)
                message = getString(R.string.you_have_new_message) + " from : " + newMessageObject.get("firstName").toString() + " " + newMessageObject.get("lastName").toString()
            } else {
                message = jsonObject.get("body").toString()
            }

            val icon = R.drawable.logo

            try {
                if ((type == "16" || type == "1") && mainScreen != null) {

                    mainScreen!!.checkRideStatus_Socket()
                } else if (type == "54" && mainScreen != null) {
                    mainScreen!!.handleDisable()
                }
            } catch (e: Exception) {

            }
            var pendingIntent: PendingIntent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val builder: NotificationCompat.Builder
                var intent: Intent
                if (type == "11") {
                    intent = Intent(this, ChatActivity::class.java)
                    val myData = JSONObject(jsonObject.get("notiData").toString())
                    val newMessageObject = JSONObject(myData.get("user").toString())
                    val driverObject = JSONObject(myData.get("driver").toString())
                    intent.putExtra("fromNotification", "not")
                    intent.putExtra("bookingId", myData.get("bookingId").toString())
                    intent.putExtra("profilePic", driverObject.get("profilePic").toString())
                    intent.putExtra("id", newMessageObject.get("id").toString())
                    intent.putExtra("driverId", driverObject.get("id").toString())
                    intent.putExtra("profilePic", driverObject.get("profilePic").toString())
                    intent.putExtra("name", driverObject.get("firstName").toString() + " " + driverObject.get("lastName").toString())


                } else {
                    intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

                    if (type != "11" && type != "54") {
                        val myData = JSONObject(jsonObject.get("notiData").toString())
                        intent.putExtra("fromNotification", myData.toString())
                    }
                    else if(type .equals("54"))
                    {
                        intent = Intent(this, PhoneLoginActivity::class.java)
                    }
                }


                val importance = NotificationManager.IMPORTANCE_HIGH
                if (mChannel == null) {
                    mChannel = NotificationChannel(packageName, title, importance)
                    mChannel!!.description = message
                    mChannel!!.enableVibration(true)
                    notifManager!!.createNotificationChannel(mChannel!!)
                }
                builder = NotificationCompat.Builder(this, packageName)

                pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                builder.setContentTitle(title)
                        .setSmallIcon(notificationIcon) // required
                        .setContentText(message)  // required
                        .setAutoCancel(true)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setLargeIcon(BitmapFactory.decodeResource(resources, icon))
                        .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack))
                        .setBadgeIconType(icon)
                        .setContentIntent(pendingIntent)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                val notification = builder.build()
                notifManager!!.notify(1251, notification)

            } else {

                var intent: Intent
                if (type == "11") {
                    intent = Intent(this, ChatActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    val myData = JSONObject(jsonObject.get("notiData").toString())
                    val newMessageObject = JSONObject(myData.get("user").toString())
                    val driverObject = JSONObject(myData.get("driver").toString())
                    intent.putExtra("fromNotification", "not")
                    intent.putExtra("bookingId", myData.get("bookingId").toString())
                    intent.putExtra("profilePic", driverObject.get("profilePic").toString())
                    intent.putExtra("id", newMessageObject.get("id").toString())
                    intent.putExtra("driverId", driverObject.get("id").toString())
                    intent.putExtra("profilePic", driverObject.get("profilePic").toString())
                    intent.putExtra("name", driverObject.get("firstName").toString() + " " + driverObject.get("lastName").toString())
                } else {
                    intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP


                    if (type != "11" && type != "54") {
                        val myData = JSONObject(jsonObject.get("notiData").toString())
                        intent.putExtra("fromNotification", myData.toString())
                    }else if(type .equals("54"))
                    {
                        intent = Intent(this, PhoneLoginActivity::class.java)

                    }
                }

                pendingIntent = PendingIntent.getActivity(this, 1251, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                val notificationBuilder = NotificationCompat.Builder(this)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setSmallIcon(notificationIcon)
                        .setContentIntent(pendingIntent)
                        .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorBlack))
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setStyle(NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(message))

                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(1251, notificationBuilder.build())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        var inChatScreen: Boolean? = false
        var mainScreen: MainActivity? = null

    }

    // Clears notification tray messages
    fun clearNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }




}



