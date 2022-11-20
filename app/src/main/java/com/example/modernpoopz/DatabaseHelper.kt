package com.example.modernpoopz

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull

class   DatabaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val first_query = ("CREATE TABLE " + TABLE_NAME +
                " (" +
                ID_COL + " INTEGER PRIMARY KEY, " +
                STREET_COL + " TEXT, " +
                HOUSENUMBER_COL + " TEXT, " +
                POSTCODE_COL + " TEXT, " +
                PAYING_COL + " TEXT, " +
                LONG_COL + " DOUBLE, " +
                LAT_COL + " DOUBLE, " +
                CATEGORY_COL + " TEXT, " +
                OMSCHRIJVING_COL + " TEXT, " +
                DOELGROEP_COL + " TEXT, " +
                INTEGRAAL_TOEGANKELIJK_COL + " TEXT, " +
                LUIERTAFEL_COL + " TEXT" +
                 ")"
                )

        db.execSQL(first_query)

    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }


    fun addToilet(
        street: String?,
        houseNumber: String?,
        paying: String?,
        postcode: Int?,
        long: Double?,
        lat: Double?,
        category: String?,
        omschrijving: String?,
        doelgroep: String?,
        integraal_toegankelijk: String?,
        luiertafel: String?
    ){

        val values = ContentValues()

        values.put(STREET_COL, street)
        values.put(HOUSENUMBER_COL, houseNumber)
        values.put(PAYING_COL, paying)
        values.put(POSTCODE_COL, postcode)
        values.put(LONG_COL, long)
        values.put(LAT_COL, lat)
        values.put(CATEGORY_COL, category)
        values.put(OMSCHRIJVING_COL, omschrijving)
        values.put(DOELGROEP_COL, doelgroep)
        values.put(INTEGRAAL_TOEGANKELIJK_COL, integraal_toegankelijk)
        values.put(LUIERTAFEL_COL, luiertafel)

        val database = this.writableDatabase

        database.insert(TABLE_NAME, null, values)

        database.close()

    }
    @SuppressLint("Range")
    fun getToilets(): ArrayList<Toilet>{

        val database = this.readableDatabase

        val cursor: Cursor?

        val toilets : ArrayList<Toilet> = ArrayList()

        try {

            cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null)

        }catch (e: Exception){
            e.printStackTrace()
            database.execSQL("SELECT * FROM" + TABLE_NAME)
            return ArrayList()
        }
        var street: String?
        var type: String?
        var paying: String?
        var housenumber: String?
        var postcode: Int?
        var longitude: Double?
        var latitude: Double?
        var category: String?
        var omschrijving: String?
        var doelgroep: String?
        var luiertafel: String?
        var integraal_toegangelijk: String?


        if(cursor.moveToFirst()) {
            do{
                street = cursor.getStringOrNull( cursor.getColumnIndex("straat"))
                paying = cursor.getStringOrNull( cursor.getColumnIndex("betalend"))
                housenumber = cursor.getStringOrNull( cursor.getColumnIndex("huisnummer"))
                postcode = cursor.getIntOrNull( cursor.getColumnIndex("postcode"))
                longitude = cursor.getDoubleOrNull( cursor.getColumnIndex("longitude"))
                latitude = cursor.getDoubleOrNull( cursor.getColumnIndex("latitude"))
                category = cursor.getStringOrNull( cursor.getColumnIndex("categorie"))
                omschrijving = cursor.getStringOrNull( cursor.getColumnIndex("omschrijving"))
                doelgroep = cursor.getStringOrNull( cursor.getColumnIndex("doelgroep"))
                luiertafel = cursor.getStringOrNull( cursor.getColumnIndex("luiertafel"))
                integraal_toegangelijk = cursor.getStringOrNull( cursor.getColumnIndex("integraal_toegangelijk"))
                type = cursor.getStringOrNull( cursor.getColumnIndex("type"))

                val toilet = Toilet(street, housenumber, postcode, longitude, latitude, paying, category, omschrijving, doelgroep, luiertafel, integraal_toegangelijk, type)
                toilets.add(toilet)

                println("toilet straat " + toilet.straat)
            } while(cursor.moveToNext())

        }
        else{
            println("cursor.movetofirst is false")
        }
        return toilets

    }

    companion object{



        private val DATABASE_NAME = "ModernPoopz"


        private val DATABASE_VERSION = 1


        val TABLE_NAME = "Toilets"


        val ID_COL = "id"


        val STREET_COL = "straat"

        val HOUSENUMBER_COL = "huisnummer"

        val PAYING_COL = "betalend"

        val POSTCODE_COL = "postcode"

        val LONG_COL = "longitude"

        val LAT_COL = "latitude"

        val CATEGORY_COL = "categorie"

        val OMSCHRIJVING_COL = "omschrijving"

        val DOELGROEP_COL = "doelgroep"

        val INTEGRAAL_TOEGANKELIJK_COL = "integraal_toegankelijk"

        val LUIERTAFEL_COL = "luiertafel"
    }
}