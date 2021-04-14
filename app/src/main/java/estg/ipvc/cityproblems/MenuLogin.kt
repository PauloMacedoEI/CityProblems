package estg.ipvc.cityproblems

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MenuLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_login)

        supportActionBar?.hide()

        //Titulo
        val welcome = findViewById<TextView>(R.id.welcomeUser)
        val sessaoAuto: SharedPreferences = getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )
        welcome.setText("Welcome " + sessaoAuto.getString("username", null)+" !")

        //Butão Notas
        val notesButton = findViewById<Button>(R.id.goToNotes)
        notesButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        //Butão Logout
        val logout = findViewById<Button>(R.id.logout)
        logout.setOnClickListener {
            val sessaoAuto: SharedPreferences = getSharedPreferences(
                getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
            )
            with(sessaoAuto.edit()) {
                clear()
                apply()
            }
            val intent = Intent(this@MenuLogin, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}