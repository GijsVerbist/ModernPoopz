package com.example.modernpoopz

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.modernpoopz.Map.MapFragment
import java.math.RoundingMode
import java.text.DecimalFormat

class ToiletAdapter: RecyclerView.Adapter<ToiletAdapter.CustomViewHolder>() {

    private var toilets: ArrayList<Toilet>? = null
    var onItemClick: ((Toilet) -> Unit)? = null

    fun setToilets( toiletList: ArrayList<Toilet>) {
        //Sort on closest distance
        val sortedListOfToilets = toiletList
            .sortedWith(object : Comparator <Toilet> {
                override fun compare (t1: Toilet, t2: Toilet) : Int {
                    val distanceInKm1 = getDistanceBetWeenPoints(t1)
                    val distanceInKm2 = getDistanceBetWeenPoints(t2)

                    if (distanceInKm1 > distanceInKm2){
                        return 1
                    }
                    if (distanceInKm1 == distanceInKm2){
                        return 0
                    }
                    else{
                        return -1
                    }
                }
            })

        this.toilets = ArrayList(sortedListOfToilets)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return toilets!!.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =  CustomViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_toilet,parent,false)
    )

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val toiletList = toilets!![position]
        if (toiletList != null) {
            holder.bindView(toiletList)
        }
    }

    inner class CustomViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        private var adres = view.findViewById<TextView>(R.id.toilet_list_title)
        private var payable = view.findViewById<TextView>(R.id.toilet_list_filter_payable)
        private var target = view.findViewById<TextView>(R.id.toilet_list_filter_target)
        private var disabled = view.findViewById<TextView>(R.id.toilet_list_filter_disabled)
        private var distance = view.findViewById<TextView>(R.id.toilet_list_filter_distance)

        fun bindView(toilet: Toilet) {
            val tempStreetname = toilet.properties.STRAAT
            val tempNumber = toilet.properties.HUISNUMMER
            val tempPostcode = toilet.properties.POSTCODE
            val tempPay = toilet.properties.BETALEND
            val tempTarget = toilet.properties.DOELGROEP
            val tempDisabled = toilet.properties.INTEGRAAL_TOEGANKELIJK

            val distanceInKm = getDistanceBetWeenPoints(toilet)

            val df = DecimalFormat("#.#")
            df.roundingMode = RoundingMode.DOWN
            val roundoff = df.format(distanceInKm)
            distance.text = roundoff + "km"


            if(tempStreetname != null && tempNumber != null){

                if (tempPostcode != null){
                    adres.text = toilet.properties.STRAAT + " " +toilet.properties.HUISNUMMER + ", " + toilet.properties.POSTCODE
                }
                else{
                    adres.text = toilet.properties.STRAAT + " " +toilet.properties.HUISNUMMER
                }
            }

            if(tempPay != null){
                if(tempPay == "ja") {
                    payable.text = "Betalend"
                    payable.visibility = VISIBLE
                }
                else{
                    payable.text = "Gratis"
                    payable.visibility = VISIBLE
                }
            }
            else{
                payable.visibility = GONE
            }

            if(tempTarget != null){

                if(tempTarget.contains("man/vrouw")){
                    target.text = "Man/Vrouw"
                    target.visibility = VISIBLE
                }
                else if (tempTarget.contains("vrouw")){
                    target.text = "Vrouw"
                    target.visibility = VISIBLE
                }
                else{
                    target.text = "Man"
                    target.visibility = VISIBLE
                }
            }
            else{
                target.visibility = GONE

            }

            if(tempDisabled != null){
                if(tempDisabled == "ja"){
                    disabled.text = "Rolstoelvriendelijk"
                    disabled.visibility = VISIBLE
                }
                else{
                    disabled.visibility = GONE
                }
            }
            else{
                disabled.visibility = GONE
            }

            view.setOnClickListener {
                onItemClick?.invoke(toilets!![adapterPosition])
            }
        }
    }


    //Calculating distance between location and toilet
    fun getDistanceBetWeenPoints(toilet:Toilet): Float {
        val distanceResult = FloatArray(1)
        if (toilet.geometry.coordinates!![0] != null && toilet.geometry.coordinates!![1] != null && MapFragment.userLat != null && MapFragment.userLong != null) {
            val toiletLat = toilet.geometry.coordinates[0]
            val toiletLong = toilet.geometry.coordinates[1]
            Location.distanceBetween(
                MapFragment.userLat,
                MapFragment.userLong,
                toiletLat,
                toiletLong,
                distanceResult
            )

        }
        return distanceResult[0] / 1000
    }

}