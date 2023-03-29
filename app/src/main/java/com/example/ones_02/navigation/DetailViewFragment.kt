package com.example.ones_02.navigation

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ones_02.R
import com.example.ones_02.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.*
import kotlinx.android.synthetic.main.item_detail.view.*

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
            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUri).into(viewholder.content_imageview)

            //title
            viewholder.title_textview.text = contentDTOs!![p1].title

            //Explain of content
//            viewholder.explain_textview.text = contentDTOs!![p1].explain

            //likes
            viewholder.like_textview.text = "likes " + contentDTOs!![p1].favoriteCount

            //ProfileImage
//            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUri).into(viewholder.profile_imageview)

            //like button에 이벤트를 달아줌
            viewholder.favorite_imageview.setOnClickListener{
                favoriteEvent(p1)
            }
            //좋아요 카운터와 색 이벤트
            if(contentDTOs!![p1].favorites.containsKey(uid)){
                //좋아요 클린한 경우
                viewholder.favorite_imageview.setImageResource(R.drawable.ic_favorite)
            }else{
                //좋아요 버튼 클릭하지 않은 경우
                viewholder.favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
            }

            //chat btn click
//            viewholder.chat_btn.setOnClickListener {
//                chat_btnEvent(p1)
//            }
            //p0.itemView = chat_btn  <- p0.itemView는 전체 테이블을 의미
            viewholder.chat_btn.setOnClickListener(View.OnClickListener { view ->
                val intent = Intent(view.context, ChatRoomActivity::class.java)
                intent.putExtra("destinationUid", contentDTOs[p1].uid)
                var activityOptions: ActivityOptions? = null

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    activityOptions = ActivityOptions.makeCustomAnimation(
                        view.context,
                        R.anim.fromright,
                        R.anim.toleft
                    )
                    startActivity(intent, activityOptions.toBundle())
                }

            })

//            holder.itemView.setOnClickListener(View.OnClickListener { view ->
//                val intent = Intent(view.context, ChatRoomActivity::class.java)
//                intent.putExtra("destinationUid", userModels[position].uid)
//                var activityOptions: ActivityOptions? = null
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    activityOptions = ActivityOptions.makeCustomAnimation(
//                        view.context,
//                        R.anim.fromright,
//                        R.anim.toleft
//                    )
//                    startActivity(intent, activityOptions.toBundle())
//                }
//            })

//            viewholder.profile_imageview.setOnClickListener {
//                var fragment = UserFragment()
//                var bundle = Bundle()
//                bundle.putString("destinationUid",contentDTOs[p1].uid)
//                bundle.putString("userId",contentDTOs[p1].userId)
//                fragment.arguments = bundle
//                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.main_content,fragment)?.commit()
//            }
        }

        //chat
//        private val auth: FirebaseAuth by lazy {
//            Firebase.auth
//        }
//        private lateinit var userDB: DatabaseReference
//        var uid : String? = null
//
//        private fun chat_btnEvent( p1: Int) {
////          var view = LayoutInflater.from(activity).inflate(R.layout.item_chatlist, container,false)
//            userDB = Firebase.database.reference.child(DB_USER)
//            uid = arguments?.getString("destinationUid")
//
//                if (auth.currentUser?.uid != uid) {
//
//                    val chatRoom = ChatDTO(
//                        buyerId = auth.currentUser!!.uid,
//                        sellerId = uid,
//                        itemTitle = "please",
//                        key = System.currentTimeMillis()
//                    )
//
//                    userDB.child(auth.currentUser!!.uid)
//                        .child(DBKey.CHILD_CHAT)
//                        .push()
//                        .setValue(chatRoom)
//
//                    userDB.child(uid!!)
//                        .child(DBKey.CHILD_CHAT)
//                        .push()
//                        .setValue(chatRoom)
//
////                    Snackbar.make(view, "채팅방이 생성되었습니다. 채팅탭에서 확인해주세요.", Snackbar.LENGTH_LONG).show()
//                    Toast.makeText(context, "성공", Toast.LENGTH_SHORT).show()
//                } else {
////                    Snackbar.make(view, "내가 올린 아이템 입니다.", Snackbar.LENGTH_LONG).show()
//                    Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show()
//                }
//        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        fun favoriteEvent(position : Int){
            //tsDoc
            var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
            //데이터 입력을 위해서 transcation을 불러와야한다.
            firestore?.runTransaction{transaction ->
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
                if(contentDTO!!.favorites.containsKey(uid)){
                    //좋아요 버튼을 취소
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount?.minus(1)
                    contentDTO?.favorites.remove(uid)
                }else{
                    //좋아요 버튼을 누르지 않았을때.
                    contentDTO?.favoriteCount = contentDTO?.favoriteCount?.plus(1)
                    contentDTO?.favorites[uid!!] = true
                }
                //transaction을 server로 되 돌려줌.
                transaction.set(tsDoc,contentDTO)
            }
        }
    }
}