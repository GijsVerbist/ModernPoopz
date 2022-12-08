package com.example.modernpoopz.List

import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.modernpoopz.Map.MapFragment
import com.example.modernpoopz.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*


class DetailView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)



        //Toiletdata
        val street = intent.getStringExtra("straat")
        val houseNumber = intent.getStringExtra("huisnummer")
        val postalCode = intent.getIntExtra("postcode", 0)
        val betalend = intent.getStringExtra("betalend")
        val disabled = intent.getStringExtra("disabled")
        val target = intent.getStringExtra("target")
        val description = intent.getStringExtra("description")
        val category = intent.getStringExtra("category")
        val lat = intent.getDoubleExtra("lat", 0.0)
        val long = intent.getDoubleExtra("long", 0.0)
        val extra_informatie = intent.getStringExtra("extra_informatie")
        val openingsuren = intent.getStringExtra("openingsuren")
        val contactgegevens = intent.getStringExtra("contactgegevens")

        //Textfields
        val addressText: TextView = findViewById(R.id.detail_toilet_list_title)
        val targetText: TextView = findViewById(R.id.detail_toilet_list_filter_target)
        val payableText: TextView = findViewById(R.id.detail_toilet_list_filter_payable)
        val disabledText: TextView = findViewById(R.id.detail_toilet_list_filter_disabled)
        val categoryText: TextView = findViewById(R.id.detail_category_text)
        val descriptionText: TextView = findViewById(R.id.detail_omschrijving_text)
        val extraInfoText: TextView = findViewById(R.id.detail_extraInfo_text)
        val openingsurenText: TextView = findViewById(R.id.detail_openingsuren_text)
        val contactgegevensText: TextView = findViewById(R.id.detail_contactgegevens_text)
        val distanceText: TextView = findViewById(R.id.detail_toilet_list_filter_distance)

        if(street != null && houseNumber != null){

            if (postalCode != null){
                addressText.text = "$street $houseNumber, $postalCode"
            }
            else{
                addressText.text = "$street $houseNumber"
            }
        }

        if(betalend != null){
            if(betalend == "ja") {
                payableText.text = "Betalend"
                payableText.visibility = View.VISIBLE
            }
            else{
                payableText.text = "Gratis"
                payableText.visibility = View.VISIBLE
            }
        }
        else{
            payableText.visibility = View.GONE
        }

        if(target != null){

            if(target.contains("man/vrouw")){
                targetText.text = "Man/Vrouw"
                targetText.visibility = View.VISIBLE
            }
            else if (target.contains("vrouw")){
                targetText.text = "Vrouw"
                targetText.visibility = View.VISIBLE
            }
            else{
                targetText.text = "Man"
                targetText.visibility = View.VISIBLE
            }
        }
        else{
            //targetText.text = " "
            targetText.visibility = View.GONE
        }

        if(disabled != null){
            if(disabled == "ja"){
                disabledText.text = "Rolstoelvriendelijk"
                disabledText.visibility = View.VISIBLE
            }
            else{
                disabledText.visibility = View.GONE
            }
        }
        else{
            disabledText.visibility = View.GONE
        }

        if (category != null){
            categoryText.text = category.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
        }
        else{
            categoryText.text = "Geen categorie gevonden voor deze locatie"
        }

        if (description != null){
            descriptionText.text = description
        }
        else{
            descriptionText.text = "Geen omschrijving gevonden voor deze locatie"
        }

        if (extra_informatie != null){
            extraInfoText.text = extra_informatie
        }
        else{
            extraInfoText.text = "Geen extra informatie gevonden voor deze locatie"
        }

        if (openingsuren != null){
            openingsurenText.text = openingsuren
        }
        else{
            openingsurenText.text = "Geen openingsuren gevonden voor deze locatie"
        }

        if (contactgegevens != null){
            contactgegevensText.text = contactgegevens
        }
        else{
            contactgegevensText.text = "Geen contactgegevens gevonden voor deze locatie"
        }


        val distanceResult = FloatArray(1)
        if (lat != null && long != null && MapFragment.userLat != null && MapFragment.userLat != null) {

            Location.distanceBetween(
                MapFragment.userLat,
                MapFragment.userLong,
                lat,
                long,
                distanceResult
            )

            var distanceInKm = distanceResult[0] / 1000
            val df = DecimalFormat("#.#")
            df.roundingMode = RoundingMode.DOWN
            val roundoff = df.format(distanceInKm)
            distanceText.text = roundoff + "km"
        }
        else{
            distanceText.text = " "
        }

        println("Coord")
        println(MapFragment.userLat)
        println(MapFragment.userLong)
        println(lat)
        println(long)


        var back_buton: Button = findViewById(R.id.detail_back_button)
        back_buton.setOnClickListener{
            finish()
        }

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

