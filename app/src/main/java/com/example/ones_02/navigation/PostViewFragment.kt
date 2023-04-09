package com.example.ones_02.navigation

import android.app.ActivityOptions
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.example.ones_02.R
import com.example.ones_02.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class PostViewFragment : AppCompatActivity() {

    var firestore: FirebaseFirestore? = null
    var uid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_view)

        firestore = FirebaseFirestore.getInstance()
        uid = FirebaseAuth.getInstance().currentUser?.uid

        val documentId = intent.getStringExtra("documentId")

        Log.d("Document ID", documentId.toString())
        val chat = findViewById<AppCompatButton>(R.id.post_view_chat)



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

                    val timestamp: Long? = document.getLong("timestamp")
                    time.text = timestamp?.toString()
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
            val intent = Intent(this, ChatRoomActivity::class.java)
            intent.putExtra("destinationUid", uid)
            val activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fromright, R.anim.toleft)
            startActivity(intent, activityOptions.toBundle())
        }

    }



}