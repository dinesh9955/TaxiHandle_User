package cabuser.com.rydz.ui.settings

import cabuser.com.rydz.ui.profile.User
import com.google.gson.annotations.SerializedName

data class NotificationResponse(
        @SerializedName("message")
        var message: String? = "", // Updated sucessfully
        @SerializedName("success")
        var success: Boolean? = false, // true
        @SerializedName("user")
        var user: User? = User()
)