package cabuser.com.rydz.ui.settings

data class ContactUsResponse(
        var contact: Contact = Contact(),
        var message: String = "", // Successful
        var success: Boolean = false // true
)