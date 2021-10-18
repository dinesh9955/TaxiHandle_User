package cabuser.com.rydz.ui.home

import com.google.gson.annotations.SerializedName

data class ChangeStatusResponse(
        @SerializedName("message")
        var message: String? = "",
        @SerializedName("success")
        var success: Boolean? = false // true
)