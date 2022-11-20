package com.example.modernpoopz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.beust.klaxon.*
import com.example.modernpoopz.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.*
import java.io.StringReader
import java.net.URL
import java.util.regex.Pattern
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController



private lateinit var listView: ListView
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Toilets.getToiletsFromApi(this)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_map, R.id.navigation_home
            )
        )
        if (navController != null) {
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }



    }
}

fun getURLContentsAsString(ourUrl: URL): String {

    val client = OkHttpClient()

    val request = Request.Builder()

        .url(ourUrl)
        .build()

    val call = client.newCall(request)
    val response = call.execute()

    return response.body!!.string()
}

fun getJsonWorking(){
    val url = URL("https://geodata.antwerpen.be/arcgissql/rest/services/P_Portal/portal_publiek1/MapServer/8/query?outFields=*&where=1%3D1&f=geojson")

    Thread(Runnable {
        // Do something in Background (Back-end)
        var result = getURLContentsAsString(url)

        //println("result: " + result)

        val pathMatcher = object : PathMatcher {
            override fun pathMatches(path: String) =
                Pattern.matches(".*features.*properties.*", path)

            override fun onMatch(path: String, value: Any) {


                /*println("Adding $path = $value")
                var straat: String = ""
                var huisnummer: String = ""
                var postcode: Int = -1
                var betalend: String = ""
                var categorie: String = ""
                var omschrijving: String = ""
                var doelgroep: String = ""
                var luiertafel: String = ""
                var doelgroep: String

                when (path) {
                    "$.features[1].properties.STRAAT"
                    -> toilet.straat.replace("", value.toString())  //straat == value as String
                    "$.features[1].properties.HUISNUMMER"
                    -> toilet.huisnummer.replace("", value.toString())
                    /*
                    "$.features[1].properties.POSTCODE"
                    -> toilet.straat.replace("", value.toString())*/
                    "$.features[1].properties.BETALEND"
                    -> toilet.betalend.replace("", value.toString())
                    "$.features[1].properties.CATEGORIE"
                    -> toilet.categorie.replace("", value.toString())
                    "$.features[1].properties.OMSCHRIJVING"
                    -> toilet.omschrijving.replace("", value.toString())
                    "$.features[1].properties.DOELGROEP"
                    -> toilet.doelgroep.replace(toilet.doelgroep, value as String) //println("RHEEEEEE " + doelgroep.replace(test, value as String))
                    "$.features[1].properties.LUIERTAFEL"
                    -> toilet.luiertafel.replace("", value.toString())
                    "$.features[1].properties.MABU"
                    -> {println("doelgroep: " + toilet.luiertafel + "?") }  //toiletLijst.add(Toilet(straat, huisnummer, postcode, betalend, categorie, omschrijving, doelgroep, luiertafel))
                }*/

                //toiletLijst.add(toilet)

                // toiletLijst.add(Toilet(value as String))
                //println(value.toString())
                /* var T1 = Toilet(

                )

                if(path.endsWith(".STRAAT")){
                    T1.straat = value as String
                }
                else if(path.endsWith("HUISNUMMER")){
                    T1.huisnummer = value as String
                }
                else if(path.endsWith("POSTCODE")){
                    T1.postcode = value as Int
                }
                else if(path.endsWith("BETALEND")){
                    T1.betalend = value as String
                }
                else if(path.endsWith("CATEGORIE")){
                    T1.categorie = value as String
                }
                else if(path.endsWith("OMSCHRIJVING")){
                    T1.omschrijving = value as String
                }
                else if(path.endsWith("DOELGROEP")){
                    T1.doelgroep = value as String
                }
                else if(path.endsWith("LUIERTAFEL")){
                    T1.luiertafel = value as String
                }

                if(T1.straat.isNotEmpty() && T1.huisnummer.isNotEmpty() && T1.postcode > -1 &&  T1.betalend.isNotEmpty()
                    && T1.categorie.isNotEmpty() && T1.omschrijving.isNotEmpty() && T1.doelgroep.isNotEmpty() && T1.luiertafel.isNotEmpty()){
                    println("Hi")

                }
                toiletLijst.add(T1)
                 //println("Here")
               // println(T1.straat)
                //println(T1.huisnummer)
*/

            }

        }

        Klaxon()
            .pathMatcher(pathMatcher)
            .parse<String>(result)

        println(result)

        val objString = """{
                 "OBJECTID": 283, "ID": 15, "OBDD": "2010-03-01T00:00:00Z", "CATEGORIE": "openbaar toilet op publiek domein (overig)", "PUBLICEREN": "wel publiceren", "PRIORITAIR": "nee", "OMSCHRIJVING": "Begraafplaats Berendrecht", "EXTRA_INFO_PUBLIEK": null, "VRIJSTAAND": "ja", "TYPE": "toilet", "STADSEIGENDOM": "ja", "BETALEND": "nee", "STRAAT": "Schouwvegerstraat", "HUISNUMMER": "1", "POSTCODE": 2040, "DISTRICT": "Bezali", "BEHEERDER": "SB", "CONTACTPERSOON": "Filip Scholtis (gebouwverantw.) / Willy Wouters (overkoepelend werkleider)", "CONTACTGEGEVENS": "0479 88 05 13 (WW)", "VERMELDING": "ja", "DOELGROEP": "man/vrouw", "INTEGRAAL_TOEGANKELIJK": "ja", "GESCREEND": "ja", "LUIERTAFEL": "nee", "MABU": "1899-12-30T08:00:00Z", "MAEU": "1899-12-30T17:30:00Z", "DIBU": "1899-12-30T08:00:00Z", "DIEU": "1899-12-30T17:30:00Z", "WOBU": "1899-12-30T08:00:00Z", "WOEU": "1899-12-30T17:30:00Z", "DOBU": "1899-12-30T08:00:00Z", "DOEU": "1899-12-30T17:30:00Z", "VRBU": "1899-12-30T08:00:00Z", "VREU": "1899-12-30T17:30:00Z", "ZABU": "1899-12-30T08:00:00Z", "ZAEU": "1899-12-30T17:30:00Z", "ZOBU": "1899-12-30T08:00:00Z", "ZOEU": "1899-12-30T17:30:00Z", "OPENINGSUREN_OPM": "zomertijd tot 20:00", "OPM_INTERN": null, "LAT": null, "LONG": null, "X_COORD": 146348.0845, "Y_COORD": 225615.9532 }, "geometry": { "type": "Point", "coordinates": [ 4.316354477431166, 51.340320345522741 ] } 
                 
                }""".trimMargin()
        JsonReader(StringReader(objString)).use{ reader ->
            reader.beginObject {
                var objectId: Int? = null
                var ID: Int? = null
                var OBDD: String? = null
                var PUBLICEREN: String? = null
                var PRIORITAIR: String? = null
                var OMSCHRIJVING: String? = null
                var EXTRA_INFO_PUBLIEK: String? = null
                var VRIJSTAAND: String? = null
                var TYPE: String? = null
                var STADSEIGENDOM: String? = null
                var BETALEND: String? = null
                var DISTRICT: String? = null
                var BEHEERDER: String? = null
                var CONTACTPERSOON: String? = null
                var STRAAT: String? = null
                var HUISNUMMER : String? = null
                var POSTCODE: Int?  = null
                var CONTACTGEGEVENS : String? = null
                var VERMELDING : String? = null
                var DOELGROEP : String? = null
                var INTEGRAAL_TOEGANKELIJK : String? = null
                var CATEGORIE : String? = null
                var LUIERTAFEL : String? = null
                var GESCREEND : String? = null
                while(reader.hasNext()){
                    val readName = reader.nextName()
                    when(readName){
                        "OBJECTID" -> objectId = reader.nextInt()
                        "ID" -> ID = reader.nextInt()
                        "STRAAT" -> STRAAT = reader.nextString()
                        "HUISNUMMER" -> HUISNUMMER = reader.nextString()
                        "POSTCODE" -> POSTCODE = reader.nextInt()
                        "BETALEND" -> BETALEND = reader.nextString()
                        "CATEGORIE" -> CATEGORIE = reader.nextString()
                        "OMSCHRIJVING" -> OMSCHRIJVING = reader.nextString()
                        "DOELGROEP" -> DOELGROEP = reader.nextString()
                        "LUIERTAFEL" -> LUIERTAFEL = reader.nextString()
                        else -> println("Unexpected name: $readName")
                    }
                }
                println("Here")
                println(STRAAT + HUISNUMMER)
            }
        }
        /*runOnUiThread {
            // Do something in UI (Front-end)
        }*/
        /*runBlocking {
            delay(3000L)
            val listIterator = toiletLijst.listIterator()
            println("Start List")
            while(listIterator.hasNext()){
                val i = listIterator.next()
                println("Straat: " + i.straat)
                println("huisnummer: " + i.huisnummer)
                println("postcode: " + i.postcode)
                //println("omschrijving: " + i.omschrijving)
                //println("doelgroep: " + i.doelgroep)
            }
        }*/
    }).start()





}