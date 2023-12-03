package com.example.project

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.Models.DisasterMsg
import com.example.project.Models.MessageResponse
import com.example.project.Models.Row
import com.example.project.network.MessageService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Disaster_message : AppCompatActivity() {
    private val pageSize = 10
    private var pageNo = 1
    private var allMessages: MutableList<Row> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disaster_message)

        val backBtn: ImageButton = findViewById(R.id.backBtn);
        backBtn.setOnClickListener {
            onBackPressed();
        }

        getAPI()
    }

    @Override
    private fun getAPI() {
        if (Constants.isNetworkAvailable(this)) {

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: MessageService = retrofit
                .create<MessageService>(MessageService::class.java)

            fetchMessages(service)
        } else {
            Toast.makeText(
                this@Disaster_message,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun fetchMessages(service: MessageService) {
        val listCall: Call<MessageResponse> = service.getMessage(
            Constants.APP_ID, pageNo, pageSize, "json"
        )
        listCall.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                Log.i("Response", response.toString())
                if (response?.isSuccessful == true) {
                    val messageList: MessageResponse? = response.body()
                    Log.i("Response Result", "$messageList")
                    val disasterMsgList: List<DisasterMsg>? = messageList?.disasterMsg
                    if (!disasterMsgList.isNullOrEmpty()) {
                        for(disasterMsg in disasterMsgList) {
                            val rowList: List<Row>? = disasterMsg.row
                            if (!rowList.isNullOrEmpty()) {
                                allMessages.addAll(rowList)
                                val messageListLinearLayout: LinearLayout =
                                    findViewById(R.id.messageList)

                                for (i in 1 until rowList.size) {

                                    val currentRow: Row = rowList[i]
                                    val currentRowCreateMsg: String = currentRow.msg

                                    //View 추가
                                    val params = LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                    )
                                    params.setMargins(60, 0, 60, 50)
                                    messageListLinearLayout.addView(TextView(this@Disaster_message).apply {
                                        text = "$currentRowCreateMsg \n"
                                        setBackgroundResource(R.drawable.rounded_background)
                                        layoutParams = params
                                        setPadding(30, 40, 30, 10)
                                        gravity = Gravity.CENTER_VERTICAL
                                    })
                                }
                            } else {
                                Log.e("err", "rowList err")
                            }
                        }
                        pageNo++
                        fetchMessages(service)
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
                                this@Disaster_message,
                                "Error!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        404 -> {
                            Log.e("Error 404", "Not Found : $errorMessage")
                            Toast.makeText(
                                this@Disaster_message,
                                "Error!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Log.e("Error", "Generic Error : $errorMessage")
                            Toast.makeText(
                                this@Disaster_message,
                                "Error!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.e("Errrrrrrr", t!!.message.toString())
                Toast.makeText(
                    this@Disaster_message,
                    "Error!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}
