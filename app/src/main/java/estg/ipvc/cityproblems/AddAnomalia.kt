package estg.ipvc.cityproblems

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import estg.ipvc.cityproblems.api.Anomalia
import estg.ipvc.cityproblems.api.EndPoints
import estg.ipvc.cityproblems.api.ServiceBuilder
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddAnomalia : AppCompatActivity() {
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDesc: EditText
    private lateinit var spinnerTextType: Spinner



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_anomalia)
        supportActionBar?.hide()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val spinner: Spinner = findViewById(R.id.type_spinner)
        ArrayAdapter.createFromResource(
                this,
                R.array.type_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

    }

    fun submitAnomalia(view: View) {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    var id: Int? = 0
                    val sessaoIniciada: SharedPreferences = getSharedPreferences(
                            getString(R.string.shared_preferences),
                            Context.MODE_PRIVATE
                    )

                    id = sessaoIniciada.all[getString(R.string.id)] as Int?

                    editTextTitle = findViewById(R.id.anomalia_title)
                    editTextDesc = findViewById(R.id.anomalia_description)
                    spinnerTextType = findViewById(R.id.type_spinner)

                    val titulo = editTextTitle.text.toString()
                    val descricao = editTextDesc.text.toString()
                    val tipo = spinnerTextType.selectedItem
                    val foto = ""
                    val user_id = id
                    val latitude = lastLocation.latitude
                    val longitude = lastLocation.longitude

                    Log.i("latitude", latitude.toString())
                    Log.i("longitude", longitude.toString())
                    Log.i("login_id", user_id.toString())
                    Log.i("titulo", titulo)
                    Log.i("tipo", tipo.toString())
                    Log.i("descricao", descricao)

                    val request = ServiceBuilder.buildService(EndPoints::class.java)
                    val call = request.insertAnomalia(titulo,descricao,tipo.toString(),foto,latitude.toString(),longitude.toString(),user_id.toString().toInt())



                    call.enqueue(object : Callback<Anomalia> {
                        override fun onResponse(call: Call<Anomalia>, response: Response<Anomalia>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@AddAnomalia, "Adicionado Com Sucesso!", Toast.LENGTH_SHORT).show()

                                val intent = Intent(this@AddAnomalia, Map::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<Anomalia>?, t: Throwable?) {
                            Toast.makeText(applicationContext, t!!.message, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
        }
    }
}