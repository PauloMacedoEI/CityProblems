package estg.ipvc.cityproblems.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import estg.ipvc.cityproblems.HomeActivity
import estg.ipvc.cityproblems.R
import estg.ipvc.cityproblems.entities.Note
import estg.ipvc.cityproblems.viewModel.NoteViewModel

//class NoteAdapter internal constructor(context: Context, private val callbackInterface: HomeActivity) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(){
//class//////
class NoteAdapter(private val listener: exemple) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NotesComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val current = getItem(position)
        holder.bindTitle(current.title)
        holder.bindDescription(current.description)
//        val id: Int? = current.id
//        holder.noteItemView3.setOnClickListener {
//            delete(current.id)
//        }
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val noteItemView: TextView = itemView.findViewById(R.id.textTitulo)
        private val noteItemView2: TextView = itemView.findViewById(R.id.textDescricao)


        init {
            itemView.findViewById<Button>(R.id.buttonRemover).setOnClickListener(this)
            itemView.findViewById<Button>(R.id.buttonEditar).setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v?.findViewById<Button>(R.id.buttonRemover)?.isClickable == true) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.deleteClick(position)
                }
            }
            if (v?.findViewById<Button>(R.id.buttonEditar)?.isClickable == true) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.editClick(position)
                }
            }
        }

//        fun bindId(id: Int?){
//            noteItemView3.text = id.toString()
//        }

        fun bindTitle(title: String?) {
            noteItemView.text = title

        }
        fun bindDescription(description: String?) {
            noteItemView2.text = description
        }


    }

    interface exemple {
        fun deleteClick(position: Int)

        fun editClick(position: Int)
    }

    class NotesComparator : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
//class NoteAdapter internal constructor(
//    context: Context
//) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
//
//    private val inflater: LayoutInflater = LayoutInflater.from(context)
//    private var notes = emptyList<Note>()
//
//    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val noteItenView: TextView = itemView.findViewById(R.id.textView)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
//        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
//        return NoteViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
//        val current = notes[position]
//        holder.noteItenView.text = current.title
//        holder.noteItenView.text = current.description
//    }
//
//    internal fun setNotes(notes: List<Note>){
//        this.notes = notes
//        notifyDataSetChanged()
//    }
//
//    override fun getItemCount() = notes.size
//    }