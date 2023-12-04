package com.example.project

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Map_myLocation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_mylocation)

        val backBtn: Button = findViewById(R.id.backBtn);
        backBtn.setOnClickListener {
            onBackPressed();
        }
    }
}