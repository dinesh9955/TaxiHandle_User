package cabuser.com.rydz.ui.profile

data class EditNumberRequest(
    val adminId: String,
    val userId: String,
    val countryCode: String,
    val phone: String
)