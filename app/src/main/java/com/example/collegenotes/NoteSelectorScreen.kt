package com.example.collegenotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
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


        val container = findViewById<LinearLayout>(R.id.noteContainer)
        val db = FirebaseFirestore.getInstance()

        db.collection("notes")
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
                    container.addView(item)
                }
            }


        fun addClassToLayout(noteName: String) {
            val item = layoutInflater.inflate(R.layout.item_note, container, false)
            val titleText = item.findViewById<TextView>(R.id.noteTitleText)

            titleText.text = noteName

            item.setOnClickListener {
                val intent = Intent(this, NoteSelectorScreen::class.java)
                intent.putExtra("NOTE_NAME", noteName)
                startActivity(intent)
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
                            "name" to noteName
                        )

                        db.collection("notes")
                            .add(noteFolder)
                            .addOnSuccessListener {
                                Log.d("FIRESTORE", "Class saved successfully!")
                                addClassToLayout(noteName)
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