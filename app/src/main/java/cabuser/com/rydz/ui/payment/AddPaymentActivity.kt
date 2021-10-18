package cabuser.com.rydz.ui.payment

import android.os.Bundle
import android.view.View
import cabuser.com.rydz.R
import cabuser.com.rydz.util.base.BaseActivity
import kotlinx.android.synthetic.main.edit_header.*

class AddPaymentActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_add_payment)
       // inits()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
        }
    }


    private fun inits() {

        tv_title.visibility = View.VISIBLE
        tv_title.setText(R.string.add_payment)

    }
}
