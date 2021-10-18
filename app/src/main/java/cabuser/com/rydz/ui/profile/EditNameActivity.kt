package cabuser.com.rydz.ui.profile

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.view.View
import cabuser.com.rydz.R
import cabuser.com.rydz.databinding.ActivityEditNameBinding
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import kotlinx.android.synthetic.main.edit_header.*

class EditNameActivity : BaseActivity() {


    var binding: ActivityEditNameBinding? = null
    var viewmodel: EditNameModel? = null

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


        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_name)
        viewmodel = ViewModelProviders.of(this).get(EditNameModel::class.java)
        binding?.viewmodel = viewmodel


        tv_title.setText(R.string.editName)
        mProgress = CustomeProgressDialog(this)

        viewmodel?.firstName!!.set(intent.getStringExtra("firstName").toString())
        viewmodel?.lastName!!.set(intent.getStringExtra("lastName").toString())


        viewmodel?.mProgress?.observe(this, Observer {
            if (it!!) {
                mProgress!!.show()
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
