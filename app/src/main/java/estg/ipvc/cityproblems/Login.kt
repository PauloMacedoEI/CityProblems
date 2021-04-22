package estg.ipvc.cityproblems

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import estg.ipvc.cityproblems.api.EndPoints
import estg.ipvc.cityproblems.api.ServiceBuilder
import estg.ipvc.cityproblems.api.User
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response


class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()


        val notesButton = findViewById<Button>(R.id.goToNotes)
        notesButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }


        val loginButton = findViewById<Button>(R.id.buttonLogin)
        loginButton.setOnClickListener {

            val usernameEditText = findViewById<EditText>(R.id.enterUsername)
            val passwordEditText = findViewById<EditText>(R.id.enterPassword)

            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.verificarUsers(username = username, password = password)

            call.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()!!

                        val intent = Intent(this@Login, MenuLogin::class.java)
                        startActivity(intent)
                        finish()
                        val sessaoIniciada: SharedPreferences = getSharedPreferences(
                                getString(R.string.shared_preferences),
                                Context.MODE_PRIVATE
                        )
                        with(sessaoIniciada.edit()) {
                            putBoolean(getString(R.string.login), true)
                            putString(getString(R.string.username), username)
                            putInt(getString(R.string.id), user.id)
                            apply()
                        }
                    }
                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@Login, "Este utilizador/password não existe", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
