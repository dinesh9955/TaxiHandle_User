package cabuser.com.rydz.ui.freerides

import android.os.Bundle
import android.view.View
import android.widget.Toast
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.util.base.BaseActivity
import kotlinx.android.synthetic.main.activity_freerides.*
import kotlinx.android.synthetic.main.edit_header.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * This class is used to redeem reward points
 */
class FreeRidesActivity : BaseActivity(), Callback<RewardsResponse> {

    private var rewardPoints: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_freerides)
        inits()
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
            R.id.btn_rewards_redeem -> {
                if (rewardPoints > 0) {
                    showProgress("")
                    RydzApplication.getRetroApiClient().redeemRewards(RydzApplication.adminId, RydzApplication.user_obj!!.id, rewardPoints.toString()).enqueue(this)
                } else {
                    Toast.makeText(this@FreeRidesActivity, getString(R.string.no_points), Toast.LENGTH_SHORT).show()
                }

            }
        }
    }


    private fun inits() {
        tv_title.visibility = View.VISIBLE
        tv_title.setText(R.string.rewards)
        showProgress("")
        RydzApplication.getRetroApiClient().getRewards(RydzApplication.user_obj!!.id).enqueue(this)

    }


    override fun onFailure(call: Call<RewardsResponse>, t: Throwable) {
        hideProgress()

    }

    override fun onResponse(call: Call<RewardsResponse>, response: Response<RewardsResponse>) {
        hideProgress()
        if (response.body()!!.success!!) {

            tv_rewards.text = response.body()!!.userRewards!!.toString()
            rewardPoints = response.body()!!.userRewards!!
            tv_wallet.text = "$ " + String.format("%1$.2f", response.body()!!.wallet).replace(",", ".").toDouble()
        }
    }


}
