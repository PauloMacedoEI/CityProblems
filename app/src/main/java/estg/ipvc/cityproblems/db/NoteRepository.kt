package estg.ipvc.cityproblems.db

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import estg.ipvc.cityproblems.dao.NoteDao
import estg.ipvc.cityproblems.entities.Note
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: Flow<List<Note>> = noteDao.getAlphabetizedNote()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread

    suspend fun insert(note: Note){
        noteDao.insert(note)
    }

    suspend fun deleteById(note: Note){
        note.id?.let {
            noteDao.deleteById(it)
        }
    }
}