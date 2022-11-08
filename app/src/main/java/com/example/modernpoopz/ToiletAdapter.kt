package com.example.modernpoopz

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ToiletAdapter(private val context: Context, private val dataSource: ArrayList<Toilet>): BaseAdapter() {


    private val inflater: LayoutInflater
    = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

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
    }



}