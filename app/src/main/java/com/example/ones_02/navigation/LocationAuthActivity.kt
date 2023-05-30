package com.example.ones_02.navigation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.ones_02.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.frag_map.*

class LocationAuthActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    var firestore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frag_map)
        firestore = FirebaseFirestore.getInstance()

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        mymap_btn_back.setOnClickListener {
            onBackPressed()
        }


        //위치 사용 인증
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val requestCode = 1
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        // 현재 위치 가져오기
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
//                val currentLatLng = LatLng(location.latitude, location.longitude)
                val currentLatLng = LatLng(35.919040329022, 128.83289336918)

                googleMap.addMarker(MarkerOptions().position(currentLatLng).title("여기"))

                // 위도와 경도로부터 주소 가져오기 (Geocoder)
                val geocoder = Geocoder(this)
                val addresses = geocoder.getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1)

                if (addresses.isNotEmpty()) {
                    val address = addresses[0].getAddressLine(0)
//                    locationdetail.text =address

                    //시 일 경우
                    //구일 경우도 있어야한다. 00구 00동일때..
                    val startIndex = address.indexOf("시") + 2
                    val endIndex = startIndex + 3
                    val extractedChars = address.substring(startIndex, endIndex)
                    locationdetail.text =extractedChars
//                     주소를 필요에 맞게 사용하세요 (예: TextView에 표시)
                    println(address)
                } else {
                    // 주소를 가져올 수 없음
                    println("주소를 찾을 수 없음")
                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))


                println(currentLatLng)
                // 현재 위치 마커로 표시
                googleMap.addMarker(MarkerOptions().position(currentLatLng).title("여기"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))

                // 등록 버튼 클릭 시 Firestore에 위치 저장
                locationAuth_button.setOnClickListener {
                    saveLocationToFirestore(currentLatLng)
                }
            }
        }
    }

    //location을 DB에 등록.
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    private fun saveLocationToFirestore(location: LatLng) {
        // Firestore에 위치 데이터 저장
        val location = GeoPoint(location.latitude, location.longitude)
        val userRef = firestore?.collection("users")?.document(uid!!)
        userRef?.update("location", location)
            ?.addOnSuccessListener {
                Toast.makeText(this@LocationAuthActivity, "저장 완료", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
            ?.addOnFailureListener{ e ->
            }
    }


    //생명주기 메서드
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }
}