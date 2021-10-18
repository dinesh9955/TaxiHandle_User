package cabuser.com.rydz.util.base

/**
 * Created by priyanka on 13/4/18.
 */
interface BaseView {

    fun showProgress(message: String)
    fun hideProgress()
    fun isNetworkConnected(): Boolean
    fun showServerError()
    fun onSuccess(s: String)
    fun onFailure(msg: String)
}