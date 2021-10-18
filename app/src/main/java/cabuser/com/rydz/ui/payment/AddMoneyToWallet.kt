package cabuser.com.rydz.ui.payment

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.home.payment.addcard.Card
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_money_to_wallet.*
import kotlinx.android.synthetic.main.header_with_back.*


class AddMoneyToWallet : BaseActivity(), View.OnClickListener {


    private var cardList: MutableList<Card> = mutableListOf<Card>()

    lateinit var paymentViewModel: PaymentViewModel

    var addAmount: Double = 0.0
    var selectedCard = -1
    var cardItem: Card? = null
    lateinit var sharedPreference: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_money_to_wallet)
        inits()
    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when (v?.id) {

            R.id.iv_back -> finish()
            R.id.tv_addpayment -> {
                mProgress?.show()
                paymentViewModel.setUpIntentApi(RydzApplication.user_obj!!.id)
            }
        }
    }

    private fun inits() {
        sharedPreference = PreferenceHelper.defaultPrefs(this@AddMoneyToWallet)
        paymentViewModel = ViewModelProviders.of(this).get(PaymentViewModel::class.java)
        tv_title.visibility=View.VISIBLE
        tv_title.text=getString(R.string.add_money_to_wallet)
        mProgress = CustomeProgressDialog(this)
        addAmount = intent.getDoubleExtra("amount", 0.0)
        tv_addwalletAmount.text = "$$addAmount "
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
        paymentViewModel.addMoneyToWalletResponse?.observe(this, Observer { response ->

            if (response != null) {
                if (response.Status) {
                    Toast.makeText(this,response.messages, Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra("walletAmount",response.walletAmount)
                    setResult(Activity.RESULT_OK, intent)
                    finish() //finishing activity


                } else {
                    onFailure("Something went wrong!")
                }


            }
        })



        paymentViewModel.cardsListResponse?.observe(this, Observer { response ->

            if (response != null && response.status) {
                if (response.Card.size == 0) {
                    //ll_placeholder.visibility=View.VISIBLE
                    rv_cards.visibility = View.GONE
                } else {
                    // ll_placeholder.visibility=View.GONE
                    rv_cards.visibility = View.VISIBLE
                    this.cardList = response.Card as MutableList<Card>
                    rv_cards.adapter = WalletCardAdapter(this, this.cardList)

                    var isSelected = false
                    if(sharedPreference.contains(PreferenceHelper.Key.PAYMENTMODE) && sharedPreference.getString(PreferenceHelper.Key.PAYMENTMODE,"").equals(PreferenceHelper.Key.CARD)  )
                    {
                        val gsonString =sharedPreference.getString(PreferenceHelper.Key.SAVEDCARD, "")
                        var     gson = Gson()
                        var savedCard =gson.fromJson(gsonString.toString(), Card::class.java) as Card
                        for (i in 0..this.cardList.size - 1) {
                            if (this.cardList.get(i)._id .equals(savedCard._id)) {

                                isSelected=true
                                this.cardList.get(i).isSelected = true
                            } else {
                                this.cardList.get(i).isSelected = false
                            }

                        }

                        if(!isSelected)
                        {

                        }

                    }
                    else
                    {

                    }

                }



            }


        })


    }

    override fun onResume() {
        super.onResume()
        mProgress?.show()
        paymentViewModel.getAllCards(RydzApplication.user_obj!!.id)
    }




    public fun changeSelectedCard(pos : Int)
    {


        for (i in 0..this.cardList.size - 1) {
            if (i == pos) {
                selectedCard = i
                cardItem = cardList.get(i)
                this.cardList.get(i).isSelected = true
            } else {
                this.cardList.get(i).isSelected = false
            }

        }

        rv_cards.adapter?.notifyDataSetChanged()

    }


    public fun addMoneyToWallet(pos : Int)
    {
        mProgress?.show()
        paymentViewModel.onChargeWallet(this.cardList.get(pos)._id,addAmount,RydzApplication.user_obj!!.id)
    }



}
