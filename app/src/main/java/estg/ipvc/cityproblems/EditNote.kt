package estg.ipvc.cityproblems

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import estg.ipvc.cityproblems.application.NoteApplication
import estg.ipvc.cityproblems.viewModel.NoteViewModel
import estg.ipvc.cityproblems.viewModel.NoteViewModelFactory

class EditNote : AppCompatActivity() {
    private lateinit var editTitle1View: EditText
    private lateinit var editDescription1View: EditText

    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((application as NoteApplication).repository)
    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        editTitle1View = findViewById(R.id.edit_title1)
        editDescription1View = findViewById(R.id.edit_description1)

        val position = intent.getIntExtra("position", 0)

        noteViewModel.allNotes.observe(this) { notes -> editTitle1View.text = SpannableStringBuilder(notes[position].title)
            editDescription1View.text = SpannableStringBuilder(notes[position].description)
        }

        val button = findViewById<Button>(R.id.buttonSave1)
        button.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editTitle1View.text) || TextUtils.isEmpty(editDescription1View.text)){
                setResult(Activity.RESULT_CANCELED, replyIntent)
                noteViewModel.allNotes.observe(this, {notes->
                    editTitle1View.text = SpannableStringBuilder(notes[position].title)
                    editDescription1View.text = SpannableStringBuilder(notes[position].description)
                    Toast.makeText(
                            applicationContext,
                            R.string.cant_be_null,
                            Toast.LENGTH_LONG).show()
                })
            }else {
                val title = editTitle1View.text.toString()
                val description = editDescription1View.text.toString()

                replyIntent.putExtra(EXTRA_REPLY_EDIT1, title)
                replyIntent.putExtra(EXTRA_REPLY_EDIT2, description)
                replyIntent.putExtra(EXTRA_REPLY_POSITION, position)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_REPLY_EDIT1 = "com.example.android.wordlistsqledit1.REPLY"
        const val EXTRA_REPLY_EDIT2 = "com.example.android.wordlistsqledit2.REPLY"
        const val EXTRA_REPLY_POSITION = "com.example.android.wordlistsqleditposition.REPLY"

    }
}