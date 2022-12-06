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
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
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
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.modernpoopz.Toilets.Companion.getToiletsFromApi
import com.example.modernpoopz.databinding.FragmentMapBinding
import com.google.android.gms.common.api.ResolvableApiException
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
import org.osmdroid.views.overlay.*
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

    private var searchField: EditText? = null
    private var searchButton: Button? = null
    private val urlNominatim = "https://nominatim.openstreetmap.org/"


    var PERMISSION_ID = 1
    private var  _binding: FragmentMapBinding? = null
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_CHECK_SETTINGS: Int = 61124

    companion object{
        var userLat: Double = 51.23020595
        var userLong: Double = 4.41655480828479
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        return inflater.inflate(R.layout.fragment_map, container, false)

    }

    fun reload(){
        var frg: Fragment? = null
        frg = getFragmentManager()?.findFragmentByTag("mapTag")
        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
        if (frg != null) {
            ft.detach(frg)
        }
        if (frg != null) {
            ft.attach(frg)
        }
        ft.commit()
        println("REFRESH HAPPEND")
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

            getLocation()


        }
        else {
            defaultLocation()
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 100
            )
        }
        giveMap()

    }



    private fun defaultLocation(){
        setCenter(GeoPoint(51.23020595, 4.41655480828479), "Ellermanstraat")

    }

    private fun giveMap(){

        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)

        map.setMultiTouchControls(true);

        map?.controller?.setZoom(17.0)

        val db = DatabaseHelper(requireContext(), null)
        val toilets = db.getToilets()


        var markerList = ArrayList<OverlayItem>()
        var hash : HashMap<Int, Toilet> = HashMap()
        for ((count, toilet) in toilets.withIndex()) {


            var markerItem =OverlayItem(

                toilet.properties.STRAAT,
                "", GeoPoint(toilet.geometry.coordinates?.get(0)!!,
                    toilet.geometry.coordinates?.get(1)!!
                )


            )

            var newMarker: Drawable = this.resources.getDrawable(R.drawable.ic_marker_foreground)
            markerItem.setMarker(newMarker)
            markerList.add(markerItem)
            hash[count] = toilet

        }
        val markerOverlay = ItemizedOverlayWithFocus(
            markerList,
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem?> {
                override fun onItemSingleTapUp(c: Int, item: OverlayItem?): Boolean {

                    val t = toilets[c]
                    if(t.properties.STRAAT != null){
                        Toast.makeText(context, t.properties.STRAAT, Toast.LENGTH_SHORT).show()
                    }else{
                        return false

                    }


                    return true
                }
                override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                    val toilet = toilets[index]
                    val intent = Intent(context, DetailView::class.java)
                    intent.putExtra("straat", toilet.properties.STRAAT)
                    intent.putExtra("huisnummer", toilet.properties.HUISNUMMER)
                    intent.putExtra("postcode", toilet.properties.POSTCODE)
                    intent.putExtra("betalend", toilet.properties.BETALEND)
                    intent.putExtra("disabled", toilet.properties.INTEGRAAL_TOEGANKELIJK)
                    intent.putExtra("target", toilet.properties.DOELGROEP)
                    intent.putExtra("description", toilet.properties.OMSCHRIJVING)
                    intent.putExtra("category", toilet.properties.CATEGORIE)
                    intent.putExtra("extra_informatie", toilet.properties.EXTRA_INFO_PUBLIEK)
                    intent.putExtra("openingsuren", toilet.properties.OPENINGSUREN_OPM)
                    intent.putExtra("contactgegevens", toilet.properties.CONTACTGEGEVENS)

                    if(toilet.geometry.coordinates!![0] != null && toilet.geometry.coordinates[1]!! != null){
                        intent.putExtra("lat", toilet.geometry.coordinates[0])
                        intent.putExtra("long", toilet.geometry.coordinates[1])
                    }
                    context?.startActivity(intent)

                    return true
                }
            }
            ,context

        )
        map.overlays.add(markerOverlay)
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
        val marker = Marker(map)
        marker.position = geoPoint
        marker.title = name
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        map?.overlays?.add(marker)

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
    private fun getLocation() {
        locationClient =
            LocationServices.getFusedLocationProviderClient(context?.applicationContext!!)

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {

                    for (location in locationResult.locations) {
                        println("lat!!" + location.latitude)
                        println("long!!"+location.longitude)
                        if (location.latitude != null && location.longitude != null) {
                            println("lat2!!" + location.latitude)
                            println("long2!!"+location.longitude)
                            LocationHelper.lat = location.latitude
                            userLat = location.latitude
                            userLong = location.longitude
                            LocationHelper.long = location.longitude
                            setCenter(
                                GeoPoint(location.latitude, location.longitude), "user location"
                            )
                            stopUpdates()
                        }

                    }
                }
            }
            startUpdates()

        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {

                try {

                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {



                }
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun startUpdates() {
        locationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopUpdates() {
        locationClient.removeLocationUpdates(locationCallback)
    }



    }

private operator fun GeoPoint.invoke(map: MapView) {

}


