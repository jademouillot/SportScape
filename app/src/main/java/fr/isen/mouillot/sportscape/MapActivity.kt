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

        // Ajoutez un marqueur à school, et déplacez la caméra.
        val school = LatLng(43.12061856356527, 5.938851591473798)
        mMap.addMarker(
            MarkerOptions()
                .position(school)
                .title("Marker at ISEN")
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLng(school))
        // Définissez les préférences de zoom minimum et maximum.
        mMap.setMinZoomPreference(1f) // Niveau de zoom minimum
        mMap.setMaxZoomPreference(25f) // Niveau de zoom maximum

        // Déplacez la caméra avec un niveau de zoom spécifique pour zoomer immédiatement.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(school, 10f)) // Le dernier paramètre est le niveau de zoom

        // Activez les contrôles de zoom UI
        mMap.uiSettings.isZoomControlsEnabled = true

        // Pour permettre aux utilisateurs de zoomer avec des gestes
        mMap.uiSettings.isZoomGesturesEnabled = true
    }
}
