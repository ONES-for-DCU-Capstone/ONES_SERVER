package com.example.ones_02.navigation

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

//        firestore?.collection("images")?.get()?.addOnSuccessListener { querySnapshot ->
//            // Loop through the documents in the QuerySnapshot
//            for (documentSnapshot in querySnapshot) {
//                // Get the document ID from each DocumentSnapshot
//                val documentId = documentSnapshot.id
//
////                Log.d("Document ID", documentId)
//            }
//        }


        val documentId = intent.getStringExtra("documentId")

//        val id = documentId.id
        Log.d("Document ID", documentId.toString())

        val docRef = firestore?.collection("images")?.document(documentId.toString())
        docRef?.get()
            ?.addOnSuccessListener { document ->
                if (document != null) {
                    val title = document.getString("title")
                    // Load title into a view
                    val textView = findViewById<TextView>(R.id.post_view_title)
                    textView.text = title
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            ?.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }
}