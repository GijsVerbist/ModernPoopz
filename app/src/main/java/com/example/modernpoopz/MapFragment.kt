package com.example.modernpoopz

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.service.quicksettings.Tile
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.modernpoopz.databinding.FragmentMapBinding
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration

import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme
import java.io.File
import java.net.URL
import java.net.URLEncoder

import java.util.ArrayList



class MapFragment : Fragment() {
    private lateinit var map: MapView

    private var mMyLocationOverlay: ItemizedOverlay<OverlayItem>? = null
    private var items = ArrayList<OverlayItem>()
    private var searchField: EditText? = null
    private var searchButton: Button? = null
    private val urlNominatim = "https://nominatim.openstreetmap.org/"
    private var notificationManager: NotificationManager? = null
    private var mChannel: NotificationChannel? = null

    var PERMISSION_ID = 1
    private var  _binding: FragmentMapBinding? = null
    private lateinit var LocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    val LOCATION_PERMISSION_REQUEST_CODE = 1







    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)

    }

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val osmConfig = Configuration.getInstance()
        osmConfig.userAgentValue = activity?.packageName
        val basePath = File(activity?.cacheDir?.absolutePath, "osmdroid")
        osmConfig.osmdroidBasePath = basePath
        val tileCache = File(osmConfig.osmdroidBasePath, "tile")
        osmConfig.osmdroidTileCache = tileCache

        map = view?.findViewById(R.id.mapview)!!
        searchField = view?.findViewById(R.id.search_txtview)
        searchButton = view?.findViewById(R.id.search_button)
        searchButton?.setOnClickListener {
            val url = URL(
                urlNominatim + "search?q=" + URLEncoder.encode(
                    searchField?.text.toString(),
                    "UTF-8"
                ) + "&format=json"
            )
            it.hideKeyboard()

            getAddressOrLocation(url)
        }

        if (locationPermission()) {
            giveMap()

        }
        else {
            defaultLocation()
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
        }

    }

    private fun defaultLocation(){
        setCenter(GeoPoint(51.23020595, 4.41655480828479), "Ellermanstraat")

    }

    private fun giveMap(){

        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)

        map.setMultiTouchControls(true);

        map?.controller?.setZoom(17.0)

        setCenter(GeoPoint(51.23020595, 4.41655480828479), "Ellermanstraat")


        val points = ArrayList<IGeoPoint>();
        val db = DatabaseHelper(requireContext(), null)
        val toilets = db.getToilets()

        var hash : HashMap<Int, Toilet> = HashMap()
        for ((counter, toilet) in toilets.withIndex()) {

            hash[counter] = toilet

            points.add(
                LabelledGeoPoint(
                    toilet.geometry.coordinates?.get(0)!!,
                    toilet.geometry.coordinates[1],
                     ": " +toilet.properties.STRAAT + " " + toilet.properties.HUISNUMMER
                )
            )
        }


        val pt = SimplePointTheme(points, true);


        val textStyle = Paint();
        textStyle.style = Paint.Style.STROKE
        textStyle.color = Color.parseColor("#0000ff")
        textStyle.textAlign = Paint.Align.CENTER
        textStyle.textSize = 23F


        val opt = SimpleFastPointOverlayOptions.getDefaultStyle()
            .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
            .setRadius(25F).setIsClickable(true).setCellSize(10).setTextStyle(textStyle);

        val sfpo = SimpleFastPointOverlay(pt, opt);

        map?.overlays?.add(sfpo);

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun locationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }



    private fun addMarker(geoPoint: GeoPoint, name: String) {
        items.add(OverlayItem(name, name, geoPoint))
        mMyLocationOverlay = ItemizedIconOverlay(items, null, view?.context)
        map?.overlays?.add(mMyLocationOverlay)
    }

    open fun setCenter(geoPoint: GeoPoint, name: String) {
        map?.controller?.setCenter(geoPoint)
        addMarker(geoPoint, name)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAddressOrLocation(url: URL) {

        var searchReverse = false

        Thread(Runnable {
            searchReverse = (url.toString().indexOf("reverse", 0, true) > -1)
            val client = OkHttpClient()
            val response: Response
            val request = Request.Builder()
                .url(url)
                .build()
            response = client.newCall(request).execute()

            val result = response.body!!.string()

            activity?.runOnUiThread {
                val jsonString = StringBuilder(result!!)

                val parser: Parser = Parser.default()

                if (searchReverse) {
                    val obj = parser.parse(jsonString) as JsonObject

                } else {
                    val array = parser.parse(jsonString) as JsonArray<JsonObject>

                    if (array.size > 0) {
                        val obj = array[0]
                        // mapView center must be updated here and not in doInBackground because of UIThread exception
                        val geoPoint = GeoPoint(
                            obj.string("lat")!!.toDouble(),
                            obj.string("lon")!!.toDouble()
                        )
                        setCenter(geoPoint, obj.string("display_name")!!)
                    } else {
                        Toast.makeText(
                            activity?.applicationContext,
                            "Address not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }).start()
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
            }
        }
    }


    fun getLocation(){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            this.activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            }
            return


        }



    }

}

