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



        val container = findViewById<LinearLayout>(R.id.classContainer)
        val db = FirebaseFirestore.getInstance()

        db.collection("classes")
            .get()
            .addOnSuccessListener{
                documents ->
                    for (doc in documents){
                        val className = doc.getString("name") ?: "Untitled Class"
                        val classCode = doc.getString("code") ?: "--"

                        val item = layoutInflater.inflate(R.layout.item_class, container, false)

                        val titleText = item.findViewById<TextView>(R.id.titleText)
                        val subtitleText = item.findViewById<TextView>(R.id.subtitleText)
                        titleText.text = className
                        subtitleText.text = classCode

                        item.setOnClickListener {
                            val intent = Intent(this, NoteSelectorScreen::class.java)
                            intent.putExtra("CLASS_NAME", className)
                            startActivity(intent)
                        }
                        container.addView(item)
                    }
            }

        fun addClassToLayout(className: String, classCode: String) {
            val item = layoutInflater.inflate(R.layout.item_class, container, false)
            val titleText = item.findViewById<TextView>(R.id.titleText)
            val subtitleText = item.findViewById<TextView>(R.id.subtitleText)

            titleText.text = className
            subtitleText.text = classCode

            item.setOnClickListener {
                val intent = Intent(this, NoteSelectorScreen::class.java)
                intent.putExtra("CLASS_NAME", className)
                startActivity(intent)
            }

            container.addView(item)
        }

        val addButton = findViewById<Button>(R.id.addClassButton)

        addButton.setOnClickListener {

            val dialogView = layoutInflater.inflate(R.layout.dialog_add_class, null)
            val classNameInput = dialogView.findViewById<EditText>(R.id.inputClassName)
            val classCodeInput = dialogView.findViewById<EditText>(R.id.inputClassCode)

            AlertDialog.Builder(this)
                .setTitle("Add New Class")
                .setView(dialogView)
                .setPositiveButton("Add") {dialog, _ ->
                    val className = classNameInput.text.toString()
                    val classCode = classNameInput.text.toString().ifBlank { "--" }

                    if (className.isNotBlank()) {
                        val db = FirebaseFirestore.getInstance()
                        val classFolder = hashMapOf(
                            "name" to className,
                            "code" to classCode
                        )

                        db.collection("classes")
                            .add(classFolder)
                            .addOnSuccessListener {
                                Log.d("FIRESTORE", "Class saved successfully!")
                                addClassToLayout(className, classCode)
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