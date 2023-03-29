//package com.example.ones_02.navigation.chat
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.ones_02.R
//import com.example.ones_02.databinding.FragmentDetailBinding
//import com.example.ones_02.navigation.DetailViewFragment
//import com.example.ones_02.navigation.chat.DBKey.Companion.CHILD_CHAT
//import com.example.ones_02.navigation.chat.DBKey.Companion.DB_ARTICLES
//import com.example.ones_02.navigation.chat.DBKey.Companion.DB_USER
//import com.example.ones_02.navigation.model.ChatDTO
//import com.example.ones_02.navigation.model.ContentDTO
//import com.google.android.material.snackbar.Snackbar
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.ktx.auth
//import com.google.firebase.database.ChildEventListener
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.ktx.database
//import com.google.firebase.ktx.Firebase
//
//class DetailViewChat : Fragment(R.layout.fragment_detail) {
//
//    private lateinit var articleDB: DatabaseReference
//    private lateinit var userDB: DatabaseReference
//    private lateinit var articleAdapter: DetailViewFragment
//
//    private val articleList = mutableListOf<ContentDTO>()
//    private val listener = object: ChildEventListener {
//        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//
//            val articleModel = snapshot.getValue(ContentDTO::class.java)
//            articleModel ?: return
//
//            articleList.add(articleModel)
//            articleAdapter.submitList(articleList)
//        }
//
//        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
//
//        override fun onChildRemoved(snapshot: DataSnapshot) {}
//
//        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
//
//        override fun onCancelled(error: DatabaseError) {}
//
//    }
//
//    private var binding: FragmentDetailBinding? = null
//    private val auth: FirebaseAuth by lazy {
//        Firebase.auth
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val fragmentHomeBinding = FragmentDetailBinding.bind(view)
//        binding = fragmentHomeBinding
//
//        articleDB = Firebase.database.reference.child(DB_ARTICLES)
//        userDB = Firebase.database.reference.child(DB_USER)
//        articleAdapter = DetailViewFragment(onItemClicked = { ContentDTO ->
//            if (auth.currentUser != null) {
//                if (auth.currentUser?.uid != ContentDTO.sellerId) {
//
//                    val chatRoom = ChatDTO(
//                        buyerId = auth.currentUser!!.uid,
//                        sellerId = ContentDTO.sellerId,
//                        itemTitle = ContentDTO.title,
//                        key = System.currentTimeMillis()
//                    )
//
//                    userDB.child(auth.currentUser!!.uid)
//                        .child(CHILD_CHAT)
//                        .push()
//                        .setValue(chatRoom)
//
//                    userDB.child(ContentDTO.sellerId)
//                        .child(CHILD_CHAT)
//                        .push()
//                        .setValue(chatRoom)
//
//                    Snackbar.make(view, "채팅방이 생성되었습니다. 채팅탭에서 확인해주세요.", Snackbar.LENGTH_LONG).show()
//
//                } else {
//                    Snackbar.make(view, "내가 올린 아이템 입니다.", Snackbar.LENGTH_LONG).show()
//                }
//            } else {
//                Snackbar.make(view, "로그인 후 사용해주세요.", Snackbar.LENGTH_LONG).show()
//            }
//
//        })
//        articleList.clear()
//
////        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
////        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter
//
////        fragmentHomeBinding.addFloatingButton.setOnClickListener {
////            context?.let {
////
////                if (auth.currentUser != null) {
////                    val intent = Intent(it, AddArticleActivity::class.java)
////                    startActivity(intent)
////                } else {
////                    Snackbar.make(view, "로그인 후 사용해주세요.", Snackbar.LENGTH_LONG).show()
////                }
////
////            }
////        }
//
//        articleDB.addChildEventListener(listener)
//
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        articleAdapter.notifyDataSetChanged()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//
//        articleDB.removeEventListener(listener)
//    }
//}
