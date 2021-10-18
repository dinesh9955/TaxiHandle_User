package cabuser.com.rydz.ui.home

data class NewMessageResponse(
        var message: String = "", // Successful
        var msg: Msg = Msg(),
        var success: Boolean = false, // true
        var result: Int = 0 // true

)