package com.example.modernpoopz

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class DetailView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)




        val straat = intent.getStringExtra("straat")

        val huisnummer = intent.getStringExtra("huisnummer")

        val postcode = intent.getIntExtra("postcode", 0)

        val buttonTest: Button = findViewById(R.id.buttonz)


        val straatText: TextView = findViewById(R.id.Straat)

        val postcodeText: TextView = findViewById(R.id.Postcode)

        straatText.text = straat + " " + huisnummer
        postcodeText.text = postcode.toString()
       // val fragment = MapFragment()
       // buttonTest.setOnClickListener {supportFragmentManager.beginTransaction().replace(R.id.actualmapview, fragment).commit() }

    }


}
