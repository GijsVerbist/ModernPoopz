package com.example.modernpoopz

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class Toilets(

    val features: List<Toilet>


) {

    companion object {
        fun getToiletsFromApi(activity: FragmentActivity){

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
                        println("json!: $json")

                        val gson = GsonBuilder().create()
                        val toilets = gson.fromJson(json, Toilets::class.java)

                        println("toilets!: "+toilets.features.toString())

                        for(toilet in toilets.features){
                           // println("STRAAT!: " + toilet.straat)
                        }

                        val database = DatabaseHelper(activity, null)


                        for (toilet in toilets.features) {
                            println("Straat!: " + toilet.properties.STRAAT)
                            database.addToilet(
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
                                //toilet.properties.type
                                )


                        }


                    }
                }
            }
            )
        }
    }
}
/*class Toilet(var straat: String?,
             var huisnummer: String?,
             var postcode: Int?,
             var long: Double?,
             var lat: Double?,
             var betalend: String?,
             var categorie: String?,
             var omschrijving: String?,
             var doelgroep: String?,
             var luiertafel: String?,
             var integraal_toegangelijk: String?,
             var type: String?)*/

class Toilet (val id: Int,val geometry: Geometry, val properties: Properties)
class Geometry(val coordinates: DoubleArray?)
class Properties(
    var STRAAT: String?,
    var HUISNUMMER: String?,
    var POSTCODE: Int?,
    var BETALEND: String?,
    var CATEGORIE: String?,
    var OMSCHRIJVING: String?,
    var DOELGROEP: String?,
    var LUIERTAFEL: String?,
    var INTEGRAAL_TOEGANKELIJK: String?,
 //   var type: String?
)



        /*try {
            // Load data
            val jsonString = loadJsonFromAsset(filename, context)
            val json = JSONObject(jsonString)
            val toilets = json.getJSONArray("features")


            (0 until toilets.length()).mapTo(toiletList) {
                Toilet(
                    toilets.getJSONObject(it).getString("STRAAT"),
                        toilets.getJSONObject(it).getString("HUISNUMMER"),
                        toilets.getJSONObject(it).getInt("POSTCODE"),
                        toilets.getJSONObject(it).getString("BETALEND"),
                        toilets.getJSONObject(it).getString("CATEGORIE"),
                        toilets.getJSONObject(it).getString("OMSCHRIJVING"),
                        toilets.getJSONObject(it).getString("DOELGROEP"),
                        toilets.getJSONObject(it).getString("INTEGRAAL_TOEGANKELIJK"),
                        toilets.getJSONObject(it).getString("LUIERTAFEL"))

            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return toiletList
    }

    }
}

private fun loadJsonFromAsset(filename: String, context: Context?): String? {
    var json: String? = null

    try {
        val inputStream = context?.assets?.open(filename)
        val size = inputStream?.available()
        val buffer = size?.let { ByteArray(it) }
        inputStream?.read(buffer)
        inputStream?.close()
        json = buffer?.let { String(it, Charsets.UTF_8) }
    } catch (ex: java.io.IOException) {
        ex.printStackTrace()
        return null
    }

    return json
    */






