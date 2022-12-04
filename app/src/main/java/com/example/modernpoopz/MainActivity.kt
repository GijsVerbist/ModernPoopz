package com.example.modernpoopz

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.example.modernpoopz.Toilets.Companion.getToiletsFromApi


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //needed for sqllite etc
       /* if(storagePermission()){
            //getToiletsFromApi(this)

        }else{
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 100)
        }
        getToiletsFromApi(this)
*/
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)


        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_map, R.id.navigation_list
            )
        )
        if (navController != null) {
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        }




    }
    private fun storagePermission(): Boolean{
        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE )== PackageManager.PERMISSION_GRANTED

    }
}

