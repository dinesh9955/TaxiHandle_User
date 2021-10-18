package cabuser.com.rydz.ui.payment

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.home.payment.addcard.Card
import cabuser.com.rydz.ui.home.payment.addcard.PromoCode
import cabuser.com.rydz.ui.settings.SupportResponse
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.Key.CARD
import cabuser.com.rydz.util.prefrences.PreferenceHelper.Key.CASH
import cabuser.com.rydz.util.prefrences.PreferenceHelper.get
import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.activity_payment.tv_add
import kotlinx.android.synthetic.main.header_with_back.*
import kotlinx.android.synthetic.main.header_with_back.iv_back
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PaymentActivity : BaseActivity(), Callback<SupportResponse> {

    var promoDialog: Dialog? = null
    lateinit var sharedPreference: SharedPreferences
    lateinit var paymentViewModel: PaymentViewModel
    private var cardList: MutableList<Card> = mutableListOf<Card>()
    var selectedPos = -1
    var walletAmount = 0.0
    var showOptions = true
    var promo: PromoCode? = null
    var promoApplied =false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        paymentViewModel = ViewModelProviders.of(this).get(PaymentViewModel::class.java)
        inits()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {


                finish()

            }
            R.id.iv_clear -> {

                promoApplied=true
                promo = null
                edt_promocode.setText("")
                tv_add.setText("Apply")
                tv_add.isEnabled=true
                edt_promocode.isEnabled=true

            }

            R.id.iv_done ->
            {
                onResultSet()
            }
            R.id.tv_addpayment -> {
                mProgress?.show()
                paymentViewModel.setUpIntentApi(RydzApplication.user_obj!!.id)
            }
            R.id.tv_addmoney -> {
                startActivity(Intent(this, WalletAcivity::class.java).apply {
                    putExtra("walletAmount", walletAmount)
                })

            }
            R.id.tv_add ->
            {
                promoApplied=true
                if (edt_promocode.text.toString().isEmpty()) {

                    Toast.makeText(this@PaymentActivity,  getString(R.string.enter_promo), Toast.LENGTH_SHORT).show()

                } else {
                    mProgress!!.show()

                    RydzApplication.getRetroApiClient().onAddPromoCode(edt_promocode.text.toString(), RydzApplication.adminId, RydzApplication.user_obj!!.id).enqueue(this)
                }
            }
        }
    }


    override fun onFailure(call: Call<SupportResponse>, t: Throwable) {
        mProgress!!.dismiss()

    }

    override fun onResponse(call: Call<SupportResponse>, response: Response<SupportResponse>) {
        mProgress!!.dismiss()

        if (response.body() != null && response.body()!!.success!!) {
            tv_add.setText("Applied")
            tv_add.isEnabled=false
            edt_promocode.isEnabled=false
            iv_clear.visibility=View.VISIBLE
            promo = response.body()!!.coupon
            Toast.makeText(this@PaymentActivity, getString(R.string.promo_applied), Toast.LENGTH_SHORT).show()

        } else {
            try {
                Toast.makeText(this@PaymentActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()


            } catch (e: Exception) {

            }
        }

    }


    //intialization of views
    private fun inits() {
        tv_title.visibility = View.VISIBLE
        tv_title.setText(R.string.payments)
        sharedPreference = PreferenceHelper.defaultPrefs(this@PaymentActivity)

          promo = intent.getParcelableExtra<PromoCode>("promo")



       intent.let {
          if( it.getStringExtra("from").equals("confirm"))
           {
               iv_done.visibility=View.VISIBLE
               cl_promotion.visibility=View.VISIBLE
               tv_promotion.visibility=View.VISIBLE
               iv_back.visibility=View.GONE
               showOptions=false

               if(promo!=null)
               {
                   edt_promocode.setText(promo?.code)
                   tv_add.setText("Applied")
                   tv_add.isEnabled=false
                   edt_promocode.isEnabled=false
                   iv_clear.visibility=View.VISIBLE
               }


           }
       }

        edt_promocode.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


                if(edt_promocode.text.toString().length==0)
                {
                    iv_clear.visibility=View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {


            }
        })

        if (sharedPreference.contains(PreferenceHelper.Key.WALLETSELECTED)) {
            cb_wallet.isChecked = sharedPreference.getBoolean(PreferenceHelper.Key.WALLETSELECTED, false)
        }
        if (intent.getStringExtra("key").equals("payment")) {
            tv_promotion.visibility = View.GONE
            cl_promotion.visibility = View.GONE
        }

        if (sharedPreference.contains(PreferenceHelper.Key.PAYMENTMODE) && sharedPreference.getString(PreferenceHelper.Key.PAYMENTMODE, "").equals(CASH)) {
            rb_cash.isChecked = true
        }

        mProgress = CustomeProgressDialog(this)




        cb_wallet.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {


                    var editor = sharedPreference.edit()
                    if (isChecked) {
                        editor.putBoolean(PreferenceHelper.Key.WALLETSELECTED, true)
                    } else {
                        editor.remove(PreferenceHelper.Key.WALLETSELECTED)
                    }
                    editor.commit()



            }

        })

        rb_cash.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {

                if (isChecked) {



                    savePaymentModeOption(CASH, null)
                    if (cardList.size > 0)
                        changeSelectedCard(-1)
                }
            }

        })



        paymentViewModel.mProgress?.observe(this, Observer {
            try {
                if (it!!) {
                    mProgress!!.show()
                } else
                    mProgress!!.dismiss()
            } catch (e: Exception) {

            }
        })


        paymentViewModel.setUpIntentResponse?.observe(this, Observer { response ->

            if (response != null && response.data != null) {
                startActivity(Intent(this, AddCardActivity::class.java)
                        .apply {
                            putExtra("setupIntentId", response.data.client_secret)
                        })
            }


        })
        paymentViewModel.deleteCardResponse?.observe(this, Observer { response ->

            if (response.status)
                paymentViewModel.getAllCards(RydzApplication.user_obj!!.id)


        })

        paymentViewModel.cardsListResponse?.observe(this, Observer { response ->

            if (response != null && response.status) {
                if (response.Card.size == 0) {
                    rv_cards.visibility = View.GONE
                    rb_cash.isChecked = true
                } else {
                    rv_cards.visibility = View.VISIBLE
                    this.cardList = response.Card as MutableList<Card>

                    rv_cards.adapter = CardAdapter(this, this.cardList, showOptions)

                    var isSelected = false
                    if (sharedPreference.contains(PreferenceHelper.Key.PAYMENTMODE) && sharedPreference.getString(PreferenceHelper.Key.PAYMENTMODE, "").equals(CARD)) {
                        val gsonString = sharedPreference[PreferenceHelper.Key.SAVEDCARD, ""]
                        var gson = Gson()
                        var savedCard = gson.fromJson(gsonString.toString(), Card::class.java) as Card
                        for (i in 0..this.cardList.size - 1) {
                            if (this.cardList.get(i)._id.equals(savedCard._id)) {

                                isSelected = true
                                this.cardList.get(i).isSelected = true
                            } else {
                                this.cardList.get(i).isSelected = false
                            }

                        }

                        if (!isSelected) {
                            if(  this.cardList.size>0)
                            this.cardList.get(0).isSelected = true
                           // rb_cash.isChecked = true
                        }

                    } /*else {
                        rb_cash.isChecked = true
                    }*/

                }
                walletAmount = response.walletAmount

                tv_walletValue.text = "$ " + String.format("%1$.2f", response.walletAmount).replace(",", ".").toDouble()



                if(response.promo!=null && response.promo.size>0)
                {
                    rv_promocodes.adapter = PromoCodeAdapter(this, response.promo)
                }
            }


        })
    }



    override fun onResume() {
        super.onResume()
        paymentViewModel.getAllCards(RydzApplication.user_obj!!.id)
    }


    fun makeCardDefault(pos: Int) {
        selectedPos = pos
        paymentViewModel.onMakeDefaultCard(this.cardList.get(pos)._id, RydzApplication.user_obj!!.id)
    }


    fun promptBeforeDelete(pos: Int) {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage("Are you sure you want to delete this card?")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action

                .setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, id ->
                    selectedPos = pos
                    paymentViewModel.onDeleteCard(this.cardList.get(pos)._id, RydzApplication.user_obj!!.id)
                    dialog.cancel()
                })
                // negative button text and action
                .setNegativeButton(R.string.no, DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("Alert")
        // show alert dialog
        alert.show()


        val posButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
        with(posButton) {
            setTextColor(resources
                    .getColor(R.color.colorAccent))
        }
        val negButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
        with(negButton) {
            setTextColor(resources
                    .getColor(R.color._1a1824))
        }
    }

    fun changeSelectedCard(pos: Int) {

        for (i in 0..this.cardList.size - 1) {
            this.cardList.get(i).isSelected = i == pos

        }

        rv_cards.adapter?.notifyDataSetChanged()

        if (pos != -1) //when cash is selected the unselect all cards
        {
            rb_cash.isChecked = false
            savePaymentModeOption(CARD, this.cardList.get(pos))
        }

    }


    private fun savePaymentModeOption(paymentOption: String, card: Card?) {
        var editor = sharedPreference.edit()

        editor.putString(PreferenceHelper.Key.PAYMENTMODE, paymentOption)
        if (card != null) {
            RydzApplication.prefs[PreferenceHelper.Key.SAVEDCARD] = card

        } else {


            editor.remove(PreferenceHelper.Key.SAVEDCARD)

        }

        editor.commit()
    }


    override fun onBackPressed() {
        onResultSet()

        super.onBackPressed()
    }


    private fun onResultSet()
    {
        intent.let {
            if( it.getStringExtra("from").equals("confirm"))
            {
                val returnIntent = Intent()
                returnIntent.putExtra("walletAmount", walletAmount)
                if(promoApplied)
                returnIntent.putExtra("promo", promo)
                else
                {
                    returnIntent.putExtra("promo", intent.getParcelableExtra<PromoCode>("promo"))
                }

                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

    }
}
