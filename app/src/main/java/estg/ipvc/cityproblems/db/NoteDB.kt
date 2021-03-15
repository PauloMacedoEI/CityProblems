package estg.ipvc.cityproblems.db

import android.content.Context
import androidx.room.CoroutinesRoom
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import estg.ipvc.cityproblems.dao.NoteDao
import estg.ipvc.cityproblems.entities.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Note::class), version = 6, exportSchema = false)
public abstract class NoteDB : RoomDatabase() {

    abstract fun NoteDao(): NoteDao

    private class NoteDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch{
                    populateDatabase(database.NoteDao())
//                    var noteDao = database.NoteDao()
//
//                    noteDao.deleteAll()
//                    var note = Note(1, "Titulo1", "Descricao1")
//                    noteDao.insert(note)
//                    note = Note(2, "Titulo2", "Descricao2")
//                    noteDao.insert(note)
                }
            }
        }

        suspend fun populateDatabase(noteDao: NoteDao) {
            // Delete all content here.
            noteDao.deleteAll()

            var note = Note(1, "Titulo1", "Descricao1")
            noteDao.insert(note)
            note = Note(2, "Titulo2", "Descricao2")
            noteDao.insert(note)

        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NoteDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NoteDB {
//            val tempInstance = INSTANCE
//            if(tempInstance != null){
//                return tempInstance
//            }
//            synchronized(this){
                // if the INSTANCE is not null, then return it,
                // if it is, then create the database
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        NoteDB::class.java,
                        "notes_database",
                    ).addCallback(NoteDatabaseCallback(scope)).build()
//                            .fallbackToDestructiveMigration().addCallback(WordDatabaseCallback(scope))
                    INSTANCE = instance
                    instance
                }
        }
    }
}