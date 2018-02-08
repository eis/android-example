package com.github.eis.myandroidapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.eis.myandroidapp.home.HomeFragment
import com.github.eis.myandroidapp.map.GPSTracker
import com.github.eis.myandroidapp.map.MapsFragment
import com.github.eis.myandroidapp.slide.ScreenSlidePagerActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private var currentShownFragment: Fragment? = null
    private val mapFragment = MapsFragment.newInstance()
    private val homeFragment = HomeFragment.newInstance()

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        Log.d(TAG, "before getMapAsync")
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment.onMapReadyFunc = { googleMap -> this.onMapReady(googleMap) }
        Log.d(TAG, "onCreate done")
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                showFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                showFragment(mapFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {

                startActivity(Intent(this, ScreenSlidePagerActivity::class.java))

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun showFragment(frag: Fragment) {
        Log.d(TAG, "showFragment")
        if (frag.equals(currentShownFragment)) return
        val ft = supportFragmentManager.beginTransaction()
        currentShownFragment?.let { ft.remove(currentShownFragment)}

        Log.d(TAG, "showFragment: add " + frag.tag)
        ft.add(R.id.container, frag, frag.tag)
        ft.commit()
        currentShownFragment = frag
        Log.d(TAG, "showFragment done")
    }

    private val MY_PERMISSIONS_REQUEST_LOCATION = 999

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
     fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady")
        mMap = googleMap

        // If using both network and gps locations, only fine_location permission is needed
        // to have both. To use only gps, one would use ACCESS_COARSE_LOCATION.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION)
        } else {
            val gps = GPSTracker(this)
            if (!gps.canGetLocation()) {
                showSettingsAlert()
            }
            gps.registerForLocationUpdates()

            val currLocation = toLatLng(gps.location)

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 17f))
            // zoom values are between 2 and 21, inclusive
            // 21 for max zoom
            val circle = drawCircle(mMap, currLocation)
            gps.registerOnLocation({location ->
                location?.let { circle.setCenter(LatLng(location.latitude, location.longitude)) } })
        }
    }

    private fun toLatLng(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }


    private fun drawCircle(googleMap: GoogleMap, point: LatLng): Circle {

        // Instantiating CircleOptions to draw a circle around the marker
        val circleOptions = CircleOptions()

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(5.0)

        // Border color of the circle
        circleOptions.strokeColor(Color.BLUE);

        // Fill color of the circle
        circleOptions.fillColor(Color.BLUE);

        // Border width of the circle
        circleOptions.strokeWidth(2f);

        // Adding the circle to the GoogleMap
        return googleMap.addCircle(circleOptions)

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    // permission was granted, yay!
                    onMapReady(mMap)

                } else {
                    // permission denied, boo!
                }
                return
            }

        // Add other 'when' lines to check for other
        // permissions this app might request.

            else -> {
                // Ignore all other requests.
            }
        }
    }

    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     */
    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(this)

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings")

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?")

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            this.startActivity(intent)
        })

        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", { dialog, which -> dialog.cancel() })

        // Showing Alert Message
        alertDialog.show()
    }

}
