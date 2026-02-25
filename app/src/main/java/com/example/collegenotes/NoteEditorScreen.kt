package com.example.collegenotes

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class NoteEditorScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)



        val noteContentContainer = findViewById<EditText>(R.id.noteContent)
        val noteTitleContainer = findViewById<EditText>(R.id.noteTitle)
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
                        noteContentContainer.setText(content)
                        noteTitleContainer.setText(title)
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
                "timestamp" to System.currentTimeMillis()
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
