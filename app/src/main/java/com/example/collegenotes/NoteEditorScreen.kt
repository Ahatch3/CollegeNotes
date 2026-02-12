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

        val input = findViewById<EditText>(R.id.inputText)
        val button = findViewById<Button>(R.id.showButton)
        val output = findViewById<TextView>(R.id.outputText)

        button.setOnClickListener {
            val text = input.text.toString()
            output.text = text
        }

        val db = FirebaseFirestore.getInstance()


        val test = hashMapOf(
            "message" to "Firestore connected"
        )

        db.collection("connection_test")
            .add(test)
            .addOnSuccessListener {
                Log.d("Firestore", "Success")
            }
            .addOnFailureListener {
                Log.e("Firestore", "Failure", it)
            }
    }
}
