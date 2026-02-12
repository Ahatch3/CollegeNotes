package com.example.collegenotes

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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


        val className = intent.getStringExtra("CLASS_NAME") ?: "Untitled Class"

        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        titleTextView.text = className

        val note = findViewById<LinearLayout>(R.id.Note)

        note.setOnClickListener {
            val intent = Intent(this, NoteEditorScreen::class.java)
            startActivity(intent)
        }
    }
}