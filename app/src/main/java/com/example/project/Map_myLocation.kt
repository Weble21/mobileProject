package com.example.project

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.GsonBuilder
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

class Map_myLocation : FragmentActivity(), OnMapReadyCallback {

    val permission_request = 100
    private lateinit var naverMap: NaverMap

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var currentLocation: String = "한국"

    private var clientID: String = "omn4728sj9"
    private var clientSecret: String = "Z4XY4WJ5UrdIN9zZOqCld7oMDFoNQw5pcUQKybiZ"

    var permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_mylocation)

        val backBtn: ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressed()
        }


        if (isPermitted()) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions(this, permissions, permission_request)
        }
    }

    fun isPermitted(): Boolean {
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(this, perm)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun startProcess() {
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.naverMap) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.naverMap, it).commit()
            }
        mapFragment.getMapAsync(this)
    }


    private fun reverseGeoCoding(
        coords: String,
        output: String,
        clientId: String,
        clientSecret: String
    ) {
        val gson = GsonBuilder().setLenient().create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.makeRequest(coords, output, clientId, clientSecret)

        call.enqueue(object : retrofit2.Callback<ApiResponse> {
            override fun onResponse(
                call: Call<ApiResponse>,
                response: retrofit2.Response<ApiResponse>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    // apiResponse를 처리하는 코드 작성
                    if (apiResponse != null) {
                        currentLocation = "${apiResponse.results[0].region.area1.name} " +
                                "${apiResponse.results[0].region.area2.name} " +
                                "${apiResponse.results[0].region.area3.name}"
                        Log.i("행정동", "${currentLocation}")
                    }
                } else {
                    Log.e("HTTP Error", "HTTP error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("Request Failure", "Error making HTTP request", t)
            }
        })
        updateUI()
    }

    fun updateUI() {
        val thread = Thread {
            val apiSearch = ApiSearch()
            if (currentLocation == "한국") {
                Log.i("currentLocationIsNull", "currentLocation has no result")
            } else {
                apiSearch.main("${currentLocation}")

                runOnUiThread {
                    val listLayout: LinearLayout = findViewById(R.id.listLayout)
                    listLayout.removeAllViews()
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(60, 10, 60, 40)
                    for (idx in 0..4) {
                        val textView = TextView(this@Map_myLocation).apply {
                            setBackgroundResource(R.drawable.rounded_background)
                            layoutParams = params
                            setPadding(30, 40, 30, 10)
                            gravity = Gravity.CENTER_VERTICAL
                        }

                        val text = buildString {
                            append(" ${apiSearch.listTitle[idx]}\n")
                            append("${apiSearch.listAdd[idx]}\n")
                            append("${apiSearch.listLoadAdd[idx]}\n")
                            append("${apiSearch.listCategory[idx]}\n")
                        }

                        val spannableString = SpannableString(text)

                        val newLineIndex = text.indexOf('\n')
                        if (newLineIndex != -1) {
                            spannableString.setSpan(
                                StyleSpan(Typeface.BOLD),
                                0,
                                newLineIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            spannableString.setSpan(
                                RelativeSizeSpan(1.2f),
                                0,
                                newLineIndex,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }

                        textView.text = spannableString

                        listLayout.addView(textView)
                    }
                }

            }
        }
        thread.start()

    }

    override fun onMapReady(naverMap: NaverMap) {
        val cameraPosition = CameraPosition(
            LatLng(37.6291, 126.0813),  //임의 위치 지정(서울시청 좌표)
            17.5 // 줌 레벨
        )

        naverMap.cameraPosition = cameraPosition
        this.naverMap = naverMap

        //gps 받아오기
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        //내 위치 가져오기
        setUpdateLocationListner()

    }

    //gps 자동으로 받기
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback

    @SuppressLint("MissingPermission")
    //좌표계 갱신
    fun setUpdateLocationListner() {
        val locationRequest = LocationRequest.create()
        locationRequest.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 2000
        }
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for ((i, location) in locationResult.locations.withIndex()) {
                    Log.d("location: ", "${location.latitude}, ${location.longitude}")
                    setLastLocation(location)
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }


    fun setLastLocation(location: Location) {
        currentLatitude = location.latitude
        currentLongitude = location.longitude

        val myLocation = LatLng(location.latitude, location.longitude)

        val marker = Marker()

        marker.position = myLocation
        marker.width = 50
        marker.height = 70

        marker.map = naverMap
        val cameraUpdate = CameraUpdate.scrollTo(myLocation)
        naverMap.moveCamera(cameraUpdate)
        naverMap.maxZoom = 20.0
        naverMap.minZoom = 7.0

        reverseGeoCoding(
            "${location.longitude},${location.latitude}",
            "json",
            "${clientID}",
            "${clientSecret}"
        )
    }

    interface ApiService {
        @GET("map-reversegeocode/v2/gc")
        fun makeRequest(
            @Query("coords") coords: String,
            @Query("output") output: String,
            @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
            @Header("X-NCP-APIGW-API-KEY") clientSecret: String
        ): Call<ApiResponse>
    }

    data class ApiResponse(
        val results: List<Result>
    )

    data class Result(
        val name: String,
        val region: Region
    )

    data class Region(
        val area0: Area,
        val area1: Area,
        val area2: Area,
        val area3: Area
    )

    data class Area(
        val name: String
    )

}