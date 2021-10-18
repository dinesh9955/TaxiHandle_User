package cabuser.com.rydz.ui.profile

import android.Manifest
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.media.MediaScannerConnection.scanFile
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.Toast
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.databinding.ActivityProfileBinding
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.prefrences.PreferenceHelper
import cabuser.com.rydz.util.prefrences.PreferenceHelper.set
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.ccp
import kotlinx.android.synthetic.main.header_with_back.tv_title
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * ProfileActivity shows user profile details and on click of each profile's param navigate to another screen for editing of that param
 */
class ProfileActivity : BaseActivity() {


    private  var binding: ActivityProfileBinding? = null
    private var viewmodel: ProfileViewModel? = null
    private val GALLERY = 1
    private val CAMERA = 2
    private val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    private var isRefresh: Boolean = false
    private val IMAGE_DIRECTORY = "/taxihandle"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        viewmodel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        binding?.viewmodel = viewmodel
        mProgress = CustomeProgressDialog(this)
        initObservables()

    }

    //intialization of observer
    private fun initObservables() {

        tv_title.visibility = View.VISIBLE
        tv_title.setText(R.string.profile)
        ccp.setAutoDetectedCountry(true)

        RydzApplication.prefs = PreferenceHelper.defaultPrefs(this@ProfileActivity)
        if (RydzApplication.prefs.contains(PreferenceHelper.Key.LOGINTYPE) && RydzApplication.prefs.getString(PreferenceHelper.Key.LOGINTYPE, "").equals("socialLogin")) {
            tv_passwordTitle.visibility = View.GONE
            edt_pwd.visibility = View.GONE
            view_pwd.visibility = View.GONE

        } else {
            tv_passwordTitle.visibility = View.VISIBLE
            edt_pwd.visibility = View.VISIBLE
            view_pwd.visibility = View.VISIBLE
        }

        viewmodel?.mProgress?.observe(this, Observer {

            if (it!!) {
                mProgress!!.show();
            } else
                mProgress!!.dismiss()        })


        viewmodel?.userLogin?.observe(this, Observer { user ->


            if (!user.success!!) {
                showMessage(user.message)

            } else {
                val prefs = PreferenceHelper.defaultPrefs(this)
                prefs[PreferenceHelper.Key.REGISTEREDUSER] = user.user //setter
                RydzApplication.user_obj = user.user!!
                if(user.user.countryCode!!.length >= 2) {
                    ccp.setCountryForPhoneCode(user.user.countryCode!!.substring(user.user.countryCode!!.length - 2, user.user.countryCode!!.length).toInt())
                }
            }
        })

        viewmodel?.getUserProfile(true)

    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_firstName -> {
                isRefresh = true
                val namIntent = Intent(this, EditNameActivity::class.java)
                namIntent.putExtra("firstName", tv_firstName.text.toString().trim())
                namIntent.putExtra("lastName", tv_lastName.text.toString().trim())
                startActivity(namIntent)
            }
            R.id.tv_lastName -> {
                isRefresh = true
                val namIntent = Intent(this, EditNameActivity::class.java)
                namIntent.putExtra("firstName", tv_firstName.text.toString().trim())
                namIntent.putExtra("lastName", tv_lastName.text.toString().trim())
                startActivity(namIntent)
            }
            R.id.ll_phoneNumber -> {
                isRefresh = true
                val phoneNumberIntent = Intent(this, EditNumberActivity::class.java)
                phoneNumberIntent.putExtra("countrycode", ccp.selectedCountryCode.toString().trim())
                phoneNumberIntent.putExtra("phoneNumber", tv_phoneNumber.text.toString().trim())
                startActivity(phoneNumberIntent)
            }
            R.id.tv_email -> {
                isRefresh = true
                val emailIntent = Intent(this, EditEmailActivity::class.java)
                emailIntent.putExtra("email", tv_email.text.toString().trim())
                startActivity(emailIntent)
            }
            R.id.edt_pwd -> {
                isRefresh = true
                startActivity(Intent(this, EditPasswordActivity::class.java))
            }
            R.id.iv_back -> {
                finish()
            }
            R.id.iv_profile -> {

                if (checkAndRequestPermissions()) {
                    isRefresh = true
                    showPictureDialog()
                } else {

                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        if (isRefresh)
            viewmodel?.getUserProfile(false)


    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/

        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    saveImage(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@ProfileActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }

        } else if (requestCode == CAMERA) {
            if(data!=null) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                saveImage(thumbnail)
            }
        }
    }

    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {

            wallpaperDirectory.mkdirs()
        }

        try {
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            scanFile(this,
                    arrayOf(f.getPath()),
                    arrayOf("image/jpeg"), null)
            fo.close()

            viewmodel?.updateProfilePhohot(f);
            return f.getAbsolutePath()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }




    private fun checkAndRequestPermissions(): Boolean {

        val camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val listPermissionsNeeded = ArrayList<String>()
        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                val perms = HashMap<String, Int>()
                // Initialize the map with both permissions
                perms[Manifest.permission.CAMERA] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] = PackageManager.PERMISSION_GRANTED


                // Fill with actual results from user
                if (grantResults.size > 0) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    // Check for both permissions
                    if (perms[Manifest.permission.CAMERA] == PackageManager.PERMISSION_GRANTED
                            && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED

                    ) {
                        showPictureDialog()
                        //else any one or both the permissions are not granted
                    } else {

                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        //                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

                        ) {
                            showDialogOK("Service Permissions are required for this app",
                                    DialogInterface.OnClickListener { dialog, which ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> checkAndRequestPermissions()
                                            DialogInterface.BUTTON_NEGATIVE ->

                                                finish()
                                        }
                                    })
                        } else {
                            explain("You need to give some mandatory permissions to continue. Please go to app settings and give permissions.")
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }//permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }
                }
            }
        }

    }


    private fun showDialogOK(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show()
    }


    private fun explain(msg: String) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
        dialog.setMessage(msg)
                .setPositiveButton("Yes") { paramDialogInterface, paramInt ->
                    startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:cabuser.com.rydz")))
                }
                .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> finish() }
        dialog.show()
    }


    override fun onStop() {
        super.onStop()

        mProgress!!.dismiss();

    }



}
