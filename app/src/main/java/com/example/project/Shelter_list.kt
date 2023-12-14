package com.example.project

import android.os.Bundle
import android.util.Log
import android.view.Gravity
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

class Shelter_list: AppCompatActivity() {
    private val numOfRows = 30
    private var pageNo = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shelter_list)

        val backBtn: ImageButton = findViewById(R.id.backBtn);
        backBtn.setOnClickListener {
            onBackPressed();
        }

        val selectedArea: String? = intent.getStringExtra("selectedArea")

        if (selectedArea != null) {
            getAPI(selectedArea)
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
                this@Shelter_list,
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
                                val params = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                params.setMargins(60, 0, 60, 50)
                                for (i in 1 until rowShelterList.size) {
                                    val currentRow: Row_shelter = rowShelterList[i]
                                    if (currentRow.sidoName == area) {

                                        val currentRowCreateMsg: String =
                                            "${currentRow.address}\n" +
                                                    "${currentRow.shelNm}\n" +
                                                    "수용가능 인원: ${currentRow.shelAv}\n" +
                                                    "대피소 분류: ${currentRow.shelDivType}"

                                        //View 추가
                                        val textView = Button(this@Shelter_list).apply {
                                            setBackgroundResource(R.drawable.rounded_background)
                                            layoutParams = params
                                            setPadding(30, 40, 30, 40)
                                            gravity = Gravity.CENTER_VERTICAL
                                        }
                                        textView.text = currentRowCreateMsg

                                        textView.setOnClickListener {
                                            val listLayout: LinearLayout = findViewById(R.id.list)
                                            listLayout.removeAllViews()
                                            val webPageView = WebView(this@Shelter_list)
                                            webPageView.loadUrl("https://map.naver.com/p/search/" + "${currentRow.address}")

                                            val layout = LinearLayout(this@Shelter_list)

                                            layout.addView(webPageView)

                                            setContentView(layout)
                                        }
                                        messageListLinearLayout.addView(textView)

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
                                this@Shelter_list,
                                "Error!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        404 -> {
                            Log.e("Error 404", "Not Found : $errorMessage")
                            Toast.makeText(
                                this@Shelter_list,
                                "Error!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Log.e("Error", "Generic Error : $errorMessage")
                            Toast.makeText(
                                this@Shelter_list,
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
                    this@Shelter_list,
                    "Error!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}