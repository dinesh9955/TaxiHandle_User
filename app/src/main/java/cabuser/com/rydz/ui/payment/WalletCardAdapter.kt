package cabuser.com.rydz.ui.payment


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.home.payment.addcard.Card
import kotlinx.android.synthetic.main.layout_wallet_item.view.*


class WalletCardAdapter(val context: Context,val cardDataList: List<Card?>) : RecyclerView.Adapter<WalletCardAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val myView= LayoutInflater.from(context).inflate(R.layout.layout_wallet_item,p0,false)
        return ViewHolder(myView)
    }

    override fun getItemCount(): Int {
        return cardDataList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, p1: Int) {

        viewHolder.tv_firstcardnumber.text="•••• •••• •••• ${cardDataList.get(p1)?.last4Digits}"
        viewHolder.tv_firstmastercarddetail.text="${cardDataList.get(p1)?.brand}/${cardDataList.get(p1)?.expiryDate}"




            try {
                if (cardDataList.get(p1)?.isSelected!!) {
                    viewHolder.iv_select.setImageResource(R.drawable.checked)
                    viewHolder.tv_addtowallet.visibility=View.VISIBLE
                    viewHolder.tv_addtowallet.text= "Pay $${(context as AddMoneyToWallet).addAmount}"
                } else {
                    viewHolder.iv_select.setImageResource(R.drawable.unchecked)
                    viewHolder.tv_addtowallet.visibility=View.GONE

                }
            }catch ( e : Exception)
            {

            }






            viewHolder.iv_select.setOnClickListener {
                (context as AddMoneyToWallet).changeSelectedCard(p1)
            }
        viewHolder.tv_addtowallet.setOnClickListener {
                (context as AddMoneyToWallet).addMoneyToWallet(p1)
            }


    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var tv_firstcardnumber=itemView.tv_firstcardnumber
        var tv_addtowallet=itemView.tv_addtowallet
        var iv_select=itemView.iv_select
        var tv_firstmastercarddetail=itemView.tv_firstmastercarddetail

    }
}

