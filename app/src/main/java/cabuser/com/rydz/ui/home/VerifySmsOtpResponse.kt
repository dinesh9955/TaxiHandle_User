package cabuser.com.rydz.ui.home

data class VerifySmsOtpResponse(
        var message: String = "", // Invalid Verification Code
        var success: Boolean = false ,// false,
        var isValidOtp: Int = 0,
        var err:String=""



)