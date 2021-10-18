package cabuser.com.rydz.ui.home

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import cabuser.com.rydz.R
import cabuser.com.rydz.RydzApplication
import cabuser.com.rydz.ui.ride.VehicleTypeX
import com.bumptech.glide.Glide


class VehicleAdapter(val context: Context, val vehicleList: List<VehicleTypeX>) : androidx.recyclerview.widget.RecyclerView.Adapter<VehicleAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: VehicleAdapter.ViewHolder, position: Int) {

        val tmpObj=vehicleList.get(position)
        holder.tv_vehileType.text = vehicleList.get(position).name.toString()

        if(vehicleList.get(position).persons==1)
        holder.tv_seats.text=vehicleList.get(position).persons.toString()
        else
            holder.tv_seats.text="1-"+vehicleList.get(position).persons.toString()


        var subTotal=0.0

     /*   if ((context as  MainActivity).promo != null &&  (context ).promo!!.promoId != null  &&  (context ).promo!!.applied ==1) {


            if (   (context ).promo!!.promoId.discountType == 0) {

                var   farecal = ( (context ).promo!!.promoId.discount / 100) * (context).estimateFare((context).tripDistance!!.toString().replace(",","").toDouble(),vehicleList.get(position).fareRate.toString().replace(",","").toDouble(),vehicleList.get(position).baseFare.toString().replace(",","").toDouble(),vehicleList.get(position).fareChangekm.toString().replace(",","").toDouble(),vehicleList.get(position).fareRateAfter.toString().replace(",","").toDouble())
                    subTotal = (context).estimateFare((context ).tripDistance!!.toString().replace(",","").toDouble(),vehicleList.get(position).fareRate.toString().replace(",","").toDouble(),vehicleList.get(position).baseFare.toString().replace(",","").toDouble(),vehicleList.get(position).fareChangekm.toString().replace(",","").toDouble(),vehicleList.get(position).fareRateAfter.toString().replace(",","").toDouble()) - farecal

                holder.tv_promostatus.visibility=View.VISIBLE

            } else if (   (context ).promo!!.promoId.discountType == 1) {


                if ((context ).promo!!.promoId.discount > (context ).estimateFare((context ).tripDistance!!.toString().replace(",","").toDouble(),vehicleList.get(position).fareRate.toString().replace(",","").toDouble(),vehicleList.get(position).baseFare.toString().replace(",","").toDouble(),vehicleList.get(position).fareChangekm.toString().replace(",","").toDouble(),vehicleList.get(position).fareRateAfter.toString().replace(",","").toDouble())) {
                    subTotal = 0.0
                } else
                    subTotal = (context ).estimateFare((context ).tripDistance!!.toString().replace(",","").toDouble(),vehicleList.get(position).fareRate.toString().replace(",","").toDouble(),vehicleList.get(position).baseFare.toString().replace(",","").toDouble(),vehicleList.get(position).fareChangekm.toString().replace(",","").toDouble(),vehicleList.get(position).fareRateAfter.toString().replace(",","").toDouble()) - (context ).promo!!.promoId.discount

                holder.tv_promostatus.visibility=View.VISIBLE

            }
            else {
             var    farecal = ( (context ).promo!!.promoId.discount / 100) * (context ).estimateFare((context ).tripDistance!!.toString().replace(",","").toDouble(),vehicleList.get(position).fareRate.toString().replace(",","").toDouble(),vehicleList.get(position).baseFare.toString().replace(",","").toDouble(),vehicleList.get(position).fareChangekm.toString().replace(",","").toDouble(),vehicleList.get(position).fareRateAfter.toString().replace(",","").toDouble())
                subTotal = if (farecal <= (context ).promo!!.promoId.maxamount!!)
                    (context ).estimateFare((context ).tripDistance!!.toString().replace(",","").toDouble(),vehicleList.get(position).fareRate.toString().replace(",","").toDouble(),vehicleList.get(position).baseFare.toString().replace(",","").toDouble(),vehicleList.get(position).fareChangekm.toString().replace(",","").toDouble(),vehicleList.get(position).fareRateAfter.toString().replace(",","").toDouble()) - farecal
                else {
                    (context ).estimateFare((context ).tripDistance.toString().replace(",","").toDouble(),vehicleList.get(position).fareRate.toString().replace(",","").toDouble(),vehicleList.get(position).baseFare.toString().replace(",","").toDouble(),vehicleList.get(position).fareChangekm.toString().replace(",","").toDouble(),vehicleList.get(position).fareRateAfter.toString().replace(",","").toDouble() - (context ).promo!!.promoId.maxamount)
                }

                holder.tv_promostatus.visibility=View.VISIBLE            }

        } else {*/

            subTotal=(context as MainActivity).estimateFare((context ).tripDistance!!.toString().replace(",","").toDouble(),vehicleList.get(position).fareRate.toString().replace(",","").toDouble(),vehicleList.get(position).baseFare.toString().replace(",","").toDouble(),vehicleList.get(position).fareChangekm.toString().replace(",","").toDouble(),vehicleList.get(position).fareRateAfter.toString().replace(",","").toDouble())
            holder.tv_promostatus.visibility=View.GONE
      //  }



/*
        if((context).sharedPreference.contains(PreferenceHelper.Key.PAYMENTMODE) && (context).sharedPreference.getString(PreferenceHelper.Key.PAYMENTMODE, "").equals(PreferenceHelper.Key.WALLET))
        {
            if(subTotal>=(context).walletAmount)
            {
                subTotal=subTotal-(context).walletAmount
            }
            else if(subTotal<(context).walletAmount)
            {
                (context).walletAmount=subTotal
                subTotal=0.0 //wallet amount deducted will be sent at that backend

            }

        }*/
        holder.tv_fare!!.text = "$ "+ String.format("%1$.2f", subTotal).replace(",", ".").toDouble()




        holder.rl_vehicle.setOnClickListener(View.OnClickListener {
            (context).selected_position = position
           // (fragment as VehicleSelectBottomSheetFragment).setSeats(position)
            notifyDataSetChanged()
            if(confirmRideDialog==null)
            (context).confirmSelectedVehicleDialog(tmpObj)
        })
        if ((context).selected_position === position) {
            holder.tv_fare.setTextColor(Color.parseColor("#000000"))
try {
    if (vehicleList.get(position).activeImage != null && !vehicleList.get(position).activeImage!!.isEmpty())
        Glide.with(this.context).load(RydzApplication.BASEURLFORPHOTO + vehicleList.get(position).activeImage).into(holder.iv_vehicle)
}catch (e:Exception)
{

}

            holder.view_divider.visibility=View.VISIBLE



        } else {

            holder.tv_fare.setTextColor(Color.parseColor("#9b9b9b"))

            try {

            if (vehicleList.get(position).image != null && !vehicleList.get(position).image!!.isEmpty())
                Glide.with(this.context).load(RydzApplication.BASEURLFORPHOTO + vehicleList.get(position).image).into(holder.iv_vehicle)

            }catch (e:Exception)
            {

            }

            holder.view_divider.visibility=View.GONE
        }


    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ViewHolder {

        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.vehicle_rowitem, viewGroup, false))
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }


    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val rl_vehicle: RelativeLayout = view.findViewById(R.id.rl_vehicle)
        val tv_fare: TextView = view.findViewById(R.id.tv_fare)
        val tv_seats:TextView=view.findViewById(R.id.tv_seats)
        val iv_vehicle: ImageView = view.findViewById(R.id.iv_vehicle)
        val tv_vehileType: TextView = view.findViewById(R.id.tv_vehileType)
        val view_divider:View=view.findViewById(R.id.view_divider)
        val tv_promostatus:TextView=view.findViewById(R.id.tv_promostatus)

    }

}