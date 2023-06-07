package com.example.ones_02.navigation.recipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ones_02.R
import com.example.ones_02.navigation.model.RecipeDTO
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_frag_search.*
import kotlinx.android.synthetic.main.item_recipe_search.view.*
class FragRecipeSearch : Fragment() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private var recipepostDTOs: ArrayList<RecipeDTO> = ArrayList()

    private lateinit var searchEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_frag_search, container, false)

        view.findViewById<ImageView>(R.id.search_btn_plus)?.setOnClickListener {
            val intent = Intent(requireActivity(), ActivityRecipeAddPost::class.java)
            startActivity(intent)
        }

        firestore = FirebaseFirestore.getInstance()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recipesearchview_recyclerveiw)
        adapter = RecipeSearchViewRecyclerViewAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val recipesearch_btn = view.findViewById<ImageButton>(R.id.recipesearch_btn)
        searchEditText = view.findViewById(R.id.search_edittext_recipe_word)

        recipesearch_btn.setOnClickListener {
            val searchQuery = searchEditText.text.toString()
            firestore.collection("recipepost")
                .whereGreaterThanOrEqualTo("title", searchQuery)
                .whereLessThanOrEqualTo("title", searchQuery + "\uf8ff")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    recipepostDTOs.clear()
                    for (document in querySnapshot) {
                        val recipepostDTO = document.toObject(RecipeDTO::class.java)
                        recipepostDTOs.add(recipepostDTO)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "Error searching posts: $exception")
                }
        }

        return view
    }

    inner class RecipeSearchViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_search, parent, false)
            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val recipepostDTO = recipepostDTOs[position]

            val viewHolder = holder as CustomViewHolder
            viewHolder.itemView.search_post_recipe_title.text = recipepostDTO.title
            viewHolder.itemView.search_post_recipe_content.text = recipepostDTO.explain

            // Load Images
            Glide.with(holder.itemView.context)
                .load(recipepostDTO.imageUri)
                .into(viewHolder.itemView.search_post_recipe_img)
        }

        override fun getItemCount(): Int {
            return recipepostDTOs.size
        }

        private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)
    }
}



//class FragRecipeSearch :Fragment() {
//
//    private lateinit var firestore: FirebaseFirestore
//    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
//    private var recipepostDTOs: ArrayList<RecipeDTO> = ArrayList()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        val view = inflater.inflate(R.layout.fragment_frag_search, container, false)
//
//        view.findViewById<ImageView>(R.id.search_btn_plus)?.setOnClickListener {
//            val intent = Intent(requireActivity(), ActivityRecipeAddPost::class.java)
//            startActivity(intent)
//        }
//
//        firestore = FirebaseFirestore.getInstance()
//
//        val recyclerView = view.findViewById<RecyclerView>(R.id.recipesearchview_recyclerveiw)
//        // 이제 recyclerView를 사용하여 원하는 작업을 수행할 수 있습니다.
//
//        adapter = RecipeSearchViewRecyclerViewAdapter()
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
////        val recipesearch_btn = findViewById<ImageButton>(R.id.recipesearch_btn)
//
//
//
//        return view
//    }
//
//    inner class RecipeSearchViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//        init {
////            val recipesearch_btn = findViewById<ImageButton>(R.id.recipesearch_btn)
//
//            val recipesearch = view?.findViewById<ImageButton>(R.id.recipesearch_btn)
//
//            recipesearch?.setOnClickListener {
//                println("111")
//                // 클릭 이벤트에 원하는 동작을 추가하세요.
//                println("111")
//                val searchQuery = search_edittext_recipe_word.text.toString()
//                firestore.collection("recipepost")
//                    .whereGreaterThanOrEqualTo("title", searchQuery)
//                    .whereLessThanOrEqualTo("title", searchQuery + "\uf8ff")
//                    .get()
//                    .addOnSuccessListener { querySnapshot ->
//                        recipepostDTOs.clear()
//                        println(querySnapshot)
//
//                        for (document in querySnapshot) {
//                            val recipepostDTO = document.toObject(RecipeDTO::class.java)
//                            recipepostDTOs.add(recipepostDTO)
//                            println("-----------")
//                            println(recipepostDTOs)
//                        }
//                        adapter.notifyDataSetChanged()
//                    }
//                    .addOnFailureListener { exception ->
//                        println("3333")
//                        Log.d("TAG", "Error searching posts: $exception")
//                    }
//            }
//
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_search, parent, false)
//
//            return CustomViewHolder(view)
//
//        }
//        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            val recipepostDTO = recipepostDTOs[position]
//
//            val viewHolder = holder as FragRecipeSearch.RecipeSearchViewRecyclerViewAdapter.CustomViewHolder
//            viewHolder.itemView.search_post_recipe_title.text = recipepostDTO.title
//            viewHolder.itemView.search_post_recipe_content.text = recipepostDTO.explain
//
//            //Images
//            Glide.with(holder.itemView.context)
//                .load(recipepostDTOs!![position].imageUri)
//                .into(viewHolder.itemView.search_post_recipe_img)
//        }
//
//        override fun getItemCount(): Int {
//            return recipepostDTOs.size
//        }
//        private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)
//    }
//}