package cabuser.com.rydz.ui.payment


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.home.payment.addcard.Card

import kotlinx.android.synthetic.main.rowitem_card.view.*

class CardAdapter(val context: Context,val cardDataList: List<Card?>,  val showOptions :Boolean) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val myView= LayoutInflater.from(context).inflate(R.layout.rowitem_card,p0,false)
        return ViewHolder(myView)
    }

    override fun getItemCount(): Int {
        return cardDataList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, p1: Int) {

        viewHolder.tvFirstcardnumber.text="•••• •••• •••• ${cardDataList.get(p1)?.last4Digits}"
        viewHolder.tvFirstmastercarddetail.text="${cardDataList.get(p1)?.brand}/${cardDataList.get(p1)?.expiryDate}"
        viewHolder.ivSelect.visibility=View.VISIBLE
        if(!showOptions) {
            viewHolder.tvDelete.visibility=View.GONE

        }
        else
        {


            viewHolder.tvDelete.setOnClickListener {
                (context as PaymentActivity).promptBeforeDelete(p1)
            }
            viewHolder.cbCard.visibility=View.GONE

            viewHolder.tvDelete.visibility=View.VISIBLE
         /*   viewHolder.tvMarkDefault.visibility=View.GONE*/



        }

        try {
            if (cardDataList.get(p1)?.isSelected!!) {
                viewHolder.ivSelect.setImageResource(R.drawable.checked)
            } else {
                viewHolder.ivSelect.setImageResource(R.drawable.unchecked)
            }
        }catch ( e : Exception)
        {

        }

        viewHolder.ivSelect.setOnClickListener {
            (context as PaymentActivity).changeSelectedCard(p1)
        }
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var tvFirstcardnumber=itemView.tv_firstcardnumber
        var tvDelete=itemView.tv_delete
        var cbCard=itemView.cb_card
        var ivSelect=itemView.iv_select
       var tvMarkDefault=itemView.tv_mark_default
        var tvFirstmastercarddetail=itemView.tv_firstmastercarddetail
    }
}

