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
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
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
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.modernpoopz.databinding.FragmentMapBinding
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
    private var clearButton: Button? = null
    private val urlNominatim = "https://nominatim.openstreetmap.org/"
    private var notificationManager: NotificationManager? = null
    private var mChannel: NotificationChannel? = null

    private var  _binding: FragmentMapBinding? = null

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
            //val task = MyAsyncTask()
            //task.execute(url)
            getAddressOrLocation(url)
        }

        clearButton = view?.findViewById(R.id.clear_button)
        clearButton?.setOnClickListener {
            map?.overlays?.clear()
            // Redraw map
            map?.invalidate()
        }

        // Permissions
        if (hasPermissions()) {
            giveMap()
        }
        else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
        }

    }



    private fun giveMap(){

        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)

        map?.controller?.setZoom(17.0)
        // default = Ellermanstraat 33
        setCenter(GeoPoint(51.23020595, 4.41655480828479), "Campus Ellermanstraat")


        val points = ArrayList<IGeoPoint>();
        val db = DatabaseHelper(requireContext(), null)
        val toilets = db.getToilets()

        var hashMap : HashMap<Int, Toilet>
                = HashMap<Int, Toilet> ()
        for ((counter, toilet) in toilets.withIndex()) {

            hashMap[counter] = toilet

            points.add(
                LabelledGeoPoint(
                    toilet.geometry.coordinates?.get(0)!!,
                    toilet.geometry.coordinates[1],
                     ": " +toilet.properties.STRAAT + " " + toilet.properties.HUISNUMMER
                )
            )
        }

// wrap them in a theme
        val pt = SimplePointTheme(points, true);

// create label style
        val textStyle = Paint();
        textStyle.style = Paint.Style.FILL
        textStyle.color = Color.parseColor("#0000ff")
        textStyle.textAlign = Paint.Align.CENTER
        textStyle.textSize = 70F

// set some visual options for the overlay
// we use here MAXIMUM_OPTIMIZATION algorithm, which works well with >100k points
        val opt = SimpleFastPointOverlayOptions.getDefaultStyle()
            .setAlgorithm(SimpleFastPointOverlayOptions.RenderingAlgorithm.MAXIMUM_OPTIMIZATION)
            .setRadius(25F).setIsClickable(true).setCellSize(15).setTextStyle(textStyle);

// create the overlay with the theme
        val sfpo = SimpleFastPointOverlay(pt, opt);


        /*sfpo.setOnClickListener { pointAdapter: SimpleFastPointOverlay.PointAdapter, i: Int ->
            val intent = Intent(requireContext(), PopUpActivity::class.java)

            intent.putExtra("popuptitle",hashMap[i]!!.properties.STRAAT)
            startActivity(intent)
        }*/


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

    private fun hasPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
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

    private fun setCenter(geoPoint: GeoPoint, name: String) {
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

                    /*createNotification(
                        nielsvandepoel.be.kotslin.R.drawable.ic_menu_compass,
                        "Reverse lookup result",
                        obj.string("display_name")!!,
                        "my_channel_01"
                    )*/
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


}

