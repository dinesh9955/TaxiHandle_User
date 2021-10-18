package cabuser.com.rydz.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import cabuser.com.rydz.R
import cabuser.com.rydz.util.base.BaseActivity
import kotlinx.android.synthetic.main.activity_privacy_policy.*
import kotlinx.android.synthetic.main.edit_header.*

class PrivacyPolicyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)
        initWebView()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_back -> {
                finish()
            }
        }
    }

    fun initWebView() {
        tv_title.visibility = View.VISIBLE
        tv_title.text = intent.getStringExtra("show")

        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let { view?.loadUrl(it) }
                return true
            }
        }

        //to enable  java script
        webview.settings.javaScriptEnabled = true
        webview.settings.domStorageEnabled = true
        Log.e("49",intent.getStringExtra("show").toString())
        if (intent.getStringExtra("show").equals(getString(R.string.faq)))
            webview.loadUrl("http://3.137.160.102/pages/faq.html")
        else if (intent.getStringExtra("show").equals(getString(R.string.about)))
            webview.loadUrl(" http://3.137.160.102/pages/about.html")
        else if (intent.getStringExtra("show").equals(getString(R.string.privacy_policy)))
            webview.loadUrl("http://3.137.160.102/pages/userPrivacy.html")
        else if (intent.getStringExtra("show").equals(getString(R.string.terms)))
            webview.loadUrl(" http://3.137.160.102/pages/userTerm.html")
        else if (intent.getStringExtra("show").equals(getString(R.string.drive_for_rydz)))
            webview.loadUrl("https://www.rydzgroup.com/drive/")


    }
}

