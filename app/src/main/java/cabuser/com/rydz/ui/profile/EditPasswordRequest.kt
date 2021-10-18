package cabuser.com.rydz.ui.profile

data class EditPasswordRequest(
        val userId: String,
        val oldPassword: String,
        val newPassword: String
)