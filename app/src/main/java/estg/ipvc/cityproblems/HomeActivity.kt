package estg.ipvc.cityproblems

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import estg.ipvc.cityproblems.adapters.NoteAdapter
import estg.ipvc.cityproblems.application.NoteApplication
import estg.ipvc.cityproblems.entities.Note
import estg.ipvc.cityproblems.viewModel.NoteViewModel
import estg.ipvc.cityproblems.viewModel.NoteViewModelFactory
//import androidx.activity.result.contract.ActivityResultContracts


class HomeActivity : AppCompatActivity(), NoteAdapter.exemple {

    //    private lateinit var noteViewModel: NoteViewModel
//    private val newNoteActivityRequestCode = 1
//    private val teste = "teste"

    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((application as NoteApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = NoteAdapter(this)
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
            resultInsertNote.launch(intent)
        }


    }

    private var resultInsertNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->


        if (result.resultCode == Activity.RESULT_OK) {
            val data:Intent?=result.data // There are no request codes
            data?.getStringExtra(AddNote.EXTRA_REPLY)?.let { title ->
                data.getStringExtra(AddNote.EXTRA_REPLY1)?.let { desc ->
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
    }

    override fun deleteClick(position: Int) {
        noteViewModel.allNotes.value?.get(position)?.let {
            noteViewModel.deleteById(it)
        }
    }

    override fun editClick(position: Int) {
        val intent = Intent(this, EditNote::class.java).apply {
            putExtra("position", position)
        }
        resultEditNote.launch(intent)
    }

    private var resultEditNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result->


        if (result.resultCode == Activity.RESULT_OK) {
            val data:Intent?=result.data
            data?.getStringExtra(EditNote.EXTRA_REPLY_EDIT1)?.let { title ->
                data.getStringExtra(EditNote.EXTRA_REPLY_EDIT2)?.let { descricao ->
                    data.getIntExtra(EditNote.EXTRA_REPLY_POSITION, 0).let { id ->
                        val word = Note(id = noteViewModel.allNotes.value?.get(id)?.id, title = title, description= descricao)
                        noteViewModel.update(word)
                    }
                }
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.cant_be_null,
                Toast.LENGTH_LONG).show()
        }
    }
}