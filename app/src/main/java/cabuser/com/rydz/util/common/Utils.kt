package cabuser.com.rydz.util.common

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.annotation.Nullable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.format.DateFormat
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import com.google.gson.Gson
import com.google.gson.JsonElement
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


/**
 * Created by deepshikha on 30/1/18.
 */

class Utils(internal var context: Context) {


    /*Just call the new Activity*/
    fun fn_newactivity(classname: Class<*>) {
        val intent = Intent(context, classname)
        context.startActivity(intent)
    }

    companion object {
        fun toJson(jsonObject: JsonElement): String {
            return Gson().toJson(jsonObject);
        }

        @Nullable
        fun getDate(toLong: Long): String {
            return DateFormat.format("dd MMM yyyy", Date(toLong)).toString()
        }

        @Nullable
        fun getDateWithtime(toLong: Long): String {
            return DateFormat.format("MMM dd, hh:mm", Date(toLong)).toString()
        }

        @Nullable
        fun getTime(toLong: Long): String {
            return DateFormat.format("hh:mm aa", Date(toLong)).toString()
        }


        fun isEmailValid(email: String): Boolean {
            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun showMessage(message: String?, context: Context) {
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()
            }
        }

        var wpayToken = ""


        //Reload activity
        fun reload(activity: Activity) {
            activity.finish()
            activity.overridePendingTransition(0, 0)
            activity.startActivity(activity.intent)
            activity.overridePendingTransition(0, 0)
        }

        /**
         * check if email is valid
         *
         * @param target
         * @return
         */
        fun isValidEmail(target: CharSequence): Boolean {
            return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }


        fun getColoredStringSpannable(tv: TextView, s: String, i: Int, length: Int) {
            val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#ea542c"))
            val ssb = SpannableStringBuilder(s)
            ssb.setSpan(foregroundColorSpan, i, length, 0)
            tv.text = ssb
        }

        /**
         * to display string with foreground color and underline
         *
         * @param s
         * @param i
         * @param length @return
         */
        fun getStringSpannable(tv: TextView, s: String, i: Int, length: Int) {
            val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#29CD9A"))
            val underlineSpan = UnderlineSpan()
            setSpanText(s, tv, foregroundColorSpan, underlineSpan, i, length)
        }

        private fun setSpanText(content: String, textView: TextView, span1: CharacterStyle, span2: CharacterStyle, i: Int, length: Int) {
            val ssb = SpannableStringBuilder(content)
            ssb.setSpan(span1, i, length, 0)
            ssb.setSpan(span2, i, length, 0)
            textView.text = ssb
        }

        /**
         * Response status -Success or not
         *
         * @param status
         */

        fun requestSuccessful(status: String): Boolean {
            return status.equals("Success", ignoreCase = true)
        }

        fun resizeAndCompressImageBeforeSend(context: Context, filePath: String, fileName: String): String {
            val MAX_IMAGE_SIZE = 700 * 1024 // max final file size in kilobytes

            // First decode with inJustDecodeBounds=true to check dimensions of image
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(filePath, options)

            // Calculate inSampleSize(First we are going to resize the image to 800x800 image, in order to not have a big but very low quality image.
            //resizing the image will already reduce the file size, but after resizing we will check the file size and start to compress image
            options.inSampleSize = calculateInSampleSize(options, 800, 800)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            val bmpPic = BitmapFactory.decodeFile(filePath, options)


            var compressQuality = 100 // quality decreasing by 5 every loop.
            var streamLength: Int
            do {
                val bmpStream = ByteArrayOutputStream()

                bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()
                streamLength = bmpPicByteArray.size
                compressQuality -= 5

            } while (streamLength >= MAX_IMAGE_SIZE)

            try {
                //save the resized and compressed file to disk cache

                val bmpFile = FileOutputStream(context.cacheDir.toString() + fileName)
                bmpPic.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpFile)
                bmpFile.flush()
                bmpFile.close()
            } catch (e: Exception) {

            }

            //return the path of resized and compressed file
            return context.cacheDir.toString() + fileName
        }


        fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
            // Image nin islenmeden onceki genislik ve yuksekligi
            val height = options.outHeight
            val width = options.outWidth

            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {

                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                    inSampleSize *= 2
                }
            }

            return inSampleSize
        }

        fun getCustomSnackBar(view: View, msg: String) {
            Toast.makeText(RydzApplication.getContext(), msg, Toast.LENGTH_LONG).show()
        }



        /**
         * @param str_date
         * @param requiredformat
         * @return
         */
        fun changeDateFormatCalendar(str_date: String, requiredformat: String): String {
            val format = SimpleDateFormat(requiredformat)
            return format.format(str_date)
        }

        /**
         * @param str_date
         * @param requiredformat
         * @return
         */
        fun changeDateFormatCalendar(str_date: Date, requiredformat: String): String {
            val format = SimpleDateFormat(requiredformat)
            return format.format(str_date)
        }

        /**
         * When network disconnected
         */



        /**
         * add promocoe dialog
         */


        fun addPromoCodePopUp(context: Activity): Dialog {
            val promoDialog = Dialog(context)
            promoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            promoDialog.setContentView(R.layout.dialog_addpromo)
            promoDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent);
            return promoDialog
        }





        /**
         * Method for converting string date format
         *
         * @param date
         * @param currFormat
         * @param RequireFormat
         * @return
         */
        fun getFormattedDate(date: String, currFormat: String, RequireFormat: String): String? {
            // Utils.e(Tag + "750", currFormat + "date " + date);
            val sdf = SimpleDateFormat(currFormat)
            val sdfReq = SimpleDateFormat(RequireFormat)


            try {
                var time = sdf.parse(date).time
                return sdfReq.format(time).toString()

            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return null
        }


        /**
         * To convert time format
         *
         * @param time
         * @return
         */
        fun getTime(time: String): String? {
            var str_date: String? = null



            try {
                val dateFormat = SimpleDateFormat("hh:mm:ss")
                var datetime    = dateFormat.parse(time)

                val sdff = SimpleDateFormat("hh:mm aa")
                str_date = sdff.format(datetime)

            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return str_date
        }

        /**
         * To convert time format
         *
         * @param time
         * @return
         */
        fun getTimeForHistory(time: String): String? {
            var str_date: String? = null


            try {
                val dateFormat = SimpleDateFormat("HH:mm:ss")
                var datetime = dateFormat.parse(time)

                val sdff = SimpleDateFormat("hh:mm aa")
                str_date = sdff.format(datetime)

            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return str_date
        }
    }







}
