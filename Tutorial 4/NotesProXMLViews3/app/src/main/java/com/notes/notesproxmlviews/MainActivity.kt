package com.notes.notesproxmlviews

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

/**
 * MainActivity — Main screen showing the user's notes list.
 *
 * Displays all notes from Firestore using a RecyclerView backed by
 * FirestoreRecyclerAdapter. Provides a FAB to add new notes and a
 * menu button with logout functionality.
 */
class MainActivity : AppCompatActivity() {
    var addNoteBtn: FloatingActionButton? = null
    var recyclerView: RecyclerView? = null
    var menuBtn: ImageButton? = null
    var noteAdapter: NoteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addNoteBtn = findViewById<FloatingActionButton?>(R.id.add_note_btn)
        recyclerView = findViewById<RecyclerView?>(R.id.recyler_view)
        menuBtn = findViewById<ImageButton?>(R.id.menu_btn)

        addNoteBtn!!.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(
                Intent(
                    this@MainActivity, NoteDetailsActivity::class.java
                )
            )
        })
        menuBtn!!.setOnClickListener(View.OnClickListener { v: View? -> showMenu() })

        setupRecyclerView()
    }

    /**
     * Sets up the RecyclerView with a FirestoreRecyclerAdapter.
     * Queries notes ordered by timestamp descending (newest first).
     */
    private fun setupRecyclerView() {
        // Query notes ordered by timestamp (newest first)
        val query: Query = Utility.getCollectionReferenceForNotes()
            .orderBy("timestamp", Query.Direction.DESCENDING)

        // Build FirestoreRecyclerOptions from the query
        val options: FirestoreRecyclerOptions<Note> = FirestoreRecyclerOptions.Builder<Note>()
            .setQuery(query, Note::class.java)
            .build()

        // Set up the RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter(options, this)
        recyclerView!!.adapter = noteAdapter
    }

    /**
     * Shows the popup menu with a Logout option.
     * On logout, signs out from Firebase and navigates to LoginActivity.
     */
    fun showMenu() {
        val popupMenu = PopupMenu(this@MainActivity, menuBtn)
        popupMenu.menu.add("Logout")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->
            if (menuItem.title == "Logout") {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
            false
        }
    }

    /**
     * Start listening for Firestore changes when activity becomes visible.
     */
    override fun onStart() {
        super.onStart()
        noteAdapter?.startListening()
    }

    /**
     * Stop listening for Firestore changes when activity is no longer visible.
     */
    override fun onStop() {
        super.onStop()
        noteAdapter?.stopListening()
    }

    /**
     * Re-start listening when returning to this activity (e.g., after editing a note).
     */
    override fun onResume() {
        super.onResume()
        noteAdapter?.notifyDataSetChanged()
    }
}