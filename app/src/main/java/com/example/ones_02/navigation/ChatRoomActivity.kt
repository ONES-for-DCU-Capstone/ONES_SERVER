package com.example.ones_02.navigation

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ones_02.R
import com.example.ones_02.navigation.model.ContentDTO
import com.example.ones_02.navigation.model.UserDTO
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_chatroom.*
import kotlinx.android.synthetic.main.item_chat.*
import java.text.SimpleDateFormat
import java.util.*
import com.example.ones_02.navigation.model.ChatDTO as Chat

class ChatRoomActivity : AppCompatActivity() {
    private var destinationUid: String? = null
    private var editText: EditText? = null
    private var uid: String? = null
    private var chatRoomUid: String? = null
    private var recyclerView: RecyclerView? = null
    //private val fireDatabase = FirebaseDatabase.getInstance().reference
    private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
    private var firestore = FirebaseFirestore.getInstance()

    private var messageActivity_textView_topName : TextView? = null
    private var messageItem_textview_name : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        messageActivity_textView_topName = findViewById(R.id.messageActivity_textView_topName)
        messageItem_textview_name = findViewById(R.id.messageItem_textview_name)

        uid = FirebaseAuth.getInstance().getCurrentUser()?.getUid()
//        uid = Firebase.auth.currentUser?.uid.toString()//채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID
        destinationUid = getIntent().getStringExtra("destinationUid")
//        destinationUid = intent.getStringExtra("destinationUid")// 채팅을 당하는 아이디
        editText = findViewById(R.id.chatmessageEditText) as EditText?
        recyclerView = findViewById(R.id.chatroomActibityRecyclerView) as RecyclerView?
        //message 보낸시간

//        val time = Timestamp.now()
//        val dateFormat = SimpleDateFormat("MMdd 'date' hh:mm:ss")
//        val curTime = dateFormat.format(time.toDate())

        chatsend_btn.setOnClickListener {
            Log.d("클릭 시 dest", "$destinationUid")
            Log.d("uid", "$uid")

            val time = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("MM월dd일 hh:mm:ss")
            val curTime = dateFormat.format(Date(time)).toString()

            val chatDTO = Chat()

            chatDTO.users[uid.toString()] = true
            chatDTO.users[destinationUid!!] = true

//          firestore?.collection("chatroom")?.add(chatDTO)   //Firestore 일때 사용.
//          fireDatabase.child("chatrooms").push().setValue(chatDTO)

            val comment = Chat.Comment(uid, editText?.text.toString(), curTime)
            if(chatRoomUid == null){
                chatsend_btn.isEnabled = false
                val chatRoomsRef = firestore.collection("chatrooms")
                chatRoomsRef.add(chatDTO)
                    .addOnSuccessListener { documentReference ->
                        //채팅방 생성
                        checkChatRoom()

                        Log.d("유저222", "$uid")
                        Log.d("유저222", "$destinationUid")

                        Log.d("유저documentfef", "${documentReference.id}")
                        Log.d("유저uid222", "$uid")
                        //메세지 보내기
                        Handler().postDelayed({
//                            println("ddddddddd")
                            println(documentReference.id)
                            chatRoomsRef.document(documentReference.id).collection("comments").add(comment)
                            chatmessageEditText.text = null
                        }, 1000L)
                        Log.d("chatUidNull dest", "$destinationUid")
                    }
            } else {
                firestore.collection("chatrooms").document(chatRoomUid.toString()).collection("comments").add(comment)
                chatmessageEditText.text = null
                Log.d("chatUidNotNull dest", "$destinationUid")
            }
        }
        checkChatRoom()
    }

    private fun checkChatRoom() {
        firestore.collection("chatrooms")
            .whereEqualTo("users.${uid}", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val chatDTO = document.toObject(com.example.ones_02.navigation.model.ChatDTO::class.java)
                    if (chatDTO.users.containsKey(destinationUid)) {
                        chatRoomUid = document.id
                        chatsend_btn.isEnabled = true
                        recyclerView?.layoutManager = LinearLayoutManager(this@ChatRoomActivity)
                        recyclerView?.adapter = RecyclerViewAdapter()
                    }
                }
            }
            .addOnFailureListener { exception ->
//                Log.w(TAG, "Error getting chat rooms: ", exception)
            }
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ChatViewHolder>() {
        //Comment(채팅 내용 리스트로 가져오기)
//        private val comments = ArrayList<com.example.ones_02.navigation.model.ChatDTO.Comment>()
////        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
//        private var contentDTO : ContentDTO? = null

        private val comments = ArrayList<com.example.ones_02.navigation.model.ChatDTO.Comment>()
        private var contentDTO: ContentDTO? = null
        var userDTO: UserDTO? = null


        init{
            //user 부분 만들고 해야하는것, profileimg 부분.
            firestore.collection("users").document(destinationUid.toString())
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    userDTO = documentSnapshot.toObject(UserDTO::class.java)
                    messageActivity_textView_topName?.text = userDTO?.nickname

                    getMessageList()
                }
                .addOnFailureListener { e ->
//                    Log.e(TAG, "Error getting friend from Firestore: ", e)
                }
        }
//?.orderBy("time")
        fun getMessageList() {
            firestore.collection("chatrooms").document(chatRoomUid.toString()).collection("comments")?.orderBy("time")?.addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
//                        Log.d(TAG, "Error getting chatroom comments: $exception")
                        return@addSnapshotListener
                    }
                    comments.clear()
                    for (document in snapshot!!) {
                        val item = document.toObject(com.example.ones_02.navigation.model.ChatDTO.Comment::class.java)
                        comments.add(item)
//                        println(comments)
                    }

                    notifyDataSetChanged()
                    recyclerView?.scrollToPosition(comments.size - 1)
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
            val view : View = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
            return ChatViewHolder(view)
        }
        @SuppressLint("RtlHardcoded")
        override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

            holder.textView_chat.textSize = 30F
            holder.textView_chat.text = comments[position].message.toString()
            holder.textView_time.text = comments[position].time

            if(comments[position].uid.equals(uid)){ // 본인 채팅
               Log.d("1commentsuid", "$comments[position].uid")

                holder.textView_chat.setBackgroundResource(R.drawable.rightbubble)
                holder.textView_name.visibility = View.INVISIBLE
                holder.layout_destination.visibility = View.INVISIBLE
                holder.layout_main.gravity = Gravity.RIGHT
            }else{ // 상대방 채팅
                Glide.with(holder.itemView.context)
                    .load(userDTO?.profileImage)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.imageView_profile)
                holder.textView_name.text = userDTO?.nickname
                holder.layout_destination.visibility = View.VISIBLE
                holder.textView_name.visibility = View.VISIBLE
                holder.textView_chat.setBackgroundResource(R.drawable.leftbubble)
                holder.layout_main.gravity = Gravity.LEFT
            }
        }

        inner class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView_chat: TextView = view.findViewById(R.id.messageItem_textView_message)
            val textView_name: TextView = view.findViewById(R.id.messageItem_textview_name)
            val imageView_profile: ImageView = view.findViewById(R.id.messageItem_imageview_profile)
            val layout_destination: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_destination)
            val layout_main: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
            val textView_time : TextView = view.findViewById(R.id.messageItem_textview_timestamp)
        }

        override fun getItemCount(): Int {
            return comments.size
        }
    }
}

