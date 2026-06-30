package com.notes.notesproxmlviews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

/**
 * NoteAdapter — RecyclerView adapter backed by Firestore data.
 *
 * Uses FirestoreRecyclerAdapter to automatically listen for real-time
 * updates from the Firestore "my_notes" collection and display them
 * in a RecyclerView. Each note shows its title, a content preview,
 * and timestamp. Tapping a note opens NoteDetailsActivity in edit mode.
 */
class NoteAdapter(
    options: FirestoreRecyclerOptions<Note>,
    private val context: Context
) : FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder>(options) {

    /**
     * ViewHolder for individual note items.
     * Binds the TextViews and ImageView from recycler_note_item.xml.
     */
    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.note_title_text_view)
        val contentTextView: TextView = itemView.findViewById(R.id.note_content_text_view)
        val timestampTextView: TextView = itemView.findViewById(R.id.note_timestamp_text_view)
        val thumbnailCard: View = itemView.findViewById(R.id.note_thumbnail_card)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.note_thumbnail_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_note_item, parent, false)
        return NoteViewHolder(view)
    }

    /**
     * Binds a Note model to the ViewHolder.
     * Sets title, content preview, and formatted timestamp.
     * Sets a click listener to open NoteDetailsActivity in edit mode.
     */
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int, model: Note) {
        holder.titleTextView.text = model.title
        holder.contentTextView.text = model.content

        // Format the timestamp (if available)
        holder.timestampTextView.text = if (model.timestamp != null) {
            Utility.timestampToString(model.timestamp)
        } else {
            ""
        }

        // Load thumbnail if imageUrl exists
        if (!model.imageUrl.isNullOrEmpty()) {
            holder.thumbnailCard.visibility = View.VISIBLE
            com.bumptech.glide.Glide.with(context)
                .load(model.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.thumbnailImageView)
        } else {
            holder.thumbnailCard.visibility = View.GONE
        }

        // Click listener — open NoteDetailsActivity in edit mode
        holder.itemView.setOnClickListener {
            val intent = Intent(context, NoteDetailsActivity::class.java)
            intent.putExtra("title", model.title)
            intent.putExtra("content", model.content)
            intent.putExtra("imageUrl", model.imageUrl)
            // Get the Firestore document ID for this note
            intent.putExtra("docId", snapshots.getSnapshot(position).id)
            context.startActivity(intent)
        }
    }
}
