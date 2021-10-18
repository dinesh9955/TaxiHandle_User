package cabuser.com.rydz.ui.payment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.ConstVariables.Companion.STRIPEPUBLISHKEY
import cabuser.com.rydz.util.common.CustomeProgressDialog
import com.stripe.android.ApiResultCallback
import com.stripe.android.SetupIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmSetupIntentParams
import com.stripe.android.model.StripeIntent
import com.stripe.android.view.CardInputWidget
import kotlinx.android.synthetic.main.header_with_back.*

class AddCardActivity : BaseActivity()  {



    lateinit var cardInputWidget:CardInputWidget
    lateinit var paymentViewModel: PaymentViewModel
    var setupIntentClientSecret = ""
    private lateinit var stripe: Stripe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)
        paymentViewModel= ViewModelProviders.of(this).get(PaymentViewModel::class.java)
        inits()
    }


    private fun inits()
    {
        // Use the publishable key from the server to initialize the Stripe instance.
        stripe = Stripe(applicationContext,STRIPEPUBLISHKEY)
        tv_title.text = "Add Card"
        tv_title.visibility=View.VISIBLE
        setupIntentClientSecret = intent.getStringExtra("setupIntentId").toString()
        mProgress = CustomeProgressDialog(this)

        cardInputWidget =
                findViewById<CardInputWidget>(R.id.cardInputWidget)
        cardInputWidget.postalCodeEnabled=false



        paymentViewModel?.mProgress?.observe(this, Observer {

                if (it!!) {
                    mProgress!!.show()
                } else
                    mProgress!!.dismiss()

        })

        paymentViewModel?.addCardResponse?.observe(this, Observer { response ->

        if(response.status) {
            this.finish()
        }
            else {
            onFailure("Something went wrong!")
        }

    })}

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.tv_save ->
            {
                // Collect card details
                paymentViewModel?.mProgress?.value=true
                cardInputWidget.paymentMethodCreateParams?.let { paymentMethodParams ->
                    // Create SetupIntent confirm parameters with the above
                    val confirmParams = ConfirmSetupIntentParams
                            .create(paymentMethodParams, setupIntentClientSecret)
                    stripe.confirmSetupIntent(this, confirmParams)
                }
            }
            R.id.iv_back -> finish()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        stripe.onSetupResult(requestCode, data, object : ApiResultCallback<SetupIntentResult> {
            override fun onSuccess(result: SetupIntentResult) {
                val setupIntent = result.intent
                val status = setupIntent.status
                if (status == StripeIntent.Status.Succeeded) {

                    Log.e("81", setupIntent.paymentMethodId.toString())

                  paymentViewModel.addNewCard(RydzApplication.user_obj!!.id,setupIntent.paymentMethodId!!)
                } else if (status == StripeIntent.Status.RequiresPaymentMethod) {




                }
            }

            override fun onError(e: Exception) {
                paymentViewModel?.mProgress?.value=false
                showAlert(e.message.toString())
                //Log.e("96", e.message)

            }
        })
    }

}
