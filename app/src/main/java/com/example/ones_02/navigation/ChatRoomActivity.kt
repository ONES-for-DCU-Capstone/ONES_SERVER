package com.example.ones_02.navigation

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ones_02.R
import com.example.ones_02.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chatroom.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.ones_02.navigation.model.ChatDTO as Chat
import org.w3c.dom.Comment as Comment1

class ChatRoomActivity : AppCompatActivity() {
    private var destinationUid: String? = null
    private var editText: EditText? = null
    private var uid: String? = null
    private var chatRoomUid: String? = null
    private var recyclerView: RecyclerView? = null
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        var firestore = FirebaseFirestore.getInstance()
//        uid = FirebaseAuth.getInstance().getCurrentUser()?.getUid()
        uid = Firebase.auth.currentUser?.uid.toString()//채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID
//        destinationUid = getIntent().getStringExtra("destinationUid")
        destinationUid = intent.getStringExtra("destinationUid")// 채팅을 당하는 아이디
        editText = findViewById(R.id.chatmessageEditText) as EditText?
        recyclerView = findViewById(R.id.chatRecyclerView) as RecyclerView?
        //message 보낸시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")
        val curTime = dateFormat.format(Date(time)).toString()

        chatsend_btn.setOnClickListener {
            Log.d("클릭 시 dest", "$destinationUid")
            Log.d("uid", "$uid")

            val chatDTO = Chat()

            chatDTO.users.put(uid.toString(), true)
            chatDTO.users.put(destinationUid!!, true)
//          firestore?.collection("chatroom")?.add(chatDTO)   //Firestore 일때 사용.

//            fireDatabase.child("chatrooms").push().setValue(chatDTO)

            val comment = com.example.ones_02.navigation.model.ChatDTO.Comment(
                uid, editText?.text.toString(),curTime
            )
            if(chatRoomUid == null){
               chatsend_btn .isEnabled = false
                fireDatabase.child("chatrooms").push().setValue(chatDTO).addOnSuccessListener {
                    //채팅방 생성
                    checkChatRoom()
                    //메세지 보내기
                    Handler().postDelayed({
                        println(chatRoomUid)
                        fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                        chatmessageEditText.text = null
                    }, 1000L)
                    Log.d("chatUidNull dest", "$destinationUid")
                }
            }else{
                fireDatabase.child("chatrooms").child(chatRoomUid.toString()).child("comments").push().setValue(comment)
                chatmessageEditText.text = null
                Log.d("chatUidNotNull dest", "$destinationUid")
            }
        }
        checkChatRoom()
    }

    //chatroom 계속해서 생기는 것을 방지하기 위해서 중복 체크하는 코드
    private fun checkChatRoom(){
        fireDatabase.child("chatrooms").orderByChild("users/$uid").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children){
                        println(item)
                        //user 2명의 값
                        val chatDTO = item.getValue<com.example.ones_02.navigation.model.ChatDTO>()
                        if(chatDTO?.users!!.containsKey(destinationUid)){
                            //Room의 방 값.
                            chatRoomUid = item.key
//                            chatsend_btn.isEnabled = true
//                            recyclerView?.layoutManager = LinearLayoutManager(this@ChatRoomActivity)
//                            recyclerView?.adapter = RecyclerViewAdapter()
                        }
                    }
                }
            })
    }

}






