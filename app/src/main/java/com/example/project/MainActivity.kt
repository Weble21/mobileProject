package com.example.project

import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.O
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.project.Models.Earthquake.EarthquakeResponse
import com.example.project.Models.Earthquake.Item
import com.example.project.network.EarthquakeService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val headerHome: ImageButton = findViewById(R.id.header_home)
        headerHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }


        val headerMessage: ImageButton = findViewById(R.id.header_message)
        headerMessage.setOnClickListener {
            val intent = Intent(this, Disaster_message::class.java)
            startActivity(intent);
        }

        val headerMy: ImageButton = findViewById(R.id.header_my)
        headerMy.setOnClickListener {
            val intent = Intent(this, Map_myLocation::class.java)
            startActivity(intent);
        }

        val headerSearch: ImageButton = findViewById(R.id.header_search)
        headerSearch.setOnClickListener {
            val intent = Intent(this, Shelter_search::class.java)
            startActivity(intent);
        }

        getEarthquakeAPI();
    }

    //현재날짜 받아오기
    fun getCurrentDate(): Int {
        if (Build.VERSION.SDK_INT < O) {
            return -1
        }
        val currentDate: LocalDate = LocalDate.now()
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        return currentDate.format(formatter).replace("-","").toInt()
    }


    //3일 전 날짜 받아오기
    fun getYesterDate(): Int {
        if (Build.VERSION.SDK_INT < O) {
            return -1
        }

        val currentDate: LocalDate = LocalDate.now()
        val yesterDate: LocalDate = currentDate.minusDays(2)
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        return yesterDate.format(formatter).replace("-","").toInt()
    }


    fun convertDateTimeFormat(inputDateTime: String): String {
        val inputFormat = SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy년MM월dd일HH시mm분", Locale.getDefault())

        try {
            val date = inputFormat.parse(inputDateTime)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    @Override
    private fun getEarthquakeAPI() {
        if(Constants.isNetworkAvailable(this)) {
            Toast.makeText(
                this@MainActivity,
                "인터넷이 연결되었습니다",
                Toast.LENGTH_SHORT
            ).show()

            val yesterDate = getYesterDate();
            val currentDate = getCurrentDate();

            Log.i("yesterDate:", "$yesterDate")
            Log.i("currentDate:", "$currentDate")

            val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service : EarthquakeService = retrofit
                .create<EarthquakeService>(EarthquakeService::class.java)

            val listCall : Call<EarthquakeResponse> = service.getMessage(
                Constants.APP_ID, 1, 10, "JSON", yesterDate, currentDate
            )
            listCall.enqueue(object  : Callback<EarthquakeResponse> {
                val loading:TextView = findViewById(R.id.loading)
                override fun onResponse(
                    call: Call<EarthquakeResponse>,
                    response: Response<EarthquakeResponse>
                ) {
                    Log.i("Response", response.toString())
                    if(response?.isSuccessful == true) {
                        loading.visibility = View.GONE
                        val allResult : EarthquakeResponse ? = response.body()
                        Log.i("Response Result", "$allResult")
                        val resultResponse = allResult?.response
                        val resultBody = resultResponse?.body
                        val resultItems = resultBody?.items
                        val itemList: List<Item>? = resultItems?.item

                        var useIdx: Int = -1;

                        val imgMap: ImageView = findViewById(R.id.imgMap)
                        val tmFc: TextView = findViewById(R.id.tmFc)
                        val loc: TextView = findViewById(R.id.loc)
                        val mt: TextView = findViewById(R.id.mt)
                        val inT: TextView = findViewById(R.id.inT)
                        val msg: TextView = findViewById(R.id.msg)
                        val earthquakeList: LinearLayout = findViewById(R.id.earthquakeList)
                        val messageTitle: TextView = findViewById(R.id.messageTitle)
                        val shelterTitle: TextView = findViewById(R.id.shelterTitle)
                        val tipsNormal:LinearLayout = findViewById(R.id.tips_normal)
                        val tipsEarth:LinearLayout = findViewById(R.id.tips_earth)

                        if(!itemList.isNullOrEmpty()) {
                            for(i in 0 until  itemList.size) {
                                val currentItem: Item = itemList[i]
                                if(currentItem.fcTp == 3) {
                                    useIdx = i
                                    break;
                                }
                            }
                            val useItem: Item = itemList[useIdx]
                            val imgUrl: String = useItem.img
                            val formattedtmFc = convertDateTimeFormat(useItem.tmFc)
                            //지진 발생 여부에 따른 결과
                            if(useIdx != -1) {
                                earthquakeList.visibility = View.VISIBLE
                                imgMap.scaleType = ImageView.ScaleType.MATRIX
                                Glide.with(this@MainActivity).load(imgUrl).into(imgMap)
                                tmFc.text = "지진 발표 시간 : " + formattedtmFc
                                loc.text = "진앙 위치 : " + useItem.loc
                                mt.text = "규모 : " + useItem.mt
                                inT.text = "진도 : " + useItem.inT
                                msg.text = " " + useItem.rem
                                msg.visibility = View.VISIBLE
                                messageTitle.visibility = View.VISIBLE
                                shelterTitle.visibility = View.VISIBLE
                                tipsEarth.visibility = View.VISIBLE

                                Log.i("item", "$useItem")
                            } else {
                                imgMap.setImageResource(R.drawable.no_event)
                                tmFc.visibility = View.INVISIBLE
                                loc.visibility = View.INVISIBLE
                                mt.visibility = View.INVISIBLE
                                inT.visibility = View.INVISIBLE
                                msg.visibility = View.INVISIBLE
                                messageTitle.visibility = View.VISIBLE
                                tipsNormal.visibility = View.VISIBLE
                            }
                        } else {

                            imgMap.setImageResource(R.drawable.no_event)
                            tmFc.visibility = View.INVISIBLE
                            loc.visibility = View.INVISIBLE
                            mt.visibility = View.INVISIBLE
                            inT.visibility = View.INVISIBLE
                            msg.visibility = View.INVISIBLE
                            messageTitle.visibility = View.VISIBLE
                            tipsNormal.visibility = View.VISIBLE

                        }


                    }


                }

                override fun onFailure(call: Call<EarthquakeResponse>, t: Throwable) {
                    loading.text = "에러 발생! 정보를 불러올 수 없습니다!"
                    loading.visibility = View.VISIBLE
                    Log.e("err", "errrr")
                    Toast.makeText(
                        this@MainActivity,
                        "정보를 불러올 수 없습니다",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            })

        } else {
            Toast.makeText(
                this@MainActivity,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}