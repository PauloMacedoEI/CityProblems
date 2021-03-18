package estg.ipvc.cityproblems.viewModel

import android.app.Application
import androidx.lifecycle.*
import estg.ipvc.cityproblems.db.NoteDB
import estg.ipvc.cityproblems.db.NoteRepository
import estg.ipvc.cityproblems.entities.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

//    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>> = repository.allNotes.asLiveData()

    fun insert(note: Note) = viewModelScope.launch {
        repository.insert(note)
    }

    fun deleteById(note: Note) = viewModelScope.launch {
        repository.deleteById(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        repository.updateNote(note)
    }

//    init {
//        val notesDao = NoteDB.getDatabase(application, viewModelScope).NoteDao()
//        repository = NoteRepository(notesDao)
//        allNotes = repository.allNotes
//    }
}

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}