package cabuser.com.rydz.ui.home

import android.Manifest
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.data.eventbus.PlacesEventObject
import cabuser.com.rydz.databinding.ActivityMainBinding
import cabuser.com.rydz.databinding.NavHeaderMainBinding
import cabuser.com.rydz.socket.AppSocketListener
import cabuser.com.rydz.socket.SocketListener
import cabuser.com.rydz.socket.SocketUrls.*
import cabuser.com.rydz.ui.chat.ChatActivity
import cabuser.com.rydz.ui.freerides.FreeRidesActivity
import cabuser.com.rydz.ui.history.YourTripsActivity
import cabuser.com.rydz.ui.home.payment.addcard.Card
import cabuser.com.rydz.ui.home.payment.addcard.PromoCode
import cabuser.com.rydz.ui.payment.PaymentActivity
import cabuser.com.rydz.ui.profile.ProfileActivity
import cabuser.com.rydz.ui.profile.User
import cabuser.com.rydz.ui.register.PhoneLoginActivity
import cabuser.com.rydz.ui.ride.*
import cabuser.com.rydz.ui.settings.PrivacyPolicyActivity
import cabuser.com.rydz.ui.settings.SettingsActivity
import cabuser.com.rydz.ui.settings.SupportActivity
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.*
import cabuser.com.rydz.util.common.ConstVariables.Companion.USERCHANGESTATUS
import cabuser.com.rydz.util.fcm.MyFirebaseMessagingService
import cabuser.com.rydz.util.location.LocationResult
import cabuser.com.rydz.util.location.MyLocation
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.get
import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import com.beust.klaxon.*
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.API
import com.google.android.gms.location.LocationServices.FusedLocationApi
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.maps.android.PolyUtil
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.dialog_vehicleselect.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.async
import org.jetbrains.anko.enabled
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

var confirmRideDialog: BottomSheetDialog? = null


/**
 * In Mainactivity class ,NavigationView, Driver bottomsheet dialog, driver rating dialog, show nearby drivers , show polylines functionalities are handled.
 */

class MainActivity : BaseActivity(), OnMapReadyCallback, LocationResult, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SocketListener, GoogleMap.OnCameraIdleListener {


    private lateinit var apiKey: String
    private val TAG = MainActivity::class.java.simpleName

    //Google Maps View
    var mMap: GoogleMap? = null
    private lateinit var mMapView: View
    private var latitude = 0.0          // UAE LatLng
    private var longitude = 0.0
    private var myLocation: MyLocation? = null
    private lateinit var vehicleTypeslist: ArrayList<VehicleTypeX>
    private var countryCode: String = ""
    private var googleApiClient: GoogleApiClient? = null
    var sendBookResponse: SendBookResponse? = null
    var paymentMode = ""
    var cardId = ""
    var tvPaymentmode: TextView? = null
    var tv_fare: TextView? = null
    var tv_subtotal: TextView? = null
    var tvTaxType: TextView? = null
    var tvTax: TextView? = null
    var tvPromoAmount: TextView? = null
    var tvPromocode: TextView? = null
    var tvWalletAmountUsed: TextView? = null
    var tv_wallet: TextView? = null
    var subTotal: Double = 0.0
    var farecal: Double = 0.0
    var taxRate: Double = 0.0
    var tax: Double = 0.0
    var tempvehicleObj: VehicleTypeX? = null
    //binding views
    private var mActivityMainBinding: ActivityMainBinding? = null
    private var mDrawer: androidx.drawerlayout.widget.DrawerLayout? = null
    private var mMainViewModel: MainViewModel? = null
    private var mNavigationView: NavigationView? = null
    private var scheduleMillisceonds = 0L
    private var scheduleTimeSlot: String = ""
    private var scheduleDate: String = ""


    private lateinit var mapFragment: SupportMapFragment
    private var LOCATION_REQUEST = 10
    private var PAYMENT_REQUEST = 5
    val reqTime = 15000L
    val totalReqTime = 300000L
    //markers and places
    private var srcMarker: Marker? = null
    private var desMarker: Marker? = null

    // lateinit var builder: LatLngBounds.Builder
    lateinit var gson: Gson
    private lateinit var jsonObject: JSONObject
    lateinit var reqJsonObject: JSONObject
    private lateinit var resendTimer: CountDownTimer
    private lateinit var resendTimer2: CountDownTimer
    var booleanDriverRequest: Boolean = false
    var findindRideProgressdialog: FindingRideBottomSheetFragment? = null
    var rideStatus: Int = -1  //to keep track of arrived, on ride
    var location: Location? = null
    //will be intialized after hiting api of google maps directions
    var tripDistance: Double? = 0.0
    private var tripTime: String? = null
    private var mMarker_dataList: HashMap<String, GetNearByDrivers.DriversLocation>? = null
    private val tolerance: Double = 30.0 // meters
    var polyLine: List<LatLng>? = ArrayList()
    private var exceededTolerance: Boolean = false
    var isCameraFocus = true
    var polyline: Polyline? = null
    var driver_previosloc: TrackStatusResponse.Location? = null
    var isSetPinLocation = false
    private var isFocusOnSourse = false
    var vehicleSelectBottomSheetFragment: BottomSheetDialog? = null
    private var scheduleDialog: BottomSheetDialog? = null
    var isbottomsheetFragment: Boolean = false
    lateinit var vehicleAdapter: VehicleAdapter
    var selected_position: Int = -1
    private var amPm: String = "AM"
    private var mProgressDialog: CustomeProgressDialog? = null
    // init the bottom sheet behavior
    lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var placesClient: PlacesClient
    private lateinit var locationButton: View
    private lateinit var compassButton: View
    lateinit var sharedPreference: SharedPreferences
    //to handle promo , wallet n pending fare
    var promo: PromoCode? = null
    var walletAmount: Double = 0.0
    var walletAmountUsed: Double = 0.0
    private var pending: Double = 0.0
    private val INITIAL_REQUEST = 13
    private val INITIAL_PERMS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    var fareDialogDial: Dialog? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var isFirstLoad = false

    companion object {
        var muserrideStatusResponse: SendBookResponse? = null
        var time_slot: String? = null
        var isScheduleRide = false
        var srcPlace: SendBookResponse.Booking.Source? = null
        var destinationPlace: SendBookResponse.Booking.Destination? = null

    }


    override fun onSocketConnected() {
        Log.e(TAG, "onSocketConnected")

        emitListeners()
    }


    private fun emitListeners() {
        try {
            if (AppSocketListener.getInstance().isSocketConnected) {


                try {
                    val params = JSONObject()
                    params.put("userId", "_" + RydzApplication.user_obj!!.id)
                    sendObjectToSocket(params, ONLINEUSER)
                } catch (ex: java.lang.Exception) {
                    ex.printStackTrace()
                }

                //to check ride status on starting of app
                AppSocketListener.getInstance().off(EVENT_CHECK_RIDE_STATUS, onCheckRideStatus)
                AppSocketListener.getInstance().addOnHandler(EVENT_CHECK_RIDE_STATUS, onCheckRideStatus)

                //to check change in ride status
                AppSocketListener.getInstance().off(RydzApplication.user_obj!!.id + "-change", onRideStatusChange)
                AppSocketListener.getInstance().addOnHandler(RydzApplication.user_obj!!.id + "-change", onRideStatusChange)

                //to check if there is any new messaage
                AppSocketListener.getInstance().off(RydzApplication.user_obj!!.id + "-message", onNewMessgaeListener)
                AppSocketListener.getInstance().addOnHandler(RydzApplication.user_obj!!.id + "-message", onNewMessgaeListener)

                //to find is there any unread msg from driver
                AppSocketListener.getInstance().off(USERREADSTATUS, onReadMsgListener)
                AppSocketListener.getInstance().addOnHandler(USERREADSTATUS, onReadMsgListener)

                //to check nearby drivers
                AppSocketListener.getInstance().off(EVENT_CHECK_NEARBY_DRIVERS, onCheckNearByDrivers)
                AppSocketListener.getInstance().addOnHandler(EVENT_CHECK_NEARBY_DRIVERS, onCheckNearByDrivers)

                //to get nearby available vehicle types
                AppSocketListener.getInstance().off(EVENT_GETVEHICLETYPES, onGetVehicleTypesListener)
                AppSocketListener.getInstance().addOnHandler(EVENT_GETVEHICLETYPES, onGetVehicleTypesListener)

                //on ride status change
                AppSocketListener.getInstance().off(USERCHANGESTATUS, onStatusChange)
                AppSocketListener.getInstance().addOnHandler(USERCHANGESTATUS, onStatusChange)

                // on send ride booking request
                AppSocketListener.getInstance().off(EVENT_SENDBOOKINGREQUEST, onSendBooking)
                AppSocketListener.getInstance().addOnHandler(EVENT_SENDBOOKINGREQUEST, onSendBooking)

                //on pool ride booking
                AppSocketListener.getInstance().off(EVENT_JOINPOOLRIDE, onJoinPoolRide)
                AppSocketListener.getInstance().addOnHandler(EVENT_JOINPOOLRIDE, onJoinPoolRide)

                //to listen is user account logged in on another phone
                AppSocketListener.getInstance().off(RydzApplication.user_obj!!.id + "-" + NEW_LOGIN, onNewLogin)
                AppSocketListener.getInstance().addOnHandler(RydzApplication.user_obj!!.id + "-" + NEW_LOGIN, onNewLogin)
            }

        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    override fun onSocketDisconnected() {
        Log.e(TAG, "onSocketDisconnected")
    }

    override fun onSocketConnectionError() {
        Log.e(TAG, "onSocketConnectionError")
    }

    override fun onSocketConnectionTimeOut() {
        Log.e(TAG, "onSocketConnectionTimeOut")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppSocketListener.getInstance().setActiveSocketListener(this)
        // Restart Socket.io
        AppSocketListener.getInstance().restartSocket()
        try {
            newLogin()
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        try {
            inits()
        } catch (e: Exception) {

        }
        isFirstLoad = true

    }

    override fun onBackPressed() {
        if (mDrawer?.isDrawerOpen(GravityCompat.START)!!) {
            mDrawer?.closeDrawer(GravityCompat.START)
        } else {
            if (isScheduleRide) {
                promo = null
                Toast.makeText(this, getString(R.string.schedule_cancelled), Toast.LENGTH_SHORT).show()
                clearViews()
                isScheduleRide = false
                time_slot = null
            } else if (srcPlace != null && srcPlace!!.latitude!!.isNotEmpty() && destinationPlace != null && destinationPlace!!.latitude!!.isNotEmpty() && rideStatus == -1) {
                promo = null
                toCancelPinLocation()
            } else {
                closePrompt()

            }

        }
    }

    override fun onPause() {
        super.onPause()

        if (findindRideProgressdialog != null && booleanDriverRequest) {
            findindRideProgressdialog!!.dismiss()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.iv_menu -> {
                mDrawer?.openDrawer(GravityCompat.START)

            }


            R.id.rl_menuheader -> {
                mDrawer?.closeDrawer(GravityCompat.START)
                startActivity(Intent(this, ProfileActivity::class.java))

            }

            R.id.tv_source -> {
                val locationIntent = Intent(this@MainActivity, LocationSetActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                }


                locationIntent.putExtra("FROM", "SOURCE")
                locationIntent.putExtra("COUNTRYCODE", countryCode)

                startActivityForResult(locationIntent, LOCATION_REQUEST)
            }

            R.id.tv_des -> {

                if (srcPlace != null) {
                    val locationIntent = Intent(this@MainActivity, LocationSetActivity::class.java)
                            .apply {
                                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            }
                    locationIntent.putExtra("FROM", "DESTINATION")
                    locationIntent.putExtra("COUNTRYCODE", countryCode)
                    startActivityForResult(locationIntent, LOCATION_REQUEST)
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.slect_source), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.iv_paymentmode -> {


            }
            R.id.tv_setpin -> {
                isSetPinLocation = false
                ll_pinLocation.visibility = View.GONE
                iv_loc_pin.visibility = View.GONE
                iv_menu.visibility = View.VISIBLE



                if (srcPlace != null && destinationPlace != null && !srcPlace!!.latitude!!.isEmpty() && !destinationPlace!!.latitude!!.isEmpty()) {
                    showLocations(srcPlace!!, destinationPlace!!)
                }
            }


            R.id.iv_schedule -> {
                showScheduleDialog()
            }
            R.id.iv_expand -> {

                // change the state of the bottom sheet
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    iv_expand.setImageResource(R.drawable.ic_expand_less)
                } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    iv_expand.setImageResource(R.drawable.ic_expand_more)

                } else {

                }


            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun showScheduleDialog() {

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val formatter = DecimalFormat("00")
        var cMonth = month + 1
        var cYear = year
        var cDay = day
        var cHour = hour
        var cMinute = minute
        val myFormat = "EEE, MMM d" // mention the format you need


        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        val sdf = SimpleDateFormat(myFormat)


        if (scheduleDialog == null) {

            scheduleDialog = BottomSheetDialog(this, R.style.TransparentDialog)
            scheduleDialog!!.setContentView(R.layout.dialog_schedule_a_ride)
            scheduleDialog!!.show()
        }

        val tv_scheduleDate = scheduleDialog!!.findViewById<TextView>(R.id.tv_schedule_date)
        val tv_scheduleTime = scheduleDialog!!.findViewById<TextView>(R.id.tv_schedule_time)
        val tv_scheduleconfirm = scheduleDialog!!.findViewById<TextView>(R.id.tv_scheduleconfirm)
        val tv_cancel = scheduleDialog!!.findViewById<TextView>(R.id.tv_cancel)

        scheduleDate = "$cDay:$cMonth:$cYear"
        tv_scheduleDate!!.text = sdf.format(c.time)
        setTimeInDialog(hour, minute, tv_scheduleTime!!)


        tv_scheduleDate.setOnClickListener { v ->
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, monthOfYear)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                cYear = year
                cMonth = monthOfYear + 1
                cDay = dayOfMonth

                val sdf = SimpleDateFormat(myFormat)


                scheduleDate = "$cDay:$cMonth:$cYear"
                // Display Selected date in textbox
                Log.e("437", "$cDay:$cMonth:$cYear")
                tv_scheduleDate.text = sdf.format(c.time)
            }, year, month, day)

            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }


        tv_scheduleTime!!.setOnClickListener { v ->

            val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { view, h, m ->

                var selectedHour = h
                val selectedMinute = m

                cHour = h
                cMinute = m
                /* if (DateFormat.is24HourFormat(this)) {*/
                if (selectedHour > 11) {
                    if (selectedHour > 12) {
                        selectedHour -= 12
                    }

                    amPm = "PM"
                } else {
                    if (selectedHour == 0) {
                        selectedHour += 12
                    }
                    amPm = "AM"
                }

                var strHour = ""


                strHour = if (selectedHour < 10) {
                    "0$selectedHour"
                } else {
                    "" + selectedHour
                }

                strHour = if (selectedMinute < 10) {
                    "$strHour:0$selectedMinute"
                } else
                    "$strHour:$selectedMinute"


                var selectedHourEnd = selectedHour
                var selectedMinuteEnd = selectedMinute
                var amPmEnd = amPm

                if (selectedMinuteEnd > 51) {
                    if (selectedHourEnd == 11) {
                        Log.e(TAG, "AAAAAAAA")
                        selectedHourEnd += 1
                        selectedMinuteEnd = selectedMinuteEnd + 10 - 60
                        amPmEnd = if (amPmEnd == "AM") {
                            "PM"
                        } else {
                            "AM"
                        }
                        tv_scheduleTime.text = strHour + " " + amPm + "-" + formatter.format(selectedHourEnd) + ":" + formatter.format(selectedMinuteEnd) + " " + amPmEnd
                    } else if (selectedHourEnd == 12) {
                        Log.e(TAG, "BBBBBBBB")
                        selectedHourEnd = 1
                        selectedMinuteEnd = selectedMinuteEnd + 10 - 60
                        tv_scheduleTime.text = strHour + " " + amPm + "-" + formatter.format(selectedHourEnd) + ":" + formatter.format(selectedMinuteEnd) + " " + amPmEnd
                    } else {
                        Log.e(TAG, "CCCCCCCCC")
                        selectedHourEnd += 1
                        selectedMinuteEnd = selectedMinuteEnd + 10 - 60
                        tv_scheduleTime.text = strHour + " " + amPm + "-" + formatter.format(selectedHourEnd) + ":" + formatter.format(selectedMinuteEnd) + " " + amPmEnd
                    }
                } else {
                    Log.e(TAG, "DDDDDDDD")

                    Log.e(TAG, "selectedHourEnd " + selectedHourEnd)
                    Log.e(TAG, "selectedMinuteEnd " + selectedMinuteEnd)

                    val myTime = ""+selectedHourEnd+":"+selectedMinuteEnd
                    val df = SimpleDateFormat("HH:mm")
                    val d = df.parse(myTime)
                    val cal = Calendar.getInstance()
                    cal.time = d
                    cal.add(Calendar.MINUTE, 10)
                    val newTime = df.format(cal.time)

                    Log.e(TAG, "newTime "+newTime.toString())

                    selectedHourEnd = newTime.toString().split(":")[0].toInt()
                    selectedMinuteEnd = newTime.toString().split(":")[1].toInt()

//                    if (selectedHourEnd == 12) {
//                        // selectedHourEnd = selectedHourEnd + 1
//                        selectedMinuteEnd = selectedMinuteEnd + 10
//                    } else {
//                        selectedHourEnd = selectedHourEnd + 1
//                        selectedMinuteEnd = 0
//                    }

                    tv_scheduleTime.text = strHour + " " + amPm + "-" + formatter.format(selectedHourEnd) + ":" + formatter.format(selectedMinuteEnd) + " " + amPmEnd

                    //   Log.e("525", scheduleDate + ": " + scheduleTimeSlot)


                }
                scheduleTimeSlot = strHour + " " + amPm
                /*  }
                  else
                  {
                    //  val setTime: CharSequence = dateFormat.format("hh:mm a", calendar)
                      tv_scheduleTime.text = selectedHour.toString()+" " + selectedMinute


                      Log.e("560"," 560")
                  }
  */
            }), hour, minute, false)

            tpd.show()
        }

        tv_cancel!!.setOnClickListener {
            scheduleDialog!!.cancel()
        }
        tv_scheduleconfirm!!.setOnClickListener { v ->

            isScheduleRide = true
            val calender = Calendar.getInstance()
            Log.e("548", cYear.toString() + " " + cMonth + " " + cDay + " " + cHour + " " + cMinute)
            calender.set(cYear, cMonth, cDay, cHour, cMinute, 0)
            scheduleMillisceonds = calender.timeInMillis
            Log.e("548", scheduleMillisceonds.toString())

            if (tv_scheduleDate.text.isEmpty()) {

            }

            var calendarr = Calendar.getInstance()
            //To get the desired time in 24 hours format as 0-23 or 1-24
            calendarr.set(Calendar.HOUR_OF_DAY, 12)
            calendarr.time = Date()
            Log.e("554", scheduleDate + " " + scheduleTimeSlot)

            if (getTimeInMilliseconds(scheduleDate + " " + scheduleTimeSlot) < calendarr.timeInMillis) {


                Toast.makeText(this@MainActivity, getString(R.string.validation_timeslot), Toast.LENGTH_SHORT).show()
            } else {

                val location_Intent = Intent(this@MainActivity, LocationSetActivity::class.java)
                        .apply {
                            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                        }
                location_Intent.putExtra("FROM", "DESTINATION")
                location_Intent.putExtra("COUNTRYCODE", countryCode)
                time_slot = "" + tv_scheduleDate.text + " at " + tv_scheduleTime.text
                startActivityForResult(location_Intent, LOCATION_REQUEST)
                scheduleMillisceonds = getTimeInMilliseconds(scheduleDate + " " + scheduleTimeSlot)
                scheduleDialog!!.cancel()
            }

        }

        scheduleDialog!!.setOnCancelListener { v ->

            scheduleDialog = null

        }
    }

    //to set time in
    private fun setTimeInDialog(h: Int, m: Int, v: TextView) {

        Log.e(TAG, "hAA " + h.toString())
        Log.e(TAG, "mAA " + m.toString())

        val formatter = DecimalFormat("00")

        var selectedHour = h
        val selectedMinute = m

        if (selectedHour > 11) {
            if (selectedHour > 12) {
                selectedHour -= 12
            }
            amPm = "PM"
        } else {
            if (selectedHour == 0) {
                selectedHour += 12
            }
            amPm = "AM"
        }

        var strHour: String


        strHour = if (selectedHour < 10) {
            "0$selectedHour"
        } else {
            "" + selectedHour
        }

        strHour = if (selectedMinute < 10) {
            "$strHour:0$selectedMinute"
        } else
            "$strHour:$selectedMinute"


        var selectedHourEnd = selectedHour
        var selectedMinuteEnd = selectedMinute
        var amPmEnd = amPm

        if (selectedMinuteEnd > 51) {
            if (selectedHourEnd == 11) {
                selectedHourEnd += 1
                selectedMinuteEnd = selectedMinuteEnd + 10 - 60
                amPmEnd = if (amPmEnd == "AM") {
                    "PM"
                } else {
                    "AM"
                }
                v.text = strHour + "" + amPm + "-" + formatter.format(selectedHourEnd) + ":" + formatter.format(selectedMinuteEnd) + "" + amPmEnd
            } else if (selectedHourEnd == 12) {
                selectedHourEnd = 1
                selectedMinuteEnd = selectedMinuteEnd + 10 - 60
                v.text = strHour + "" + amPm + "-" + formatter.format(selectedHourEnd) + ":" + formatter.format(selectedMinuteEnd) + "" + amPmEnd
            } else {
                selectedHourEnd += 1
                selectedMinuteEnd = selectedMinuteEnd + 10 - 60
                v.text = strHour + "" + amPm + "-" + formatter.format(selectedHourEnd) + ":" + formatter.format(selectedMinuteEnd) + "" + amPmEnd
            }
        } else {
           // selectedMinuteEnd += 10
           // selectedHourEnd + 1

            val myTime = ""+selectedHourEnd+":"+selectedMinuteEnd
            val df = SimpleDateFormat("HH:mm")
            val d = df.parse(myTime)
            val cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.MINUTE, 10)
            val newTime = df.format(cal.time)

            Log.e(TAG, "newTime "+newTime.toString())

            selectedHourEnd = newTime.toString().split(":")[0].toInt()
            selectedMinuteEnd = newTime.toString().split(":")[1].toInt()

            v.text = strHour + "" + amPm + "-" + formatter.format(selectedHourEnd) + ":" + formatter.format(selectedMinuteEnd) + "" + amPmEnd

        }
        scheduleTimeSlot = strHour + " " + amPm

//        scheduleTimeSlotAA 04:50 PM
        Log.e(TAG, "scheduleTimeSlotAA " + scheduleTimeSlot.toString())

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {

            return

        }
        mMap!!.isMyLocationEnabled = true
        mMap!!.uiSettings!!.isMyLocationButtonEnabled = true
        showCurLocBtn()


        checkRideStatus_Socket()


    }


    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            INITIAL_REQUEST -> if (canAccessLocation() && canAccessCoreLocation()) {
                val networkPresent = myLocation!!.getLocation(this, this)
                if (!networkPresent) {
                    showSettingsAlert()
                } else {


                    checkRideStatus_Socket()

                    val location = FusedLocationApi.getLastLocation(googleApiClient)
                    if (location != null) {
                        //Getting longitude and latitude
                        longitude = location.longitude
                        latitude = location.latitude

                        Log.e("747", latitude.toString() + "  " + longitude.toString())

                        //moving the map to location
                        //  goToMYLocation()

                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()



        showCurLocBtn()
        MyFirebaseMessagingService.mainScreen = this
        MyFirebaseMessagingService.inChatScreen = false

        if (mMainViewModel != null)
            mMainViewModel?.onNavMenuCreated()


        try {
            if (rideStatus != -1) {
                myLocation!!.getLocation(this, this)

                checkRideStatus_Socket()
            } else {
                if (driver_bottom_sheet!!.visibility == View.VISIBLE) {


                    checkRideStatus_Socket()
                }

            }


        } catch (e: Exception) {

        }

        if (findindRideProgressdialog != null && booleanDriverRequest) {
            findindRideProgressdialog = FindingRideBottomSheetFragment()
            findindRideProgressdialog!!.isCancelable = false
            findindRideProgressdialog!!.show(supportFragmentManager, "bdbd")
        }



        onSocketConnected()


        try {
            if (muserrideStatusResponse != null && muserrideStatusResponse!!.booking != null) {
                readMsgStatus(muserrideStatusResponse!!.booking!!.id!!)
            }
        } catch (e: java.lang.Exception) {

        }


        /*  if(isFirstLoad &&  rideStatus == -1 && srcPlace!=null && ( destinationPlace == null || (destinationPlace != null && destinationPlace?.latitude.equals("0.0") )))
          {
  Log.e("814","814")
              AppSocketListener.getInstance().setActiveSocketListener(this)
              // Restart Socket.io
              AppSocketListener.getInstance().restartSocket()
              checkRideStatus_Socket()

          }*/

    }

    override fun onConnected(p0: Bundle?) {
        getCurrentLocation()

        try {
            val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder().addLocationRequest(myLocation?.mLocationRequest!!)
            val client: SettingsClient = LocationServices.getSettingsClient(this)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
            task.addOnSuccessListener(this) { locationSettingsResponse ->
                /* Toast.makeText(
                    this@BookRideActivity, "Gps already open",
                    Toast.LENGTH_LONG
                ).show()*/
                Log.d("location settings", locationSettingsResponse.toString())
            }
            task.addOnFailureListener(this) { e ->
                if (e is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        val resolvable: ResolvableApiException = e
                        resolvable.startResolutionForResult(
                                this@MainActivity,
                                121
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                    }
                }
            }
        } catch (e: Exception) {

        }


    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        showMessage(p0.errorMessage.toString())
    }

    override fun gotLocation(location: Location) {

        checkRideStatus_Socket()
        latitude = location.latitude
        longitude = location.longitude

        Log.e("778", "gotLocation " + location.latitude + " " + location.longitude)


        if (rideStatus == -1)
            goToMYLocation()
    }


    private val onCheckRideStatus = Emitter.Listener { args ->
        Log.e(TAG, "call: onCheckRideStatus   " + args[0].toString())

        val jsonObj = JSONObject(args[0].toString())
        val userRideStatusResponse = gson.fromJson(jsonObj.toString(), SendBookResponse::class.java) as SendBookResponse
        this.isSetPinLocation = false
        if (userRideStatusResponse.success!! && userRideStatusResponse.status != 4 && userRideStatusResponse.status != 9 && userRideStatusResponse.booking != null) {


            readMsgStatus(userRideStatusResponse.booking!!.id!!)
            rideStatus = userRideStatusResponse.status!!
            when (userRideStatusResponse.status) {


                0 -> {

                    hideViews()
                    showDriverInfo(userRideStatusResponse)
                    muserrideStatusResponse = userRideStatusResponse
                    sendBookResponse = muserrideStatusResponse
                    polyline = null
                    destinationPlace = SendBookResponse.Booking.Destination(userRideStatusResponse.booking!!.destination!!.latitude!!, userRideStatusResponse.booking!!.destination!!.longitude!!, userRideStatusResponse.booking!!.destination!!.name)
                    srcPlace = SendBookResponse.Booking.Source(userRideStatusResponse.booking!!.source!!.latitude!!, userRideStatusResponse.booking!!.source!!.longitude!!, userRideStatusResponse.booking!!.source!!.name)
                    userTracking_Socket(userRideStatusResponse.booking!!.id!!)
                    showDriver(TrackStatusResponse.Location(0.0, userRideStatusResponse.booking!!.id!!, userRideStatusResponse.booking!!.driverId!!.id, userRideStatusResponse.booking!!.driverId!!.latitude, userRideStatusResponse.booking!!.driverId!!.longitude, 0), srcPlace!!)


                }
                1 -> {

                    hideViews()
                    muserrideStatusResponse = userRideStatusResponse
                    sendBookResponse = muserrideStatusResponse
                    showDriverInfo(userRideStatusResponse)
                    srcPlace = SendBookResponse.Booking.Source(userRideStatusResponse.booking!!.source!!.latitude!!, userRideStatusResponse.booking!!.source!!.longitude!!, userRideStatusResponse.booking!!.source!!.name)
                    destinationPlace = SendBookResponse.Booking.Destination(userRideStatusResponse.booking!!.destination!!.latitude!!, userRideStatusResponse.booking!!.destination!!.longitude!!, userRideStatusResponse.booking!!.destination!!.name)
                    userTracking_Socket(userRideStatusResponse.booking!!.id!!)


                    if (srcPlace != null && userRideStatusResponse.booking!!.driverId!!.latitude != null && userRideStatusResponse.booking!!.driverId!!.latitude!!.toDouble() > 0.0)
                        onDriverArrivalonSource(srcPlace!!, TrackStatusResponse.Location(0.0, userRideStatusResponse.booking!!.id!!, userRideStatusResponse.booking!!.driverId!!.id, userRideStatusResponse.booking!!.driverId!!.latitude, userRideStatusResponse.booking!!.driverId!!.longitude, 0))                          //  showDriver(TrackStatusResponse.Location(0.0,userRideStatusResponse.booking!!.id!!,userRideStatusResponse.booking!!.driverId!!.id,userRideStatusResponse.booking!!.driverId!!.latitude,userRideStatusResponse.booking!!.driverId!!.longitude,1),srcPlace!!)


                }
                2 -> {

                    hideViews()
                    polyline = null
                    muserrideStatusResponse = userRideStatusResponse
                    sendBookResponse = muserrideStatusResponse
                    showDriverInfo(userRideStatusResponse)
                    Log.e("846", userRideStatusResponse.toString())
                    srcPlace = SendBookResponse.Booking.Source(userRideStatusResponse.booking!!.source!!.latitude!!, userRideStatusResponse.booking!!.source!!.longitude!!, userRideStatusResponse.booking!!.source!!.name)
                    destinationPlace = SendBookResponse.Booking.Destination(userRideStatusResponse.booking!!.destination!!.latitude!!, userRideStatusResponse.booking!!.destination!!.longitude!!, userRideStatusResponse.booking!!.destination!!.name)
                    userTracking_Socket(userRideStatusResponse.booking!!.id!!)
                    rideTracking(TrackStatusResponse.Location(bearingBetweenLocations(LatLng(userRideStatusResponse.booking!!.source!!.latitude!!.toDouble(), userRideStatusResponse.booking!!.source!!.longitude!!.toDouble()), LatLng(userRideStatusResponse.booking!!.destination!!.latitude!!.toDouble(), userRideStatusResponse.booking!!.destination!!.longitude!!.toDouble())), userRideStatusResponse.booking!!.id!!, userRideStatusResponse.booking!!.driverId!!.id!!, userRideStatusResponse.booking!!.driverId!!.latitude!!.toDouble(), userRideStatusResponse.booking!!.driverId!!.longitude!!.toDouble()), destinationPlace!!)


                }
                3 -> {
                    muserrideStatusResponse = userRideStatusResponse
                    Log.e("855", muserrideStatusResponse?.booking!!.status.toString())
                    Log.e("paayment mode ", muserrideStatusResponse?.booking?.paymentMode!!)

                    runOnUiThread {
                        if (muserrideStatusResponse != null)
                            ratingPopUp(muserrideStatusResponse?.booking!!.subtotalFare!!, muserrideStatusResponse?.booking?.paymentMode!!, muserrideStatusResponse?.booking!!.id.toString(), muserrideStatusResponse?.booking!!.driverId?.id.toString())
                        onStatusThree()
                    }

                }

            }
        } else if (userRideStatusResponse.status == 4) {  //4=previous ride rating is pending so show rating dialog


            rideStatus = userRideStatusResponse.status!!
            sendBookResponse = gson.fromJson(jsonObj.toString(), SendBookResponse::class.java) as SendBookResponse
            try {
                runOnUiThread {
                    if (mMap != null)
                        mMap!!.clear()
                    tv_des.text = ""
                    tv_source.text = ""
                    driver_bottom_sheet.visibility = View.GONE

                    if (supportFragmentManager != null && sendBookResponse != null) {
                        if (!isbottomsheetFragment) {
                            isbottomsheetFragment = true
                            val bottomSheetFragment = DriverRatingBottomsheetFragment()
                            bottomSheetFragment.isCancelable = false
                            try {
                                if (!bottomSheetFragment.isVisible)
                                    bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                            } catch (e: Exception) {

                            }
                        }
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
                isbottomsheetFragment = false
            }


        } else if (userRideStatusResponse.status == 9 /*&& userRideStatusResponse.booking == null*/) { //9=no ride then hit nearby drivers socket


            rideStatus = userRideStatusResponse.status!!

            runOnUiThread {
                rideStatus = -1
                cl_main.visibility = View.VISIBLE
                iv_schedule.visibility = View.VISIBLE
                iv_loc_pin.visibility = View.VISIBLE
                tv_des.text = ""
                tv_source.text = ""
                isFocusOnSourse = true
                getCurrentLocation()
                // setLocation()
                srcPlace = null
                isSetPinLocation = false
                destinationPlace = null
                vehicleSelectBottomSheetFragment = null
                //to show src & des views

                cl_main.visibility = View.VISIBLE
                iv_schedule.visibility = View.VISIBLE
                iv_menu.visibility = View.VISIBLE
                iv_loc_pin.visibility = View.VISIBLE

                if (mMap != null) {
                    Log.e("map", "not null")
                    mMap!!.clear()
                    mMap!!.isMyLocationEnabled = true
                }


                driver_bottom_sheet.visibility = View.GONE

                muserrideStatusResponse = null
                rideStatus = -1
                setLocation()

                Handler().postDelayed({
                    userGetNearByDrivers_Socket(latitude, longitude)
                }, 5000)


            }

        }
    }

    //listener to listen  nearby drivers
    private val onCheckNearByDrivers = Emitter.Listener { args ->
        Log.e(TAG, "call: onCheckNearByDrivers   " + args[0].toString())
        runOnUiThread {
            if (args[0] != null) {

                val jsonObj = JSONObject(args[0].toString())

                val getNearByDrivers = gson.fromJson(jsonObj.toString(), GetNearByDrivers::class.java) as GetNearByDrivers
                if (getNearByDrivers.success!!) {

                    try {

                        addNearByDrivers(getNearByDrivers.driversLocations as List<GetNearByDrivers.DriversLocation>)
                    } catch (e: java.lang.Exception) {

                    }


                    try {
                        if (RydzApplication.user_obj != null) {
                            RydzApplication.user_obj!!.rating = getNearByDrivers.rating!!
                            if (RydzApplication.user_obj!!.rating != null) {
                                mMainViewModel!!.userRating!!.set(RydzApplication.user_obj!!.rating.toString())
                            } else {
                                mMainViewModel!!.userRating!!.set("0")
                            }

                            RydzApplication.prefs = PreferenceHelper.defaultPrefs(this@MainActivity)
                            RydzApplication.prefs[PreferenceHelper.Key.REGISTEREDUSER] = RydzApplication.user_obj!! //setter
                        }

                    } catch (e: Exception) {

                    }

                }

            }
        }
    }


    //any new message listener
    private val onNewMessgaeListener = Emitter.Listener { args ->
        Log.e(TAG, "call: onNewMessgaeListener   " + args[0].toString())
        try {
            runOnUiThread {
                try {
                    val gson1 = Gson()


                    val reqResponse = gson1.fromJson(args[0].toString(), NewMessageResponse::class.java)

                    if (reqResponse.success) {

                        if (reqResponse.msg.opponentReadStatus == 0 && rideStatus < 2)
                            iv_chatIndicator.visibility = View.VISIBLE
                        else
                            iv_chatIndicator.visibility = View.GONE


                    }
                } catch (ex: java.lang.Exception) {
                    ex.printStackTrace()
                }
            }

        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }


    //to check is there any unread msg
    private val onReadMsgListener = Emitter.Listener { args ->
        Log.e(TAG, "call: onReadMsgListener   " + args[0].toString())
        try {
            runOnUiThread {
                try {
                    val gson1 = Gson()


                    val reqResponse = gson1.fromJson(args[0].toString(), NewMessageResponse::class.java)
                    if (reqResponse.success) {
                        if (reqResponse.result == 1 && rideStatus < 2)
                            iv_chatIndicator.visibility = View.VISIBLE
                        else
                            iv_chatIndicator.visibility = View.GONE


                    }
                } catch (ex: java.lang.Exception) {
                    ex.printStackTrace()
                }
            }

        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }


    //get vehicle type   listener response

    private val onGetVehicleTypesListener = Emitter.Listener { args ->
        Log.e(TAG, "call: onGetVehicleTypesListener  612 " + args[0].toString())
        runOnUiThread {
            mProgressDialog!!.dismiss()
            if (args[0] != null) {

                val jsonObj = JSONObject(args[0].toString())

                val getVehiclesTypeList = gson.fromJson(jsonObj.toString(), VehicleTypeResponse::class.java) as VehicleTypeResponse
                if (getVehiclesTypeList.success) {
                    isSetPinLocation = false
                    if (!isSetPinLocation) {
                        if (srcPlace != null && destinationPlace != null) {
/*
                            if (getVehiclesTypeList.promo.applied == 1) {
                                promo = getVehiclesTypeList.promo
                            } else {
                                promo = null
                            }*/
                            pending = getVehiclesTypeList.pending

                            walletAmount = getVehiclesTypeList.walletAmount
                            taxRate = getVehiclesTypeList.tax

                            vehicleTypeslist = (getVehiclesTypeList.vehicleTypeList as ArrayList<VehicleTypeX>?)!!
                            if (vehicleTypeslist != null && vehicleTypeslist.size > 0) {
                                //  promo = getVehiclesTypeList.promo!!
                                if (vehicleSelectBottomSheetFragment == null) {


                                    vehicleTypeslist[0].isselected = true
                                    selected_position = 0
                                    vehicleSelectBottomSheetFragment = BottomSheetDialog(this@MainActivity, R.style.TransparentDialog)
                                    //vehicleSelectBottomSheetFragment!!.show(supportFragmentManager, vehicleSelectBottomSheetFragment!!.getTag())
                                    vehicleSelectBottomSheetFragment!!.setContentView(R.layout.dialog_vehicleselect)

                                    //not to show pool option when scheduling a ride.
                                    if (isScheduleRide) for (i in 0 until vehicleTypeslist.size) if (vehicleTypeslist[i]._id == RydzApplication.poolingType) {
                                        vehicleTypeslist.removeAt(i)
                                        break
                                    }

                                    vehicleAdapter = VehicleAdapter(this@MainActivity, vehicleTypeslist)
                                    vehicleSelectBottomSheetFragment!!.rv_vehicle.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
                                    vehicleSelectBottomSheetFragment!!.rv_vehicle.adapter = vehicleAdapter
                                    vehicleSelectBottomSheetFragment!!.show()


                                    vehicleSelectBottomSheetFragment!!.setOnCancelListener {
                                        vehicleSelectBottomSheetFragment = null


                                    }

                                    vehicleSelectBottomSheetFragment!!.setOnDismissListener { v ->
                                        if (vehicleSelectBottomSheetFragment == null) {
                                            Log.e("1164", "setOnCancelListener")

                                            clearViews()
                                        }
                                        vehicleSelectBottomSheetFragment = null
                                        Log.e("1171", "setOnDismissListener")

                                    }

                                }


                            } else {
                                runOnUiThread {

                                    srcPlace = null
                                    destinationPlace = null
                                    mMap!!.clear()



                                    clearViews()
                                }
                            }
                        } else {

                        }
                    }


                }
            } else {
                Toast.makeText(this@MainActivity, "Some problem occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //send booking request response

    private val onSendBooking = Emitter.Listener { args ->
        Log.e(TAG, "call: onSendBooking   " + args[0].toString())
        promo = null
        runOnUiThread {
            isSetPinLocation = false
            if (rideStatus < 2) {
                btn__chat.enabled = true
                btn__chat.setTextColor(resources.getColor(R.color.colorBlue))

            } else {
                btn__chat.enabled = false
                iv_chatIndicator.visibility = View.GONE
                btn__chat.setTextColor(resources.getColor(R.color.colorDisableText))
            }


            Log.e("onSendBooking", args[0].toString())
            val jsonObj = JSONObject(args[0].toString())
            sendBookResponse = gson.fromJson(jsonObj.toString(), SendBookResponse::class.java) as SendBookResponse

            try {
                if (!sendBookResponse!!.success!! && sendBookResponse!!.status != 2) {
                    rideStatus = 0
                    polyline = null
                    booleanDriverRequest = false
                    try {
                        findindRideProgressdialog!!.dismiss()
                        findindRideProgressdialog = null
                    } catch (e: Exception) {

                    }
                    clearViews()
                    Toast.makeText(this@MainActivity, sendBookResponse!!.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: java.lang.Exception) {

            }
            muserrideStatusResponse = sendBookResponse
            if (sendBookResponse!!.success!! && sendBookResponse!!.status == 0) {
                rideStatus = 0
                polyline = null
                booleanDriverRequest = false
                try {
                    findindRideProgressdialog!!.dismiss()
                    findindRideProgressdialog = null
                } catch (e: Exception) {

                }
                try {
                    userTracking_Socket(sendBookResponse!!.booking!!.id!!)
                } catch (e: Exception) {

                }

                srcPlace = sendBookResponse!!.booking!!.source
                destinationPlace = sendBookResponse!!.booking!!.destination
                // userTracking_Socket(sendBookResponse!!.booking!!.id!!)
                showDriver(TrackStatusResponse.Location(0.0, sendBookResponse!!.booking!!.id!!, sendBookResponse!!.booking!!.driverId!!.id, sendBookResponse!!.booking!!.driverId!!.latitude, sendBookResponse!!.booking!!.driverId!!.longitude, 0), srcPlace!!)

                showDriverBottomSheet()


            } else if (sendBookResponse!!.status == 3) {
                onStatusThree()

            } else if (sendBookResponse!!.status == 2) {

                startTimer()

            }
        }
    }

    private fun onStatusThree() {
        rideStatus = -1
        booleanDriverRequest = false

        runOnUiThread {

            try {
                if (findindRideProgressdialog != null) {
                    findindRideProgressdialog!!.dismiss()
                    findindRideProgressdialog = null
                }
            } catch (e: Exception) {

            }



            clearViews()

        }
    }


    //track ride status
    private val onUserTracking = Emitter.Listener { args ->
        Log.e(TAG, "call: onUserTracking   " + args[0].toString())

        if (rideStatus != -1 && srcPlace != null && destinationPlace != null) {
            isSetPinLocation = false
            val jsonObject = args[0].toString()
            val trackResponse: TrackStatusResponse = gson.fromJson(jsonObject, TrackStatusResponse::class.java)
            if (trackResponse.success!!) {
                rideStatus = trackResponse.location!!.status!!

                if (rideStatus == 0) {
                    showDriver(trackResponse.location!!, srcPlace!!)


                } else if (rideStatus == 1) {
                    onDriverArrivalonSource(srcPlace!!, trackResponse.location!!)
                } else if (rideStatus == 2) {

                    rideTracking(trackResponse.location!!, destinationPlace!!)
                }

            } else {

            }
        }
    }


    //to listen ride status change
    private val onStatusChange = Emitter.Listener { args ->
        Log.e(TAG, "call: onStatusChange   " + args[0].toString())

        val jsonObject = args[0].toString()
        val changeStatusResponse: ChangeStatusResponse = gson.fromJson(jsonObject, ChangeStatusResponse::class.java)
        if (changeStatusResponse.success!!) {
            //closeFareSummaryDialog()
            runOnUiThread {
                try {
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("BottomSheetFragment ", "ivnbcvcvfgcvcs true")
                }




                if (muserrideStatusResponse != null) {
                    muserrideStatusResponse = null
                    srcPlace = null
                    destinationPlace = null
                    driver_bottom_sheet.visibility = View.GONE
                    if (mMap != null)
                        mMap!!.clear()

                    clearViews()
                } else {

                }

            }

        }
    }


    //to listen ride status change
    private val onRideStatusChange = Emitter.Listener { args ->
        Log.e(TAG, "call: onRideStatusChange   " + args[0].toString())
        isSetPinLocation = false
        val jsonObj = JSONObject(args[0].toString())
        val rideStatusChangeResponse = gson.fromJson(jsonObj.toString(), RideStatusChangeResponse::class.java) as RideStatusChangeResponse
        runOnUiThread {
            if (rideStatusChangeResponse.success!!) {
                rideStatus = rideStatusChangeResponse.status!!.toInt()
                when (rideStatus) {

                    1 -> {
                        polyline = null
                        // Toast.makeText(this@MainActivity, "Driver arrived at your location", Toast.LENGTH_SHORT).show()
                        isCameraFocus = true
                        if (srcPlace != null && driver_previosloc != null)
                            onDriverArrivalonSource(srcPlace!!, driver_previosloc!!)

                        runOnUiThread {
                            Log.e(TAG, "status 1")
                            cl_main.visibility = View.GONE
                            iv_schedule.visibility = View.GONE
                            iv_menu.visibility = View.GONE
                            iv_loc_pin.visibility = View.GONE
                            driver_bottom_sheet.visibility = View.VISIBLE

                            tv_cash?.text = rideStatusChangeResponse.booking?.paymentMode
                            if (rideStatus < 2) {
                                btn__chat.enabled = true
                                btn__chat.setTextColor(resources.getColor(R.color.colorBlue))
                                btn_cancel.visibility = View.VISIBLE
                            } else {
                                btn__chat.enabled = false
                                iv_chatIndicator.visibility = View.GONE

                                btn__chat.setTextColor(resources.getColor(R.color.colorDisableText))
                                btn_cancel.visibility = View.GONE
                            }
                        }

                    }
                    2 -> {
                        polyline = null
                        // Toast.makeText(this@MainActivity, "Ride started", Toast.LENGTH_SHORT).show()
                        btn__chat.enabled = false
                        iv_chatIndicator.visibility = View.GONE

                        btn__chat.setTextColor(Color.GRAY)
                        isCameraFocus = true
                        if (destinationPlace != null && driver_previosloc != null)
                            try {
                                rideTracking(driver_previosloc!!, destinationPlace!!)
                            } catch (e: java.lang.Exception) {

                            }


                        hideViews()
                        runOnUiThread {
                            tv_cash?.text = rideStatusChangeResponse.booking?.paymentMode

                            driver_bottom_sheet.visibility = View.VISIBLE


                        }

                    }
                    3 -> {
                        Log.e("paayment mode ", sendBookResponse?.booking?.paymentMode!!)
                        ratingPopUp(sendBookResponse?.booking!!.subtotalFare!!, sendBookResponse?.booking?.paymentMode!!, sendBookResponse?.booking!!.id.toString(), sendBookResponse?.booking!!.driverId?.id.toString())
                        clearViews()

                        btn__chat.enabled = false
                        iv_chatIndicator.visibility = View.GONE

                    }
                    4 -> {


                        try {

                            closeFareSummaryDialog()
                            /* runOnUiThread {
                                 try {
                                     fareDialogDial!!.cancel()
                                 } catch (e: java.lang.Exception) {

                                 }
                                 driver_bottom_sheet.visibility = View.GONE
                                 if (supportFragmentManager != null && sendBookResponse != null) {

                                     //  if (!isbottomsheetFragment) {
                                     isbottomsheetFragment = true

                                     val bottomSheetFragment = DriverRatingBottomsheetFragment()
                                     bottomSheetFragment.isCancelable = false
                                     bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)


                                     // }
                                 }


                             }*/
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("BottomSheetFragment ", "ivnbcvcvfgcvcs true")
                        }
                    }


                    0 -> {
                        runOnUiThread {
                            Log.e(TAG, "status 0")
                            cl_main.visibility = View.GONE
                            iv_schedule.visibility = View.GONE
                            iv_menu.visibility = View.GONE
                            iv_loc_pin.visibility = View.GONE

                        }


                        if (rideStatus < 2) {
                            btn__chat.enabled = true
                            btn__chat.setTextColor(resources.getColor(R.color.colorBlue))
                            btn_cancel.visibility = View.VISIBLE
                        } else {
                            btn__chat.enabled = false
                            btn__chat.setTextColor(resources.getColor(R.color.colorDisableText))
                            btn_cancel.visibility = View.GONE
                            iv_chatIndicator.visibility = View.GONE

                        }

                    }

                }

            } else {
                Toast.makeText(this@MainActivity, rideStatusChangeResponse.message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private val onJoinPoolRide = Emitter.Listener { args -> Log.e(TAG, "call: onJoinPoolRide   " + args[0].toString()) }

    /********************************custom methods*********************************/

    //Getting current location
    private fun getCurrentLocation() {
        if (mMap != null) {
            mMap!!.clear()
        }

        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        val location = FusedLocationApi.getLastLocation(googleApiClient)
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.longitude
            latitude = location.latitude

            //moving the map to location
            goToMYLocation()
        }
    }


    private fun goToMYLocation() {
        Log.e("1579", "1579")
        setLocation()
        drawMarker(latitude, longitude)
    }


    private fun drawMarker(lat: Double?, lng: Double?) {


        try {
            if (mMap != null && lat != 0.0) {
                val gps = LatLng(lat!!, lng!!)
                latitude = lat
                longitude = lng

                //  var cameraUpdate = CameraUpdateFactory.newLatLngZoom(gps, 15.2f)
                var cameraUpdate = CameraUpdateFactory.newLatLngZoom(gps, 15.2f)
                if (mMap != null) {


                    mMap!!.animateCamera(cameraUpdate)
                }
                mMap!!.setOnMarkerClickListener { true }
            }
        } catch (e: Exception) {

        }
    }

    private fun canAccessLocation(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun canAccessCoreLocation(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun hasPermission(perm: String): Boolean {

        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm)
    }


    private fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(
                this@MainActivity
        )
        alertDialog.setTitle("SETTINGS")
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?")
        alertDialog.setPositiveButton("Settings"
        ) { dialog, which ->
            val intent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
            )
            startActivity(intent)
        }
        alertDialog.setNegativeButton("Cancel"
        ) { dialog, which -> dialog.cancel() }

        alertDialog.show()
    }


    private fun setupNavMenu() {
        val navHeaderMainBinding: NavHeaderMainBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.nav_header_main, mNavigationView, false)
        mNavigationView?.addHeaderView(navHeaderMainBinding.root)
        navHeaderMainBinding.viewModel = mMainViewModel

        mMainViewModel?.onNavMenuCreated()
        mNavigationView?.setNavigationItemSelectedListener { item ->
            mDrawer?.closeDrawer(GravityCompat.START)
            when (item.itemId) {

                R.id.nav_promoCode -> {
                    // Handle the camera action
                    mDrawer?.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, FreeRidesActivity::class.java))
                    true
                }

                R.id.nav_yourtrips -> {
                    // Handle the camera action
                    mDrawer?.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, YourTripsActivity::class.java))
                    true
                }

                R.id.nav_payments -> {
                    mDrawer?.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, PaymentActivity::class.java).apply {
                        putExtra("key", "settings")
                    })
                    true
                }

                R.id.nav_help -> {
                    mDrawer?.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, SupportActivity::class.java))
                    true
                }
                R.id.nav_drive -> {
                    mDrawer?.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, PrivacyPolicyActivity::class.java).apply {
                        putExtra("show", getString(R.string.drive_for_rydz))
                    })
                    true
                }
                R.id.nav_settings -> {
                    mDrawer?.closeDrawer(GravityCompat.START)
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }

                else -> {
                    false
                }
            }
        }
    }


    //intialization
    private fun inits() {

        //places api
        apiKey = getString(R.string.places_api_key)
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        placesClient = Places.createClient(this)
        mProgressDialog = CustomeProgressDialog(this)
        sharedPreference = PreferenceHelper.defaultPrefs(this@MainActivity)
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mMainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mActivityMainBinding?.viewmodel = mMainViewModel
        //  builder = LatLngBounds.Builder()
        RydzApplication.prefs = PreferenceHelper.defaultPrefs(applicationContext)
        val gsonString = RydzApplication.prefs[PreferenceHelper.Key.REGISTEREDUSER, ""]
        gson = Gson()
        RydzApplication.user_obj = gson.fromJson(gsonString.toString(), User::class.java) as User
        bottomSheetBehavior = BottomSheetBehavior.from(driver_bottom_sheet)
        mDrawer = mActivityMainBinding!!.topLayout
        mNavigationView = mActivityMainBinding!!.navView
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mMapView = mapFragment.view!!
        myLocation = MyLocation()
        gson = Gson()

        //Initializing googleApiClient
        googleApiClient = GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(API).build()
        googleApiClient?.connect()
        if (!canAccessLocation() || !canAccessCoreLocation()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(INITIAL_PERMS, INITIAL_REQUEST)
            }
        } else {
            val networkPresent = myLocation!!.getLocation(this, this)
            if (!networkPresent) {
                showSettingsAlert()
            }
        }
        setupNavMenu()


        mMainViewModel?.checkPhoneStatus?.observe(this, androidx.lifecycle.Observer {

            if (it != null) {
                promo = null
                showMessage(it.message.toString())
                clearViews()
            }
        })


// set callback for changes
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {


            }

            override fun onStateChanged(p0: View, p1: Int) {

                // change the state of the bottom sheet
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {

                    iv_expand.setImageResource(R.drawable.ic_expand_more)
                } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    iv_expand.setImageResource(R.drawable.ic_expand_less)

                }

            }


        })


        registerReceiver(GpsLocationReceiver(), IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))

        try {
            if (intent != null) {
                if (intent.getStringExtra("fromNotification") != null && intent.getStringExtra("fromNotification").equals(
                                "54"
                        )) {

                    var notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancelAll()
                    val intent = Intent(this, PhoneLoginActivity::class.java)
                    finishAffinity()
                    finish()
                    startActivity(intent)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()

        }


    }


    /** first socket to hit when user comes on home screen to check his ride status -
     *  0 for on way, 1 = reached source, 2= start, 3 = complete, 4 = paid 5 = rating done, 6 = skip rating, 9=no ride, 11 for cancel
     **/
    fun checkRideStatus_Socket() {
        try {
            jsonObject = JSONObject()
            jsonObject.put("userId", RydzApplication.user_obj!!.id)
            jsonObject.put("bookingId", "")
            sendObjectToSocket(jsonObject, ConstVariables.USERRIDESTATUS)
        } catch (e: Exception) {

        }

    }


    //to get the list of nearby drivers it will hit if there is no ride going on
    private fun userGetNearByDrivers_Socket(lat: Double, lng: Double) {

        jsonObject = JSONObject()
        jsonObject.put("userId", RydzApplication.user_obj!!.id)
        jsonObject.put("adminId", RydzApplication.adminId)
        jsonObject.put("latitude", lat)
        jsonObject.put("longitude", lng)
        sendObjectToSocket(jsonObject, ConstVariables.GETDRIVERS)

    }

    //to emit vehciletype event
    private fun getVehicleTypes_Socket() {
        mProgressDialog!!.show()

        jsonObject = JSONObject()
        jsonObject.put("userId", RydzApplication.user_obj!!.id)
        sendObjectToSocket(jsonObject, EVENT_GETVEHICLETYPES)

    }


    // request cab booking socket
    private fun userScheduleRideRequestBooking(src: SendBookResponse.Booking.Source, des: SendBookResponse.Booking.Destination, paymentMode: String, vehicleType: String, fare: Double, notes: String, subTotal: Double, couponAmount: Double, pending: Double, walletAmount: Double, distane: Double) {

        Log.e("distance", "" + distane)
        var couponCode = ""
        couponCode = if (promo != null)
            promo!!.code
        else {

            ""
        }
        val source_obj = RideSource(src.latitude!!.toDouble(), src.longitude!!.toDouble(), src.name!!)

        val destinationObj = RideSource(des.latitude!!.toDouble(), des.longitude!!.toDouble(), des.name!!)


        val scheduleRideRequest = ScheduleRideRequest(RydzApplication.adminId, RydzApplication.user_obj!!.id, vehicleType, source_obj, destinationObj, fare, scheduleMillisceonds, scheduleTimeSlot, paymentMode, RydzApplication.user_obj!!.firstName, RydzApplication.user_obj!!.lastName, RydzApplication.user_obj!!.countryCode + RydzApplication.user_obj!!.phone, notes, subTotal, couponAmount, couponCode, tax, pending, walletAmount, 0.0, distane)

        mMainViewModel!!.scheduleRideRequest(scheduleRideRequest)

        isScheduleRide = false
        time_slot = null

    }

    //common method to emit any socket event
    fun sendObjectToSocket(jsonObject: JSONObject, type: String) =
            AppSocketListener.getInstance().emit(type, jsonObject)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LOCATION_REQUEST) {
            if (EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java) != null) {
                if (mMap != null)
                    mMap!!.clear()


                isSetPinLocation = EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java).isPin
                srcPlace = EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java).source
                destinationPlace = EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java).destination
                isFocusOnSourse = EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java).isFocusOnSource
                EventBus.getDefault().removeStickyEvent(PlacesEventObject::class.java)

                if (!isSetPinLocation) {
                    if (srcPlace != null && destinationPlace != null && !srcPlace!!.latitude!!.isNullOrEmpty() && !destinationPlace!!.latitude!!.isNullOrEmpty()) {
                        showLocations(srcPlace!!, destinationPlace!!)
                    } else {
                        isSetPinLocation = false
                        srcPlace = null
                        destinationPlace = null
                        clearViews()
                    }
                } else {
                    isSetPinLocation = false
                    setResultLocation()


                }


            } else {

                srcPlace = null
                destinationPlace = null
                clearViews()
            }
        } else if (requestCode == PAYMENT_REQUEST && resultCode == Activity.RESULT_OK) {
            Log.e("1791", " 1791")

            if (confirmRideDialog != null && tempvehicleObj != null) {


                if (data?.getParcelableExtra<PromoCode>("promo") != null) {
                    promo = data.getParcelableExtra<PromoCode>("promo")
                } else
                    promo = null

                if (promo != null) {
                    tvPromoAmount?.visibility = View.VISIBLE
                    tvPromocode?.visibility = View.VISIBLE
                    tvPromocode?.text = "Promo(${promo?.code})"
                } else {
                    tvPromoAmount?.visibility = View.GONE
                    tvPromocode?.visibility = View.GONE

                }

                farecal = 0.0
                walletAmount = data?.getDoubleExtra("walletAmount", walletAmount)!!

                subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRate.toString().replace(",", "").toDouble(), tempvehicleObj?.baseFare.toString().replace(",", "").toDouble(), tempvehicleObj?.fareChangekm.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRateAfter.toString().replace(",", "").toDouble()) - farecal
                tv_fare!!.text = "$ " + String.format("%1$.2f", subTotal).replace(",", ".").toDouble()


                if (promo != null && promo!!.discountType == 0) {    // 0 -> for percentage discount


                    farecal = (promo!!.discount / 100) * estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRate.toString().replace(",", "").toDouble(), tempvehicleObj?.baseFare.toString().replace(",", "").toDouble(), tempvehicleObj?.fareChangekm.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRateAfter.toString().replace(",", "").toDouble())


                    subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRate.toString().replace(",", "").toDouble(), tempvehicleObj?.baseFare.toString().replace(",", "").toDouble(), tempvehicleObj?.fareChangekm.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRateAfter.toString().replace(",", "").toDouble()) - farecal


                } else if (promo != null && promo!!.discountType == 1) {   // 1-> for flat discount


                    if (promo!!.discount > estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRate.toString().replace(",", "").toDouble(), tempvehicleObj?.baseFare.toString().replace(",", "").toDouble(), tempvehicleObj?.fareChangekm.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRateAfter.toString().replace(",", "").toDouble())) {
                        farecal = subTotal
                        subTotal = 0.0

                    } else {
                        farecal = promo!!.discount.toDouble()
                        subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRate.toString().replace(",", "").toDouble(), tempvehicleObj?.baseFare.toString().replace(",", "").toDouble(), tempvehicleObj?.fareChangekm.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRateAfter.toString().replace(",", "").toDouble()) - promo!!.discount
                    }


                } else if (promo != null && promo!!.discountType == 2) {
                    farecal = (promo!!.discount / 100) * estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRate.toString().replace(",", "").toDouble(), tempvehicleObj?.baseFare.toString().replace(",", "").toDouble(), tempvehicleObj?.fareChangekm.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRateAfter.toString().replace(",", "").toDouble())

                    if (farecal > promo!!.maxamount) {
                        farecal = promo!!.maxamount.toDouble()
                        subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRate.toString().replace(",", "").toDouble(), tempvehicleObj?.baseFare.toString().replace(",", "").toDouble(), tempvehicleObj?.fareChangekm.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRateAfter.toString().replace(",", "").toDouble() - promo!!.maxamount)
                    } else {

                        subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRate.toString().replace(",", "").toDouble(), tempvehicleObj?.baseFare.toString().replace(",", "").toDouble(), tempvehicleObj?.fareChangekm.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRateAfter.toString().replace(",", "").toDouble()) - farecal
                    }

                } else {
                    subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRate.toString().replace(",", "").toDouble(), tempvehicleObj?.baseFare.toString().replace(",", "").toDouble(), tempvehicleObj?.fareChangekm.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRateAfter.toString().replace(",", "").toDouble())
                }

                tvPromoAmount?.text = "$ " + String.format("%1$.2f", farecal).replace(",", ".").toDouble()
                tax = (taxRate / 100) * subTotal

                subTotal = subTotal + tax


                tvTax?.text = "$ " + String.format("%1$.2f", tax).replace(",", ".").toDouble()
                tvTaxType?.text = "FL sales tax($taxRate%)"
                if (sharedPreference.contains(PreferenceHelper.Key.PAYMENTMODE) && sharedPreference.getString(PreferenceHelper.Key.PAYMENTMODE, "").equals(PreferenceHelper.Key.CARD) && sharedPreference.contains(PreferenceHelper.Key.SAVEDCARD)) {
                    val gsonString = sharedPreference[PreferenceHelper.Key.SAVEDCARD, ""]
                    var gson = Gson()
                    var savedCard = gson.fromJson(gsonString.toString(), Card::class.java) as Card
                    tvPaymentmode?.text = "Card ••••••••••••${savedCard.last4Digits}"
                    paymentMode = "card"
                    cardId = savedCard._id
                } else {
                    /* tvPaymentmode?.text = "Cash"
                     paymentMode = "cash"*/
                    tvPaymentmode?.text = "Card  (No card selected)"
                    paymentMode = "card"
                    cardId = ""
                }
                if (sharedPreference.contains(PreferenceHelper.Key.WALLETSELECTED) && sharedPreference.getBoolean(PreferenceHelper.Key.WALLETSELECTED, false)) {

                    tvWalletAmountUsed?.visibility = View.VISIBLE
                    tv_wallet?.visibility = View.VISIBLE
                    if (subTotal >= walletAmount) {
                        subTotal -= walletAmount
                        walletAmountUsed = walletAmount


                        tvPaymentmode?.text = tvPaymentmode?.text.toString() + "+Wallet"

                    } else if (subTotal < walletAmount) {
                        walletAmountUsed = subTotal
                        subTotal = 0.0 //wallet amount deducted will be sent at that backend

                        tvPaymentmode?.text = "Wallet"

                    }
                    tvWalletAmountUsed?.text = "$ " + String.format("%1$.2f", walletAmountUsed).replace(",", ".").toDouble()
                } else {
                    tvWalletAmountUsed?.visibility = View.GONE
                    tv_wallet?.visibility = View.GONE

                    walletAmountUsed = 0.0
                }
                tv_subtotal!!.text = "$ " + String.format("%1$.2f", subTotal).replace(",", ".").toDouble()

            }


        }
        if (resultCode == Activity.RESULT_OK && requestCode == 121) {

        }
    }

    //set location using pin functionality
    private fun setResultLocation() {


        if (mMap != null) {
            iv_loc_pin.visibility = View.VISIBLE
            ll_pinLocation.visibility = View.VISIBLE


            if (isFocusOnSourse) {

                if (srcPlace != null && destinationPlace != null && !srcPlace!!.latitude!!.isNullOrEmpty() && !destinationPlace!!.latitude!!.isNullOrEmpty()) {
                    tv_setpin.visibility = View.VISIBLE
                } else {
                    tv_setpin.visibility = View.GONE
                }

                getCountryCode(srcPlace!!.latitude!!.toDouble(), srcPlace!!.longitude!!.toDouble())

                changePlaces2(isFocusOnSourse)
                tv_source.text = srcPlace!!.name

                drawMarker(srcPlace!!.latitude!!.toDouble(), srcPlace!!.longitude!!.toDouble())
            } else {
                iv_schedule.visibility = View.GONE

                if (srcPlace != null && destinationPlace != null && !srcPlace!!.latitude!!.isNullOrEmpty() && !destinationPlace!!.latitude!!.isNullOrEmpty()) {
                    tv_setpin.visibility = View.VISIBLE
                } else {
                    tv_setpin.visibility = View.GONE
                }
                changePlaces2(isFocusOnSourse)

                tv_des.text = destinationPlace!!.name
                drawMarker(destinationPlace!!.latitude!!.toDouble(), destinationPlace!!.longitude!!.toDouble())
            }

            mMap!!.setOnCameraIdleListener(this@MainActivity)  //before changes

        }
    }


    //set location using pin functionality
    private fun setPinLocation() {

        if (mMap != null) {

            if (rideStatus == -1) {
                iv_loc_pin.visibility = View.VISIBLE
                ll_pinLocation.visibility = View.VISIBLE
            }


            if (isFocusOnSourse) {
                tv_source.hint = getString(R.string.pickup)
                tv_setpin.hint = getString(R.string.confirmPickup)

                if (srcPlace != null && destinationPlace != null && !srcPlace!!.latitude!!.isNullOrEmpty() && !destinationPlace!!.latitude!!.isNullOrEmpty()) {
                    tv_setpin.visibility = View.VISIBLE
                } else {
                    tv_setpin.visibility = View.GONE
                }
                changePlaces2(isFocusOnSourse)

                drawMarker(srcPlace!!.latitude!!.toDouble(), srcPlace!!.longitude!!.toDouble())
            } else {
                tv_des.hint = getString(R.string.whereto)
                tv_setpin.hint = getString(R.string.confirmDes)
                iv_schedule.visibility = View.GONE

                if (srcPlace != null && destinationPlace != null && !srcPlace!!.latitude!!.isNullOrEmpty() && !destinationPlace!!.latitude!!.isNullOrEmpty()) {
                    tv_setpin.visibility = View.VISIBLE
                } else {
                    tv_setpin.visibility = View.GONE
                }
                changePlaces2(isFocusOnSourse)

                drawMarker(destinationPlace!!.latitude!!.toDouble(), destinationPlace!!.longitude!!.toDouble())
            }

            mMap!!.setOnCameraIdleListener(this@MainActivity)  //before changes

        }
    }

    //when pin location is changed
    override fun onCameraIdle() {

        if (isSetPinLocation) {


            if (isFocusOnSourse) {
                tv_source.text = ""
                tv_source.text = getString(R.string.loading)
            } else {
                tv_des.text = ""
                tv_des.text = getString(R.string.loading)
            }




            Handler().postDelayed({
                try {
                    Log.e("onCameraIdle", mMap!!.cameraPosition!!.target.latitude.toString() + " " + mMap!!.cameraPosition!!.target.longitude)
                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    val addresses: List<Address>
                    addresses = geocoder.getFromLocation(mMap!!.cameraPosition!!.target.latitude, mMap!!.cameraPosition!!.target.longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    if (isFocusOnSourse) {
                        srcPlace = SendBookResponse.Booking.Source(addresses[0].latitude.toString(), addresses[0].longitude.toString(), address)
                        tv_source.text = address
                    } else {
                        destinationPlace = SendBookResponse.Booking.Destination(addresses[0].latitude.toString(), addresses[0].longitude.toString(), address)
                        tv_des.text = address
                    }

                    changePlaces2(isFocusOnSourse)

                } catch (e: Exception) {
                }

            }, 700)


        } else {
            isSetPinLocation = true
        }
    }


    // to cancel pin location
    private fun toCancelPinLocation() {
        isSetPinLocation = false
        ll_pinLocation.visibility = View.GONE
        //cl_pinloc.visibility = View.GONE
        iv_loc_pin.visibility = View.GONE
        iv_menu.visibility = View.VISIBLE
        clearViews()

    }


    @SuppressLint("InflateParams")
    private fun onDriverArrivalonSource(src: SendBookResponse.Booking.Source, cabLocation: TrackStatusResponse.Location) {
        val LatLongB = LatLngBounds.Builder()
        runOnUiThread {

            mMap!!.clear()
            val desMarker_view = layoutInflater.inflate(R.layout.marker_des, null) as View
            val tv_desAddress = desMarker_view.findViewById<TextView>(R.id.tv_desAddress)

            tv_desAddress.text = getString(R.string.meet_driver)
            if (polyline != null) {
                polyline = null
                polyline = null


            }
            tv_time.text = getString(R.string.meet_driver)

            tv_distance.visibility = View.GONE
            driver_previosloc = cabLocation
            mMap!!.addMarker(MarkerOptions().position(LatLng(src.latitude!!.toDouble(), src.longitude!!.toDouble())).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this@MainActivity, desMarker_view))))
            desMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(src.latitude!!.toDouble(), src.longitude!!.toDouble())).anchor(0.0f, 0.2f).icon(BitmapDescriptorFactory.fromResource(R.drawable.src_marker)))

            if (cabLocation != null) {
                srcMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(cabLocation.latitude!!.toDouble(), cabLocation.longitude!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cab)))
                srcMarker!!.rotation = cabLocation.bearing!!.toFloat()
            }


            LatLongB.include(LatLng(src.latitude!!.toDouble(), src.longitude!!.toDouble()))
            LatLongB.include(LatLng(cabLocation.latitude!!.toDouble(), cabLocation.longitude!!.toDouble()))
            val bounds = LatLongB.build()


            // show map with route centered
            if (isCameraFocus) {
                isCameraFocus = false
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15))
            }


        }

    }

    //add locations on map with path and time estimation
    @SuppressLint("InflateParams")
    private fun showLocations(src: SendBookResponse.Booking.Source, destination: SendBookResponse.Booking.Destination) {

        val LatLongB = LatLngBounds.Builder()
        //markers layouts
        val srcMarker_view = layoutInflater.inflate(R.layout.marker_src, null) as View
        val desMarker_view = layoutInflater.inflate(R.layout.marker_des, null) as View
        val tv_time_estimation = srcMarker_view.findViewById<TextView>(R.id.tv_time_estimation)
        val src_marker_name = srcMarker_view.findViewById<TextView>(R.id.tv_address)
        val des_marker_name = desMarker_view.findViewById<TextView>(R.id.tv_desAddress)


        tv_source.text = src.name
        tv_des.text = destination.name

        cl_main.visibility = View.GONE
        iv_schedule.visibility = View.GONE


        // Declare polyline object and set up color and width
        val options = PolylineOptions()
        options.color(Color.BLACK)
        options.width(8f)
        // build URL to call API
        val url = getURL(LatLng(src.latitude!!.toDouble(), src.longitude!!.toDouble()), LatLng(destination.latitude!!.toDouble(), destination.longitude!!.toDouble()))

        async {
            // Connect to URL, download content and convert into string asynchronously
            val result = URL(url).readText()
            val pathResponse: PathResponse = gson.fromJson(result, PathResponse::class.java)

            uiThread {
                mMap!!.clear()
                if (pathResponse.routes != null && pathResponse.routes!!.isNotEmpty()) {
                    //set time estimation
                    tripTime = pathResponse.routes!![0]!!.legs!![0]!!.duration!!.text.toString()
                    tv_time_estimation.text = tripTime

                    tripDistance = pathResponse.routes!![0]!!.legs!![0]!!.distance!!.text!!.replace(",", "").split(" ")[0].toDouble()


                    mMap!!.addMarker(MarkerOptions().position(LatLng(destination.latitude!!.toDouble(), destination.longitude!!.toDouble())).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.des_marker)))
                    des_marker_name.text = destination.name

                    mMap!!.addMarker(MarkerOptions().position(LatLng(src.latitude!!.toDouble(), src.longitude!!.toDouble())).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.src_marker)))
                    src_marker_name.text = src.name

                }
                // When API call is done, create parser and convert into JsonObjec
                val parser: Parser = Parser()
                val stringBuilder: StringBuilder = StringBuilder(result)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                // get to the correct element in JsonObject
                Log.e("773", json.toString())
                val routes = json.array<JsonObject>("routes")
                if (routes != null && routes.size > 0) {


                    val points = routes["legs"]["steps"][0] as JsonArray<JsonObject>
                    // For every element in the JsonArray, decode the polyline string and pass all points to a List
                    val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                    polyLine = ArrayList()
                    polyLine = polypts
                    // Add  points to polyline and bounds
                    options.add(LatLng(src.latitude!!.toDouble(), src.longitude!!.toDouble()))
                    LatLongB.include(LatLng(src.latitude!!.toDouble(), src.longitude!!.toDouble()))
                    for (point in polypts) {
                        options.add(point)

                        LatLongB.include(point)
                    }
                    options.add(LatLng(destination.latitude!!.toDouble(), destination.longitude!!.toDouble()))
                    // build bounds
                    val bounds = LatLongB.build()
                    // add polyline to the map
                    if (srcPlace != null && destinationPlace != null) {
                        polyline = mMap!!.addPolyline(options)

                        // show map with route centered
                        // mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                    }

                    //after drawing path get vehicle types
                    emitListeners()
                    getVehicleTypes_Socket()


                } else {
                    Toast.makeText(this@MainActivity, "Please select your locations from a same country", Toast.LENGTH_SHORT).show()
                    clearViews()
                }
            }
        }

    }

    //to generate url to get directions from google api
    private fun getURL(from: LatLng, to: LatLng): String {

        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor&mode=driving&key=" + getString(R.string.places_api_key)
        Log.e("797", "https://maps.googleapis.com/maps/api/directions/json?$params")
        return "https://maps.googleapis.com/maps/api/directions/json?$params"

    }

    /**
     * Method to decode polyline points
     * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }


    //to
    fun startTimer() {


        runOnUiThread {


            resendTimer = object : CountDownTimer(reqTime, 1000) {
                override fun onFinish() {
                    if (booleanDriverRequest) {
                        sendObjectToSocket(reqJsonObject, ConstVariables.SENDBOOKING)
                        startTimer()
                    }
                }

                override fun onTick(millisUntilFinished: Long) {}
            }

            resendTimer.start()

        }
    }


    private fun userTracking_Socket(tracking_id: String) {


        try {
            AppSocketListener.getInstance().off(tracking_id, onUserTracking)
        } catch (e: Exception) {
        }

        try {

            Log.e("onUserTracking", tracking_id)
            AppSocketListener.getInstance().addOnHandler(tracking_id, onUserTracking)
        } catch (e: Exception) {
        }


    }


    fun addNearByDrivers(driversLocations: List<GetNearByDrivers.DriversLocation>) {

        runOnUiThread {
            if (driversLocations != null && driversLocations.isNotEmpty()) {
                mMarker_dataList = HashMap()
                for (i in 0 until driversLocations.size) {
                    if (driversLocations[i] != null) {
                        val markerOptions = MarkerOptions()

                        markerOptions.anchor(0.0f, 0.0f)
                        markerOptions.zIndex(0.0f)
                        markerOptions.position(LatLng(driversLocations[i].latitude!!, driversLocations[i].longitude!!))
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cab))
                        if (mMap != null) {
                            val m = mMap!!.addMarker(markerOptions)
                            // builder.include(LatLng(driversLocations[i].latitude!!, driversLocations[i].longitude!!))
                            mMarker_dataList!![m.id!!] = driversLocations[i]
                        }
                    }
                }
                try {
                    // val bounds: LatLngBounds = builder.build()
                    //  mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 16))

                } catch (e: java.lang.IllegalStateException) {

                }

            } else {

                try {
                    mMap!!.isMyLocationEnabled = true
                } catch (e: Exception) {
                }
            }


        }
    }


    private fun createDrawableFromView(context: Context, view: View): Bitmap {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    //to show driver's info dialog
    private fun showDriverBottomSheet() {
        if (sendBookResponse != null) runOnUiThread {


            cl_main.visibility = View.GONE
            iv_schedule.visibility = View.GONE
            iv_loc_pin.visibility = View.GONE


            driver_bottom_sheet.visibility = View.VISIBLE
            val sendBookResponse: SendBookResponse = sendBookResponse!!
            tv_driverName.text = sendBookResponse.booking!!.driverId!!.firstName + " " + sendBookResponse.booking!!.driverId!!.lastName
            if (sendBookResponse.booking!!.driverId!!.ratings != null)
                tv_driverrating.text = sendBookResponse.booking!!.driverId!!.totalDrivingRating!!.toString()
            else
                tv_driverrating.text = "0"

            tv_carModel.text = sendBookResponse.booking!!.driverId!!.vehicleName + " " + sendBookResponse.booking!!.driverId!!.manufacturerName
            tv_carNumber.text = sendBookResponse.booking!!.driverId!!.vehicleNumber
            tv_desName.text = sendBookResponse.booking!!.destination!!.name
            tv_fare?.text = "$ " + String.format("%1$.2f", sendBookResponse.booking!!.fare!!).replace(",", ".").toDouble()

            try {

                if (sendBookResponse.booking!!.driverId!!.profilePic != null && sendBookResponse.booking!!.driverId!!.profilePic!!.isNotEmpty())
                    Glide.with(this@MainActivity).load(RydzApplication.BASEURLFORPHOTO + sendBookResponse.booking!!.driverId!!.profilePic).into(iv_driver)
                else
                    Glide.with(this@MainActivity).load(R.drawable.group).into(iv_driver)
            } catch (e: Exception) {

            }
            try {


                if (sendBookResponse.booking!!.driverDocuments[0].image != null && sendBookResponse.booking!!.driverDocuments[0].image.isNotEmpty()) {
                    Glide.with(this@MainActivity).load(RydzApplication.BASEURLFORPHOTO + sendBookResponse.booking!!.driverDocuments[0].image).into(iv_car)
                } else
                    Glide.with(this@MainActivity).load(R.drawable.finding_ride).into(iv_car)
            } catch (e: java.lang.Exception) {

            }



            btn__contact.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + (sendBookResponse.booking!!.driverId!!.contryCode + sendBookResponse.booking!!.driverId!!.phone))
                startActivity(intent)
            }

            tv_sos.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + ("911"))
                startActivity(intent)
            }

            btn_cancel.setOnClickListener {
                jsonObject = JSONObject()
                jsonObject.put("status", 11)
                jsonObject.put("bookingId", sendBookResponse.booking!!.id)
                sendObjectToSocket(jsonObject, USERCHANGESTATUS)
            }

            btn__chat.setOnClickListener {
                val chatIntent = Intent(this@MainActivity, ChatActivity::class.java)

                chatIntent.putExtra("fromNotification", "yes")
                startActivity(chatIntent)
            }
        }
    }

    //to show driver's info dialog
    private fun showDriverInfo(userrideStatusResponse: SendBookResponse) {
        if (userrideStatusResponse != null) {
            muserrideStatusResponse = userrideStatusResponse
            runOnUiThread {

                driver_bottom_sheet.visibility = View.VISIBLE
                //show details of driver
                tv_driverName.text = userrideStatusResponse.booking!!.driverId!!.firstName + " " + userrideStatusResponse.booking!!.driverId!!.lastName

                if (userrideStatusResponse.booking!!.driverId!!.totalDrivingRating != null)
                    tv_driverrating.text = userrideStatusResponse.booking!!.driverId!!.totalDrivingRating.toString()
                else
                    tv_driverrating.text = "0"

                tv_carModel.text = userrideStatusResponse.booking!!.driverId!!.vehicleName + " " + userrideStatusResponse.booking!!.driverId!!.manufacturerName
                tv_carNumber.text = userrideStatusResponse.booking!!.driverId!!.vehicleNumber
                tv_desName.text = userrideStatusResponse.booking!!.destination!!.name



                tv_fare?.text = "$ " + String.format("%1$.2f", userrideStatusResponse.booking!!.fare!!).replace(",", ".").toDouble()

                if (userrideStatusResponse.booking!!.driverId!!.profilePic != null && userrideStatusResponse.booking!!.driverId!!.profilePic!!.isNotEmpty())
                    Glide.with(this@MainActivity).load(RydzApplication.BASEURLFORPHOTO + userrideStatusResponse.booking!!.driverId!!.profilePic).into(iv_driver)
                else
                    Glide.with(this@MainActivity).load(R.drawable.group).into(iv_driver)


                try {


                    if (userrideStatusResponse.booking!!.driverDocuments[0].image != null && userrideStatusResponse.booking!!.driverDocuments[0].image.isNotEmpty()) {
                        Glide.with(this@MainActivity).load(RydzApplication.BASEURLFORPHOTO + userrideStatusResponse.booking!!.driverDocuments[0].image).into(iv_car)
                    } else
                        Glide.with(this@MainActivity).load(R.drawable.finding_ride).into(iv_car)
                } catch (e: java.lang.Exception) {

                }

                btn__contact.setOnClickListener {
                    if ((userrideStatusResponse.booking!!.driverId!!.contryCode + userrideStatusResponse.booking!!.driverId!!.phone) != null) {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:" + (userrideStatusResponse.booking!!.driverId!!.contryCode + userrideStatusResponse.booking!!.driverId!!.phone))
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@MainActivity, getString(R.string.str_wrong), Toast.LENGTH_SHORT).show()
                    }
                }

                tv_sos.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + ("911"))
                    startActivity(intent)
                }

                btn_cancel.setOnClickListener {
                    jsonObject = JSONObject()
                    jsonObject.put("status", 11)
                    jsonObject.put("bookingId", userrideStatusResponse.booking!!.id)
                    sendObjectToSocket(jsonObject, USERCHANGESTATUS)
                }

                btn__chat.setOnClickListener {
                    val chatIntent = Intent(this@MainActivity, ChatActivity::class.java)
                    chatIntent.putExtra("fromNotification", "yes")
                    startActivity(chatIntent)
                }


            }

        }
    }


    //socket to skip rating
    fun userSkipRating_socket(bookingId: String) {

        try {
            jsonObject = JSONObject()
            jsonObject.put("bookingId", bookingId)
            jsonObject.put("status", "6") //send status=6 to skip rating
            sendObjectToSocket(jsonObject, USERCHANGESTATUS)

        } catch (e: Exception) {

        }

        userGetNearByDrivers_Socket(latitude, longitude)

    }

    //calculate rate as per distance, base fare and fare rate
    fun estimateFare(distance: Double, fareRate: Double, baseFare: Double, baseDistance: Double, fareRateAfter: Double): Double {

        var estimateFare: Double = 0.0

        if (distance > baseDistance) {
            var additinalDistance = distance - baseDistance
            estimateFare = (baseDistance * fareRate) + (additinalDistance * fareRateAfter)

        } else {

            estimateFare = fareRate * distance

        }
        if (estimateFare <= baseFare)  //use of replace(",", ".") to handle double format issue in turkish language
            estimateFare = String.format("%1$.2f", baseFare).replace(",", ".").toDouble()

        return estimateFare

    }


    @SuppressLint("InflateParams")
    fun showDriver(cab_loc: TrackStatusResponse.Location, rider_location: SendBookResponse.Booking.Source)//if status=0 then show path b/w driver n user loc
    {

        if (polyline == null) {

            runOnUiThread {
                driver_previosloc = cab_loc
                mMap!!.clear()

                if (rideStatus < 2) {
                    btn__chat.enabled = true
                    btn_cancel.visibility = View.VISIBLE
                } else {
                    btn__chat.enabled = false
                    btn_cancel.visibility = View.GONE
                }


                val LatLongB = LatLngBounds.Builder()


                //markers layouts
                val desMarker_view = layoutInflater.inflate(R.layout.marker_des, null) as View
                val tv_desAddress = desMarker_view.findViewById<TextView>(R.id.tv_desAddress)

                tv_desAddress.text = rider_location.name


                // build URL to call API
                val url = getURL(LatLng(cab_loc.latitude!!.toDouble(), cab_loc.longitude!!.toDouble()), LatLng(rider_location.latitude!!.toDouble(), rider_location.longitude!!.toDouble()))
                val options = PolylineOptions()
                options.color(Color.BLACK)
                options.width(8f)

                async {
                    // Connect to URL, download content and convert into string asynchronously
                    val result = URL(url).readText()

                    Log.e("1347", result)
                    val pathResponse: PathResponse = gson.fromJson(result, PathResponse::class.java)

                    uiThread {
                        mMap!!.clear()
                        if (pathResponse.routes != null && pathResponse.routes!!.isNotEmpty()) {

                            tv_time.text = pathResponse.routes!![0]!!.legs!![0]!!.duration!!.text!!
                            tv_distance.text = pathResponse.routes!![0]!!.legs!![0]!!.distance!!.text!!
                            tv_distance.visibility = View.VISIBLE

                            Log.e("1470 showDriver", Utils.getTime(pathResponse.routes!![0]!!.legs!![0]!!.duration!!.value!!.toLong()))


                            // When API call is done, create parser and convert into JsonObjec
                            val parser: Parser = Parser()
                            val stringBuilder: StringBuilder = StringBuilder(result)
                            val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                            // get to the correct element in JsonObject

                            val routes = json.array<JsonObject>("routes")
                            if (routes != null && routes.size > 0) {


                                desMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(rider_location.latitude!!.toDouble(), rider_location.longitude!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.src_marker)))

                                srcMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(cab_loc.latitude!!.toDouble(), cab_loc.longitude!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cab)))
                                srcMarker!!.rotation = cab_loc.bearing!!.toFloat()

                                val points = routes["legs"]["steps"][0] as JsonArray<JsonObject>
                                // For every element in the JsonArray, decode the polyline string and pass all points to a List
                                val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                                polyLine = polypts


                                LatLongB.include(LatLng(cab_loc.latitude!!.toDouble(), cab_loc.longitude!!.toDouble()))
                                for (point in polypts) {
                                    options.add(point)
                                    LatLongB.include(point)
                                }
                                LatLongB.include(LatLng(rider_location.latitude!!.toDouble(), rider_location.longitude!!.toDouble()))


                                // build bounds
                                val bounds = LatLongB.build()
                                // add polyline to the map
                                polyline = mMap!!.addPolyline(options)
                                // show map with route centered
                                if (isCameraFocus) {

                                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 13))
                                    isCameraFocus = false
                                }


                            }
                        }
                    }
                }
            }
        } else {

            exceededTolerance = !PolyUtil.isLocationOnPath(LatLng(cab_loc.latitude!!, cab_loc.longitude!!), polyLine, false, tolerance)



            if (exceededTolerance) {
                runOnUiThread {
                    driver_previosloc = cab_loc


                    val LatLongB = LatLngBounds.Builder()


                    //markers layouts
                    val desMarker_view = layoutInflater.inflate(R.layout.marker_des, null) as View
                    val tv_desAddress = desMarker_view.findViewById<TextView>(R.id.tv_desAddress)

                    tv_desAddress.text = rider_location.name


                    // build URL to call API
                    val url = getURL(LatLng(cab_loc.latitude!!.toDouble(), cab_loc.longitude!!.toDouble()), LatLng(rider_location.latitude!!.toDouble(), rider_location.longitude!!.toDouble()))
                    val options = PolylineOptions()
                    options.color(Color.BLACK)
                    options.width(8f)

                    async {
                        // Connect to URL, download content and convert into string asynchronously
                        val result = URL(url).readText()


                        val pathResponse: PathResponse = gson.fromJson(result, PathResponse::class.java)


                        uiThread {
                            mMap!!.clear()
                            if (pathResponse.routes != null && pathResponse.routes!!.isNotEmpty()) {

                                tv_time.text = pathResponse.routes!![0]!!.legs!![0]!!.duration!!.text
                                tv_distance.text = pathResponse.routes!![0]!!.legs!![0]!!.distance!!.text!!
                                tv_distance.visibility = View.VISIBLE

                                // When API call is done, create parser and convert into JsonObjec
                                val parser: Parser = Parser()
                                val stringBuilder: StringBuilder = StringBuilder(result)
                                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                                // get to the correct element in JsonObject

                                val routes = json.array<JsonObject>("routes")
                                if (routes != null && routes.size > 0) {


                                    // desMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(rider_location!!.latitude!!.toDouble(), rider_location!!.longitude!!.toDouble())).anchor(0.0f, 0.2f).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this@MainActivity, desMarker_view))));
                                    desMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(rider_location.latitude!!.toDouble(), rider_location.longitude!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.src_marker)))

                                    //  srcMarker = mMap!!.addMarker(MarkerOptions().position(srcLat).anchor(0.0f, 0.2f).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this@MainActivity, srcMarker_view))));
                                    srcMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(cab_loc.latitude!!.toDouble(), cab_loc.longitude!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cab)))
                                    srcMarker!!.rotation = cab_loc.bearing!!.toFloat()

                                    val points = routes["legs"]["steps"][0] as JsonArray<JsonObject>
                                    // For every element in the JsonArray, decode the polyline string and pass all points to a List
                                    val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                                    // Add  points to polyline and bounds
                                    polyLine = polypts
                                    for (point in polypts) {
                                        options.add(point)

                                    }

                                    // add polyline to the map
                                    polyline = mMap!!.addPolyline(options)


                                }
                            }
                        }
                    }
                }

            } else
                runOnUiThread {

                    animateCar(LatLng(cab_loc.latitude!!, cab_loc.longitude!!), cab_loc.bearing!!.toFloat())
                }
        }
    }


    //calculate bearing b/w two values
    private fun bearingBetweenLocations(latLng1: LatLng, latLng2: LatLng): Double {
        val PI = 3.14159
        val lat1 = latLng1.latitude * PI / 180
        val long1 = latLng1.longitude * PI / 180
        val lat2 = latLng2.latitude * PI / 180
        val long2 = latLng2.longitude * PI / 180
        val dLon = (long2 - long1)
        val y = sin(dLon) * cos(lat2)
        val x = (cos(lat1) * sin(lat2) - (sin(lat1)
                * cos(lat2) * cos(dLon)))
        var brng = atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        return brng
    }


    @SuppressLint("InflateParams", "MissingPermission")
    fun rideTracking(driver_loc: TrackStatusResponse.Location, deslocation: SendBookResponse.Booking.Destination)//if status=3 then show path b/w driverloc n destination loc
    {


        if (polyline == null) {

            Log.e("rideTracking", "polyline is null")
            driver_previosloc = driver_loc

            runOnUiThread {
                if (mMap != null)
                    mMap!!.isMyLocationEnabled = false
                btn__chat.enabled = false
                btn__chat.setTextColor(resources.getColor(R.color.colorDisableText))

                val LatLongB = LatLngBounds.Builder()

                //markers layouts
                val desMarker_view = layoutInflater.inflate(R.layout.marker_des, null) as View
                val tv_desAddress = desMarker_view.findViewById<TextView>(R.id.tv_desAddress)

                tv_desAddress.text = deslocation.name
                // build URL to call API
                val url = getURL(LatLng(driver_loc.latitude!!.toDouble(), driver_loc.longitude!!.toDouble()), LatLng(deslocation.latitude!!.toDouble(), deslocation.longitude!!.toDouble()))
                val options = PolylineOptions()
                options.color(Color.BLACK)
                options.width(8f)

                async {
                    // Connect to URL, download content and convert into string asynchronously
                    val result = URL(url).readText()
                    val pathResponse: PathResponse = gson.fromJson(result, PathResponse::class.java)


                    uiThread {
                        mMap!!.clear()
                        if (pathResponse.routes != null && pathResponse.routes!!.isNotEmpty()) {


                            // When API call is done, create parser and convert into JsonObjec
                            val parser = Parser()
                            val stringBuilder = StringBuilder(result)
                            val json = parser.parse(stringBuilder) as JsonObject


                            val routes = json.array<JsonObject>("routes")
                            if (routes != null && routes.size > 0) {
                                tv_time.text = pathResponse.routes!![0]!!.legs!![0]!!.duration!!.text
                                tv_distance.text = pathResponse.routes!![0]!!.legs!![0]!!.distance!!.text!!
                                tv_distance.visibility = View.VISIBLE

                                Log.e("1585", routes.toString())
                                srcMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(driver_loc.latitude!!.toDouble(), driver_loc.longitude!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cab)))
                                srcMarker!!.rotation = driver_loc.bearing!!.toFloat()

                                desMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(deslocation.latitude!!.toDouble(), deslocation.longitude!!.toDouble())).anchor(0.0f, 0.2f).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this@MainActivity, desMarker_view))))
                                mMap!!.addMarker(MarkerOptions().position(LatLng(deslocation.latitude!!.toDouble(), deslocation.longitude!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.des_marker)))


                                val points = routes["legs"]["steps"][0] as JsonArray<JsonObject>
                                // For every element in the JsonArray, decode the polyline string and pass all points to a List
                                val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                                // Add  points to polyline and bounds
                                //  options.add(LatLng(driver_loc!!.latitude!!.toDouble(), driver_loc!!.longitude!!.toDouble()))

                                polyLine = polypts
                                LatLongB.include(LatLng(deslocation.latitude!!.toDouble(), deslocation.longitude!!.toDouble()))
                                for (point in polypts) {
                                    options.add(point)
                                    LatLongB.include(point)
                                }

                                // build bounds
                                val bounds = LatLongB.build()
                                // add polyline to the map
                                polyline = mMap!!.addPolyline(options)
                                // show map with route centered
                                if (isCameraFocus) {
                                    Log.e("1760", "1760")
                                    mMap!!.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 17))
                                    isCameraFocus = false
                                }


                            }
                        }
                    }
                }
            }
        } else {

            exceededTolerance = if (PolyUtil.isLocationOnPath(LatLng(driver_loc.latitude!!, driver_loc.longitude!!), polyLine, false, tolerance)) {
                Log.e("rideTracking", "Following same path")
                false
            } else {
                Log.e("rideTracking", "Path diverted")
                true
            }

            if (!exceededTolerance) {

                runOnUiThread {

                    animateCar(LatLng(driver_loc.latitude!!, driver_loc.longitude!!), driver_loc.bearing!!.toFloat())

                }


            } else {
                runOnUiThread {
                    if (mMap != null)
                        mMap!!.isMyLocationEnabled = false
                    val LatLongB = LatLngBounds.Builder()

                    //markers layouts
                    val desMarker_view = layoutInflater.inflate(R.layout.marker_des, null) as View
                    val tv_desAddress = desMarker_view.findViewById<TextView>(R.id.tv_desAddress)

                    tv_desAddress.text = deslocation.name
                    // build URL to call API
                    val url = getURL(LatLng(driver_loc.latitude!!.toDouble(), driver_loc.longitude!!.toDouble()), LatLng(deslocation.latitude!!.toDouble(), deslocation.longitude!!.toDouble()))
                    val options = PolylineOptions()
                    options.color(Color.BLACK)
                    options.width(8f)

                    async {
                        // Connect to URL, download content and convert into string asynchronously
                        val result = URL(url).readText()
                        val pathResponse: PathResponse = gson.fromJson(result, PathResponse::class.java)


                        uiThread {
                            mMap!!.clear()
                            if (pathResponse.routes != null && pathResponse.routes!!.isNotEmpty()) {

                                tv_time.text = pathResponse.routes!![0]!!.legs!![0]!!.duration!!.text
                                tv_distance.text = pathResponse.routes!![0]!!.legs!![0]!!.distance!!.text!!
                                tv_distance.visibility = View.VISIBLE
                                Log.e("1470 rideTracking1", Utils.getTime(pathResponse.routes!![0]!!.legs!![0]!!.duration!!.value!!.toLong()))


                                // When API call is done, create parser and convert into JsonObjec
                                val parser: Parser = Parser()
                                val stringBuilder: StringBuilder = StringBuilder(result)
                                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                                // get to the correct element in JsonObject
                                Log.e("1177", json.toString())
                                val routes = json.array<JsonObject>("routes")
                                if (routes != null && routes.size > 0) {
                                    // Log.e("776",routes!!["legs"]{"duration"} +"")
                                    //  srcMarker = mMap!!.addMarker(MarkerOptions().position(srcLat).anchor(0.0f, 0.2f).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this@MainActivity, srcMarker_view))));
                                    srcMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(driver_loc.latitude!!.toDouble(), driver_loc.longitude!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cab)))
                                    srcMarker!!.rotation = driver_loc.bearing!!.toFloat()

                                    desMarker = mMap!!.addMarker(MarkerOptions().position(LatLng(deslocation.latitude!!.toDouble(), deslocation.longitude!!.toDouble())).anchor(0.0f, 0.2f).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this@MainActivity, desMarker_view))))
                                    mMap!!.addMarker(MarkerOptions().position(LatLng(deslocation.latitude!!.toDouble(), deslocation.longitude!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.drawable.des_marker)))


                                    val points = routes["legs"]["steps"][0] as JsonArray<JsonObject>
                                    // For every element in the JsonArray, decode the polyline string and pass all points to a List
                                    val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!) }
                                    // Add  points to polyline and bounds
                                    // options.add(LatLng(driver_loc!!.latitude!!.toDouble(), driver_loc!!.longitude!!.toDouble()))
                                    polyLine = polypts

                                    //LatLongB.include(LatLng(deslocation!!.latitude!!.toDouble(), deslocation!!.longitude!!.toDouble()))
                                    for (point in polypts) {
                                        options.add(point)
                                        //LatLongB.include(point)
                                    }


                                    // add polyline to the map
                                    polyline = mMap!!.addPolyline(options)


                                }
                            }
                        }
                    }
                }


            }
        }
    }

    /** ride status change lsitener
     *  0 for on way, 1 = reached source, 2= start, 3 = complete, 4 = paid 5 = rating done
     **/

    //clear all places and clean map
    @SuppressLint("MissingPermission")
    fun clearViews() {
        Log.e("clearViews", "yes")
        isFocusOnSourse = true
        getCurrentLocation()
        setLocation()
        srcPlace = null
        isSetPinLocation = false
        destinationPlace = null
        vehicleSelectBottomSheetFragment = null


        //to show src & des views

        cl_main.visibility = View.VISIBLE
        iv_schedule.visibility = View.VISIBLE
        iv_menu.visibility = View.VISIBLE
        iv_loc_pin.visibility = View.VISIBLE

        if (mMap != null) {
            Log.e("map", "not null")
            mMap!!.clear()
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mMap!!.isMyLocationEnabled = true
        }
        tv_des.text = ""
        tv_source.text = ""

        driver_bottom_sheet.visibility = View.GONE

        muserrideStatusResponse = null
        rideStatus = -1
        try {
            if (longitude != null)
                goToMYLocation()


            userGetNearByDrivers_Socket(latitude, longitude)
        } catch (e: java.lang.Exception) {
        }
    }


    private fun changePlaces2(isFocusOnSource: Boolean) {
        var placesEventObject = PlacesEventObject(srcPlace!!, destinationPlace!!, true, isFocusOnSource)
        EventBus.getDefault().postSticky(placesEventObject)

    }


    /*
 Animates car by moving it by fractions of the full path and finally moving it to its
 destination in a duration of 5 seconds.
 */
    private fun animateCar(destination: LatLng, bearing: Float) {


        val startPosition = srcMarker!!.position
        val endPosition = LatLng(destination.latitude, destination.longitude)
        val latLngInterpolator = LatLngInterpolator.LinearFixed()
        val valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        valueAnimator.duration = 2000 // duration 5 seconds
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation ->
            try {
                val v = animation.animatedFraction
                val newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition)


                srcMarker!!.position = newPosition
                srcMarker!!.rotation = bearing


                try {
                    val nearestPoint = findNearestPoint(newPosition, polyLine!!)

                    //update polyline according to points
                    if (polyline != null && polyLine != null && polyLine!!.size > 1 && polyline!!.points.size > 1 && polyLine!!.indexOf(nearestPoint) >= 0 && polyLine!!.indexOf(nearestPoint) <= polyLine!!.size)
                        polyline!!.points = polyLine!!.subList(polyLine!!.indexOf(nearestPoint), polyLine!!.size)

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()

                }
            } catch (ex: Exception) {
            }
        }

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
        })

        valueAnimator.start()
    }


    private interface LatLngInterpolator {
        fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng
        class LinearFixed : LatLngInterpolator {
            override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
                val lat = (b.latitude - a.latitude) * fraction + a.latitude
                var lngDelta = b.longitude - a.longitude
                if (abs(lngDelta) > 180) {
                    lngDelta -= sign(lngDelta) * 360
                }
                val lng = lngDelta * fraction + a.longitude
                return LatLng(lat, lng)
            }
        }
    }


    private fun findNearestPoint(test: LatLng, target: List<LatLng>): LatLng {
        var distance = -1.0
        var minimumDistancePoint = test
        if (target == null) {
            return minimumDistancePoint
        }
        for (i in 0 until target.size) {
            val point = target[i]
            var segmentPoint = i + 1
            if (segmentPoint >= target.size) {
                segmentPoint = 0
            }
            val currentDistance = PolyUtil.distanceToLine(test, point, target[segmentPoint])
            if (distance == -1.0 || currentDistance < distance) {
                distance = currentDistance
                minimumDistancePoint = findNearestPoint(test, point, target[segmentPoint])
            }
        }
        return minimumDistancePoint
    }


    private fun findNearestPoint(p: LatLng, start: LatLng, end: LatLng): LatLng {
        if (start == end) {
            return start
        }

        val s0lat = Math.toRadians(p.latitude)
        val s0lng = Math.toRadians(p.longitude)
        val s1lat = Math.toRadians(start.latitude)
        val s1lng = Math.toRadians(start.longitude)
        val s2lat = Math.toRadians(end.latitude)
        val s2lng = Math.toRadians(end.longitude)

        val s2s1lat = s2lat - s1lat
        val s2s1lng = s2lng - s1lng
        val u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng) / (s2s1lat * s2s1lat + s2s1lng * s2s1lng)
        if (u <= 0) {
            return start
        }
        return if (u >= 1) {
            end
        } else LatLng(start.latitude + u * (end.latitude - start.latitude),
                start.longitude + u * (end.longitude - start.longitude))


    }


    //to cancel a ride brefore ride booking response
    fun toCancelRideBeforeConfirmation() {


        jsonObject = JSONObject()
        jsonObject.put("status", 13)
        jsonObject.put("userId", RydzApplication.user_obj!!.id)
        sendObjectToSocket(jsonObject, ConstVariables.CANCELREQUEST)

        clearViews()

    }


    //to confirm the selected vehicle
    fun confirmSelectedVehicleDialog(vehicleObj: VehicleTypeX) {
        tempvehicleObj = vehicleObj
        paymentMode = ""
        cardId = ""

        subTotal = 0.0
        farecal = 0.0
        //to show src & des views
        cl_main.visibility = View.GONE
        iv_schedule.visibility = View.GONE

        confirmRideDialog = BottomSheetDialog(this, R.style.TransparentDialog)
        confirmRideDialog!!.setContentView(R.layout.dialog_confirm_vehicle)


        tv_fare = confirmRideDialog!!.findViewById<TextView>(R.id.tv_fare)
        tvPaymentmode = confirmRideDialog!!.findViewById<TextView>(R.id.tv_payment_mode)
        val tv_capacity: TextView? = confirmRideDialog!!.findViewById<TextView>(R.id.tv_capacity)
        val iv_vehicle: ImageView? = confirmRideDialog!!.findViewById<ImageView>(R.id.iv_vehicle)
        val tv_vehileType: TextView? = confirmRideDialog!!.findViewById<TextView>(R.id.tv_vehileType)
        val tv_cust_name: TextView? = confirmRideDialog!!.findViewById<TextView>(R.id.tv_cust_name)
        val tv_cust_number: TextView? = confirmRideDialog!!.findViewById<TextView>(R.id.tv_cust_number)
        val tv_cust_notes: EditText? = confirmRideDialog!!.findViewById<EditText>(R.id.tv_cust_notes)
        val btn_confirm: Button? = confirmRideDialog!!.findViewById<Button>(R.id.btn_confirm)
        val tv_pendingfare: TextView? = confirmRideDialog!!.findViewById<TextView>(R.id.tv_pendingfare)
        tvWalletAmountUsed = confirmRideDialog!!.findViewById<TextView>(R.id.tv_walletamountused)
        tv_wallet = confirmRideDialog!!.findViewById<TextView>(R.id.tv_wallet)
        tv_subtotal = confirmRideDialog!!.findViewById<TextView>(R.id.tv_subtotal)
        tvPromocode = confirmRideDialog!!.findViewById<TextView>(R.id.tvPromocode)
        tvPromoAmount = confirmRideDialog!!.findViewById<TextView>(R.id.tvPromoAmount)
        tvTaxType = confirmRideDialog!!.findViewById<TextView>(R.id.tv_taxType)
        tvTax = confirmRideDialog!!.findViewById<TextView>(R.id.tv_tax)



        confirmRideDialog!!.setOnDismissListener {
            confirmRideDialog = null
        }

        if (pending > 0.0) {
            tv_pendingfare!!.text = "Pending\n$" + String.format("%1$.2f", pending).replace(",", ".").toDouble()
        }
        if (promo != null) {
            tvPromoAmount?.visibility = View.VISIBLE
            tvPromocode?.visibility = View.VISIBLE
            tvPromocode?.text = "Promo(${promo?.code})"
        } else {
            tvPromoAmount?.visibility = View.GONE
            tvPromocode?.visibility = View.GONE

        }

        subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRate.toString().replace(",", "").toDouble(), tempvehicleObj?.baseFare.toString().replace(",", "").toDouble(), tempvehicleObj?.fareChangekm.toString().replace(",", "").toDouble(), tempvehicleObj?.fareRateAfter.toString().replace(",", "").toDouble()) - farecal

        tv_fare!!.text = "$ " + String.format("%1$.2f", subTotal).replace(",", ".").toDouble()

        if (promo != null && promo!!.discountType == 0) {    // 0 -> for percentage discount


            farecal = (promo!!.discount / 100) * estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), vehicleObj.fareRate.toString().replace(",", "").toDouble(), vehicleObj.baseFare.toString().replace(",", "").toDouble(), vehicleObj.fareChangekm.toString().replace(",", "").toDouble(), vehicleObj.fareRateAfter.toString().replace(",", "").toDouble())


            subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), vehicleObj.fareRate.toString().replace(",", "").toDouble(), vehicleObj.baseFare.toString().replace(",", "").toDouble(), vehicleObj.fareChangekm.toString().replace(",", "").toDouble(), vehicleObj.fareRateAfter.toString().replace(",", "").toDouble()) - farecal


        } else if (promo != null && promo!!.discountType == 1) {   // 1-> for flat discount


            if (promo!!.discount > estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), vehicleObj.fareRate.toString().replace(",", "").toDouble(), vehicleObj.baseFare.toString().replace(",", "").toDouble(), vehicleObj.fareChangekm.toString().replace(",", "").toDouble(), vehicleObj.fareRateAfter.toString().replace(",", "").toDouble())) {
                farecal = subTotal
                subTotal = 0.0

            } else {
                farecal = promo!!.discount.toDouble()
                subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), vehicleObj.fareRate.toString().replace(",", "").toDouble(), vehicleObj.baseFare.toString().replace(",", "").toDouble(), vehicleObj.fareChangekm.toString().replace(",", "").toDouble(), vehicleObj.fareRateAfter.toString().replace(",", "").toDouble()) - promo!!.discount
            }


        } else if (promo != null && promo!!.discountType == 2) {
            farecal = (promo!!.discount / 100) * estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), vehicleObj.fareRate.toString().replace(",", "").toDouble(), vehicleObj.baseFare.toString().replace(",", "").toDouble(), vehicleObj.fareChangekm.toString().replace(",", "").toDouble(), vehicleObj.fareRateAfter.toString().replace(",", "").toDouble())

            if (farecal > promo!!.maxamount) {
                farecal = promo!!.maxamount.toDouble()
                subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), vehicleObj.fareRate.toString().replace(",", "").toDouble(), vehicleObj.baseFare.toString().replace(",", "").toDouble(), vehicleObj.fareChangekm.toString().replace(",", "").toDouble(), vehicleObj.fareRateAfter.toString().replace(",", "").toDouble() - promo!!.maxamount)
            } else {

                subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), vehicleObj.fareRate.toString().replace(",", "").toDouble(), vehicleObj.baseFare.toString().replace(",", "").toDouble(), vehicleObj.fareChangekm.toString().replace(",", "").toDouble(), vehicleObj.fareRateAfter.toString().replace(",", "").toDouble()) - farecal
            }

        } else {
            subTotal = estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), vehicleObj.fareRate.toString().replace(",", "").toDouble(), vehicleObj.baseFare.toString().replace(",", "").toDouble(), vehicleObj.fareChangekm.toString().replace(",", "").toDouble(), vehicleObj.fareRateAfter.toString().replace(",", "").toDouble())
        }

        tvPromoAmount?.text = "$ " + String.format("%1$.2f", farecal).replace(",", ".").toDouble()
        tax = (taxRate / 100) * subTotal



        subTotal = subTotal + tax




        tvTax?.text = "$ " + String.format("%1$.2f", tax).replace(",", ".").toDouble()
        tvTaxType?.text = "FL sales tax($taxRate%)"
        if (sharedPreference.contains(PreferenceHelper.Key.PAYMENTMODE) && sharedPreference.getString(PreferenceHelper.Key.PAYMENTMODE, "").equals(PreferenceHelper.Key.CARD) && sharedPreference.contains(PreferenceHelper.Key.SAVEDCARD)) {
            val gsonString = sharedPreference[PreferenceHelper.Key.SAVEDCARD, ""]
            var gson = Gson()
            var savedCard = gson.fromJson(gsonString.toString(), Card::class.java) as Card
            tvPaymentmode?.text = "Card ••••••••••••${savedCard.last4Digits}"


            paymentMode = "card"
            cardId = savedCard._id
        } else {
            /* tvPaymentmode?.text = "Cash"
             paymentMode = "cash"*/
            tvPaymentmode?.text = "Card  (No card selected)"
            paymentMode = "card"
            cardId = ""
        }
        if (sharedPreference.contains(PreferenceHelper.Key.WALLETSELECTED) && sharedPreference.getBoolean(PreferenceHelper.Key.WALLETSELECTED, false)) {
            tvWalletAmountUsed?.visibility = View.VISIBLE
            tv_wallet?.visibility = View.VISIBLE
            if (subTotal >= walletAmount) {
                subTotal -= walletAmount
                walletAmountUsed = walletAmount
                tvPaymentmode?.text = tvPaymentmode?.text.toString() + "+ Wallet"
            } else if (subTotal < walletAmount) {
                walletAmountUsed = subTotal
                subTotal = 0.0 //wallet amount deducted will be sent at that backend
                tvPaymentmode?.text = "Wallet"
            }
            tvWalletAmountUsed?.text = "$ " + String.format("%1$.2f", walletAmountUsed).replace(",", ".").toDouble()
        } else {
            tvWalletAmountUsed?.visibility = View.GONE
            tv_wallet?.visibility = View.GONE

            walletAmountUsed = 0.0
        }

        tv_subtotal!!.text = "$ " + String.format("%1$.2f", subTotal).replace(",", ".").toDouble()
        tv_vehileType!!.text = vehicleObj.name
        tv_cust_name!!.text = RydzApplication.user_obj!!.firstName + " " + RydzApplication.user_obj!!.lastName
        tv_cust_number!!.text = RydzApplication.user_obj!!.countryCode + " " + RydzApplication.user_obj!!.phone


        tv_capacity!!.text = vehicleObj.persons.toString()

        try {
            if (vehicleObj.image.isNotEmpty())
                Glide.with(this).load(RydzApplication.BASEURLFORPHOTO + vehicleObj.image).into(iv_vehicle!!)
        } catch (e: Exception) {

        }
        tvPaymentmode?.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity, PaymentActivity::class.java).apply {
                putExtra("from", "confirm")
                putExtra("promo", promo)
            }, PAYMENT_REQUEST
            )
        }

        btn_confirm!!.setOnClickListener {

            if (cardId.isNullOrEmpty()) {

                Toast.makeText(this, "Please add card", Toast.LENGTH_SHORT).show()


            } else {

                resendTimer2 = object : CountDownTimer(totalReqTime, 1000) {
                    override fun onFinish() {
                        if (booleanDriverRequest) {
                            booleanDriverRequest = false
                        }
                    }

                    override fun onTick(millisUntilFinished: Long) {}
                }

                if (isScheduleRide) {

                    userScheduleRideRequestBooking(srcPlace!!, destinationPlace!!, paymentMode, vehicleObj._id + "", estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), vehicleObj.fareRate.toString().replace(",", "").toDouble(), vehicleObj.baseFare.toString().replace(",", "").toDouble(), vehicleObj.fareChangekm.toString().replace(",", "").toDouble(), vehicleObj.fareRateAfter.toString().replace(",", "").toDouble()) + tax, tv_cust_notes!!.text.toString().trim(), subTotal, farecal, pending, walletAmountUsed, tripDistance!!)
                    confirmRideDialog!!.dismiss()
                    confirmRideDialog = null

                } else {

                    if (vehicleObj._id == RydzApplication.poolingType) //check if selected ride type is pooling
                    {
                        userRequestPoolBooking_Socket(cardId, srcPlace!!, destinationPlace!!, paymentMode, vehicleObj._id, subTotal, estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), vehicleObj.fareRate.toString().replace(",", "").toDouble(), vehicleObj.baseFare.toString().replace(",", "").toDouble(), vehicleObj.fareChangekm.toString().replace(",", "").toDouble(), vehicleObj.fareRateAfter.toString().replace(",", "").toDouble()) + tax, tripTime!!, tv_cust_notes!!.text.toString().trim(), tv_cust_name.text.toString(), farecal, tripDistance!!)

                    } else
                        userRequestBooking_Socket(cardId, srcPlace!!, destinationPlace!!, paymentMode, vehicleObj._id, subTotal, estimateFare(tripDistance!!.toString().replace(",", "").toDouble(), vehicleObj.fareRate.toString().replace(",", "").toDouble(), vehicleObj.baseFare.toString().replace(",", "").toDouble(), vehicleObj.fareChangekm.toString().replace(",", "").toDouble(), vehicleObj.fareRateAfter.toString().replace(",", "").toDouble()) + tax, tripTime!!, tv_cust_notes!!.text.toString().trim(), tv_cust_name.text.toString(), farecal, tripDistance!!)

                    findindRideProgressdialog = FindingRideBottomSheetFragment()
                    findindRideProgressdialog!!.isCancelable = false
                    findindRideProgressdialog!!.show(supportFragmentManager, "bdbd")
                    try {
                        if (confirmRideDialog != null)
                            confirmRideDialog!!.dismiss()
                        confirmRideDialog = null
                    } catch (e: java.lang.Exception) {

                    }
                }

                vehicleSelectBottomSheetFragment!!.dismiss()

            }
        }
        confirmRideDialog!!.show()


    }

    // request cab booking socket
    //"subtotal"   -   Actual estimation of fare - (fare + tax)
    //"fare"  - To pay after deducting promo amount   (If any) - wallet amount (If any)
    private fun userRequestBooking_Socket(cardId: String, src: SendBookResponse.Booking.Source, des: SendBookResponse.Booking.Destination, paymentMode: String, vehicleType: String, subTotal: Double, fare: Double, tripTime: String, notes: String, firstName: String, couponAmount: Double, distance: Double) {


        //Source(from) object
        val source_obj = JSONObject()
        source_obj.put("name", src.name)
        source_obj.put("latitude", src.latitude)
        source_obj.put("longitude", src.longitude)


        //Destination(to) object
        val destination_obj = JSONObject()
        destination_obj.put("name", des.name)
        destination_obj.put("latitude", des.latitude)
        destination_obj.put("longitude", des.longitude)

        reqJsonObject = JSONObject()
        reqJsonObject.put("userId", RydzApplication.user_obj!!.id)
        reqJsonObject.put("adminId", RydzApplication.adminId)
        reqJsonObject.put("triptime", tripTime)
        reqJsonObject.put("paymentMode", paymentMode)
        reqJsonObject.put("vehicleType", vehicleType)
        reqJsonObject.put("source", source_obj)
        reqJsonObject.put("destination", destination_obj)
        reqJsonObject.put("note", notes)
        reqJsonObject.put("firstName", firstName)
        reqJsonObject.put("lastName", " ")
        reqJsonObject.put("fare", subTotal)
        reqJsonObject.put("subtotalFare", fare)
        //coupon data
        reqJsonObject.put("couponAmount", couponAmount)
        reqJsonObject.put("distance", distance)

        if (promo != null)
            reqJsonObject.put("couponCode", promo!!.code)
        reqJsonObject.put("tax", tax)
        reqJsonObject.put("pending", pending)

        if (sharedPreference.contains(PreferenceHelper.Key.WALLETSELECTED) && sharedPreference.getBoolean(PreferenceHelper.Key.WALLETSELECTED, false))
            reqJsonObject.put("walletAmountUsed", walletAmountUsed)
        else
            reqJsonObject.put("walletAmountUsed", 0.0)



        reqJsonObject.put("isPaid", 0)
        reqJsonObject.put("cardId", cardId)


        sendObjectToSocket(reqJsonObject, ConstVariables.SENDBOOKING)
        booleanDriverRequest = true
        startTimer()

    }


    // request cab booking socket
    //"subtotal"   -   Actual estimation of fare - (fare + tax)
    //"fare"  - To pay after deducting promo amount   (If any) - wallet amount (If any)
    private fun userRequestPoolBooking_Socket(cardId: String, src: SendBookResponse.Booking.Source, des: SendBookResponse.Booking.Destination, paymentMode: String, vehicleType: String, subTotal: Double, fare: Double, tripTime: String, notes: String, firstName: String, couponAmount: Double, distance: Double) {


        //Source(from) object
        val source_obj = JSONObject()
        source_obj.put("name", src.name)
        source_obj.put("latitude", src.latitude)
        source_obj.put("longitude", src.longitude)
        //Destination(to) object
        val destination_obj = JSONObject()
        destination_obj.put("name", des.name)
        destination_obj.put("latitude", des.latitude)
        destination_obj.put("longitude", des.longitude)
        reqJsonObject = JSONObject()
        reqJsonObject.put("userId", RydzApplication.user_obj!!.id)
        reqJsonObject.put("adminId", RydzApplication.adminId)
        reqJsonObject.put("paymentMode", paymentMode)
        reqJsonObject.put("vehicleType", vehicleType)
        reqJsonObject.put("source", source_obj)
        reqJsonObject.put("destination", destination_obj)
        reqJsonObject.put("note", notes)
        reqJsonObject.put("firstName", firstName)
        reqJsonObject.put("lastName", "")
        reqJsonObject.put("fare", subTotal)
        reqJsonObject.put("subtotalFare", fare)
        reqJsonObject.put("distance", distance)
        //coupon data
        reqJsonObject.put("couponAmount", couponAmount)
        if (promo != null)
            reqJsonObject.put("couponCode", promo!!.code)
        reqJsonObject.put("tax", tax)
        reqJsonObject.put("pending", pending)
        if (sharedPreference.contains(PreferenceHelper.Key.WALLETSELECTED) && sharedPreference.getBoolean(PreferenceHelper.Key.WALLETSELECTED, false))
            reqJsonObject.put("walletAmountUsed", walletAmountUsed)
        else
            reqJsonObject.put("walletAmountUsed", 0.0)

        reqJsonObject.put("isPaid", 0)
        reqJsonObject.put("cardId", cardId)


        sendObjectToSocket(reqJsonObject, ConstVariables.JOINPOOLRIDE)
        booleanDriverRequest = true
        startTimer()


    }


    private fun setLocation() {


        runOnUiThread {
            Handler().postDelayed({
                try {

                    val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                    val addresses: List<Address>
                    addresses = geocoder.getFromLocation(latitude, longitude, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    if (addresses[0].getAddressLine(0).isNullOrEmpty()) {
                        val address = addresses[1].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                        srcPlace = SendBookResponse.Booking.Source(addresses[1].latitude.toString(), addresses[1].longitude.toString(), address)
                        tv_source.text = address
                        getCountryCode(latitude, longitude)

                    } else {
                        val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                        srcPlace = SendBookResponse.Booking.Source(addresses[0].latitude.toString(), addresses[0].longitude.toString(), address)
                        tv_source.text = address
                        getCountryCode(latitude, longitude)

                    }

                    destinationPlace = SendBookResponse.Booking.Destination()
                    isFocusOnSourse = true
                    setPinLocation()

                } catch (e: Exception) {
                }

            }, 700)
        }


        /*        val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            val request = FindCurrentPlaceRequest.builder(placeFields).build()

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val placeResponse = placesClient.findCurrentPlace(request)

                placeResponse.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val response = task.result
                        if (response != null && response.placeLikelihoods.size > 0) {

                            if(response.placeLikelihoods[0].place.address.isNullOrEmpty())
                            {
                                Log.e("3575", response.placeLikelihoods[1].place.latLng?.latitude.toString()+ " "+response.placeLikelihoods[1].place.latLng?.longitude.toString())
                                tv_source.text = response.placeLikelihoods[1].place.name + "," + response.placeLikelihoods[1].place.address
                                srcPlace = SendBookResponse.Booking.Source(response.placeLikelihoods[1].place.latLng!!.latitude.toString(), response.placeLikelihoods[1].place.latLng!!.longitude.toString(), response.placeLikelihoods[1].place.name + "," + response.placeLikelihoods[1].place.address)


                                getCountryCode(response.placeLikelihoods[1].place.latLng!!.latitude, response.placeLikelihoods[1].place.latLng!!.longitude)

                            }
                            else
                            {
                                Log.e("3575", response.placeLikelihoods[0].place.latLng?.latitude.toString()+ " "+response.placeLikelihoods[0].place.latLng?.longitude.toString())

                                tv_source.text = response.placeLikelihoods[0].place.name + "," + response.placeLikelihoods[0].place.address
                                srcPlace = SendBookResponse.Booking.Source(response.placeLikelihoods[0].place.latLng!!.latitude.toString(), response.placeLikelihoods[0].place.latLng!!.longitude.toString(), response.placeLikelihoods[0].place.name + "," + response.placeLikelihoods[0].place.address)


                                getCountryCode(response.placeLikelihoods[0].place.latLng!!.latitude, response.placeLikelihoods[0].place.latLng!!.longitude)

                            }

                            destinationPlace = SendBookResponse.Booking.Destination()
                            isFocusOnSourse = true
                            setPinLocation()

                        }

                    } else {
                        val exception = task.exception
                        if (exception is ApiException) {
                            val apiException = exception as ApiException?
                            Log.e("TAGGGGG", "Place not found: " + apiException!!.statusCode)
                        }
                    }
                }
            }
        }*/

    }



    fun checkRideAfterRating() {
        Handler().postDelayed({
            jsonObject = JSONObject()
            jsonObject.put("userId", RydzApplication.user_obj!!.id)
            jsonObject.put("bookingId", "")
            sendObjectToSocket(jsonObject, ConstVariables.USERRIDESTATUS)
        }, 2000)


    }


    //Send status to server Driver is login
    private fun newLogin() {
        try {
            val params = JSONObject()
            params.put("userId", RydzApplication.user_obj!!.id)
            sendObjectToSocket(params, USERNEWLOGIN)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }


    }


    //to get msg read status
    private fun readMsgStatus(bookingId: String) {
        try {
            val params = JSONObject()
            params.put("userId", RydzApplication.user_obj!!.id)
            params.put("bookingId", bookingId)


            sendObjectToSocket(params, USERREADSTATUS)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }


    //OnNew login Respose
    private val onNewLogin = Emitter.Listener { args ->
        try {
            Log.e("320", "call: onNewLogin   " + args[0].toString())
            try {
                val jsonObject = args[0] as JSONObject
                if (jsonObject.get("success").toString() == "true") {

                    runOnUiThread {

                        //clear shared prefrences

                        val deviceToken = sharedPreference.getString(PreferenceHelper.Key.FCMTOKEN, "")
                        val appLang = sharedPreference.getString(PreferenceHelper.Key.APPLANGUAGE, "")

                        val editor = sharedPreference.edit()
                        //editor.clear()
                        editor.remove(PreferenceHelper.Key.REGISTEREDUSER)
                        editor.commit()

                        val prefs = PreferenceHelper.defaultPrefs(this@MainActivity)
                        prefs[PreferenceHelper.Key.FCMTOKEN] = deviceToken
                        prefs[PreferenceHelper.Key.APPLANGUAGE] = appLang

                        onAlreadyLogin()
                    }
                }
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    //Logout a user if already login on other device
    fun onAlreadyLogin() {
        try {
            AlertDialog.Builder(this@MainActivity)
                    .setTitle("Exit")
                    .setCancelable(false)
                    .setMessage(getString(R.string.already_login))
                    .setPositiveButton(android.R.string.yes) { arg0, arg1 ->
                        val intent = Intent(applicationContext, PhoneLoginActivity::class.java)
                        intent.putExtra("navigate", "phonelogin")
                        startActivity(intent)
                        finishAffinity()
                    }.create().show()
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }

    }


    //got Accept fragment if click on notification request
    private fun closePrompt() {

        try {
            AlertDialog.Builder(this@MainActivity)
                    .setTitle("Alert")
                    .setCancelable(false)
                    .setMessage(getString(R.string.exitApp))
                    .setNegativeButton(android.R.string.no) { arg0, arg1 -> arg0.dismiss() }
                    .setPositiveButton(android.R.string.yes) { arg0, arg1 ->
                        finish()
                        System.exit(0)
                    }.create().show()

        } catch (e: java.lang.Exception) {
        }

    }


    fun checkLocaion() {
        myLocation!!.getLocation(this, this)

    }


    private fun getCountryCode(lat: Double, lng: Double) {

        try {
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val countryName = addresses[0].countryCode

            countryCode = countryName
        } catch (e: Exception) {
            countryCode = ""
        }

    }


    private fun showCurLocBtn() {

        try {
            locationButton = (mMapView.findViewById<View>(Integer.parseInt("1"))!!.parent as View).findViewById(Integer.parseInt("2"))
            val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            rlp.setMargins(0, 1000, 20, 0)
            locationButton.visibility = View.VISIBLE

            compassButton = (mMapView.findViewById<View>(Integer.parseInt("1"))!!.parent as View).findViewById(Integer.parseInt("5"))
            compassButton.visibility = View.GONE
        } catch (e: Exception) {

        }


    }


    private fun hideViews() {
        runOnUiThread {

            cl_main.visibility = View.GONE
            iv_schedule.visibility = View.GONE
            iv_menu.visibility = View.GONE
            iv_loc_pin.visibility = View.GONE



            if (rideStatus < 2) {
                btn__chat.enabled = true
                btn__chat.setTextColor(resources.getColor(R.color.colorBlue))
                btn_cancel.visibility = View.VISIBLE
            } else {
                btn__chat.enabled = false
                btn__chat.setTextColor(resources.getColor(R.color.colorDisableText))
                btn_cancel.visibility = View.GONE
            }


        }

    }


    fun handleDisable() {

        runOnUiThread {
            try {


                //clear shared prefrences
                val sharedPreference = PreferenceHelper.defaultPrefs(applicationContext)
                val deviceToken = sharedPreference.getString(PreferenceHelper.Key.FCMTOKEN, "")
                val appLang = sharedPreference.getString(PreferenceHelper.Key.APPLANGUAGE, "")

                var editor = sharedPreference.edit()
                editor.clear()
                editor.remove(PreferenceHelper.Key.REGISTEREDUSER)
                editor.commit()

                val prefs = PreferenceHelper.defaultPrefs(this)
                prefs[PreferenceHelper.Key.FCMTOKEN] = deviceToken
                prefs[PreferenceHelper.Key.APPLANGUAGE] = appLang

                var notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()

                val intent = Intent(this, PhoneLoginActivity::class.java)
                finishAffinity()
                finish()
                if (!isAppIsInBackground(this)) {

                    //Toast.makeText(this,getString(R.string.str_wrong),Toast.LENGTH_SHORT).show()


                    startActivity(intent)


                }


                /*  }.create().show()*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


    private fun ratingPopUp(fare: Double, paymentMode: String, bookingId: String, driverId: String) {


        fareDialogDial = Dialog(this)
        fareDialogDial!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        fareDialogDial!!.setContentView(R.layout.dialog_payment)
        fareDialogDial!!.setCanceledOnTouchOutside(false)
        val tvFare = fareDialogDial!!.findViewById<TextView>(R.id.tv_fare)
        val tvPay = fareDialogDial!!.findViewById<TextView>(R.id.tv_pay)




        tvFare.text = "$ " + String.format("%1$.2f", fare)
        if (paymentMode.equals("card", true)) {
            tvPay.visibility = View.VISIBLE
        }

        tvPay.setOnClickListener {


            jsonObject = JSONObject()
            jsonObject.put("status", 4)
            jsonObject.put("bookingId", bookingId)
            jsonObject.put("driverId", driverId)
            sendObjectToSocket(jsonObject, USERCHANGESTATUS)


        }

        fareDialogDial!!.show()
    }


    private fun closeFareSummaryDialog() {
        runOnUiThread {
            try {
                fareDialogDial!!.cancel()
            } catch (e: java.lang.Exception) {

            }
            driver_bottom_sheet.visibility = View.GONE
            if (supportFragmentManager != null && sendBookResponse != null) {

                //  if (!isbottomsheetFragment) {
                isbottomsheetFragment = true

                val bottomSheetFragment = DriverRatingBottomsheetFragment()
                bottomSheetFragment.isCancelable = false
                bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)


                // }
            }


        }

    }


}


