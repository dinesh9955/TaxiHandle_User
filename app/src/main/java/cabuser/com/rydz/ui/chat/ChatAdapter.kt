package cabuser.com.rydz.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cabuser.com.rydz.R
import cabuser.com.rydz.util.common.Utils
import kotlinx.android.synthetic.main.item_chat.view.*

class ChatAdapter(var context: Context, private var messageList: ArrayList<MessageList>) : androidx.recyclerview.widget.RecyclerView.Adapter<ChatAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

        val context = p0.context
        val inflater = LayoutInflater.from(context!!)
        // Inflate the custom layout
        val contactView = inflater.inflate(R.layout.item_chat, p0, false)
        return ViewHolder(contactView)

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, p1: Int) =
            if (messageList[p1].messageBy.toString().equals("0")) {
                viewHolder.llSender.visibility = View.VISIBLE
                viewHolder.llReciever.visibility = View.GONE
                viewHolder.tvSenderMessage.text = this.messageList[p1].message
                viewHolder.tvSenderTime.text = Utils.getTime(messageList.get(p1).date)
            } else {
                viewHolder.llSender.visibility = View.GONE
                viewHolder.llReciever.visibility = View.VISIBLE
                viewHolder.tvRecieverMessage.text = messageList.get(p1).message
                viewHolder.tvRecieverTime.text = Utils.getTime(messageList.get(p1).date)
            }

    fun updateChatList(newmessageList: ArrayList<MessageList>) {

        this.messageList = newmessageList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var llSender = itemView.ll_sender
        var llReciever = itemView.ll_reciever
        var tvSenderMessage = itemView.tv_sender_message
        var tvRecieverMessage = itemView.tv_reciever_message
        var tvSenderTime = itemView.tv_sender_time
        var tvRecieverTime = itemView.tv_reciever_time

    }


}