package com.example.ones_02.navigation

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ones_02.R
import com.example.ones_02.navigation.model.ContentDTO
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.fragment_postview.view.*
import kotlinx.android.synthetic.main.item_detail.view.*
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import java.util.Collections.max
import kotlin.collections.ArrayList
import kotlin.math.max

class DetailViewFragment : Fragment() {
    var firestore : FirebaseFirestore?= null
    var uid : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail,container,false)
        firestore = FirebaseFirestore.getInstance()

        uid = FirebaseAuth.getInstance().currentUser?.uid
//
//        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
//        var contentUidList : ArrayList<String> = arrayListOf()

        view.detailview_recyclerveiw.adapter = DetailViewRecyclerViewAdapter()
        view.detailview_recyclerveiw.layoutManager = LinearLayoutManager(activity)

        return view
    }

    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
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
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_detail, p0, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            //서버에서 넘어오는 데이터를 매핑시켜줌
            var viewholder = (p0 as CustomViewHolder).itemView

            //UserId
//            viewholder.profile_textview.text = contentDTOs!![p1].userId

            //Images
            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUri).into(viewholder.post_feed_product_img)

            //title
            viewholder.post_feed_product_title.text = contentDTOs!![p1].title
            viewholder.post_feed_product_price.text = contentDTOs!![p1].price

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = contentDTOs!![p1].timestamp?.toDate()
//            viewholder.post_feed_product_time.text = dateFormat.format(date)
            if (date != null) {
                viewholder.post_feed_product_time.text = dateFormat.format(date)
            } else {
                viewholder.post_feed_product_time.text = ""
            }

//            viewholder.post_feed_product_time.text = contentDTOs!![p1].timestamp.toString()

            val tagLayout = viewholder.post_feed_product_tag_layout
            val tags = contentDTOs!![p1].tag?.split(",")

            // 기존에 있던 버튼들 삭제
            tagLayout.removeAllViews()

            // 각 태그마다 버튼 생성
            if (tags != null) {
                for (tag in tags) {
                    val tagButton = AppCompatButton(context!!)

                    // 버튼의 글자 크기 측정
                    val paint = tagButton.paint
                    val textWidth = paint.measureText(tag)

                    // 버튼의 크기 동적으로 설정
                    val params = LinearLayout.LayoutParams(
                        textWidth.toInt() + 40,  // 버튼의 크기 = 글자의 길이 + 여백
                        78
                    )
                    params.setMargins(0, 0, 10, 0) // left, top, right, bottom
                    tagButton.layoutParams = params

                    tagButton.text = tag
                    tagButton.setBackgroundResource(R.drawable.shape_tagbox)
                    tagButton.setTextColor(Color.WHITE)
                    tagLayout.addView(tagButton)
                }
            }


//            val tagLayout = viewholder.post_feed_product_tag_layout
//            val tags = contentDTOs!![p1].tag?.split(",")
//
//            // 기존에 있던 버튼들 삭제
//            tagLayout.removeAllViews()
//
//            // 각 태그마다 버튼 생성
//            if (tags != null) {
//                for (tag in tags) {
//                    val tagButton = AppCompatButton(context!!)
//                    tagButton.layoutParams = LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                    )
//                    tagButton.text = tag.toString()
//                    tagButton.setBackgroundResource(R.drawable.shape_tagbox)
//                    tagButton.setTextColor(Color.WHITE)
//                    tagLayout.addView(tagButton)
//                }
//            }

//            val tagButton = viewholder.post_feed_product_tag as AppCompatButton
//            val tags = contentDTOs!![p1].tag
//            for (tag in tags) {
//                val tagView = LayoutInflater.from(context).inflate(R.layout.item_detail, null)
//                val tagTextView = tagView.findViewById<TextView>(R.id.post_feed_product_tag_text)
//                tagTextView.text = tag
//                viewholder.post_feed_product_tag_layout.addView(tagView)
//            }


            //Explain of content
//            viewholder.explain_textview.text = contentDTOs!![p1].explain

//            //likes
//            viewholder.like_textview.text = "likes " + contentDTOs!![p1].favoriteCount

            //ProfileImage
//            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUri).into(viewholder.profile_imageview)

//            //like button에 이벤트를 달아줌
//            viewholder.favorite_imageview.setOnClickListener{
//                favoriteEvent(p1)
//            }
//            //좋아요 카운터와 색 이벤트
//            if(contentDTOs!![p1].favorites.containsKey(uid)){
//                //좋아요 클린한 경우
//                viewholder.favorite_imageview.setImageResource(R.drawable.ic_favorite)
//            }else{
//                //좋아요 버튼 클릭하지 않은 경우
//                viewholder.favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
//            }

            post_feed_bnt_post_wrt.setOnClickListener{
                val intent = Intent(p0.itemView.context, AddPhotoActivity::class.java)
                p0.itemView.context.startActivity(intent)
            }

            p0.itemView.setOnClickListener(View.OnClickListener { view ->
                firestore?.collection("images")?.get()?.addOnSuccessListener { querySnapshot ->
                    // Loop through the documents in the QuerySnapshot
                    for (documentReference in querySnapshot) {
                        // Get the document ID from each DocumentSnapshot
                    }
                }

                val intent = Intent(view.context, PostViewFragment::class.java)
                intent.putExtra("documentId", contentDTOs[p1].id)
                intent.putExtra("destinationUid", contentDTOs[p1].uid)

                var activityOption: ActivityOptions? = null

                activityOption = ActivityOptions.makeCustomAnimation(
                        view.context,
                        R.anim.fromright,
                        R.anim.toleft
                    )
                startActivity(intent, activityOption.toBundle())
            })

//            p0.itemView.setOnClickListener{
//
//                val newFragment = PostViewFragment()
//
//                // Use the FragmentManager to replace the current fragment with the new one
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.pl, newFragment)
//                    .addToBackStack(null)
//                    .commit()
//
////                startActivity(PostViewFragment)
//
//
////                val targetFragment = PostViewFragment() // Replace with the name of the target Fragment
////                val transaction = parentFragmentManager.beginTransaction()
////                transaction.replace(R.id.post_feed_product_title, targetFragment)
////                transaction.addToBackStack(null)
////                transaction.commit()
//
//            }

            //p0.itemView = chat_btn  <- p0.itemView는 전체 테이블을 의미
//            viewholder.chat_btt.setOnClickListener(View.OnClickListener { view ->
//                val intent = Intent(view.context, ChatRoomActivity::class.java)
//                intent.putExtra("destinationUid", contentDTOs[p1].uid)
//                var activityOptions: ActivityOptions? = null
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    activityOptions = ActivityOptions.makeCustomAnimation(
//                        view.context,
//                        R.anim.fromright,
//                        R.anim.toleft
//                    )
//                    Log.d("uid", "$uid")
//                    Log.d("ui2", "${contentDTOs[p1].uid}")
//
//                    startActivity(intent, activityOptions.toBundle())
//
//                    //당근 마켓 처럼 자신의 게시글인 경우에는 채팅대화방 보기 버튼을 하고 싶은데.. 그건 아직..
////                    if( uid == contentDTOs[p1].uid ){
////                        Snackbar.make(view, "본인이 등록한 아이템입니다.", Snackbar.LENGTH_LONG).show()
////                    }
////                    else{
////                        startActivity(intent, activityOptions.toBundle())
////                    }
//                }
//            })
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

//        fun favoriteEvent(position : Int){
//            //tsDoc
//            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
//            //데이터 입력을 위해서 transcation을 불러와야한다.
//            firestore?.runTransaction{transaction ->
//                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
//                if(contentDTO!!.favorites.containsKey(uid)){
//                    //좋아요 버튼을 취소
//                    contentDTO?.favoriteCount = contentDTO?.favoriteCount?.minus(1)
//                    contentDTO?.favorites.remove(uid)
//                }else{
//                    //좋아요 버튼을 누르지 않았을때.
//                    contentDTO?.favoriteCount = contentDTO?.favoriteCount?.plus(1)
//                    contentDTO?.favorites[uid!!] = true
//                }
//                //transaction을 server로 되 돌려줌.
//                transaction.set(tsDoc,contentDTO)
//            }
        }
//    }
}