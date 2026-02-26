package com.example.collegenotes

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class NoteEditorScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_input)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editorMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbarEdit)
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


        val noteContentContainer = findViewById<EditText>(R.id.noteContent)
        val noteTitleContainer = findViewById<EditText>(R.id.noteTitle)
        var classIdText = ""
        val noteId = intent.getStringExtra("NOTE_ID")
        if (noteId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("notes") // match your save collection
                .document(noteId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val content = doc.getString("content") ?: ""
                        val title = doc.getString("title") ?: ""
                        val classId = doc.getString("classId") ?: ""
                        noteContentContainer.setText(content)
                        noteTitleContainer.setText(title)
                        classIdText = classId
                    }
                }
                .addOnFailureListener {
                    Log.d("FIRESTORE", "Failed to fetch note")
                }
        } else {
            Log.d("INTENT", "No NOTE_ID passed to editor")
        }


        val saveButton = findViewById<Button>(R.id.saveButton)

        saveButton.setOnClickListener {
            val titleText = noteTitleContainer.text.toString()
            val contentText = noteContentContainer.text.toString()


            val note = hashMapOf(
                "title" to titleText,
                "content" to contentText,
                "timestamp" to System.currentTimeMillis(),
                "classId" to classIdText
            )

            if (noteId != null){
                val db = FirebaseFirestore.getInstance()
                db.collection("notes")
                    .document(noteId)
                    .set(note)
                    .addOnSuccessListener {
                        Log.d("FIRESTORE", "Note saved successfully!")
                    }
                    .addOnFailureListener {
                        Log.d("FIRESTORE", "Failed saving note...")
                    }
            }
            else
            {
                Log.d("INTENT", "No NOTE_ID passed to editor")
            }
        }
    }
}
