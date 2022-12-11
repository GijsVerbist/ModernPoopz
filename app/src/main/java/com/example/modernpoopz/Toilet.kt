package com.example.modernpoopz

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.Feature
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class Toilets(

    val features: List<Toilet>


) {

    companion object {
        var toilets2: ArrayList<Toilet> = ArrayList()
        var toilets3: ArrayList<Toilet> = ArrayList()
        fun getToiletsFromApi(activity: FragmentActivity) {

            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://geodata.antwerpen.be/arcgissql/rest/services/P_Portal/portal_publiek1/MapServer/8/query?outFields=*&where=1%3D1&f=geojson")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("httperror: ", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")


                        val json = response.body!!.string()
                        println("JSON!: " + json)
                        val gson = GsonBuilder().create()
                        println("GSON!: " + gson)
                        val toilets = gson.fromJson(json, Toilets::class.java)

                        val database = DatabaseHelper(activity, null)


                        for (toilet in toilets.features) {
                            if (!database.NoDuplicationAllowed(toilet.properties.OBJECTID!!)) {
                                database.addToilet(
                                    toilet.properties.OBJECTID,
                                    toilet.properties.STRAAT,
                                    toilet.properties.HUISNUMMER,
                                    toilet.properties.BETALEND,
                                    toilet.properties.POSTCODE,
                                    toilet.geometry.coordinates?.get(0),
                                    toilet.geometry.coordinates?.get(1),
                                    toilet.properties.CATEGORIE,
                                    toilet.properties.OMSCHRIJVING,
                                    toilet.properties.DOELGROEP,
                                    toilet.properties.INTEGRAAL_TOEGANKELIJK,
                                    toilet.properties.LUIERTAFEL,
                                    toilet.properties.EXTRA_INFO_PUBLIEK,
                                    toilet.properties.CONTACTGEGEVENS,
                                    toilet.properties.OPENINGSUREN_OPM
                                )
                            } else (println("${toilet.properties.OBJECTID} is een duplicate!"))


                        }


                    }
                }
            }
            )
        }

        fun getDataWithoutPermission() {

            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://geodata.antwerpen.be/arcgissql/rest/services/P_Portal/portal_publiek1/MapServer/8/query?outFields=*&where=1%3D1&f=geojson")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("httperror: ", e.toString())
                }

                override fun onResponse(call: Call, response: Response) {

                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")


                        val json = response.body!!.string()
                        val gson = GsonBuilder().create()
                        var toilets = gson.fromJson(json, Toilets::class.java)

                        for (toilet in toilets.features) {
                            var test: DoubleArray = doubleArrayOf(
                                toilet.geometry.coordinates?.get(1)!!,
                                toilet.geometry.coordinates?.get(0)!!
                            )
                            var geo: Geometry? = Geometry(test)

                            var props: Properties? = Properties(
                                toilet.properties.OBJECTID,
                                toilet.properties.STRAAT,
                                toilet.properties.HUISNUMMER,
                                toilet.properties.POSTCODE,
                                toilet.properties.BETALEND,
                                toilet.properties.CATEGORIE,
                                toilet.properties.OMSCHRIJVING,
                                toilet.properties.DOELGROEP,
                                toilet.properties.LUIERTAFEL,
                                toilet.properties.INTEGRAAL_TOEGANKELIJK,
                                toilet.properties.EXTRA_INFO_PUBLIEK,
                                toilet.properties.CONTACTGEGEVENS,
                                toilet.properties.OPENINGSUREN_OPM
                            )
                            var id = 0
                            toilets2.add(Toilet(id, geo!!, props!!))
                            toilets3 = toilets2.distinctBy { it.properties.OBJECTID } as ArrayList<Toilet>


                        }
                    }

                }

            }
            )
        }

        fun getToiletsWithoutPermission(): ArrayList<Toilet>{
            getDataWithoutPermission()
            println("TOILETS21: " + toilets2.count())
            return toilets3
        }
    }
}




class Toilet (val id: Int,val geometry: Geometry, val properties: Properties)
class Geometry(val coordinates: DoubleArray?)
class Properties(
    var OBJECTID: Int?,
    var STRAAT: String?,
    var HUISNUMMER: String?,
    var POSTCODE: Int?,
    var BETALEND: String?,
    var CATEGORIE: String?,
    var OMSCHRIJVING: String?,
    var DOELGROEP: String?,
    var LUIERTAFEL: String?,
    var INTEGRAAL_TOEGANKELIJK: String?,
    var EXTRA_INFO_PUBLIEK: String?,
    var CONTACTGEGEVENS: String?,
    var OPENINGSUREN_OPM: String?
)







