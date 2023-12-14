package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class Shelter_search : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelter_search)

        val backBtn: ImageButton = findViewById(R.id.backBtn);
        backBtn.setOnClickListener {
            onBackPressed();
        }

        var area: String = ""

        val btnGangwon: Button = findViewById(R.id.btnGangwon)
        btnGangwon.setOnClickListener {
            Log.i("selectArea", "강원특별자치도")
            area = "강원특별자치도"
            val intent = Intent(this, Shelter_list::class.java)
            intent.putExtra("selectedArea", area)
            startActivity(intent)
        }
        val btnBusan: Button = findViewById(R.id.btnBusan)
        btnBusan.setOnClickListener {
            Log.i("selectArea", "부산광역시")
            area = "부산광역시"
            val intent = Intent(this, Shelter_list::class.java)
            intent.putExtra("selectedArea", area)
            startActivity(intent)
        }
        val btnGwangju: Button = findViewById(R.id.btnGwangju)
        btnGwangju.setOnClickListener {
            Log.i("selectArea", "광주광역시")
            area = "광주광역시"
            val intent = Intent(this, Shelter_list::class.java)
            intent.putExtra("selectedArea", area)
            startActivity(intent)
        }
        val btnGyeongsang: Button = findViewById(R.id.btnGyeongsang)
        btnGyeongsang.setOnClickListener {
            Log.i("selectArea", "경상북도")
            area = "경상북도"
            val intent = Intent(this, Shelter_list::class.java)
            intent.putExtra("selectedArea", area)
            startActivity(intent)
        }
        val btnJeonla: Button = findViewById(R.id.btnJeonla)
        btnJeonla.setOnClickListener {
            Log.i("selectArea", "전라남도")
            area = "전라남도"
            val intent = Intent(this, Shelter_list::class.java)
            intent.putExtra("selectedArea", area)
            startActivity(intent)
        }
        val btnUlsan: Button = findViewById(R.id.btnUlsan)
        btnUlsan.setOnClickListener {
            Log.i("selectArea", "울산광역시")
            area = "울산광역시"
            val intent = Intent(this, Shelter_list::class.java)
            intent.putExtra("selectedArea", area)
            startActivity(intent)
        }


    }

}