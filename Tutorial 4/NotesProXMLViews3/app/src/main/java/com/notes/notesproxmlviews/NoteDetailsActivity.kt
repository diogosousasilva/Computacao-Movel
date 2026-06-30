package com.notes.notesproxmlviews

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp.Companion.now
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.FirebaseStorage

class NoteDetailsActivity : AppCompatActivity() {
    var titleEditText: EditText? = null
    var contentEditText: EditText? = null
    var saveNoteBtn: ImageButton? = null
    var pageTitleTextView: TextView? = null
    var title: String? = null
    var content: String? = null
    var docId: String? = null
    var isEditMode: Boolean = false
    var deleteNoteTextViewBtn: TextView? = null

    // Image integration fields
    var noteImageView: ImageView? = null
    var noteImageCard: View? = null
    var removeImageBtn: ImageButton? = null
    var selectImageBtn: View? = null

    private var selectedImageUri: Uri? = null
    private var isImageRemoved = false
    private var imageUrl: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            isImageRemoved = false
            noteImageView?.setImageURI(uri)
            noteImageCard?.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_details)

        titleEditText = findViewById<EditText?>(R.id.notes_title_text)
        contentEditText = findViewById<EditText?>(R.id.notes_content_text)
        saveNoteBtn = findViewById<ImageButton?>(R.id.save_note_btn)
        pageTitleTextView = findViewById<TextView?>(R.id.page_title)
        deleteNoteTextViewBtn = findViewById<TextView?>(R.id.delete_note_text_view_btn)

        // Bind image elements
        noteImageView = findViewById<ImageView>(R.id.note_image_view)
        noteImageCard = findViewById<View>(R.id.note_image_card)
        removeImageBtn = findViewById<ImageButton>(R.id.remove_image_btn)
        selectImageBtn = findViewById<View>(R.id.select_image_btn)

        //receive data
        title = intent.getStringExtra("title")
        content = intent.getStringExtra("content")
        imageUrl = intent.getStringExtra("imageUrl")
        docId = intent.getStringExtra("docId")

        if (docId != null && !docId!!.isEmpty()) {
            isEditMode = true
        }

        titleEditText!!.setText(title)
        contentEditText!!.setText(content)

        // Load existing image if any
        if (!imageUrl.isNullOrEmpty()) {
            noteImageCard?.visibility = View.VISIBLE
            com.bumptech.glide.Glide.with(this)
                .load(imageUrl)
                .into(noteImageView!!)
        }

        if (isEditMode) {
            pageTitleTextView!!.text = getString(R.string.edit_your_note)
            deleteNoteTextViewBtn!!.visibility = View.VISIBLE
        }

        saveNoteBtn!!.setOnClickListener(View.OnClickListener { v: View? -> saveNote() })
        deleteNoteTextViewBtn!!.setOnClickListener(View.OnClickListener { v: View? -> deleteNoteFromFirebase() })

        selectImageBtn?.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        removeImageBtn?.setOnClickListener {
            selectedImageUri = null
            imageUrl = null
            isImageRemoved = true
            noteImageCard?.visibility = View.GONE
        }
    }

    fun saveNote() {
        val noteTitle = titleEditText!!.getText().toString()
        val noteContent = contentEditText!!.getText().toString()
        if (noteTitle.isEmpty()) {
            titleEditText!!.error = "Title is required"
            return
        }

        val note = Note()
        note.setTitle(noteTitle)
        note.setContent(noteContent)
        note.setTimestamp(now())

        if (isImageRemoved) {
            note.setImageUrl(null)
        } else {
            note.setImageUrl(imageUrl)
        }

        if (selectedImageUri != null) {
            uploadImageAndSaveNote(note)
        } else {
            saveNoteToFirebase(note)
        }
    }

    fun uploadImageAndSaveNote(note: Note) {
        saveNoteBtn?.isEnabled = false

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid ?: "anonymous"
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("notes_images/$uid/${System.currentTimeMillis()}.jpg")

        imageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    note.setImageUrl(uri.toString())
                    saveNoteToFirebase(note)
                }.addOnFailureListener { e ->
                    Utility.showToast(this@NoteDetailsActivity, "Failed to get image link: ${e.message}")
                    saveNoteBtn?.isEnabled = true
                }
            }
            .addOnFailureListener { e ->
                Utility.showToast(this@NoteDetailsActivity, "Failed to upload image: ${e.message}")
                saveNoteBtn?.isEnabled = true
            }
    }

    fun saveNoteToFirebase(note: Note) {
        val documentReference: DocumentReference
        if (isEditMode) {
            //update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId.toString())
        } else {
            //create new note
            documentReference = Utility.getCollectionReferenceForNotes().document()
        }

        documentReference.set(note).addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                saveNoteBtn?.isEnabled = true
                if (task.isSuccessful) {
                    //note is added
                    Utility.showToast(this@NoteDetailsActivity, "Note saved successfully")
                    finish()
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while adding note")
                }
            }
        })
    }

    fun deleteNoteFromFirebase() {
        val documentReference: DocumentReference = Utility.getCollectionReferenceForNotes().document(
            docId.toString()
        )
        documentReference.delete().addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    //note is deleted
                    Utility.showToast(this@NoteDetailsActivity, "Note deleted successfully")
                    finish()
                } else {
                    Utility.showToast(this@NoteDetailsActivity, "Failed while deleting note")
                }
            }
        })
    }
}