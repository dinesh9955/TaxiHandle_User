package cabuser.com.rydz.ui.settings

import com.google.gson.annotations.SerializedName

data class NotificationRequest(
        @SerializedName("sendNoti")
        var sendNoti: String? = "", // 0
        @SerializedName("isBroadcast")
        var isBroadcast: String? = "", // 0
        @SerializedName("userId")
        var userId: String? = "" // 5c484ac3198e8c60b9ddbc44
)