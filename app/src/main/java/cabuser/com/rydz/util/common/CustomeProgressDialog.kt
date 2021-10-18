package cabuser.com.rydz.util.common

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import cabuser.com.rydz.R


class CustomeProgressDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progress_dialog)
        setCancelable(false)
        setCanceledOnTouchOutside(false)


    }

}