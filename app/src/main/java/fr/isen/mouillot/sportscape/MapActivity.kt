package fr.isen.mouillot.sportscape

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap // Corrigé: Déclaré mMap correctement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Obtenez le SupportMapFragment et soyez notifié lorsque la carte est prête à être utilisée.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment // Corrigé: Ajouté une vérification de type sécurisée
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Ajoutez un marqueur à Singapore, et déplacez la caméra.
        val singapore = LatLng(1.35, 103.87)
        mMap.addMarker(
            MarkerOptions()
                .position(singapore)
                .title("Marker in Singapore")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(singapore))
    }
}
