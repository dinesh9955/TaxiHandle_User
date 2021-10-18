package cabuser.com.rydz.ui.profile

data class UpdateProfileRequest(
    val adminId: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val countryCode: String,
    val phone: String
)