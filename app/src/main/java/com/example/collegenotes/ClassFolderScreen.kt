package com.example.collegenotes

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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

        val toolbar = findViewById<Toolbar>(R.id.toolbarEdit)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
             // go back to the previous activity
        }

        fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (item.itemId == android.R.id.home) {
                onBackPressed() // or finish() if you prefer
                return true
            }
            return super.onOptionsItemSelected(item)
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
                        val classId = doc.id

                        val item = layoutInflater.inflate(R.layout.item_class, container, false)

                        val titleText = item.findViewById<TextView>(R.id.titleText)
                        val subtitleText = item.findViewById<TextView>(R.id.subtitleText)
                        titleText.text = className
                        subtitleText.text = classCode

                        item.setOnClickListener {
                            val intent = Intent(this, NoteSelectorScreen::class.java)
                            intent.putExtra("CLASS_NAME", className)
                            intent.putExtra("CLASS_ID", classId)
                            startActivity(intent)
                        }
                        item.setOnLongClickListener {
                            AlertDialog.Builder(this)
                                .setTitle("Delete Note?")
                                .setMessage("Are you sure you want to delete this note?")
                                .setPositiveButton("Confirm") { dialog, _ ->
                                    val db = FirebaseFirestore.getInstance()
                                    db.collection("classes").document(classId)
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
            item.setOnLongClickListener { doc ->
                val classId: String = doc.id.toString()
                AlertDialog.Builder(this)
                    .setTitle("Delete Note?")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Confirm") { dialog, _ ->
                        val db = FirebaseFirestore.getInstance()
                        db.collection("classes").document(classId)
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