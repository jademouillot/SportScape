package fr.isen.mouillot.sportscape

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest
import androidx.compose.ui.graphics.Color
import android.widget.Button
import com.google.android.gms.maps.model.LatLngBounds
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // Configure the ComposeView to display the ActionBar
        val actionBarComposeView = findViewById<ComposeView>(R.id.actionBarComposeView)
        actionBarComposeView.setContent {
            ActionMapBar(this) { navigateClass ->
                val intent = Intent(this, navigateClass)
                startActivity(intent)
            }
        }
        // Configuration initiale et demande de permissions
        // Votre code actuel ici
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            mapFragment?.getMapAsync(this)
        }
    }

    // Assurez-vous de surcharger onRequestPermissionsResult pour gérer le résultat de la demande de permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si la permission est accordée, vous pouvez appeler getMapAsync ici
                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                mapFragment?.getMapAsync(this)
            } else {
                // Gérer le cas où la permission est refusée
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        val school = LatLng(43.12061856356527, 5.938851591473798)
        mMap.addMarker(MarkerOptions().position(school).title("Marker at ISEN"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(school, 10f))
        mMap.setMinZoomPreference(10f)
        mMap.setMaxZoomPreference(20f)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true

        val gpxPoints = loadAndParseGpxFile("Vélo Toulon - Cassis.gpx")
        if (gpxPoints.isNotEmpty()) {
            val polylineOptions = com.google.android.gms.maps.model.PolylineOptions()
            gpxPoints.forEach { point ->
                polylineOptions.add(LatLng(point.latitude, point.longitude))
            }
            mMap.addPolyline(polylineOptions)
        }

        if (gpxPoints.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            gpxPoints.forEach { point ->
                builder.include(LatLng(point.latitude, point.longitude))
            }
            val bounds = builder.build()

            // Ajustez selon la taille de votre vue de carte
            val padding = 100 // en pixels
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
        }


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        }
    }


    @Composable
    fun ActionMapBar(context: AppCompatActivity, navigateFunction: (Class<*>) -> Unit) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            IconButton(onClick = { navigateFunction(MainActivity::class.java) },
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.home), contentDescription = "Home", tint = Color(0,0,255), modifier = Modifier.size(24.dp))
            }
            IconButton(
                onClick = { navigateFunction(FindActivity::class.java) },
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.find), contentDescription = "Find", tint = Color(0,0,255), modifier = Modifier.size(24.dp))
            }
            IconButton(
                onClick = { navigateFunction(NewPublicationActivity::class.java) },
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.add), contentDescription = "Add", tint = Color(0,0,255), modifier = Modifier.size(24.dp))
            }
            IconButton(
                onClick = {},
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.map), contentDescription = "Map", tint = Color(0,0,255), modifier = Modifier.size(24.dp))
            }
            IconButton(
                onClick = { navigateFunction(ProfileActivity::class.java) },
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 12.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.profile), contentDescription = "Profile", tint = Color(0,0,255), modifier = Modifier.size(24.dp))
            }
        }
    }
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val REQUEST_CODE_CHOOSE_GPX = 1001
    }
    private fun loadAndParseGpxFile(fileName: String): List<GpxPoint> {
        return parseGpx(assets.open(fileName))
    }
}

data class GpxPoint(val latitude: Double, val longitude: Double)

fun parseGpx(inputStream: InputStream): List<GpxPoint> {
    val points = mutableListOf<GpxPoint>()
    val factory = XmlPullParserFactory.newInstance()
    val parser = factory.newPullParser()

    parser.setInput(inputStream, null)
    var eventType = parser.eventType
    while (eventType != XmlPullParser.END_DOCUMENT) {
        if (eventType == XmlPullParser.START_TAG && (parser.name == "trkpt" || parser.name == "wpt")) {
            val lat = parser.getAttributeValue(null, "lat").toDouble()
            val lon = parser.getAttributeValue(null, "lon").toDouble()
            points.add(GpxPoint(lat, lon))
        }
        eventType = parser.next()
    }
    return points
}