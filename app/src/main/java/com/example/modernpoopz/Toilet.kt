package com.example.modernpoopz

import android.content.Context
import org.json.JSONException
import org.json.JSONObject


class Toilet(
            var straat: String,
             var huisnummer: String,
             var postcode: Int,
             var betalend: String,
             var categorie: String,
             var omschrijving: String,
             var doelgroep: String,
             var luiertafel: String,
             var integraal_toegangelijk: String


) {

companion object{
    fun getToiletsFromFile(filename: String, context: Context?): ArrayList<Toilet?>{
        val toiletList = ArrayList<Toilet?>()

        try {
            // Load data
            val jsonString = loadJsonFromAsset(filename, context)
            val json = JSONObject(jsonString)
            val toilets = json.getJSONArray("features")

            // Get Recipe objects from data
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
}



