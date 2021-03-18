package estg.ipvc.cityproblems

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText

class AddNote : AppCompatActivity() {

    private lateinit var editTitleView: EditText
    private lateinit var editDescriptionView: EditText


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        editTitleView = findViewById(R.id.edit_title)
        editDescriptionView = findViewById(R.id.edit_description)

        val button = findViewById<Button>(R.id.buttonSave)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editTitleView.text)){
                setResult(Activity.RESULT_CANCELED, replyIntent)
            }else {
                val title = editTitleView.text.toString()
                val description = editDescriptionView.text.toString()
                replyIntent.putExtra(EXTRA_REPLY, title)
                replyIntent.putExtra(EXTRA_REPLY1, description)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
        const val EXTRA_REPLY1 = "com.example.android.wordlistsql1.REPLY"

    }
}