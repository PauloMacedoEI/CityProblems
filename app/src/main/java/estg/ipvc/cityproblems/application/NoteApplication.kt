package estg.ipvc.cityproblems.application

import android.app.Application
import estg.ipvc.cityproblems.db.NoteDB
import estg.ipvc.cityproblems.db.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NoteApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { NoteDB.getDatabase(this, applicationScope) }
    val repository by lazy { NoteRepository(database.NoteDao()) }
}