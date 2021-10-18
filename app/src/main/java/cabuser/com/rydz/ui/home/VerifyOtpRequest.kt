package cabuser.com.rydz.ui.home

data class VerifyOtpRequest(
        var countryCode: String = "", // +91
        var otp: String = "", // 2314
        var phone: String = "", // 8629803009
        var role: String = "" // user
)