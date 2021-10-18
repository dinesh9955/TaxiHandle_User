package cabuser.com.rydz.ui.settings

data class SubmitFeedbackRequest(
        val type: String,
        val userId: String,
        val subject: String,
        val comment: String,
        val bookingId:String
)