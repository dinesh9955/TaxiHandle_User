package cabuser.com.rydz.ui.profile

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import cabuser.com.rydz.R
import cabuser.com.rydz.databinding.ActivityEditPasswordBinding
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import kotlinx.android.synthetic.main.activity_edit_password.*

class EditPasswordActivity : BaseActivity() {


    var binding: ActivityEditPasswordBinding? = null
    var viewmodel: EditPasswordViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inits()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> finish()

        }
    }


    private fun inits() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_password)
        viewmodel = ViewModelProviders.of(this).get(EditPasswordViewModel::class.java)
        binding?.viewmodel = viewmodel
        tv_title.setText(R.string.changePassword)
        mProgress = CustomeProgressDialog(this)
        viewmodel?.mProgress?.observe(this, Observer {
            if (it!!) {
                mProgress!!.show();
            } else
                mProgress!!.dismiss()
        })



        viewmodel?.userLogin?.observe(this, Observer { user ->

            if (user.success!!) {
                showMessage(user.message)
                finish()
            } else {
                showMessage(user.message)
            }
        })


    }


}
