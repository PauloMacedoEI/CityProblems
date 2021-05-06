package estg.ipvc.cityproblems

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
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
////
    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var results = FloatArray(1)


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    @SuppressLint("ResourceType")
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
            finish()
        }

        //Butao Switch Distancia 1000 metros
        val switchDistancia1000 = findViewById<Switch>(R.id.switch1000)
        switchDistancia1000.setOnCheckedChangeListener { _, isCheked ->
            if (isCheked) {
                filtroDistancia1000()
            } else {
                onMapReady(map)
            }
        }

        //Butao Switch Distancia 5000 metros
        val switchDistancia5000 = findViewById<Switch>(R.id.switch5000)
        switchDistancia5000.setOnCheckedChangeListener { _, isCheked ->
            if (isCheked) {
                filtroDistancia5000()
            } else {
                onMapReady(map)
            }
        }

        val layout = findViewById<RelativeLayout>(R.id.layout)

        //Radio buttons filtro dos tipos
        val butaoEstragos = RadioButton(this)
        butaoEstragos.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        butaoEstragos.setText(R.string.damage) //setting text of first radio button
        butaoEstragos.id = 0

        val butaoObras = RadioButton(this)
        butaoObras.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        butaoObras.setText(R.string.construction) //setting text of first radio button
        butaoObras.id = 1

        val butaoTransito = RadioButton(this)
        butaoTransito.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        butaoTransito.setText(R.string.traffic) //setting text of first radio button
        butaoTransito.id = 2

        val butaoAll = RadioButton(this)
        butaoAll.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        butaoAll.setText(R.string.allTypes)
        butaoAll.setChecked(true)//setting text of first radio button
        butaoAll.id = 3

        val radioGroup = RadioGroup(this)
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(50, 0, 0, 0)
        radioGroup.layoutParams = params

        radioGroup.addView(butaoEstragos)
        radioGroup.addView(butaoObras)
        radioGroup.addView(butaoTransito)
        radioGroup.addView(butaoAll)
        layout.addView(radioGroup)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                0 -> getEstragos()
                1 -> getObras()
                2 -> getTransito()
                3 -> onMapReady(map)
            }
        }
    }


    //calcular Distancia
    fun calcularDistancia(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Float {
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0]
    }

    //função calcular distancia até 1000 metros
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
                                                .snippet(getString(R.string.distance)+ " " + results[0].roundToInt() + " " + getString(R.string.meters))
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

    //função calcular distancia até 5000 metros
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
                                                .snippet(getString(R.string.distance)+ " " + results[0].roundToInt() + " " + getString(R.string.meters))
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

    //função para listar todas as anomalias do tipo Estrago
    fun getEstragos() {
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
                    val call = request.getAnomaliaByType(tipo = "Estragos")
                    var position: LatLng

                    val sessaoAuto: SharedPreferences = getSharedPreferences(
                            getString(R.string.shared_preferences), Context.MODE_PRIVATE
                    )

                    call.enqueue(object : Callback<List<Anomalia>> {
                        override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {
                            if (response.isSuccessful) {
                                val anomalias = response.body()!!
                                for (anomalia in anomalias) {
                                    val latlong = LatLng(anomalia.latitude, anomalia.longitude)
                                    if (anomalia.user_id.equals(sessaoAuto.all[getString(R.string.id)])) {
                                        map.addMarker(MarkerOptions()
                                                .position(latlong)
                                                .title(anomalia.titulo)
                                                .snippet(anomalia.tipo)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
                                    } else {
                                        map.addMarker(MarkerOptions().position(latlong).title(anomalia.titulo).snippet(anomalia.tipo).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                                    }
                                }
                            }
                        }
                        override fun onFailure(call: Call<List<Anomalia>>, t: Throwable) {
                            Toast.makeText(this@Map, getString(R.string.filter_error), Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }

    //função para listar todas as anomalias do tipo Obras
    fun getObras() {
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
                    val call = request.getAnomaliaByType(tipo = "Obras")
                    var position: LatLng

                    val sessaoAuto: SharedPreferences = getSharedPreferences(
                            getString(R.string.shared_preferences), Context.MODE_PRIVATE
                    )

                    call.enqueue(object : Callback<List<Anomalia>> {
                        override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {
                            if (response.isSuccessful) {
                                val anomalias = response.body()!!
                                for (anomalia in anomalias) {
                                    val latlong = LatLng(anomalia.latitude, anomalia.longitude)
                                    if (anomalia.user_id.equals(sessaoAuto.all[getString(R.string.id)])) {
                                        map.addMarker(MarkerOptions()
                                                .position(latlong)
                                                .title(anomalia.titulo)
                                                .snippet(anomalia.tipo)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
                                    } else {
                                        map.addMarker(MarkerOptions().position(latlong).title(anomalia.titulo).snippet(anomalia.tipo).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                                    }
                                }
                            }
                        }
                        override fun onFailure(call: Call<List<Anomalia>>, t: Throwable) {
                            Toast.makeText(this@Map, getString(R.string.filter_error), Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }

    //função para listar todas as anomalias do tipo Transito
    fun getTransito() {
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
                    val call = request.getAnomaliaByType(tipo = "Transito")
                    var position: LatLng

                    val sessaoAuto: SharedPreferences = getSharedPreferences(
                            getString(R.string.shared_preferences), Context.MODE_PRIVATE
                    )

                    call.enqueue(object : Callback<List<Anomalia>> {
                        override fun onResponse(call: Call<List<Anomalia>>, response: Response<List<Anomalia>>) {
                            if (response.isSuccessful) {
                                val anomalias = response.body()!!
                                for (anomalia in anomalias) {
                                    val latlong = LatLng(anomalia.latitude, anomalia.longitude)
                                    if (anomalia.user_id.equals(sessaoAuto.all[getString(R.string.id)])) {
                                        map.addMarker(MarkerOptions()
                                                .position(latlong)
                                                .title(anomalia.titulo)
                                                .snippet(anomalia.tipo)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
                                    } else {
                                        map.addMarker(MarkerOptions().position(latlong).title(anomalia.titulo).snippet(anomalia.tipo).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                                    }
                                }
                            }
                        }
                        override fun onFailure(call: Call<List<Anomalia>>, t: Throwable) {
                            Toast.makeText(this@Map, getString(R.string.filter_error), Toast.LENGTH_SHORT).show()
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

        map.clear()
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
                            val marker: Marker? = map.addMarker(MarkerOptions()
                                    .position(latlong)
                                    .title(i.titulo)
                                    .snippet(i.tipo)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
                            marker?.tag = i
                        }else{
                            val marker: Marker? = map.addMarker(MarkerOptions().position(latlong).title(i.titulo).snippet(i.tipo).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                            marker?.tag = i
                        }
                    }
                }
            }
            override fun onFailure(call: Call<List<Anomalia>>, t: Throwable) {
                Toast.makeText(this@Map, R.string.marker_error, Toast.LENGTH_SHORT).show()
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


//        map.setOnMarkerClickListener {
//            map.setInfoWindowAdapter(MarkerInfo(this))
//            false
//        }

        map.setInfoWindowAdapter(MarkerInfo(this))
        //Passar a informação quando a InfoWindow é clicada

        map.setOnInfoWindowClickListener { marker ->
            val anomalia = arrayListOf<Anomalia>()
            anomalia.add(marker.tag as Anomalia)
            val intent = Intent(this, AnomaliaInfo::class.java).apply{
                putExtra("anomaliaX", anomalia)
            }
            startActivity(intent)
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