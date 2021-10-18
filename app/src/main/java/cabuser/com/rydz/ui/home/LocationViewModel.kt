package cabuser.com.rydz.ui.home


import android.app.Application
import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel

class LocationViewModel(application: Application) : AndroidViewModel(application) {


    var activity: Context = application.applicationContext
    var source: ObservableField<String>? = null
    var destination: ObservableField<String>? = null


    init {
        source = ObservableField("")
        destination = ObservableField("")
    }


}