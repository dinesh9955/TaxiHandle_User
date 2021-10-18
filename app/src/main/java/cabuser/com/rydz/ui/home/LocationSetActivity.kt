package cabuser.com.rydz.ui.home

import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.PlacesEventObject
import cabuser.com.rydz.databinding.ActivityLocationSetBinding
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.OnLocationItemClickListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.android.synthetic.main.activity_location_set.*
import org.greenrobot.eventbus.EventBus
import rx.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit


/**
 *This class is used to slect source location and destination location
 */
class LocationSetActivity : BaseActivity(), OnLocationItemClickListener {

    lateinit var location: Location
    private lateinit var apiKey: String
    var binding: ActivityLocationSetBinding? = null
    var viewmodel: LocationViewModel? = null
    private lateinit var placesClient: PlacesClient
    lateinit var request: FindAutocompletePredictionsRequest
    private lateinit var token: AutocompleteSessionToken
    lateinit var src_behaviorSubject: BehaviorSubject<String>
    lateinit var des_behaviorSubject: BehaviorSubject<String>
    var isSource: Boolean = false
    var isDestination: Boolean = false
    private lateinit var sourcePlace: SendBookResponse.Booking.Source
    private lateinit var destinationPlace: SendBookResponse.Booking.Destination
    lateinit var autocompletePrediction: MutableList<AutocompletePrediction>
    lateinit var progress: ProgressBar
    var countryCode: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_set)
        inits()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {

                clearScheduleBooking()



            }
            R.id.tv_pinloc -> {
                try {
                    if (sourcePlace == null && destinationPlace != null)
                        sourcePlace = SendBookResponse.Booking.Source()
                } catch (e: UninitializedPropertyAccessException) {
                    sourcePlace = SendBookResponse.Booking.Source()
                }
                try {
                    if (sourcePlace != null && destinationPlace == null)
                        destinationPlace = SendBookResponse.Booking.Destination()
                } catch (e: UninitializedPropertyAccessException) {
                    destinationPlace = SendBookResponse.Booking.Destination()
                }

                val placesEventObject: PlacesEventObject?
                placesEventObject = if (intent.hasExtra("FROM")) {
                    if (intent.getStringExtra("FROM") == "SOURCE")
                        PlacesEventObject(sourcePlace, destinationPlace, true, true)
                    else
                        PlacesEventObject(sourcePlace, destinationPlace, true, false)
                } else {
                    PlacesEventObject(sourcePlace, destinationPlace, true, edt_source.isFocused)
                }
                EventBus.getDefault().postSticky(placesEventObject)
                finish()
            }

            R.id.tv_done -> {
                val placesEventObject: PlacesEventObject
                placesEventObject = if (intent.hasExtra("FROM")) {
                    if (intent.getStringExtra("FROM") == "SOURCE")
                        PlacesEventObject(sourcePlace, destinationPlace, false, true)
                    else
                        PlacesEventObject(sourcePlace, destinationPlace, false, false)
                } else {
                    PlacesEventObject(sourcePlace, destinationPlace, false, edt_source.isFocused)
                }

                EventBus.getDefault().postSticky(placesEventObject)
                finish()
            }

        }
    }

    override fun onItemClick(name: String, srcplace_obj: SendBookResponse.Booking.Source, place_obj: SendBookResponse.Booking.Destination) {

        if (this.isSource) {
            sourcePlace = srcplace_obj
            destinationPlace = place_obj
            edt_source.setText(srcplace_obj.name.toString())

            progress.visibility = View.GONE
            if (rv_location.adapter != null) {
                autocompletePrediction.clear()
                rv_location.adapter!!.notifyDataSetChanged()
                rv_location.visibility = View.GONE

            }
            /*********************************************/
            val placesEventObject: PlacesEventObject?
            placesEventObject = if (intent.hasExtra("FROM")) {
                if (intent.getStringExtra("FROM") == "SOURCE")
                    PlacesEventObject(sourcePlace, destinationPlace, true, true)
                else
                    PlacesEventObject(sourcePlace, destinationPlace, true, false)
            } else {
                PlacesEventObject(sourcePlace, destinationPlace, true, edt_source.isFocused)
            }

            EventBus.getDefault().postSticky(placesEventObject)
            finish()
            /*********************************************/

        } else {
            sourcePlace = srcplace_obj
            destinationPlace = place_obj
            edt_destination.setText(place_obj.name.toString())

            progress.visibility = View.GONE
            if (rv_location.adapter != null) {
                autocompletePrediction.clear()
                rv_location.adapter!!.notifyDataSetChanged()
                rv_location.visibility = View.GONE
            }

            /*********************************************/
            val placesEventObject: PlacesEventObject?
            placesEventObject = if (intent.hasExtra("FROM")) {
                if (intent.getStringExtra("FROM") == "SOURCE")
                    PlacesEventObject(sourcePlace, destinationPlace, true, true)
                else
                    PlacesEventObject(sourcePlace, destinationPlace, isPin = true, isFocusOnSource = false)
            } else {
                PlacesEventObject(sourcePlace, destinationPlace, true, edt_source.isFocused)
            }

            EventBus.getDefault().postSticky(placesEventObject)
            finish()
            /*********************************************/
        }

    }

    //intialization
    private fun inits() {

        progress = findViewById(R.id.progress)
        edt_source.setSelection(edt_source.text.length)
        src_behaviorSubject = BehaviorSubject.create()
        des_behaviorSubject = BehaviorSubject.create()


        if (intent != null && intent.hasExtra("COUNTRYCODE"))
            countryCode = intent.getStringExtra("COUNTRYCODE")!!


        if (MainActivity.time_slot != null) {
            tv_timeSlot.visibility = View.VISIBLE
            tv_timeSlot.text = MainActivity.time_slot
        } else {
            tv_timeSlot.visibility = View.GONE
        }


        src_behaviorSubject.debounce(100, TimeUnit.MILLISECONDS).onBackpressureLatest().subscribe {

            if (intent.hasExtra("FROM")) {
                if (intent.getStringExtra("FROM") == "SOURCE") {
                    isSource = true
                    isDestination = false
                    runOnUiThread {
                        edt_source.hint = "Enter your pickup"
                    }
                } else {
                    isSource = false
                    isDestination = true
                    runOnUiThread {
                        edt_source.hint = "Enter your drop off"
                    }
                }
            }

            request = FindAutocompletePredictionsRequest.builder()
                    .setCountry(countryCode)
                    .setSessionToken(token)
                    .setQuery(edt_source.text.toString())
                    .build()


            // progress.visibility = View.VISIBLE
            placesClient.findAutocompletePredictions(request).addOnSuccessListener {
                progress.visibility = View.GONE

                if (it.autocompletePredictions != null && it.autocompletePredictions.size > 0 && isSource) {
                    autocompletePrediction = it.autocompletePredictions
                    if (intent.hasExtra("FROM")) if (intent.getStringExtra("FROM") == "SOURCE") rv_location.adapter = LocationBottomsheetAdapter(placesClient, this@LocationSetActivity, autocompletePrediction, this@LocationSetActivity, "source") else {
                        rv_location.adapter = LocationBottomsheetAdapter(placesClient, this@LocationSetActivity, autocompletePrediction, this@LocationSetActivity, "destination")
                    }
                } else {

                    try {
                        autocompletePrediction.clear()
                    } catch (e: UninitializedPropertyAccessException) {
                        autocompletePrediction = ArrayList()
                    }
                    if (rv_location.adapter != null)
                        rv_location.adapter!!.notifyDataSetChanged()


                }
            }.addOnCanceledListener {
                Log.e("Pridictions", "Cancelled")
            }.addOnFailureListener {

            }

        }


        des_behaviorSubject.debounce(100, TimeUnit.MILLISECONDS).onBackpressureLatest().subscribe { it ->
            isSource = false
            isDestination = true
            request = FindAutocompletePredictionsRequest.builder()
                    .setCountry(countryCode)
                    .setSessionToken(token)
                    .setQuery(it.toString())
                    .build()

            placesClient.findAutocompletePredictions(request).addOnSuccessListener {
                progress.visibility = View.GONE

                if (it.autocompletePredictions != null && it.autocompletePredictions.size > 0 && isDestination) {
                    autocompletePrediction = it.autocompletePredictions
                    rv_location.adapter = LocationBottomsheetAdapter(placesClient, this@LocationSetActivity, autocompletePrediction, this@LocationSetActivity, "destination")
                } else {
                    if (rv_location.adapter != null) {
                        autocompletePrediction.clear()
                        rv_location.adapter!!.notifyDataSetChanged()
                    }
                }
            }.addOnCanceledListener {
                try {
                    if (autocompletePrediction != null) {
                        autocompletePrediction.clear()

                    }
                } catch (e: UninitializedPropertyAccessException) {
                    autocompletePrediction = ArrayList()
                }
                if (rv_location.adapter != null)
                    rv_location.adapter!!.notifyDataSetChanged()
            }
        }


        //places api
        apiKey = getString(R.string.places_api_key)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        placesClient = Places.createClient(this)
        token = AutocompleteSessionToken.newInstance()

        rv_location.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@LocationSetActivity)

        if (EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java) != null) {
            sourcePlace = EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java).source
            destinationPlace = EventBus.getDefault().getStickyEvent(PlacesEventObject::class.java).destination

        } else {
            isSource = false
            isDestination = true
        }

        iv_cleardesc.setOnClickListener {
            edt_destination.setText("")
            destinationPlace = SendBookResponse.Booking.Destination()
            iv_cleardesc.visibility = View.GONE

        }

        iv_clearsrc.setOnClickListener {
            edt_source.setText("")
            sourcePlace = SendBookResponse.Booking.Source()
            iv_clearsrc.visibility = View.GONE

        }

        edt_source.setOnFocusChangeListener { v, hasFocus ->
            if (v.hasFocus()) {
                if (intent.hasExtra("FROM")) {
                    if (intent.getStringExtra("FROM") == "SOURCE") {
                        isSource = true
                        isDestination = false
                        edt_source.hint = "Enter your pickup"
                    } else {
                        isSource = false
                        isDestination = true
                        edt_source.hint = "Enter your drop off"

                    }
                }


                handleRecyclerView()


            }
        }

        edt_destination.setOnFocusChangeListener { v, hasFocus ->
            if (v.hasFocus()) {
                isSource = false
                isDestination = true
                handleRecyclerView()
            }
        }


        edt_source.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isSource) {
                    src_behaviorSubject.onNext(s.toString())
                } else if (isDestination) {
                    des_behaviorSubject.onNext(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {

                if (isSource && edt_source.text.isEmpty()) {

                    if (rv_location.adapter != null) {
                        autocompletePrediction.clear()
                        rv_location.adapter!!.notifyDataSetChanged()
                    }

                }

            }
        })


        //set destintaion text change
        edt_destination.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isDestination)
                    des_behaviorSubject.onNext(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {

                if (isDestination && edt_destination.text.isEmpty()) {

                    if (rv_location.adapter != null) {
                        autocompletePrediction.clear()
                        rv_location.adapter!!.notifyDataSetChanged()
                    }

                } else {
                    Log.e("afterTextChanged ", "afterTextChanged")

                }
            }
        })

        edt_source.setOnClickListener {

            isSource = true
            isDestination = false
            handleRecyclerView()
            if (edt_source.isFocused && edt_source.text.isNotEmpty()) {
                edt_source.setSelection(edt_source.text.length, 0)
                iv_clearsrc.visibility = View.VISIBLE

            } else {
                edt_source.requestFocus()
                edt_source.clearFocus()
                iv_clearsrc.visibility = View.GONE
            }
        }



        edt_destination.setOnClickListener {

            isSource = false
            isDestination = true
            handleRecyclerView()

            if (edt_destination.isFocused && edt_destination.text.isNotEmpty()) {
                edt_destination.setSelection(edt_destination.text.length, 0)
                iv_cleardesc.visibility = View.VISIBLE
            } else {
                edt_destination.requestFocus()
                edt_destination.clearFocus()
                iv_cleardesc.visibility = View.GONE

            }

        }


    }

    override fun onBackPressed() {

        clearScheduleBooking()
    }

    override fun onResume() {
        super.onResume()

        edt_source.requestFocus()
    }

    private fun handleRecyclerView() {

        try {
            this.autocompletePrediction.clear()
        } catch (e: UninitializedPropertyAccessException) {
            autocompletePrediction = ArrayList()
        }
        if (rv_location.adapter != null)
            rv_location.adapter!!.notifyDataSetChanged()

        rv_location.visibility = View.VISIBLE
    }


    private fun clearScheduleBooking() {
        if (MainActivity.time_slot != null && MainActivity?.destinationPlace != null && MainActivity?.srcPlace!=null) {

        }
        else     if (MainActivity.time_slot != null && MainActivity?.srcPlace!=null) {
            Toast.makeText(this, getString(R.string.schedule_cancelled), Toast.LENGTH_SHORT).show()

            MainActivity.isScheduleRide = false
            MainActivity.time_slot = null
        }
        finish()
    }

}