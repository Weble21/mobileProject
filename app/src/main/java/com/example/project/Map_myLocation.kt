package com.example.project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Map_myLocation : AppCompatActivity() {
    companion object {
        const val TAG = "MapActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_mylocation)

    }

}