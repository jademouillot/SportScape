package fr.isen.mouillot.sportscape

import android.content.Intent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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
                onClick = {  },
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
}