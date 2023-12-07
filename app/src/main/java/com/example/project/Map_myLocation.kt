package com.example.project

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
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
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker

class Map_myLocation : FragmentActivity(), OnMapReadyCallback {

    val permission_request = 100
    private lateinit var naverMap: NaverMap

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_mylocation)

        val backBtn : ImageButton = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            onBackPressed();
        }
        if (isPermitted()) {
            startProcess()
        } else {
            ActivityCompat.requestPermissions(this, permissions, permission_request)
        }
        val thread = Thread {
            var apiSearch = ApiSearch()
            apiSearch.main()
            runOnUiThread {
                val resultTextView: TextView = findViewById(R.id.resultTextView)
                val listLayout:LinearLayout = findViewById(R.id.listLayout)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(60, 0, 60, 50)
                for(idx in 0 .. 3) {
                    listLayout.addView(TextView(this@Map_myLocation).apply {
                        text = buildString {
                            append("${apiSearch.listTitle[idx]}\n")
                            append("${apiSearch.listAdd[idx]}\n")
                            append("${apiSearch.listLoadAdd[idx]}\n")
                            append("${apiSearch.listCategory[idx]}\n")
                        }
                        setBackgroundResource(R.drawable.rounded_background)
                        layoutParams = params
                        setPadding(30, 40, 30, 10)
                        gravity = Gravity.CENTER_VERTICAL
                    })

                }
            }
        }
        thread.start()
    }
    fun isPermitted(): Boolean {
        for(perm in permissions) {
            if(ContextCompat.checkSelfPermission(this, perm)
                != PackageManager.PERMISSION_GRANTED) {
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

    override fun onMapReady(naverMap: NaverMap) {
        val cameraPosition = CameraPosition(
            LatLng(37.6291, 126.0813),  // 위치 지정
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
                for((i, location) in locationResult.locations.withIndex()) {
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

    }



}