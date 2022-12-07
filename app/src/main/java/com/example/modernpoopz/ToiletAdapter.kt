package com.example.modernpoopz

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.math.RoundingMode
import java.text.DecimalFormat

class ToiletAdapter: RecyclerView.Adapter<ToiletAdapter.CustomViewHolder>() {

    private var toilets: ArrayList<Toilet>? = null
    var onItemClick: ((Toilet) -> Unit)? = null

    fun setToilets( toiletList: ArrayList<Toilet>) {
        var sortedListOfToilets = toiletList
            .sortedWith(object : Comparator <Toilet> {
                override fun compare (t1: Toilet, t2: Toilet) : Int {
                    var distanceInKm1 = GetDistanceBetWeenPoints(t1)
                    var distanceInKm2 = GetDistanceBetWeenPoints(t2)

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
            var tempStreetname = toilet.properties.STRAAT
            var tempNumber = toilet.properties.HUISNUMMER
            var tempPostcode = toilet.properties.POSTCODE
            var tempPay = toilet.properties.BETALEND
            var tempTarget = toilet.properties.DOELGROEP
            var tempDisabled = toilet.properties.INTEGRAAL_TOEGANKELIJK

            var distanceInKm = GetDistanceBetWeenPoints(toilet)

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
                //payable.text = " "
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
                //target.text = " "
                target.visibility = GONE

            }

            if(tempDisabled != null){
                if(tempDisabled == "ja"){
                    disabled.text = "Rolstoelvriendelijk"
                    disabled.visibility = VISIBLE
                }
                else{
                    //disabled.text = " "
                    disabled.visibility = GONE
                }
            }
            else{
                //disabled.text = " "
                disabled.visibility = GONE
            }

            view.setOnClickListener {
                onItemClick?.invoke(toilets!![adapterPosition])
            }
        }




   /* override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val rowView = inflater.inflate(R.layout.list_item_toilet, parent, false)


        val titleTextView = rowView.findViewById(R.id.toilet_list_title) as TextView


        val subtitleTextView = rowView.findViewById(R.id.toilet_list_subtitle) as TextView


        val detailTextView = rowView.findViewById(R.id.toilet_list_detail) as TextView

        val thumbnailImageView = rowView.findViewById(R.id.toilet_list_thumbnail) as ImageView

        val toilet = getItem(position) as Toilet

        titleTextView.text = toilet.straat
        subtitleTextView.text = toilet.huisnummer
        detailTextView.text = toilet.postcode.toString()


        //Picasso.with(context).load(toilet.imageUrl).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView)



        return rowView
    }*/


    }

    fun GetDistanceBetWeenPoints(toilet:Toilet): Float {

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