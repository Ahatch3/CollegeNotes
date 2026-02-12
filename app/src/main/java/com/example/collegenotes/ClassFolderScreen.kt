package com.example.collegenotes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ClassFolderScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_file_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val left_class_button = findViewById<LinearLayout>(R.id.leftClass)
        val right_class_button = findViewById<LinearLayout>(R.id.rightClass)


        left_class_button.setOnClickListener {
            val intent = Intent(this, NoteSelectorScreen::class.java)
            intent.putExtra("CLASS_NAME", "Math 101")
            startActivity(intent)
        }

        right_class_button.setOnClickListener {
            val intent = Intent(this, NoteSelectorScreen::class.java)
            intent.putExtra("CLASS_NAME", "Intro to Programming")
            startActivity(intent)
        }
    }
}