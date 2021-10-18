package cabuser.com.rydz.ui.history

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.R
import cabuser.com.rydz.data.eventbus.RatingEvent
import cabuser.com.rydz.generated.callback.OnClickListener
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.common.OnItemClickListener
import kotlinx.android.synthetic.main.activity_refund.*
import kotlinx.android.synthetic.main.activity_trip_details.*
import kotlinx.android.synthetic.main.header_with_back.*
import org.greenrobot.eventbus.EventBus

class RefundActvity : BaseActivity() , OnItemClickListener , View.OnClickListener{

    var viewmodel: TripRatingViewModel? = null
    var  reasonsList: MutableList<RefundReason> = mutableListOf()
    var reason=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refund)
        inits()
    }

    override fun onItemClick(pos: Int) {

        for(i in 0..reasonsList.size-1)
        {
            if(i==pos)
                reasonsList.get(i).isSelected=true
            else
                reasonsList.get(i).isSelected=false
        }
        rvReasons.adapter?.notifyDataSetChanged()


        reason =  reasonsList.get(pos).reason


    }


    override fun onClick(v: View?) {
when(v?.id)
{
    R.id.btn_submit->
    {
        if(reason.isEmpty())
            onFailure("Please select an option")
        else if(edt_enterIssueDiscription.text.isNullOrEmpty())
            onFailure("Please enter description")
        else
            viewmodel?.onRefundFare(intent.getStringExtra("bookingId")!!,reason,edt_enterIssueDiscription.text.toString())

    }
    R.id.iv_back -> finish()
}
    }



    private fun inits()
    {
        tv_title.visibility = View.VISIBLE
        tv_title.setText(R.string.refund)

        reasonsList.add(RefundReason("Rider didn't take the trip.",false))
        reasonsList.add(RefundReason("The trip didn't start or end on time.",false))
        reasonsList.add(RefundReason("Technical issues impacted the trip.",false))
        reasonsList.add(RefundReason("Cash was requested or paid to the driver.",false))
        reasonsList.add(RefundReason("Other",false))
        rvReasons.adapter = RefundAdapter(reasonsList,this,this)
        mProgress = CustomeProgressDialog(this@RefundActvity)


        //Get parcelable data
        viewmodel = ViewModelProviders.of(this@RefundActvity).get(TripRatingViewModel::class.java)

        viewmodel?.refundResponse?.observe(this, Observer { user ->

            mProgress!!.dismiss()
            if (user.status!!) {
                     Toast.makeText(this,user.messages,Toast.LENGTH_SHORT).show()
                var mIntent = Intent()
                setResult(Activity.RESULT_OK, mIntent)

                finish()
            }
            else
            {
                Toast.makeText(this,user.messages,Toast.LENGTH_SHORT).show()
            }


        })


    }






}
