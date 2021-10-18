package cabuser.com.rydz.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.header_with_back.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsActivity : BaseActivity(), Callback<ContactUsResponse> {

    var contactUsResponse: ContactUsResponse = ContactUsResponse()


    override fun onFailure(call: Call<ContactUsResponse>, t: Throwable) {
        mProgress!!.dismiss()
    }

    override fun onResponse(call: Call<ContactUsResponse>, response: Response<ContactUsResponse>) {

        mProgress!!.dismiss()
        if (response != null) {
            if (response.body()!!.success!! && response.body()!!.contact != null) {
                contactUsResponse = response.body()!!

                if (response.body()!!.contact.phone.length > 0)
                {
                    if(!contactUsResponse.contact.phone.contains("+"))
                        tv_contact.text = "+"+response.body()!!.contact.phone
                    else
                        tv_contact.text = response.body()!!.contact.phone
                }


                if (response.body()!!.contact.email.length > 0)
                    tv_email.text = response.body()!!.contact.email


            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        inits()
    }


    private fun inits() {
        tv_title.visibility = View.VISIBLE
        tv_title.setText(R.string.contact_us)
        mProgress = CustomeProgressDialog(this)
        mProgress!!.show()
        RydzApplication.getRetroApiClient().getContactUs().enqueue(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()

            R.id.tv_contact -> {
                if (contactUsResponse != null && contactUsResponse.contact != null && contactUsResponse.contact.phone.trim().length > 0) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    if(!contactUsResponse.contact.phone.contains("+"))
                        intent.data = Uri.parse("tel:" + (  "+"+contactUsResponse.contact.phone))
                    else
                    intent.data = Uri.parse("tel:" + (  contactUsResponse.contact.phone))
                    startActivity(intent)
                } else {
                    Toast.makeText(this@ContactUsActivity, getString(R.string.str_wrong), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.tv_email -> {

                if (contactUsResponse != null && contactUsResponse.contact != null && contactUsResponse.contact.email.trim().length > 0) {
                    val emailIntent = Intent(Intent.ACTION_SENDTO)
                    emailIntent.data = Uri.parse("mailto: " + contactUsResponse.contact.email.trim())
                    startActivity(Intent.createChooser(emailIntent, ""))
                } else {
                    Toast.makeText(this@ContactUsActivity, getString(R.string.str_wrong), Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

}
