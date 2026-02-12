package com.example.collegenotes

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class IntroLoadScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_intro_load)

        val splashImage = findViewById<ImageView>(R.id.splashImage)

        splashImage.setOnClickListener {
            val intent = Intent(this, ClassFolderScreen::class.java)
            startActivity(intent)
        }

    }
}
