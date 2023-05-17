package com.example.ones_02.navigation.chat

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ones_02.R
import com.example.ones_02.navigation.ChatRoomActivity
import com.example.ones_02.navigation.DetailViewFragment
import com.example.ones_02.navigation.model.ChatDTO
import com.example.ones_02.navigation.model.ContentDTO
import com.example.ones_02.navigation.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.grpc.Context.key
import java.util.*
import kotlin.collections.ArrayList

class ChatListFragment : Fragment() {
    companion object{
        fun newInstance() : ChatListFragment {
            return ChatListFragment()
        }
    }
    private val firestore = FirebaseFirestore.getInstance()

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //프레그먼트를 포함하고 있는 액티비티에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    //뷰가 생성되었을 때
    //프레그먼트와 레이아웃을 연결시켜주는 부분
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chatlist, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.chatlistRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecyclerViewAdapter()

        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.CustomViewHolder>() {

        private val chatDTO = ArrayList<ChatDTO>()
        private var uid : String? = null
        private val destinationUsers : ArrayList<String> = arrayListOf()
        var chatroomsdocumentidlist : ArrayList<String> = arrayListOf()

        init {
            uid = FirebaseAuth.getInstance().currentUser?.uid
//            println("uid: $uid")

            firestore.collection("chatrooms")
                .whereEqualTo("users.$uid", true)
                .get()
                .addOnSuccessListener { documents ->
                    chatDTO.clear()
//                    println("documents: $documents")
                    chatroomsdocumentidlist.clear()
                    for (document in documents) {
                        chatDTO.add(document.toObject(ChatDTO::class.java))
                        println("document")
                        println(document)
                        chatroomsdocumentidlist.add(document.id)

                    }
                    notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting documents: ", exception)
                }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.item_chatlist, parent, false))
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageButton = itemView.findViewById(R.id.chatlist_img_user)
            val textView_Nickname : TextView = itemView.findViewById(R.id.chatlist_text_nickname)
//            val textView_title : TextView = itemView.findViewById(R.id.chat_textview_title)
            val textView_lastMessage : TextView = itemView.findViewById(R.id.chatlist_text_content)
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

            var destinationUid: String? = null
//            destinationUsers.clear()

            //채팅방에 있는 유저 모두 체크, 이를 통해서 채팅방 열수 있다.
            for (user in chatDTO[position].users.keys) {
                println("position: $position")
                println("user")
                println(user)
                if (!user.equals(uid)) {
                    destinationUid = user
                    destinationUsers.add(destinationUid)
                    println("destinationUsers: $destinationUsers")
                }
            }

            firestore.collection("users")
                .document(destinationUid.toString())
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    println("destinationUid: $destinationUid")
                    val user = documentSnapshot.toObject(UserDTO::class.java)
//                    Glide.with(holder.itemView.context).load(user?.profileImage)
//                        .apply(RequestOptions().circleCrop())
//                        .into(holder.imageView)
                    holder.textView_Nickname.text = user?.name
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting document: ", exception)
                }

            // 채팅방의 마지막 메시지 가져오기
            val chatroomId = chatroomsdocumentidlist[position]
            firestore.collection("chatrooms")
                .document(chatroomId)
                .collection("comments")
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val lastMessage = querySnapshot.documents[0].toObject(ChatDTO.Comment::class.java)?.message
                        holder.textView_lastMessage.text = lastMessage
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting last message: ", exception)
                }

            //채팅창 선책 시 이동
            holder.itemView.setOnClickListener {
                val intent = Intent(context, ChatRoomActivity::class.java)
                intent.putExtra("destinationUid", destinationUsers[position])
                context?.startActivity(intent)
            }
        }
        override fun getItemCount(): Int {
            return chatDTO.size
        }
    }
}