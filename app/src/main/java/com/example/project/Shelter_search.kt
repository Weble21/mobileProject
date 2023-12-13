package com.example.project

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.Models.shelter.Row_shelter
import com.example.project.Models.shelter.ShelterResponse
import com.example.project.Models.shelter.TsunamiShelter
import com.example.project.network.ShelterService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Shelter_search : AppCompatActivity() {
    private val numOfRows = 30
    private var pageNo = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelter_search)

        val backBtn: ImageButton = findViewById(R.id.backBtn);
        backBtn.setOnClickListener {
            onBackPressed();
        }

        val selectList: LinearLayout = findViewById(R.id.selectList)

        var area: String = ""

        val btnGangwon: Button = findViewById(R.id.btnGangwon)
        btnGangwon.setOnClickListener {
            Log.i("selectArea", "강원특별자치도")
            area = "강원특별자치도"
            selectList.visibility = View.GONE

        }
        val btnBusan: Button = findViewById(R.id.btnBusan)
        btnBusan.setOnClickListener {
            Log.i("selectArea", "강원특별자치도")
            area = "부산광역시"
            selectList.visibility = View.GONE
        }
        val btnGwangju: Button = findViewById(R.id.btnGwangju)
        btnGwangju.setOnClickListener {
            Log.i("selectArea", "광주광역시")
            area = "광주광역시"
            selectList.visibility = View.GONE
        }
        val btnGyeongsang: Button = findViewById(R.id.btnGyeongsang)
        btnGyeongsang.setOnClickListener {
            Log.i("selectArea", "경상북도")
            area = "경상북도"
            selectList.visibility = View.GONE
        }
        val btnJeonla: Button = findViewById(R.id.btnJeonla)
        btnJeonla.setOnClickListener {
            Log.i("selectArea", "전라남도")
            area = "전라남도"
            selectList.visibility = View.GONE
        }
        val btnUlsan: Button = findViewById(R.id.btnUlsan)
        btnUlsan.setOnClickListener {
            Log.i("selectArea", "울산광역시")
            area = "울산광역시"
            selectList.visibility = View.GONE
        }


        if(area != "") {
            getAPI(area)
        }
    }
    @Override
    private fun getAPI(area: String) {
        if (Constants.isNetworkAvailable(this)) {

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: ShelterService = retrofit
                .create<ShelterService>(ShelterService::class.java)

            fetchMessages(service, area)
        } else {
            Toast.makeText(
                this@Shelter_search,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun fetchMessages(service: ShelterService, area:String) {
        val listCall: Call<ShelterResponse> = service.getMessage(
            Constants.APP_ID, pageNo, numOfRows, "json"
        )
        listCall.enqueue(object : Callback<ShelterResponse> {
            override fun onResponse(
                call: Call<ShelterResponse>,
                response: Response<ShelterResponse>
            ) {
                Log.i("Response", response.toString())
                if (response?.isSuccessful == true) {
                    val messageList: ShelterResponse? = response.body()
                    Log.i("Response Result", "$messageList")
                    val shelterList: List<TsunamiShelter>? = messageList?.tsunamiShelter
                    if (!shelterList.isNullOrEmpty()) {
                        for(tsunamiShelter in shelterList) {
                            val rowShelterList: List<Row_shelter> = tsunamiShelter.rowShelter
                            if (!rowShelterList.isNullOrEmpty()) {
                                val allMessages: MutableList<Row_shelter> = mutableListOf()
                                allMessages.addAll(rowShelterList)
                                val messageListLinearLayout: LinearLayout =
                                    findViewById(R.id.messageList)

                                for (i in 1 until rowShelterList.size) {
                                    val currentRow: Row_shelter = rowShelterList[i]
                                    if (currentRow.sidoName == area) {

                                        val currentRowCreateMsg: String =
                                            "${currentRow.address}\n" +
                                                    "${currentRow.sidoName}\n" +
                                                    "${currentRow.shelNm}\n" +
                                                    "수용가능 인원: ${currentRow.shelAv}\n" +
                                                    "대피소 분류: ${currentRow.shelDivType}"

                                        //View 추가
                                        val params = LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                        )
                                        params.setMargins(60, 0, 60, 50)
                                        val textView = Button(this@Shelter_search).apply {
                                            setBackgroundResource(R.drawable.rounded_background)
                                            layoutParams = params
                                            setPadding(30, 40, 30, 40)
                                            gravity = Gravity.CENTER_VERTICAL
                                        }
                                        textView.text = currentRowCreateMsg
                                        messageListLinearLayout.addView(textView)
                                        messageListLinearLayout.visibility = View.VISIBLE
                                        textView.setOnClickListener {
                                            val webPageView = WebView(this@Shelter_search)
                                            webPageView.loadUrl("https://map.naver.com/p/search/" + "${currentRow.address}")

                                            val layout = LinearLayout(this@Shelter_search)

                                            layout.addView(webPageView)

                                            setContentView(layout)
                                        }

                                    }
                                }
                            } else {
                                Log.e("err", "rowList err")
                            }
                        }
                        pageNo++
                        fetchMessages(service, area)
                    } else {
                        Log.e("err", "disasterMsgList err")
                    }

                } else {
                    val rc = response?.code() ?: -1
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    when (rc) {
                        400 -> {
                            Log.e("Error 400", "Bad Connection : $errorMessage")
                            Toast.makeText(
                                this@Shelter_search,
                                "Error!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        404 -> {
                            Log.e("Error 404", "Not Found : $errorMessage")
                            Toast.makeText(
                                this@Shelter_search,
                                "Error!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Log.e("Error", "Generic Error : $errorMessage")
                            Toast.makeText(
                                this@Shelter_search,
                                "Error!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ShelterResponse>, t: Throwable) {
                Log.e("Errrrrrrr", t!!.message.toString())
                Toast.makeText(
                    this@Shelter_search,
                    "Error!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}