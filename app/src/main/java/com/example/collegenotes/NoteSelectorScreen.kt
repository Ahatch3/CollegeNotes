package com.example.collegenotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class NoteSelectorScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_note_selector_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish() // go back to the previous activity
        }

        fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId == android.R.id.home) {
                onBackPressed() // or finish() if you prefer
                return true
            }
            return super.onOptionsItemSelected(item)
        }


        val container = findViewById<LinearLayout>(R.id.noteContainer)
        val classId = intent.getStringExtra("CLASS_ID")
        val db = FirebaseFirestore.getInstance()

        db.collection("notes")
            .whereEqualTo("classId", classId)
            .get()
            .addOnSuccessListener{
                    documents ->
                for (doc in documents){
                    val noteName = doc.getString("title") ?: "Untitled Note"
                    val noteId = doc.id

                    val item = layoutInflater.inflate(R.layout.item_note, container, false)

                    val titleText = item.findViewById<TextView>(R.id.noteTitleText)
                    titleText.text = noteName


                    item.setOnClickListener {
                        val intent = Intent(this, NoteEditorScreen::class.java)
                        intent.putExtra("NOTE_NAME", noteName)
                        intent.putExtra("NOTE_ID", noteId)
                        startActivity(intent)
                    }
                    item.setOnLongClickListener {
                        AlertDialog.Builder(this)
                            .setTitle("Delete Note?")
                            .setMessage("Are you sure you want to delete this note?")
                            .setPositiveButton("Confirm") { dialog, _ ->
                                val db = FirebaseFirestore.getInstance()
                                db.collection("notes").document(noteId)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d("FIRESTORE", "Note deleted successfully!")
                                        container.removeView(item) // remove from UI immediately
                                    }
                                    .addOnFailureListener {
                                        Log.d("FIRESTORE", "Failed to delete note...")
                                    }
                            }
                            .setNegativeButton("Cancel", null)
                            .show()

                        true // return true to indicate the long-click is handled
                    }
                    container.addView(item)
                }
            }


        fun addNoteToLayout(noteName: String, noteId: String) {
            val item = layoutInflater.inflate(R.layout.item_note, container, false)
            val titleText = item.findViewById<TextView>(R.id.noteTitleText)

            titleText.text = noteName

            item.setOnClickListener {
                val intent = Intent(this, NoteEditorScreen::class.java)
                intent.putExtra("NOTE_NAME", noteName)
                intent.putExtra("NOTE_ID", noteId)
                startActivity(intent)
            }
            item.setOnLongClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Delete Note?")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Confirm") { dialog, _ ->
                        val db = FirebaseFirestore.getInstance()
                        db.collection("notes").document(noteId)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("FIRESTORE", "Note deleted successfully!")
                                container.removeView(item) // remove from UI immediately
                            }
                            .addOnFailureListener {
                                Log.d("FIRESTORE", "Failed to delete note...")
                            }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()

                true // return true to indicate the long-click is handled
            }
            container.addView(item)
        }

        val addButton = findViewById<Button>(R.id.addNoteButton)

        addButton.setOnClickListener {

            val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
            val noteNameInput = dialogView.findViewById<EditText>(R.id.inputNoteName)

            AlertDialog.Builder(this)
                .setTitle("Add New Note")
                .setView(dialogView)
                .setPositiveButton("Add") {dialog, _ ->
                    val noteName = noteNameInput.text.toString()

                    if (noteName.isNotBlank()) {
                        val db = FirebaseFirestore.getInstance()
                        val noteFolder = hashMapOf(
                            "title" to noteName,
                            "classId" to classId,
                            "timestamp" to System.currentTimeMillis()
                        )

                        db.collection("notes")
                            .add(noteFolder)
                            .addOnSuccessListener { doc ->
                                Log.d("FIRESTORE", "Class saved successfully!")
                                addNoteToLayout(noteName, doc.id)
                            }
                            .addOnFailureListener {
                                Log.d("FIRESTORE", "Failed saving class...")
                            }
                    }

                }
                .setNegativeButton("Cancel", null)
                .show()


        }
    }
}