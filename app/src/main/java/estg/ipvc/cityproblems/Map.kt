package estg.ipvc.cityproblems

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import estg.ipvc.cityproblems.api.Anomalia
import estg.ipvc.cityproblems.api.EndPoints
import estg.ipvc.cityproblems.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class Map : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
//
    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var results = FloatArray(1)


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        supportActionBar?.hide()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val addAnomaliaButton = findViewById<FloatingActionButton>(R.id.buttonAdicionarAnomalia)
        addAnomaliaButton.setOnClickListener {
            val intent = Intent(this@Map, AddAnomalia::class.java)
            startActivity(intent)
        }

        val switchDistancia1000 = findViewById<Switch>(R.id.switch1000)
        switchDistancia1000.setOnCheckedChangeListener { _, isCheked ->
            if (isCheked) {
                filtroDistancia1000()
            } else {
                onMapReady(map)
            }
        }

        val switchDistancia5000 = findViewById<Switch>(R.id.switch5000)
        switchDistancia5000.setOnCheckedChangeListener { _, isCheked ->
            if (isCheked) {
                filtroDistancia5000()
            } else {
                onMapReady(map)
            }
        }
    }

    fun calcularDistancia(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0]
    }

    fun filtroDistancia1000() {

        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location

                    map.clear()

                    val request = ServiceBuilder.buildService(EndPoints::class.java)
                    val call = request.getAllAnomalias()
                    var position: LatLng


                    call.enqueue(object : Callback<List<Anomalia>> {
                        override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {

                            if (response.isSuccessful) {

                                val anomalias = response.body()!!

                                for (anomalia in anomalias) {

                                    position = LatLng(anomalia.latitude, anomalia.longitude)

                                    if (calcularDistancia(location.latitude, location.longitude, anomalia.latitude, anomalia.longitude) <= 1000) {
                                        map.addMarker(MarkerOptions()
                                                .position(position)
                                                .title(anomalia.titulo)
                                                .snippet("Distancia: " + results[0].roundToInt() + " metros")
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))

                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<List<Anomalia>>, t: Throwable) {
                            Toast.makeText(this@Map, getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }


    fun filtroDistancia5000() {

        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location

                    map.clear()

                    val request = ServiceBuilder.buildService(EndPoints::class.java)
                    val call = request.getAllAnomalias()
                    var position: LatLng


                    call.enqueue(object : Callback<List<Anomalia>> {
                        override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {

                            if (response.isSuccessful) {

                                val anomalias = response.body()!!

                                for (anomalia in anomalias) {

                                    position = LatLng(anomalia.latitude, anomalia.longitude)

                                    if (calcularDistancia(location.latitude, location.longitude, anomalia.latitude, anomalia.longitude) <= 5000) {
                                        map.addMarker(MarkerOptions()
                                                .position(position)
                                                .title(anomalia.titulo)
                                                .snippet("Distancia: " + results[0].roundToInt() + " metros")
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)))

                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<List<Anomalia>>, t: Throwable) {
                            Toast.makeText(this@Map, getString(R.string.error), Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.mapType = GoogleMap.MAP_TYPE_SATELLITE

        setUpMap()

        val sessaoIniciada: SharedPreferences = getSharedPreferences(
                getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
        )

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getAllAnomalias()

        call.enqueue(object : Callback<List<Anomalia>> {
            override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {

                if (response.isSuccessful) {
                    val anomalias = response.body()!!

                    for(i in anomalias){
                        val latlong = LatLng(i.latitude,i.longitude)
                        //criar markers
                        if(i.user_id.equals(sessaoIniciada.all[getString(R.string.id)])) {
                            map.addMarker(MarkerOptions()
                                    .position(latlong)
                                    .title(i.titulo)
                                    .snippet(i.descricao)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))



                        }else{
                            map.addMarker(MarkerOptions().position(latlong).title(i.titulo).snippet(i.descricao).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<Anomalia>>, t: Throwable) {
                Toast.makeText(this@Map, "Markers Errado!", Toast.LENGTH_SHORT).show()
            }

        })

        // premissoes de localização
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        map.isMyLocationEnabled = true

        //zoom na localização atual do telemovel
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
            }
        }


        map.setOnMarkerClickListener {
            map.setInfoWindowAdapter(AnomaliaInfo(this))
            false
        }
    }

    override fun onMarkerClick(p0: Marker?) = false

    private fun setUpMap() {
        //permissões de localização
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
    }
}