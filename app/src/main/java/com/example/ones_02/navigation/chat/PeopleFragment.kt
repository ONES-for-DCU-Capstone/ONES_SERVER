//package com.example.ones_02.navigation.chat
//
//import android.app.Fragment
//import android.os.Build
//import android.support.v7.widget.LinearLayoutManager
//import android.view.View
//import com.bumptech.glide.request.RequestOptions
//
///**
// * Created by myeongsic on 2017. 9. 11..
// */
//class PeopleFragment : Fragment() {
//    @Nullable
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        @Nullable container: ViewGroup?,
//        savedInstanceState: Bundle
//    ): View? {
//        val view: View = inflater.inflate(R.layout.fragment_people, container, false)
//        val recyclerView: RecyclerView =
//            view.findViewById<View>(R.id.peoplefragment_recyclerview) as RecyclerView
//        recyclerView.setLayoutManager(LinearLayoutManager(inflater.getContext()))
//        recyclerView.setAdapter(PeopleFragmentRecyclerViewAdapter())
//        return view
//    }
//
//    internal inner class PeopleFragmentRecyclerViewAdapter :
//        RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
//        var userModels: MutableList<UserModel>
//        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            val view: View = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_friend, parent, false)
//            return CustomViewHolder(view)
//        }
//
//        fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            Glide.with(holder.itemView.getContext())
//                .load(userModels[position].profileImageUrl)
//                .apply(RequestOptions().circleCrop())
//                .into((holder as CustomViewHolder).imageView)
//            (holder as CustomViewHolder).textView.setText(
//                userModels[position].userName
//            )
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
//        }
//
//        val itemCount: Int
//            get() = userModels.size
//
//        private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            var imageView: ImageView
//            var textView: TextView
//
//            init {
//                imageView = view.findViewById<View>(R.id.frienditem_imageview) as ImageView
//                textView = view.findViewById<View>(R.id.frienditem_textview) as TextView
//            }
//        }
//
//        init {
//            userModels = ArrayList<UserModel>()
////            val myUid: String = FirebaseAuth.getInstance().getCurrentUser().getUid()
//            FirebaseDatabase.getInstance().getReference().child("users")
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        userModels.clear()
//                        for (snapshot in dataSnapshot.getChildren()) {
//                            val userModel: UserModel = snapshot.getValue(UserModel::class.java)
//                            if (userModel.uid.equals(myUid)) {
//                                continue
//                            }
//                            userModels.add(userModel)
//                        }
//                        notifyDataSetChanged()
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {}
//                })
//        }
//    }
//}