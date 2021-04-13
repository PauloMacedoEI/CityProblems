package estg.ipvc.cityproblems

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()


        val NotesButton = findViewById<Button>(R.id.goToNotes)

        NotesButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)

            startActivity(intent)
        }
    }
}