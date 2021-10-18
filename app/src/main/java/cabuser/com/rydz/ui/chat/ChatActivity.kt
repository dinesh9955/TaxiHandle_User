package cabuser.com.rydz.ui.chat


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.R
import cabuser.com.rydz.socket.AppSocketListener
import cabuser.com.rydz.socket.SocketListener
import cabuser.com.rydz.ui.home.MainActivity
import cabuser.com.rydz.ui.home.SendBookResponse
import cabuser.com.rydz.util.base.BaseActivity
import cabuser.com.rydz.util.common.ConstVariables.Companion.ADMIN_ID
import cabuser.com.rydz.util.common.ConstVariables.Companion.BOOKING_ID
import cabuser.com.rydz.util.common.ConstVariables.Companion.DRIVER_ID
import cabuser.com.rydz.util.common.ConstVariables.Companion.MESSAGE
import cabuser.com.rydz.util.common.ConstVariables.Companion.MESSAGE_BY
import cabuser.com.rydz.util.common.ConstVariables.Companion.MY_CHAT_ID
import cabuser.com.rydz.util.common.ConstVariables.Companion.NEWMESSAGE
import cabuser.com.rydz.util.common.ConstVariables.Companion.OPPONENT_ID
import cabuser.com.rydz.util.common.ConstVariables.Companion.READMESSAGE
import cabuser.com.rydz.util.common.CustomeProgressDialog
import cabuser.com.rydz.util.fcm.MyFirebaseMessagingService
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_chat.*
import org.json.JSONException
import org.json.JSONObject


class ChatActivity : BaseActivity(), SocketListener {

    override fun onSocketConnected() {
        e("ChatActivity", "onSocketConnected")
        emitListeners()
    }

    private fun emitListeners() {
        try {
            AppSocketListener.getInstance().off(RydzApplication.user_obj!!.id + viewmodel!!.chatHistoryRequest.driverId, onMessaging)
            AppSocketListener.getInstance().addOnHandler(RydzApplication.user_obj!!.id + viewmodel!!.chatHistoryRequest.driverId, onMessaging)

        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    override fun onSocketDisconnected() {
        e("ChatActivity", "onSocketDisconnected")
    }

    override fun onSocketConnectionError() {
        e("ChatActivity", "onSocketConnectionError")
    }

    override fun onSocketConnectionTimeOut() {
        e("ChatActivity", "onSocketConnectionTimeOut")
    }


    private var messageList: ArrayList<MessageList>? = null

    private var chatAdapter: ChatAdapter? = null

    private var from: String = ""


    var viewmodel: ChatViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        from = intent?.getStringExtra("fromNotification")!!
        AppSocketListener.getInstance().setActiveSocketListener(this)
        // Restart Socket.io
        AppSocketListener.getInstance().restartSocket()
        viewmodel = ViewModelProviders.of(this).get(ChatViewModel::class.java)
        mProgress = CustomeProgressDialog(this)
        messageList = ArrayList()
        initViews()

    }


    private val onMessaging: Emitter.Listener
        get() = Emitter.Listener { args ->
            val jsonObject: JSONObject = args[0] as JSONObject
            if (jsonObject.getBoolean("success")) {
                val data: String = jsonObject.getJSONObject("msg").toString()
                val gson1 = Gson()
                val msgResponse = gson1.fromJson(data, MessageList::class.java)


                try {

                    if (msgResponse != null && msgResponse.messageBy.compareTo(1) == 0) {
                        readMessage()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }



                messageList!!.add(msgResponse)
                runOnUiThread {
                    if (chatAdapter == null) {
                        chatAdapter = ChatAdapter(this@ChatActivity, messageList!!)
                        rv_chat.adapter = chatAdapter
                    }
                    chatAdapter!!.updateChatList(messageList!!)
                    rv_chat!!.scrollToPosition(messageList!!.size - 1)


                }
            }
        }


    @SuppressLint("CheckResult")
    private fun initViews() {

        rv_chat!!.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@ChatActivity)
        rv_chat!!.setHasFixedSize(true)
        iv_close!!.setOnClickListener(this)
        iv_send!!.setOnClickListener(this)

        if (intent.getStringExtra("fromNotification") != null && intent.getStringExtra("fromNotification") == "not") {
            viewmodel!!.chatHistoryRequest = ChatHistoryRequest()
            viewmodel!!.chatHistoryRequest.bookingId = intent.getStringExtra("bookingId")
            viewmodel!!.chatHistoryRequest.userId = intent.getStringExtra("id")
            viewmodel!!.chatHistoryRequest.driverId = intent.getStringExtra("driverId")

            if (intent.getStringExtra("profilePic") != null && intent.getStringExtra("profilePic").toString().isNotEmpty()) {
                val requestOptions = RequestOptions().also({
                    it.placeholder(R.drawable.group)
                    it.error(R.drawable.group)
                })

                try {

                    Glide.with(this).setDefaultRequestOptions(requestOptions).load(RydzApplication.BASEURLFORPHOTO + intent.getStringExtra("profilePic").toString()).into(iv_userImage)
                } catch (e: Exception) {

                }
            }
            tv_userName.text = intent.getStringExtra("name").toString()

        } else {
            Log.e("156", MainActivity.muserrideStatusResponse.toString())
            viewmodel!!.userrideStatusResponse = SendBookResponse()
            viewmodel!!.userrideStatusResponse = MainActivity.muserrideStatusResponse
            viewmodel!!.chatHistoryRequest = ChatHistoryRequest()

            viewmodel!!.chatHistoryRequest.bookingId = viewmodel!!.userrideStatusResponse!!.booking!!.id
            viewmodel!!.chatHistoryRequest.driverId = viewmodel!!.userrideStatusResponse!!.booking!!.driverId!!.id
            viewmodel!!.chatHistoryRequest.userId = viewmodel!!.userrideStatusResponse!!.booking!!.userId!!.id
            if (viewmodel!!.userrideStatusResponse!!.booking!!.driverId!!.profilePic != null && !viewmodel!!.userrideStatusResponse!!.booking!!.driverId!!.profilePic.toString().isEmpty()) {
                val requestOptions = RequestOptions().also({
                    it.placeholder(R.drawable.group)
                    it.error(R.drawable.group)
                })
                try {


                    Glide.with(this).setDefaultRequestOptions(requestOptions).load(RydzApplication.BASEURLFORPHOTO + viewmodel!!.userrideStatusResponse!!.booking!!.driverId!!.profilePic.toString()).into(iv_userImage)


                } catch (e: Exception) {

                }
            }
            tv_userName.text = viewmodel!!.userrideStatusResponse!!.booking!!.driverId!!.firstName + " " + viewmodel!!.userrideStatusResponse!!.booking!!.driverId!!.lastName
            e("Driver Details", " name" + MainActivity.muserrideStatusResponse!!.booking!!.driverId!!.firstName + "--name " + viewmodel!!.userrideStatusResponse!!.booking!!.driverId!!.firstName + " " + viewmodel!!.userrideStatusResponse!!.booking!!.driverId!!.lastName)

        }

        viewmodel?.mProgress?.observe(this, Observer {
            if (it!!) {
                mProgress!!.show()
            } else
                mProgress!!.dismiss()
        })

        viewmodel?.messageHistory?.observe(this, Observer { messageHistory ->

            if (messageHistory != null && messageHistory.success == 0) {
                showMessage("")

            } else {
                renderApiSuccessResponse(messageHistory!!)
            }

        })
        viewmodel?.getMessageHistory()

    }


    /*
   * method to handle success response
   * */
    fun renderApiSuccessResponse(response: MessageHistory) {
        if (response.success == 1) {
            messageList = response.messageList
            chatAdapter = ChatAdapter(this@ChatActivity, messageList!!)
            rv_chat.adapter = chatAdapter
            chatAdapter!!.updateChatList(messageList!!)
            rv_chat!!.scrollToPosition(messageList!!.size - 1)

        } else {
            showAlert(resources.getString(R.string.errorString))
        }
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        when (v!!.id) {
            R.id.iv_close -> {

                MyFirebaseMessagingService.inChatScreen = false
                if (from.equals("not")) {  // if coming from notificationclick
                    startActivity(Intent(this@ChatActivity, MainActivity::class.java))

                }
                finish()
            }
            R.id.iv_send -> if (et_message!!.text.toString().trim { it <= ' ' }.isEmpty()) {
                showAlert(getString(R.string.message_should_not_empty))
            } else {
                val myObject: JSONObject = JSONObject()
                try {
                    myObject.put(DRIVER_ID, viewmodel!!.chatHistoryRequest.driverId)
                    myObject.put("userId", viewmodel!!.chatHistoryRequest.userId)
                    myObject.put(BOOKING_ID, viewmodel!!.chatHistoryRequest.bookingId)
                    myObject.put(ADMIN_ID, RydzApplication.adminId)
                    myObject.put(MESSAGE, et_message!!.text.toString())
                    myObject.put(MESSAGE_BY, "0")
                    myObject.put(MY_CHAT_ID, viewmodel!!.chatHistoryRequest.userId + viewmodel!!.chatHistoryRequest.driverId!!)
                    myObject.put(OPPONENT_ID, viewmodel!!.chatHistoryRequest.driverId!! + viewmodel!!.chatHistoryRequest.userId!!)
                    sendObjectToSocket(myObject, NEWMESSAGE)
                    et_message!!.setText("")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
    }


    override fun onResume() {
        super.onResume()
        MyFirebaseMessagingService.inChatScreen = true
    }


    private fun sendObjectToSocket(jsonObject: JSONObject, type: String) {
        AppSocketListener.getInstance().emit(type, jsonObject)
    }


    private fun readMessage() {
        val myObject = JSONObject()
        try {
            myObject.put("userId", viewmodel!!.chatHistoryRequest.userId)
            myObject.put(BOOKING_ID, viewmodel!!.chatHistoryRequest.bookingId)
            sendObjectToSocket(myObject, READMESSAGE)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}






