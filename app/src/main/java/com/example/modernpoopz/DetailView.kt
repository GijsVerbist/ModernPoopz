package com.example.modernpoopz

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.navigation.NavDeepLinkBuilder
import org.osmdroid.util.GeoPoint


class DetailView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)




        val straat = intent.getStringExtra("straat")

        val huisnummer = intent.getStringExtra("huisnummer")

        val postcode = intent.getIntExtra("postcode", 0)

        val lat = intent.getDoubleExtra("lat", 0.0)

        val long = intent.getDoubleExtra("long", 0.0)




        val straatText: TextView = findViewById(R.id.Straat)

        val postcodeText: TextView = findViewById(R.id.Postcode)

        val latText: TextView = findViewById(R.id.lat)

        val longText: TextView = findViewById(R.id.longg)

        straatText.text = straat + " " + huisnummer
        postcodeText.text = postcode.toString()
        latText.text = lat.toString()
        longText.text = long.toString()
       //val fragment = MapFragment()
       //buttonTest.setOnClickListener {supportFragmentManager.beginTransaction().replace(R.id.actualmapview, fragment).commit() }

        /*buttonTest.setOnClickListener {
            supportFragmentManager.commit {
                replace<MapFragment>(R.id.actualmapview, "mapTag")

            }
        }


        /*val fragment: MapFragment? = MapFragment()

        val pendingIntent = NavDeepLinkBuilder(this.applicationContext)
            .setGraph(R.navigation.navigation)
            .setDestination(R.id.navigation_map)
            .createPendingIntent()

        buttonTest.setOnClickListener { pendingIntent.send() }
        */
        */






    }
}

