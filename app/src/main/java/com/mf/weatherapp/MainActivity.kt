package com.mf.weatherapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mf.weatherapp.ui.theme.WeatherAppTheme
import com.mf.weatherapp.ui.views.screens.SearchScreen
import dagger.Module
import dagger.Provides
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext

//@Module
@AndroidEntryPoint()
class MainActivity : ComponentActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 400

//    @Provides
//    @ApplicationContext var applicationContext: ContextCompat
//    @ApplicationContext
//    @set:Provides
//    lateinit var context: Context

//    private fun setAppContext(){
//        context = applicationContext as Context
//    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, proceed with the functionality
            onPermissionGranted()
        } else {
            // Permission denied, handle accordingly
            onPermissionDenied()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    NavGraph(innerPadding)
                }
            }
        }

        checkLocationPermission()
//        setAppContext()
    }

    private fun checkLocationPermission() {
        // Step 3: Check if permission is already granted
        when {
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED  &&
                    ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted
                onPermissionGranted()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                // Step 4: Optionally, show rationale and then request the permission
                requestLocationPermission()
            }

//            else -> {
//                // Step 5: Request the permission if it's not granted and no rationale needed
//                requestLocationPermission()
//            }
        }
    }

//    private fun askForLocationPermissions(){
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//            LOCATION_PERMISSION_REQUEST_CODE
//        )
//
//    }

    // Request permission using the registered launcher
    private fun requestLocationPermission() {
//        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    // Optional: Show rationale dialog
    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Needed")
            .setMessage("This app needs the Location permission to work properly.")
            .setPositiveButton("OK") { _, _ ->
                // Request the permission after showing rationale
                requestLocationPermission()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // Handle permission granted
    private fun onPermissionGranted() {
        // Perform actions that require the location permission
        Toast.makeText(this, "You have enabled location permissions", Toast.LENGTH_SHORT).show()
    }

    // Handle permission denied
    private fun onPermissionDenied() {
        // Inform the user that the permission was denied
        Toast.makeText(this, "Please restart the app and grant location permissions", Toast.LENGTH_SHORT).show()
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    WeatherAppTheme {
//        Greeting("Android")
//    }
//}

@Composable()
fun NavGraph(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "search_screen") {
        composable("search_screen") { SearchScreen(innerPadding) }
    }
}