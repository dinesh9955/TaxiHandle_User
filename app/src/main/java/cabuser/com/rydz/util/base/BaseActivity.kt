package cabuser.com.rydz.util.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import cabuser.com.rydz.R
import cabuser.com.rydz.util.common.ConnectivityReceiver
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.common.Utils
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.whatsappclone.utils.common.NetworkUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by root on 12/2/18.
 */

open class BaseActivity : AppCompatActivity(), BaseView, View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {


    private var mSnackBar: Snackbar? = null
    lateinit var receiver: ConnectivityReceiver
    var mProgress: CustomeProgressDialog? = null


    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessage(isConnected)
    }


    override fun onClick(v: View?) {

    }

    override fun isNetworkConnected(): Boolean {
        return NetworkUtils.checkInternet(applicationContext)
    }

    override fun showServerError() {
    }


    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            receiver = ConnectivityReceiver(this, Handler())
            registerReceiver(receiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        } catch (e: Exception) {
        }

    }


    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        if (mProgress != null && mProgress!!.isShowing) {
            mProgress!!.cancel()
        }
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {
        }
        super.onDestroy()
    }


    override fun showProgress(msg: String) {
        mProgress = CustomeProgressDialog(this)
        mProgress!!.show()
    }

    override fun hideProgress() {
        mProgress!!.cancel()
    }

    override fun onSuccess(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    private fun showMessage(isConnected: Boolean) {

        try {

            if (!isConnected) {

                val messageToUser = getString(R.string.check_internet)//TODO
                mSnackBar = Snackbar.make(
                        findViewById(R.id.topLayout),
                        messageToUser,
                        Snackbar.LENGTH_LONG
                ) //Assume "rootLayout" as the root layout of every activity.
                mSnackBar?.duration = Snackbar.LENGTH_INDEFINITE
                mSnackBar?.show()
            } else {
                mSnackBar?.dismiss()
            }

        } catch (e: java.lang.Exception) {

        }
    }

    fun onError(message: String?) {
        if (message != null) {
            showSnackBar(message)
        } else {
            showSnackBar(getString(R.string.error))
        }
    }

    fun onError(@StringRes resId: Int) {
        onError(getString(resId))
    }

    fun showMessage(message: String?) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }

    fun showMessage(@StringRes resId: Int) {
        showMessage(getString(resId))
    }


    fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    fun showServerError(response: Int) {
        when (response) {
            404 -> showMessage(R.string.server_not_found)
            500 -> showMessage(R.string.server_broken)
            else -> showMessage(R.string.error_unknown)
        }
    }

    fun showSnackBar(message: String) {
        Utils.getCustomSnackBar(window.decorView, message)
    }

    companion object {
        //var mProgress: CustomeProgressDialog? = null

    }

    fun showAlert(message: String) {
        Handler(Looper.getMainLooper()).post(object : Runnable {
            override fun run() {
                if (message != null && !message.isEmpty()) {
                    AlertDialog.Builder(this@BaseActivity)
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener {
                                override fun onClick(arg0: DialogInterface, arg1: Int) {
                                    arg0.dismiss()
                                }
                            }).create().show()
                }

            }
        })

    }


    fun showKeyboard(editText: EditText) {
        if (editText.text.length > 0) {
            editText.setSelection(editText.text.length)
        }
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val editText = currentFocus

        if (editText != null)
            imm.showSoftInput(editText, 0)
    }


    fun getTimeInMilliseconds(date: String): Long {

        var sdf = SimpleDateFormat("dd:MM:yyyy hh:mm a")
        try {
            var mDate = sdf.parse(date)
            var timeInMilliseconds = mDate.time
            Log.e("Date in milli :: ", timeInMilliseconds.toString())
            return timeInMilliseconds
        } catch (e: ParseException) {

            e.printStackTrace()
        }

        return 0

    }




    fun getDateFromMilliseconds( milliSeconds : Long,  dateFormat : String ): String {
        // Create a DateFormatter object for displaying date in specified format.
        var formatter =  SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        var  calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime())
    }



    @SuppressLint("NewApi")
    public fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                val runningProcesses = am.runningAppProcesses
                for (processInfo in runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (activeProcess in processInfo.pkgList) {

                            if (activeProcess == context.packageName) {
                                isInBackground = false
                            }
                        }
                    }
                }
            } else {
                val taskInfo = am.getRunningTasks(1)
                val componentInfo = taskInfo[0].topActivity

                if (componentInfo?.packageName == context.packageName) {
                    isInBackground = false
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }


        return isInBackground
    }





    /**
     * To showzoomableimage
     */
    fun showZoomableImage(img : String) {
        val zoomableImgDialog = Dialog(this)
        zoomableImgDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        zoomableImgDialog.setContentView(R.layout.dialog_image)
        zoomableImgDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
        val myZoomageView = zoomableImgDialog.findViewById<ImageView>(R.id.myZoomageView)
        val ivClose = zoomableImgDialog.findViewById<ImageView>(R.id.iv_close)
        Glide.with(this@BaseActivity).load(img).into(myZoomageView)
        ivClose.setOnClickListener {
            zoomableImgDialog.dismiss()
        }
        zoomableImgDialog.show()
    }


}
