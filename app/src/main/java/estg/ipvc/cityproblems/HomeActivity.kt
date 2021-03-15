package estg.ipvc.cityproblems

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import estg.ipvc.cityproblems.adapters.NoteAdapter
import estg.ipvc.cityproblems.application.NoteApplication
import estg.ipvc.cityproblems.entities.Note
import estg.ipvc.cityproblems.viewModel.AddNote
import estg.ipvc.cityproblems.viewModel.NoteViewModel
import estg.ipvc.cityproblems.viewModel.NoteViewModelFactory
//import androidx.activity.result.contract.ActivityResultContracts


class HomeActivity : AppCompatActivity() {

    //    private lateinit var noteViewModel: NoteViewModel
    private val newNoteActivityRequestCode = 1
    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((application as NoteApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = NoteAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
//        val adapter = NoteAdapter(this)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(this)

//        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
//        noteViewModel.allNotes.observe(this, {notes ->
//            notes?.let { adapter.setNotes(it) }
//        })

        noteViewModel.allNotes.observe(this, { notes ->
            // Update the cached copy of the words in the adapter.
            notes?.let { adapter.submitList(it) }
        })


        val fab = findViewById<FloatingActionButton>(R.id.buttonAdicionarNota)
        fab.setOnClickListener {
            val intent = Intent(this@HomeActivity, AddNote::class.java)
            startActivityForResult(intent, newNoteActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        if (requestCode == newNoteActivityRequestCode && resultCode == Activity.RESULT_OK) {            // There are no request codes
            intentData?.getStringExtra(AddNote.EXTRA_REPLY)?.let { title ->
                intentData.getStringExtra(AddNote.EXTRA_REPLY1)?.let { desc ->
                    val note = Note(title = title, description = desc)
                    noteViewModel.insert(note)
                }
            }
        } else {
            Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
            ).show()
        }


//    }

    }
}