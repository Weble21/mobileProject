package com.example.project

import android.os.Bundle
import android.util.Log
import android.net.Uri
import android.view.Gravity
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.Models.shelter.Row_shelter
import com.example.project.Models.shelter.ShelterResponse
import com.example.project.network.ShelterService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Shelter_list: AppCompatActivity() {
    private val numOfRows = 1000
    private var matchedCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelter_list)

        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressed()
        }

        val selectedArea: String? = intent.getStringExtra("selectedArea")

        if (selectedArea != null) {
            getAPI(selectedArea)
        }
    }
    @Override
    private fun getAPI(area: String) {
        if (Constants.isNetworkAvailable(this)) {
            findViewById<TextView>(R.id.message).text = "대피소 정보를 불러오는 중..."
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://www.safetydata.go.kr/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: ShelterService = retrofit
                .create<ShelterService>(ShelterService::class.java)

            fetchMessages(service, area, 1)
        } else {
            findViewById<TextView>(R.id.message).text = "인터넷 연결을 확인해 주세요."
            Toast.makeText(
                this@Shelter_list,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun fetchMessages(service: ShelterService, area: String, pageNo: Int) {
        val listCall: Call<ShelterResponse> = service.getMessage(
            Constants.TSUNAMI_SERVICE_KEY, pageNo, numOfRows, "json"
        )
        listCall.enqueue(object : Callback<ShelterResponse> {
            override fun onResponse(
                call: Call<ShelterResponse>,
                response: Response<ShelterResponse>
            ) {
                val data = response.body()
                if (!response.isSuccessful || data?.header?.resultCode != "00") {
                    Log.e("Shelter_list", "대피소 API 오류: HTTP ${response.code()}, ${data?.header?.resultMsg ?: response.errorBody()?.string()}")
                    findViewById<TextView>(R.id.message).text = "대피소 정보를 불러오지 못했습니다."
                    return
                }

                val messageList: LinearLayout = findViewById(R.id.messageList)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(60, 0, 60, 50) }

                for (shelter: Row_shelter in data.body) {
                    val address = shelter.address ?: shelter.roadAddress.orEmpty()
                    if (!address.startsWith(area)) continue

                    val shelterName = shelter.name.orEmpty().ifBlank { "이름 없는 대피소" }
                    val shelterText = buildString {
                        append(shelterName)
                        append("\n")
                        append(address)
                        shelter.capacity?.let { append("\n수용가능 인원: ${it}명") }
                    }

                    val button = Button(this@Shelter_list).apply {
                        setBackgroundResource(R.drawable.rounded_background)
                        layoutParams = params
                        setPadding(30, 40, 30, 40)
                        gravity = Gravity.CENTER_VERTICAL
                        text = shelterText
                        setOnClickListener {
                            val webPageView = WebView(this@Shelter_list)
                            webPageView.loadUrl("https://map.naver.com/p/search/" + Uri.encode(address))
                            val layout = LinearLayout(this@Shelter_list)
                            layout.addView(webPageView)
                            setContentView(layout)
                        }
                    }
                    messageList.addView(button)
                    matchedCount++
                }

                if (pageNo * numOfRows < data.totalCount && data.body.isNotEmpty()) {
                    fetchMessages(service, area, pageNo + 1)
                } else {
                    findViewById<TextView>(R.id.message).text =
                        if (matchedCount == 0) "해당 지역에 등록된 지진해일 대피소가 없습니다."
                        else "${area} 지진해일 대피소 ${matchedCount}곳"
                }
            }

            override fun onFailure(call: Call<ShelterResponse>, t: Throwable) {
                Log.e("Shelter_list", "대피소 API 요청 실패", t)
                findViewById<TextView>(R.id.message).text = "대피소 정보를 불러오지 못했습니다."
                Toast.makeText(
                    this@Shelter_list,
                    "대피소 정보를 불러오지 못했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}
