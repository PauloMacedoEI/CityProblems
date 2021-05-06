package estg.ipvc.cityproblems

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import retrofit2.Callback
import androidx.core.view.isVisible
import estg.ipvc.cityproblems.api.Anomalia
import estg.ipvc.cityproblems.api.EndPoints
import estg.ipvc.cityproblems.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Response

class AnomaliaInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anomalia_info)
        supportActionBar?.hide()

        val sessaoIniciada: SharedPreferences = getSharedPreferences(
                getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
        )

        val userID = sessaoIniciada.getInt("id", 0)

        val anomalia = intent?.getParcelableArrayListExtra<Anomalia>("anomaliaX")


        val titleTextInputLayout = findViewById<EditText>(R.id.info_anomalia_title)
        titleTextInputLayout.text = SpannableStringBuilder(anomalia?.get(0)?.titulo)
        val descTextInputLayout = findViewById<EditText>(R.id.info_anomalia_description)
        descTextInputLayout.text = SpannableStringBuilder(anomalia?.get(0)?.descricao)
        val typeTextInputLayout = findViewById<EditText>(R.id.info_anomalia_type)
        typeTextInputLayout.text = SpannableStringBuilder(anomalia?.get(0)?.tipo)

        val deleteAnomalyButton = findViewById<Button>(R.id.buttonDelete)

        if(userID == anomalia?.get(0)?.user_id) {
            deleteAnomalyButton.isVisible = true
        }

        deleteAnomalyButton.setOnClickListener {
            val builder = AlertDialog.Builder(window.context)
            builder.apply {
                setTitle(R.string.popupTitulo)
                setMessage(R.string.popupMensage)
                setPositiveButton(R.string.popConfirm
                ) { dialog, _ ->
                    // User clicked Yes button
                    val request = ServiceBuilder.buildService(EndPoints::class.java)
                    val call = request.eliminarAnomalia(id = anomalia?.get(0)?.id!!)

                    call.enqueue(object : Callback<Anomalia> {
                        override fun onResponse(call: Call<Anomalia>, response: Response<Anomalia>) {
                            if (response.isSuccessful) {

                                val intent = Intent(this@AnomaliaInfo, Map::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }

                        override fun onFailure(call: Call<Anomalia>?, t: Throwable?) {
                            val intent = Intent(this@AnomaliaInfo, Map::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
                    dialog.dismiss()
                }
                setNegativeButton(R.string.popupRefuse
                ) { dialog, _ ->
                    // User cancelled the dialog
                    dialog.dismiss()
                }
                
            }
            // Create the AlertDialog
            builder.create()
            builder.show()
        }
    }

}
