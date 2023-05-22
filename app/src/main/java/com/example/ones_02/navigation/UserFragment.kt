package com.example.ones_02.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.ones_02.LoginActivity
import com.example.ones_02.MainActivity
import com.example.ones_02.R
import com.example.ones_02.navigation.model.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.frag_my_profile.*
import kotlinx.android.synthetic.main.frag_my_profile.view.*

class UserFragment : Fragment() {
    var fragmentView : View? = null
    var firestore : FirebaseFirestore? = null
    var uid : String? = null
    var auth : FirebaseAuth? = null
    //나의 계정인지 상대방 계정인지 판단하는 변수.
    var currentUserUid : String? = null
    companion object{
        var PICK_PROFILE_FROM_ALBUM = 10
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentView = LayoutInflater.from(activity).inflate(R.layout.frag_my_profile,container,false)
        uid =arguments?.getString("destinationUid")
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserUid = auth?.currentUser?.uid

        println("-------------------")
        println(uid)

        println(auth)

        println(currentUserUid)

        //
        if(uid == currentUserUid){
            //myPage
//            fragmentView?.account_btn_follow_signout?.text = getString(R.string.signout)
            fragmentView?.myprofile_btn_logout?.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity,LoginActivity::class.java))
                auth?.signOut()
            }

            fragmentView?.myprofile_btn_location?.setOnClickListener {
                val intent = Intent(activity, LocationAuthActivity::class.java)
                startActivity(intent)
            }



        }else{
            //OtherPage
//            fragmentView?.account_btn_follow_signout?.text = getString(R.string.send)
            var mainActivity = (activity as MainActivity)
//            mainActivity?.toolbar_username?.text = arguments?.getString("userId")
//            mainActivity?.toolbar_btn_back?.setOnClickListener {
//                mainActivity.bottom_navigation.selectedItemId = R.id.action_home
//            }
//            mainActivity?.toolbar_title_image?.visibility = View.GONE
//            mainActivity?.toolbar_username?.visibility = View.VISIBLE
//            mainActivity?.toolbar_btn_back?.visibility = View.VISIBLE
        }
//        fragmentView?.account_recyclerview?.adapter = UserFragmentRecyclerViewAdapter()
//        fragmentView?.account_recyclerview?.layoutManager = GridLayoutManager(activity, 3)

        fragmentView?.myprofile_img_picture?.setOnClickListener{
            var photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            activity?.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
        }
        getProfileImage()
        getName()
        return fragmentView
    }
    fun getProfileImage(){
        firestore?.collection("profileImages")?.document(uid!!)?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot == null) return@addSnapshotListener
            if(documentSnapshot.data != null){
                var url = documentSnapshot?.data!!["image"]
                Glide.with(requireActivity()).load(url).apply(RequestOptions().circleCrop()).into(fragmentView?.myprofile_img_picture!!)
            }
        }
    }

    fun getName(){

        firestore?.collection("users")?.document(uid!!)?.get()
            ?.addOnSuccessListener { document ->
                if (document != null) {
                myprofile_text_username.text = document.getString("nickname")
            }
        }
    }


    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs : ArrayList<ContentDTO>  = arrayListOf()
        init {
            firestore?.collection("images")?.whereEqualTo("uid",uid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if(querySnapshot==null) return@addSnapshotListener

                    //Get data
                    for (snapshot in querySnapshot.documents){
                        contentDTOs.add(snapshot.toObject(ContentDTO::class.java)!!)
                    }
//                    fragmentView?.account_post_textview?.text = contentDTOs.size.toString()
//                    notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            //폭의 3분의 1 크기만 가져옴.
            var width = resources.displayMetrics.widthPixels /3

            var imageview = ImageView(parent.context)
            imageview.layoutParams = LinearLayoutCompat.LayoutParams(width,width)
            return CustomViewHolder(imageview)
        }
        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview) {
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageview = (holder as CustomViewHolder).imageview
            Glide.with(holder.itemView.context).load(contentDTOs[position].imageUri).apply(RequestOptions().centerCrop()).into(imageview)
        }

        override fun getItemCount(): Int {
            return contentDTOs.size
        }
    }
}