package com.example.project

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val headerMessage: ImageButton = findViewById(R.id.header_message);

        headerMessage.setOnClickListener {
            val intent = Intent(this, Disaster_message::class.java)
            startActivity(intent);
        }
    }
}