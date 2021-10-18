package cabuser.com.rydz.ui.profile

import android.app.Application
import android.content.Context
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.util.common.SingleLiveEvent
import cabuser.com.rydz.util.common.Utils
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.get
import com.bumptech.glide.Glide
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ProfileViewModel(application: Application) : AndroidViewModel(application), Callback<ProfileResponse> {


    var activity: Context = application.applicationContext
    var btnSelected: ObservableBoolean? = null
    var profilePhoto: ObservableField<String>
    var fullName: ObservableField<String>? = null
    var firstName: ObservableField<String>? = null
    var lastName: ObservableField<String>? = null
    var phoneNumber: ObservableField<String>? = null
    var email: ObservableField<String>? = null
    var mProgress: SingleLiveEvent<Boolean>? = null
    var password: ObservableField<String>? = null
    var userLogin: MutableLiveData<ProfileResponse>? = null
    var countryCode: ObservableField<String>? = null


    companion object {
        @BindingAdapter("imageUrl")
        @JvmStatic
        fun ImageView.setImageUrl(url: String?) {
            if (url != null && !url.isEmpty()) {
               /* val requestOptions = RequestOptions()
                requestOptions.placeholder(R.drawable.group)
                requestOptions.error(R.drawable.group)*/
                try{
                Glide.with(this.context).load(url).thumbnail(0.01f).into(this)
                }catch (e:Exception)
                {

                }
            }
        }
    }


    init {
        btnSelected = ObservableBoolean(false)
        mProgress = SingleLiveEvent<Boolean>()
        firstName = ObservableField("")
        lastName = ObservableField("")
        fullName = ObservableField("")
        phoneNumber = ObservableField("")
        email = ObservableField("")
        password = ObservableField("")
        userLogin = MutableLiveData<ProfileResponse>()
        profilePhoto = ObservableField("")
        countryCode = ObservableField("")
    }


    override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
        mProgress?.value = false
    }

    override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {

        mProgress?.value = false
        if (response.body()!!.success) {

            //userLogin?.value = response.body()
            val profileObj = response.body()!!.user
            firstName?.set(profileObj.firstName)
            lastName?.set(profileObj.lastName)
            fullName?.set(profileObj.firstName + " " + profileObj.lastName)
            profilePhoto.set(RydzApplication.BASEURLFORPHOTO + profileObj.profilePic)

            email?.set(profileObj.email)
            lastName?.set(profileObj.lastName)
            phoneNumber?.set(profileObj.phone)
            countryCode?.set(profileObj.countryCode)
            userLogin?.value = response.body()
        } else {
            Utils.showMessage(response.body()!!.message, activity.applicationContext)
        }

    }

    fun getUserProfile(isProgress: Boolean) {

        RydzApplication.prefs = PreferenceHelper.defaultPrefs(activity.applicationContext)
        val gson_String = RydzApplication.prefs[PreferenceHelper.Key.REGISTEREDUSER, ""]
        var gson = Gson()
        RydzApplication.user_obj = gson.fromJson(gson_String.toString(), User::class.java) as User
        if (isProgress)
            mProgress?.value = true
        RydzApplication.getRetroApiClient().getUser(RydzApplication.user_obj!!.id).enqueue(this)

    }

    fun updateProfilePhohot(file: File) {
        val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("pic", file.name, reqFile)
        val id = RequestBody.create(MediaType.parse("text/plain"), RydzApplication.user_obj!!.id)
        mProgress?.value = true
        RydzApplication.getRetroApiClient().onProfilePicUpload(id, body).enqueue(this)

    }

}







