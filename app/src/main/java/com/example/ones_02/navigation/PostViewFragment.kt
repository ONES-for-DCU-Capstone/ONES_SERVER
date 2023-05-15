package com.example.ones_02.navigation

import android.app.Activity
import android.app.ActivityOptions
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.example.ones_02.R
import com.example.ones_02.navigation.model.ContentDTO
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.item_detail.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PostViewFragment : AppCompatActivity() {

    var firestore: FirebaseFirestore? = null
//    var uid: String? = null
    var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_view)

        firestore = FirebaseFirestore.getInstance()
//        uid = FirebaseAuth.getInstance().currentUser?.uid

        val documentId = intent.getStringExtra("documentId")
        val destinationUid = intent.getStringExtra("destinationUid")

        Log.d("Document ID", documentId.toString())
        val chat = findViewById<AppCompatButton>(R.id.post_view_chat)

        val more = findViewById<ImageButton>(R.id.post_view_btn_more)
        more.setOnClickListener {
            val editButton = findViewById<Button>(R.id.edit_button)
            val deleteButton = findViewById<Button>(R.id.delete_button)

            // 수정, 삭제 버튼을 보여줄 레이아웃을 가져옵니다.
            val moreLayout = findViewById<LinearLayout>(R.id.more_layout)

            // 수정, 삭제 버튼을 보여줄 레이아웃을 보이도록 변경합니다.
            moreLayout.visibility = View.VISIBLE

            // 수정 버튼 클릭 이벤트
            editButton.setOnClickListener {
                val intent = Intent(this, ActivityPostEdit::class.java)
                intent.putExtra("documentId", documentId)
                startActivity(intent)
            }

            // 삭제 버튼 클릭 이벤트
            deleteButton.setOnClickListener {
                // TODO: 게시물 삭제 기능 구현
                // 삭제할 게시물의 document ID 가져오기
                val documentId = intent.getStringExtra("documentId")
                if (documentId != null) {
                    // 게시물 document에 대한 참조 생성
                    val documentReference = firestore?.collection("images")?.document(documentId)
                    // 데이터 삭제
                    documentReference?.delete()
                        ?.addOnSuccessListener {
                            // 삭제 성공 처리
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                        ?.addOnFailureListener { e ->
                            // 삭제 실패 처리
                        }
                }
            }
        }


        val docRef = firestore?.collection("images")?.document(documentId.toString())
        docRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null) {
//                    val title = document.getString("title")

                    // Load title into a view
                    val backbtn = findViewById<ImageButton>(R.id.post_view_btn_back)
                    val more = findViewById<ImageButton>(R.id.post_view_btn_more)
                    val profileImg = findViewById<ImageButton>(R.id.post_view_btn_user_profile)
                    val name = findViewById<TextView>(R.id.post_view_text_user_name)
                    val time = findViewById<TextView>(R.id.post_view_text_wrt_time)
                    val picture = findViewById<ImageView>(R.id.post_view_img_picture)
                    val title = findViewById<TextView>(R.id.post_view_title)
                    val content  = findViewById<TextView>(R.id.post_view_content)
                    val views = findViewById<TextView>(R.id.post_view_watched)
                    val price = findViewById<TextView>(R.id.post_view_price)

                    backbtn.setOnClickListener {
                        finish() // This method closes the current activity and returns to the previous activity in the stack.
                    }

                    name.text = document.getString("usernickname")

                    val timestamp: Timestamp? = document.getTimestamp("timestamp")
                    time.text = timestamp?.let { android.text.format.DateFormat.format("yyyy년 MM월 dd일 HH:mm", it.toDate()) }

//                    Glide.with(this).load(document.getString("profileImage")).into(profileImg)

                    title.text = document.getString("title")
                    Glide.with(this).load(document.getString("imageUri")).into(picture)

                    content.text = document.getString("explain")
                    price.text = document.getString("price")

                } else {
                    Log.d(TAG, "No such document")
                }
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        chat.setOnClickListener {

            var contentDTOs : ArrayList<ContentDTO> = arrayListOf()

            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("destinationUid", destinationUid)
            val activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fromright, R.anim.toleft)
            startActivity(intent, activityOptions.toBundle())
        }

    }
}