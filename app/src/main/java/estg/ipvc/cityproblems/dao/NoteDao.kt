package estg.ipvc.cityproblems.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import estg.ipvc.cityproblems.entities.Note

@Dao
interface NoteDao {
    @Query("Select * from note_table ORDER BY title ASC")
    fun getAlphabetizedNote(): LiveData<List<Note>>

    @Query("Select * from note_table WHERE id == :id")
    fun getNoteByTitle(id: Int): LiveData<List<Note>>

    @Update
    suspend fun updateNote(note: Note)

    @Query("Delete from note_table")
    suspend fun deleteAll()

    @Query("Delete from note_table where id == :id")
    suspend fun deleteById(id: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)
}