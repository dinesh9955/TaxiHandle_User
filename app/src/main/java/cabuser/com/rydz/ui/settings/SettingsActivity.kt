package cabuser.com.rydz.ui.settings

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.profile.ProfileActivity
import cabuser.com.rydz.ui.register.PhoneLoginActivity
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.edit_header.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * SettingsActivity displays terms , FAQ , About , navigation to profile , language change and logout functionality
 */
class SettingsActivity : BaseActivity(), Callback<NotificationResponse> {

    lateinit var lang_str: String
    var isChecked = true
    var isBroadCastChecked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.tv_terms -> {
                startActivity(Intent(this, PrivacyPolicyActivity::class.java).apply {
                    putExtra("show", getString(R.string.terms))
                })
            }

            R.id.tv_faq -> {


                startActivity(Intent(this, PrivacyPolicyActivity::class.java).apply {
                    putExtra("show", getString(R.string.faq))
                })
            }

            R.id.tv_about -> {
                startActivity(Intent(this, PrivacyPolicyActivity::class.java).apply {
                    putExtra("show", getString(R.string.about))
                })
            }

            R.id.tv_signOut -> {
                logOutPopUp()
            }

            R.id.tv_privacy -> {
                startActivity(Intent(this, PrivacyPolicyActivity::class.java).apply {
                    putExtra("show", getString(R.string.privacy_policy))
                })
            }

            R.id.cl_mainSetting -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.tv_contactus -> {
                startActivity(Intent(this, ContactUsActivity::class.java))
            }


        }
    }


    private fun inits() {

        tv_title.visibility = View.VISIBLE
        tv_title.setText(R.string.settings)
        mProgress = CustomeProgressDialog(this)
        val prefs = PreferenceHelper.defaultPrefs(this@SettingsActivity)
        lang_str = prefs.getString(PreferenceHelper.Key.APPLANGUAGE, "")!!



        if (RydzApplication.user_obj != null) {
            tv_profileName?.setText(RydzApplication.user_obj!!.firstName + " " + RydzApplication.user_obj!!.lastName)
            tv_phoneNumber?.setText(RydzApplication.user_obj!!.countryCode + RydzApplication.user_obj!!.phone)
            try {


            if (RydzApplication.user_obj!!.profilePic != null && !RydzApplication.user_obj!!.profilePic.isEmpty())
                Glide.with(this).load(RydzApplication.BASEURLFORPHOTO + RydzApplication.user_obj!!.profilePic).into(iv_profile)
            else
                Glide.with(this).load(R.drawable.group).into(iv_profile)
            }catch (e:Exception)
            {

            }

            if (RydzApplication.user_obj!!.sendNoti == 0) {
                isChecked = false
                switch_notification.setImageResource(R.drawable.off)
            } else {
                isChecked = true
                switch_notification.setImageResource(R.drawable.on)
            }


            if (RydzApplication.user_obj!!.isBroadcast == 0) {
                isBroadCastChecked = false
                switch_broadcastnotification.setImageResource(R.drawable.off)
            } else {
                isBroadCastChecked = true
                switch_broadcastnotification.setImageResource(R.drawable.on)
            }

        }





        switch_notification.setOnClickListener {
            if (isChecked) {
                handleDisableAccount(0, getString(R.string.disable_booking_notification))

            } else {
                toHandleBookingNotifications()
            }


        }

        switch_broadcastnotification.setOnClickListener {


            if (isBroadCastChecked) {
                handleDisableAccount(1, getString(R.string.disable_broadcast_notification))

            } else {
                toHandleBroadcastNotifications()
            }


        }


    }


    override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
        Log.e("17", "failure")
    }

    override fun onResponse(call: Call<NotificationResponse>, response: Response<NotificationResponse>) {
        mProgress!!.dismiss()


            if (response.body()!!.success!! && response.body()!!.user != null) {

                val prefs = PreferenceHelper.defaultPrefs(this)
                prefs[PreferenceHelper.Key.REGISTEREDUSER] = response.body()!!.user
                RydzApplication.user_obj = response.body()!!.user

                if (RydzApplication.user_obj!!.sendNoti == 0) {
                    isChecked = false
                    switch_notification.setImageResource(R.drawable.off)
                } else {
                    isChecked = true
                    switch_notification.setImageResource(R.drawable.on)
                }
                if (RydzApplication.user_obj!!.isBroadcast == 0) {
                    isBroadCastChecked = false
                    switch_broadcastnotification.setImageResource(R.drawable.off)
                } else {
                    isBroadCastChecked = true
                    switch_broadcastnotification.setImageResource(R.drawable.on)
                }
            }


    }

    /**
     * To logout from app
     */
    fun logOutPopUp() {
        val logoutDial = Dialog(this@SettingsActivity)
        //        logoutDial.setTitle(R.string.Logout);
        logoutDial.requestWindowFeature(Window.FEATURE_NO_TITLE)
        logoutDial.setContentView(R.layout.logout_dialog)
        val cancel = logoutDial.findViewById<TextView>(R.id.tv_cancel)
        val ok = logoutDial.findViewById<TextView>(R.id.tv_ok)
        cancel.setOnClickListener(View.OnClickListener { logoutDial.dismiss() })
        ok.setOnClickListener(View.OnClickListener {

            //clear shared prefrences
            val sharedPreference = PreferenceHelper.defaultPrefs(applicationContext)
            val deviceToken = sharedPreference.getString(PreferenceHelper.Key.FCMTOKEN, "")
            val appLang = sharedPreference.getString(PreferenceHelper.Key.APPLANGUAGE, "")

            var editor = sharedPreference.edit()
           // editor.clear()
            editor.remove(PreferenceHelper.Key.REGISTEREDUSER)
            editor.commit()

            val prefs = PreferenceHelper.defaultPrefs(this)
            prefs[PreferenceHelper.Key.FCMTOKEN] = deviceToken
            prefs[PreferenceHelper.Key.APPLANGUAGE] = appLang


            val intent = Intent(this@SettingsActivity, PhoneLoginActivity::class.java)
            startActivity(intent)
            finishAffinity();
            finish()

            logoutDial.dismiss()

        })
        logoutDial.show()
    }


    override fun onResume() {
        super.onResume()
        inits()
    }


    //got Accept fragment if click on notification request
    public fun handleDisableAccount(notificationType: Int, notificationMsg: String) {


        try {
            AlertDialog.Builder(this@SettingsActivity)
                    .setTitle("Alert")
                    .setCancelable(false)
                    .setMessage(notificationMsg)
                    .setNegativeButton(android.R.string.no, object : DialogInterface.OnClickListener {
                        override fun onClick(arg0: DialogInterface, arg1: Int) {

                            arg0.dismiss()

                        }
                    })
                    .setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener {
                        override fun onClick(arg0: DialogInterface, arg1: Int) {
                            if (notificationType == 0) {
                                toHandleBookingNotifications()
                            } else {
                                toHandleBroadcastNotifications()
                            }


                        }
                    }).create().show()

        } catch (e: java.lang.Exception) {
        }


    }


    private fun toHandleBroadcastNotifications() {
        mProgress!!.show()
        var notificationRequest: NotificationRequest = NotificationRequest()
        if (isBroadCastChecked) {
            notificationRequest.isBroadcast = "0"
        } else {
            notificationRequest.isBroadcast = "1"
        }
        if (isChecked) {
            notificationRequest.sendNoti = "1"
        } else {
            notificationRequest.sendNoti = "0"
        }
        notificationRequest.userId = RydzApplication.user_obj!!.id
        RydzApplication.getRetroApiClient().onNotificationSettingsChange(notificationRequest).enqueue(this)

    }


    private fun toHandleBookingNotifications() {
        mProgress!!.show()
        var notificationRequest: NotificationRequest = NotificationRequest()
        if (isChecked) {
            notificationRequest.sendNoti = "0"
        } else {
            notificationRequest.sendNoti = "1"
        }
        if (isBroadCastChecked) {
            notificationRequest.isBroadcast = "1"
        } else {
            notificationRequest.isBroadcast = "0"
        }
        notificationRequest.userId = RydzApplication.user_obj!!.id
        RydzApplication.getRetroApiClient().onNotificationSettingsChange(notificationRequest).enqueue(this)
    }

}
