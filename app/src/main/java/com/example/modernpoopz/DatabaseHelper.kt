package com.example.modernpoopz

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.database.getDoubleOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull

class DatabaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {

        val first_query = ("CREATE TABLE " + TABLE_NAME +
                " (" +
                ID_COL + " INTEGER PRIMARY KEY, " +
                OBJECTID_COL + " INTEGER, " +
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
                LUIERTAFEL_COL + " TEXT, " +
                OPENINGSUREN_OPM_COL + " TEXT, " +
                CONTACTGEGEVENS_COL + " TEXT, " +
                EXTRA_INFORMATIE_COL + " TEXT" +
                 ")"
                )

        db.execSQL(first_query)

//
        //
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun dropTable(db: SQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }


    fun addToilet(
        objectId: Int?,
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
        luiertafel: String?,
        extra_informatie: String?,
        contactgegevens: String?,
        openingsuren: String?
       // type: String?
    ){

        val values = ContentValues()

        values.put(OBJECTID_COL, objectId)
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
        values.put(EXTRA_INFORMATIE_COL, extra_informatie)
        values.put(CONTACTGEGEVENS_COL, contactgegevens)
        values.put(OPENINGSUREN_OPM_COL, openingsuren)
        //values.put(TYPE_COL, type)

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
        var objectId: Int?
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
        var integraal_toegankelijk: String?
        var extra_informatie:String?
        var openingsuren:String?
        var contactgegevens:String?


        if(cursor.moveToFirst()) {
            do{
                street = cursor.getStringOrNull( cursor.getColumnIndex(STREET_COL))
                objectId = cursor.getIntOrNull( cursor.getColumnIndex(OBJECTID_COL))
                paying = cursor.getStringOrNull( cursor.getColumnIndex(PAYING_COL))
                housenumber = cursor.getStringOrNull( cursor.getColumnIndex(HOUSENUMBER_COL))
                postcode = cursor.getIntOrNull( cursor.getColumnIndex(POSTCODE_COL))
                longitude = cursor.getDoubleOrNull( cursor.getColumnIndex(LONG_COL))
                latitude = cursor.getDoubleOrNull( cursor.getColumnIndex(LAT_COL))
                category = cursor.getStringOrNull( cursor.getColumnIndex(CATEGORY_COL))
                omschrijving = cursor.getStringOrNull( cursor.getColumnIndex(OMSCHRIJVING_COL))
                doelgroep = cursor.getStringOrNull( cursor.getColumnIndex(DOELGROEP_COL))
                luiertafel = cursor.getStringOrNull( cursor.getColumnIndex(LUIERTAFEL_COL))
                integraal_toegankelijk = cursor.getStringOrNull( cursor.getColumnIndex(INTEGRAAL_TOEGANKELIJK_COL))
                extra_informatie = cursor.getStringOrNull( cursor.getColumnIndex( EXTRA_INFORMATIE_COL))
                contactgegevens = cursor.getStringOrNull( cursor.getColumnIndex(CONTACTGEGEVENS_COL))
                openingsuren = cursor.getStringOrNull( cursor.getColumnIndex(OPENINGSUREN_OPM_COL))


                val geometryArray = doubleArrayOf(latitude!!, longitude!!)
                val toilet = Toilet(
                    properties = Properties(objectId, street, housenumber, postcode, paying, category, omschrijving, doelgroep, luiertafel, integraal_toegankelijk,extra_informatie, contactgegevens, openingsuren),
                    geometry = Geometry(geometryArray),
                    id = 0 )
                toilets.add(toilet)

            } while(cursor.moveToNext())

        }
        else{
            println("cursor.movetofirst is false")
        }
        return toilets

    }

    fun NoDuplicationAllowed(id: Int): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor?

        cursor = db.rawQuery("select * from $TABLE_NAME where objectid =$id", null)
        if (cursor.count  <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }



    companion object{


        private val DATABASE_NAME = "ModernPoopz"


        private var DATABASE_VERSION = 29

        val TABLE_NAME = "Toilets"

        val ID_COL = "id"

        val OBJECTID_COL = "objectid"

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

        val CONTACTGEGEVENS_COL = "contactgegevens"

        val EXTRA_INFORMATIE_COL = "EXTRA_INFO_PUBLIEK"

        val OPENINGSUREN_OPM_COL = "OPENINGSUREN_OPM"

    }
}