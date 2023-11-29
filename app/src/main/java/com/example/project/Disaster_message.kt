package com.example.project

import android.os.Bundle
import android.util.Log
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

            val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service : MessageService = retrofit
                .create<MessageService>(MessageService::class.java)

            val listCall : Call<MessageResponse> = service.getMessage(
                Constants.APP_ID, 1, 10, "json"
            )
            listCall.enqueue(object  : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    Log.i("Response", response.toString())
                    if(response?.isSuccessful == true) {
                        val messageList : MessageResponse ? = response.body()
                        Log.i("Response Result", "$messageList")


                        val disasterMsgList: List<DisasterMsg>? = messageList?.disasterMsg
                        if(disasterMsgList != null && disasterMsgList.isNotEmpty()) {
                            val firstDisasterMsg: DisasterMsg? = disasterMsgList[1]
                            val rowList: List<Row>? = firstDisasterMsg?.row
                            if(rowList != null && rowList.isNotEmpty()) {
                                val linearLayout : LinearLayout = findViewById(R.id.messageList)
                                for(i in 1..12) {
                                    if(i < rowList.size) {
                                        val currentRow: Row = rowList[i]
                                        val currentRowCreateMsg : String = currentRow.msg

                                        val newTextView = TextView(this@Disaster_message)
                                        newTextView.text = "$currentRowCreateMsg \n"
                                        linearLayout.addView(newTextView)
                                    } else {
                                        break;
                                    }
                                }

                            } else {
                                Log.e("err", "rowList err")
                            }
                        } else {
                            Log.e("err", "disasterMsgList err")
                        }





                        Toast.makeText(this@Disaster_message, "success", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        val rc = response?.code() ?: -1
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        when(rc) {
                            400 -> {
                                Log.e("Error 400", "Bad Connection : $errorMessage")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found : $errorMessage")
                            } else -> {
                                Log.e("Error", "Generic Error : $errorMessage")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    Log.e("Errrrrrrr", t!!.message.toString())
                }

            })

        } else {
            Toast.makeText(
                this@Disaster_message,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}