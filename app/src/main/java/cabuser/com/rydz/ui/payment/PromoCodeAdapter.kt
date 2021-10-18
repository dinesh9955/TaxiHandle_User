package cabuser.com.rydz.ui.payment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cabuser.com.rydz.R
import cabuser.com.rydz.ui.home.payment.addcard.Card
import cabuser.com.rydz.ui.home.payment.addcard.PromoCode
import cabuser.com.rydz.util.base.BaseActivity

import kotlinx.android.synthetic.main.rowitem_card.view.*
import kotlinx.android.synthetic.main.rowitem_promocode.view.*

class PromoCodeAdapter(val context: Context,val promosList: List<PromoCode>) : RecyclerView.Adapter<PromoCodeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val myView= LayoutInflater.from(context).inflate(R.layout.rowitem_promocode,p0,false)
        return ViewHolder(myView)
    }

    override fun getItemCount(): Int {
        return promosList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, p1: Int) {

           viewHolder.tvDesc.text = promosList.get(p1).description
           viewHolder.tvCode.text = promosList.get(p1).code

        if(promosList.get(p1).discountType==1) {
            viewHolder.tvValidtill.text=" Valid till:  ${(context as BaseActivity).getDateFromMilliseconds(promosList.get(p1).enddate, "MMM dd yyyy hh:mm a")}"
            viewHolder.tvDiscountType.text = "$"+promosList.get(p1).discount.toString() + " flat off on ride"
        }
             else {
            viewHolder.tvValidtill.text=" Valid upto: ${(context as BaseActivity).getDateFromMilliseconds(promosList.get(p1).enddate, "MMM dd yyyy hh:mm a")}"
            viewHolder.tvDiscountType.text = promosList.get(p1).discount.toString() + " % off on ride"
        }



    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var tvDesc=itemView.tv_desc
        var tvCode=itemView.tv_code
        var tvDiscountType=itemView.tv_discountType
        var tvValidtill=itemView.tv_validtill

    }
}

