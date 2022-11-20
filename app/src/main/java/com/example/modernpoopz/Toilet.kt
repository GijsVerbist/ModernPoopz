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
            val toiletList = ArrayList<Toilet?>()

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


                        val json = response.body!!.string()

                        val gson = GsonBuilder().create()
                        val toilets = gson.fromJson(json, Toilets::class.java)

                        val database = DatabaseHelper(activity, null)

                        for (toilet in toilets.features) {

                            database.addToilet(
                                toilet.straat,
                                toilet.huisnummer,
                                toilet.betalend,
                                toilet.postcode,
                                toilet.long,
                                toilet.lat,
                                toilet.categorie,
                                toilet.omschrijving,
                                toilet.doelgroep,
                                toilet.integraal_toegangelijk,
                                toilet.luiertafel,
                                )


                        }


                    }
                }
            }
            )
        }
    }
}
class Toilet(var straat: String?,
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
             var type: String?)



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






