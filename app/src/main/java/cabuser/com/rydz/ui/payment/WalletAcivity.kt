package cabuser.com.rydz.ui.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import cabuser.com.rydz.R
import cabuser.com.rydz.util.base.BaseActivity

import kotlinx.android.synthetic.main.activity_wallet.*
import kotlinx.android.synthetic.main.header_with_back.*

class WalletAcivity : BaseActivity(), View.OnClickListener {

    var isRefresh = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        tv_title.visibility=View.VISIBLE
          tv_title.text=getString(R.string.add_money_to_wallet)
        tv_walletAmount.text = "Available Balance $${intent.getDoubleExtra("walletAmount",0.0)}"
    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when (v?.id) {
            R.id.iv_back -> { finish() }
            R.id.tv_proceed -> {
                if(edt_amount.text.toString().trim().isEmpty() || edt_amount.text.toString().trim().toInt()<1)
                {
                    showAlert("Minimum amount should be $1")

                }
                else {
                    isRefresh = true
                    startActivityForResult(Intent(this, AddMoneyToWallet::class.java).apply {
                        putExtra("amount", edt_amount.text.toString().trim().toDouble())
                    },1)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {

                if(isRefresh)
                {
                    edt_amount.setText("")
                    isRefresh=false
                }

        } catch (e: Exception) {

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK)
        {
            Log.e("68",data?.getDoubleExtra("walletAmount",0.0).toString())
            tv_walletAmount.text = "Available Balance $${data?.getDoubleExtra("walletAmount",0.0)}"

        }
    }

}
