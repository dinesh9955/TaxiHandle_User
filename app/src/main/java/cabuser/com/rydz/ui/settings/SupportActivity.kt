package cabuser.com.rydz.ui.settings


import androidx.lifecycle.MutableLiveData
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.util.base.BaseActivity
import kotlinx.android.synthetic.main.activity_support.*
import kotlinx.android.synthetic.main.header_with_back.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * SupportActivity class to used to report any issue
 */
class SupportActivity : BaseActivity(), Callback<SupportResponse> {

    var type: Int = 1
    private var userId: String =RydzApplication.user_obj!!.id
    private var subject:String=""
    private var bookingId:String=""

    private lateinit var comment: String

    private var feedBack: MutableLiveData<SupportResponse>? = null


    override fun onFailure(call: Call<SupportResponse>, t: Throwable) {

        hideProgress()
    }

    override fun onResponse(call: Call<SupportResponse>, response: Response<SupportResponse>) {
        hideProgress()
        if (response.isSuccessful) {
            feedBack?.value = response.body()
            Toast.makeText(this, response.body()!!.message, Toast.LENGTH_LONG).show()
            edt_enterIssueTitle.setText("")
            edt_enterIssueDiscription.setText("")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        inits()

    }

    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.iv_back -> {
                finish()
            }

            R.id.btn_submitFeedback -> {
                if (validate()) {
                    showProgress("")
                    subject = edt_enterIssueTitle.text.toString()
                    comment = edt_enterIssueDiscription.text.toString()
                    val submitFeedbackRequest= SubmitFeedbackRequest(""+type, userId, subject, comment,bookingId)
                    RydzApplication.getRetroApiClient().onSendFeedback(submitFeedbackRequest).enqueue(this)
                }
            }
        }


    }


    private fun inits() {
        tv_title.visibility = View.VISIBLE
        tv_title.setText(R.string.support)

        if(intent.getStringExtra("bookingId")!=null)
        bookingId= intent.getStringExtra("bookingId")!!

    }

    //validations on views
    private fun validate(): Boolean {

        if (edt_enterIssueTitle!!.text.toString().trim().isEmpty()) {

            Toast.makeText(this, "Enter Title", Toast.LENGTH_LONG).show()
            return false

        } else if (edt_enterIssueDiscription!!.text.toString().trim().isEmpty()) {

            Toast.makeText(this, "Enter Description", Toast.LENGTH_LONG).show()
            return false

        }

        return true

    }
}