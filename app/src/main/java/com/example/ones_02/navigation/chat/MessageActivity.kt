//package com.example.ones_02.navigation.chat
//
//import android.support.v7.app.AppCompatActivity
//import android.view.View
//import com.bumptech.glide.request.RequestOptions
//import com.google.android.gms.tasks.Task
//import com.google.firebase.database.ServerValue
//import java.text.SimpleDateFormat
//import java.util.*
//import kotlin.collections.ArrayList
//
//class MessageActivity : AppCompatActivity() {
//    private var destinatonUid: String? = null
//    private var button: Button? = null
//    private var editText: EditText? = null
//    private var uid: String? = null
//    private var chatRoomUid: String? = null
//    private var recyclerView: RecyclerView? = null
//    private val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
//    protected fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_message2)
//        uid = FirebaseAuth.getInstance().getCurrentUser().getUid() //채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID
//        destinatonUid = getIntent().getStringExtra("destinationUid") // 채팅을 당하는 아이디
//        button = findViewById(R.id.messageActivity_button) as Button?
//        editText = findViewById(R.id.messageActivity_editText) as EditText?
//        recyclerView = findViewById(R.id.messageActivity_reclclerview) as RecyclerView?
//        button.setOnClickListener(View.OnClickListener {
//            val chatModel = ChatModel()
//            chatModel.users.put(uid, true)
//            chatModel.users.put(destinatonUid, true)
//            if (chatRoomUid == null) {
//                button.setEnabled(false)
//                FirebaseDatabase.getInstance().getReference().child("chatrooms").push()
//                    .setValue(chatModel).addOnSuccessListener(object : OnSuccessListener<Void?> {
//                    override fun onSuccess(aVoid: Void) {
//                        checkChatRoom()
//                    }
//                })
//            } else {
//                val comment: ChatModel.Comment = Comment()
//                comment.uid = uid
//                comment.message = editText.getText().toString()
//                comment.timestamp = ServerValue.TIMESTAMP
//                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid)
//                    .child("comments").push().setValue(comment)
//                    .addOnCompleteListener(object : OnCompleteListener<Void?> {
//                        override fun onComplete(@NonNull task: Task<Void>) {
//                            editText.setText("")
//                        }
//                    })
//            }
//        })
//        checkChatRoom()
//    }
//
//    fun checkChatRoom() {
//        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/$uid")
//            .equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (item in dataSnapshot.getChildren()) {
//                    val chatModel: ChatModel = item.getValue(ChatModel::class.java)
//                    if (chatModel.users.containsKey(destinatonUid)) {
//                        chatRoomUid = item.getKey()
//                        button.setEnabled(true)
//                        recyclerView.setLayoutManager(LinearLayoutManager(this@MessageActivity))
//                        recyclerView.setAdapter(RecyclerViewAdapter())
//                    }
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {}
//        })
//    }
//
//    internal inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
//        var comments: MutableList<ChatModel.Comment>
//        var userModel: UserModel? = null
//
//        //메세지가 갱신
//        val messageList: Unit
//            get() {
//                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid)
//                    .child("comments").addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        comments.clear()
//                        for (item in dataSnapshot.getChildren()) {
//                            comments.add(item.getValue(ChatModel.Comment::class.java))
//                        }
//                        //메세지가 갱신
//                        notifyDataSetChanged()
//                        recyclerView.scrollToPosition(comments.size - 1)
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {}
//                })
//            }
//
//        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            val view: View = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_message, parent, false)
//            return MessageViewHolder(view)
//        }
//
//        fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            val messageViewHolder = holder as MessageViewHolder
//
//
//            //내가보낸 메세지
//            if (comments[position].uid.equals(uid)) {
//                messageViewHolder.textView_message.setText(comments[position].message)
//                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble)
//                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE)
//                messageViewHolder.textView_message.setTextSize(25f)
//                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT)
//                //상대방이 보낸 메세지
//            } else {
//                Glide.with(holder.itemView.getContext())
//                    .load(userModel.profileImageUrl)
//                    .apply(RequestOptions().circleCrop())
//                    .into(messageViewHolder.imageView_profile)
//                messageViewHolder.textview_name.setText(userModel.userName)
//                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE)
//                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble)
//                messageViewHolder.textView_message.setText(comments[position].message)
//                messageViewHolder.textView_message.setTextSize(25f)
//                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT)
//            }
//            val unixTime = comments[position].timestamp as Long
//            val date = Date(unixTime)
//            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
//            val time = simpleDateFormat.format(date)
//            messageViewHolder.textView_timestamp.setText(time)
//        }
//
//        val itemCount: Int
//            get() = comments.size
//
//        private inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var textView_message: TextView
//            var textview_name: TextView
//            var imageView_profile: ImageView
//            var linearLayout_destination: LinearLayout
//            var linearLayout_main: LinearLayout
//            var textView_timestamp: TextView
//
//            init {
//                textView_message =
//                    view.findViewById<View>(R.id.messageItem_textView_message) as TextView
//                textview_name = view.findViewById<View>(R.id.messageItem_textview_name) as TextView
//                imageView_profile =
//                    view.findViewById<View>(R.id.messageItem_imageview_profile) as ImageView
//                linearLayout_destination =
//                    view.findViewById<View>(R.id.messageItem_linearlayout_destination) as LinearLayout
//                linearLayout_main =
//                    view.findViewById<View>(R.id.messageItem_linearlayout_main) as LinearLayout
//                textView_timestamp =
//                    view.findViewById<View>(R.id.messageItem_textview_timestamp) as TextView
//            }
//        }
//
//        init {
//            comments = ArrayList<ChatModel.Comment>()
//            FirebaseDatabase.getInstance().getReference().child("users").child(destinatonUid)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        userModel = dataSnapshot.getValue(UserModel::class.java)
//                        messageList
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {}
//                })
//        }
//    }
//
//    fun onBackPressed() {
//        //super.onBackPressed();
//        finish()
//        overridePendingTransition(R.anim.fromleft, R.anim.toright)
//    }
//}