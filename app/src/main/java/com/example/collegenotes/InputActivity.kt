package com.example.collegenotes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class InputActivity : AppCompatActivity() {

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
    }
}
