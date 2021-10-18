package cabuser.com.rydz.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cabuser.com.rydz.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_finding_ride_bottom_sheet_fragment.*


class FindingRideBottomSheetFragment : BottomSheetDialogFragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_finding_ride_bottom_sheet_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        inits()
    }

    //intialization of views & clicks
    private fun inits() = btn_cancelReq.setOnClickListener {


        (activity as MainActivity).booleanDriverRequest = false

        (activity as MainActivity).toCancelRideBeforeConfirmation()
        dismiss()


    }

    override fun onStop() {
        super.onStop()
        Log.e("46", "46")
    }
}