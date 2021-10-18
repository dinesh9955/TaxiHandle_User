package cabuser.com.rydz.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cabuser.com.rydz.R
import cabuser.com.rydz.util.common.OnItemClickListener


class RefundAdapter(private val reasonsList: List<RefundReason>, internal var context: Context, val itemClick : OnItemClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<RefundAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.refund_rowitem, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.tvReason.setText(reasonsList.get(position).reason )

      if(reasonsList.get(position).isSelected)
      holder.ivSelected.visibility=View.VISIBLE
        else
          holder.ivSelected.visibility=View.GONE

        holder.tvReason.setOnClickListener {
            itemClick.onItemClick(position)

        }
    }

    override fun getItemCount(): Int {
        return reasonsList.size
    }

    inner class MyViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

        val tvReason: TextView
        val ivSelected: ImageView

        init {
            tvReason = view.findViewById<View>(R.id.tv_reason) as TextView
            ivSelected = view.findViewById<View>(R.id.iv_selected) as ImageView
        }
    }
}