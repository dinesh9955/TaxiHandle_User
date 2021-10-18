package cabuser.com.rydz.ui.chat

import android.app.Application
import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.home.SendBookResponse
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.databinding.ObservableField as ObservableField1
import cabuser.com.rydz.util.common.SingleLiveEvent as SingleLiveEvent1


class ChatViewModel(application: Application) : AndroidViewModel(application), Callback<MessageHistory> {
    override fun onFailure(call: Call<MessageHistory>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<MessageHistory>, response: Response<MessageHistory>) {
        mProgress?.value = false
        if (response.body()!!.success == 1) {
            messageHistory?.value = response.body()
        }

    }


    companion object {
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun ImageView.setImageUrl(url: String?) {
            if (url != null && !url.isEmpty()) {
                val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.group)
                requestOptions.error(R.drawable.group)

                try {

                    Glide.with(this.context).setDefaultRequestOptions(requestOptions).load(url).into(this)
                } catch (e: Exception) {

                }
            }


        }
    }


    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    private var profilePhoto: ObservableField1<String>
    private var fullName: ObservableField1<String>? = null

    var rating: ObservableField1<String>? = null
    var mProgress: SingleLiveEvent1<Boolean>? = null

    var messageHistory: MutableLiveData<MessageHistory>? = null
    var userrideStatusResponse: SendBookResponse? = null
    var chatHistoryRequest = ChatHistoryRequest()

    init {
        btnSelected = ObservableBoolean(false)
        mProgress = SingleLiveEvent1()
        messageHistory = MutableLiveData()
        profilePhoto = ObservableField1("")
        fullName = ObservableField1("")
        rating = ObservableField1("")

    }


    fun getMessageHistory() {

        mProgress?.value = true

        RydzApplication.getRetroApiClient().getMessageHistory(chatHistoryRequest).enqueue(this)

    }

}
