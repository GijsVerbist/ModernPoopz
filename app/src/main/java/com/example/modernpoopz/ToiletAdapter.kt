package com.example.modernpoopz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class ToiletAdapter: RecyclerView.Adapter<ToiletAdapter.CustomViewHolder>() {

    private var toilets: ArrayList<Toilet>? = null

    fun setToilets( toiletList: ArrayList<Toilet>) {
        this.toilets = toiletList
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
        private var street = view.findViewById<TextView>(R.id.toilet_list_title)
       // private var huisnummer = view.findViewById<TextView>(R.id.toilet_list_subtitle)
        private var postcode = view.findViewById<TextView>(R.id.toilet_list_detail)

        fun bindView(toilet: Toilet) {

            street.text = toilet.properties.STRAAT + " " +toilet.properties.HUISNUMMER
            //huisnummer.text = toilet.huisnummer
            postcode.text = toilet.properties.POSTCODE.toString()
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



}