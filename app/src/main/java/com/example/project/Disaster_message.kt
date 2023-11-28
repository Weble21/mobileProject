package com.example.project

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Disaster_message : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disaster_message)

        val backBtn: ImageButton = findViewById(R.id.backBtn);
        backBtn.setOnClickListener{
            onBackPressed();
        }

        getAPI()
    }

    @Override
    private fun getAPI() {
        if(Constants.isNetworkAvailable(this)) {
            Toast.makeText(
                this@Disaster_message,
                "인터넷이 연결되었습니다",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this@Disaster_message,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}