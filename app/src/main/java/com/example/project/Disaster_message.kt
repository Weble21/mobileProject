package com.example.project

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project.Models.MessageResponse
import com.example.project.network.MessageService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Disaster_message : AppCompatActivity() {
    private val pageSize = 10
    private var pageNo = 1
    private var loadedCount = 0
    private var isLoading = false
    private var hasMore = true
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
                .baseUrl("https://www.safetydata.go.kr/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: MessageService = retrofit
                .create<MessageService>(MessageService::class.java)

            val scrollView: ScrollView = findViewById(R.id.messageScroll)
            val threshold = (120 * resources.displayMetrics.density).toInt()
            scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                val contentHeight = scrollView.getChildAt(0)?.height ?: 0
                if (scrollY + scrollView.height >= contentHeight - threshold) {
                    fetchMessages(service)
                }
            }
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
        if (isLoading || !hasMore) return
        isLoading = true

        val listCall: Call<MessageResponse> = service.getMessage(
            "204C79EK51AEI0FR", "json", pageNo, pageSize
        )
        listCall.enqueue(object : Callback<MessageResponse> {
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                if (response.isSuccessful) {
                    val messageList = response.body()
                    val messages = messageList?.body.orEmpty()
                    val messageListLayout: LinearLayout = findViewById(R.id.messageList)

                    for (item in messages) {
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(60, 0, 60, 50)

                        messageListLayout.addView(TextView(this@Disaster_message).apply {
                            text = item.message.orEmpty()
                            setBackgroundResource(R.drawable.rounded_background)
                            layoutParams = params
                            setPadding(30, 40, 30, 10)
                            gravity = Gravity.CENTER_VERTICAL
                        })
                    }

                    loadedCount += messages.size
                    hasMore = messages.isNotEmpty() && loadedCount < (messageList?.totalCount ?: 0)
                    pageNo++
                    isLoading = false

                    val scrollView: ScrollView = findViewById(R.id.messageScroll)
                    if (hasMore) {
                        scrollView.post {
                            if (!scrollView.canScrollVertically(1)) fetchMessages(service)
                        }
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
                    isLoading = false
                }
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                isLoading = false
                Log.e("Errrrrrrr", t.message.toString())
                Toast.makeText(
                    this@Disaster_message,
                    "Error!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}
