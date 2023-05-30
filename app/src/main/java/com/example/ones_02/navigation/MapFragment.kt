import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ones_02.R
import com.example.ones_02.navigation.model.ContentDTO
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import kotlinx.android.synthetic.main.item_detail.view.*
import kotlinx.android.synthetic.main.item_mappost.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MapFragment : Fragment(), OnMapReadyCallback {
    var firestore : FirebaseFirestore?= null

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var locationManager: LocationManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        firestore = FirebaseFirestore.getInstance()

        mapView = view.findViewById(R.id.mapViewmain)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        view.mappost_recyclerveiw.adapter = MappostRecyclerViewAdapter()
        view.mappost_recyclerveiw.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class MappostRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()


        init {
            firestore?.collection("images")?.orderBy("timestamp",
                Query.Direction.DESCENDING)?.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()

                if(querySnapshot == null) return@addSnapshotListener
                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
//
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_mappost, p0, false)
            return CustomViewHolder(view)
        }
        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)


        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView

            //title
            viewholder.mappost_title.text = contentDTOs!![p1].title
            viewholder.mappost_pirce.text = contentDTOs!![p1].price


//            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
//            val date = contentDTOs!![p1].timestamp?.toDate()
//            if (date != null) {
//                viewholder.post_feed_product_time.text = dateFormat.format(date)
//            } else {
//                viewholder.post_feed_product_time.text = ""
//            }
//
//            val tagLayout = viewholder.post_feed_product_tag_layout
//            val tags = contentDTOs!![p1].tag?.split(",")
//
//            // 각 태그마다 버튼 생성
//            if (tags != null) {
//                for (tag in tags) {
//                    val tagButton = AppCompatButton(context!!)
//
//                    // 버튼의 글자 크기 측정
//                    val paint = tagButton.paint
//                    val textWidth = paint.measureText(tag)
//
//                    // 버튼의 크기 동적으로 설정
//                    val params = LinearLayout.LayoutParams(
//                        textWidth.toInt() + 40,  // 버튼의 크기 = 글자의 길이 + 여백
//                        78
//                    )
//                    params.setMargins(0, 0, 10, 0) // left, top, right, bottom
//                    tagButton.layoutParams = params
//
//                    tagButton.text = tag
//                    tagButton.setBackgroundResource(R.drawable.shape_tagbox)
//                    tagButton.setTextColor(Color.WHITE)
//                    tagLayout.addView(tagButton)
//                }
//            }


        }

        override fun getItemCount(): Int {
            return contentDTOs.size
            println("contentDTOs.sizemap")
            println(contentDTOs.size)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 위치 사용 권한 확인
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 위치 권한이 허용된 경우
            getCurrentLocation()
        } else {
            // 위치 권한 요청
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
        }
    }

    private fun getCurrentLocation() {
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
//                val currentLatLng = LatLng(location.latitude, location.longitude)
                val currentLatLng = LatLng(35.908633261636, 128.81049581679)

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13f))
                googleMap.addMarker(MarkerOptions().position(currentLatLng).title("Current Location"))

                locationManager.removeUpdates(this)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}
