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

    //1
    override fun getCount(): Int {
        return dataSource.size
    }

    //2
    override fun getItem(position: Int): Any {
        return dataSource[position]
    }

    //3
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //4
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Get view for row item
        val rowView = inflater.inflate(R.layout.list_item_toilet, parent, false)

        // Get title element
        val titleTextView = rowView.findViewById(R.id.toilet_list_title) as TextView

// Get subtitle element
        val subtitleTextView = rowView.findViewById(R.id.toilet_list_subtitle) as TextView

// Get detail element
        val detailTextView = rowView.findViewById(R.id.toilet_list_detail) as TextView

// Get thumbnail element
        val thumbnailImageView = rowView.findViewById(R.id.toilet_list_thumbnail) as ImageView

        // 1
        val toilet = getItem(position) as Toilet

// 2
        titleTextView.text = toilet.straat
        subtitleTextView.text = toilet.huisnummer
        detailTextView.text = toilet.postcode.toString()

// 3
        //Picasso.with(context).load(toilet.imageUrl).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView)



        return rowView
    }



}