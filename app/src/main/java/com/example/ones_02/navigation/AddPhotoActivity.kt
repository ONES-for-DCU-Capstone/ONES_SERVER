package com.example.ones_02.navigation

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.example.ones_02.R
import com.example.ones_02.navigation.model.ContentDTO
import com.example.ones_02.navigation.model.UserDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import kotlinx.android.synthetic.main.activity_post_wrt.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMSGE_FROM_ALBUM =0
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_wrt)

        //Intitiate Storge 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type= "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMSGE_FROM_ALBUM)

        //add image upload event
        val sendbtn = findViewById<ImageButton>(R.id.post_wrt_btn_send)

        sendbtn.setOnClickListener{
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMSGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
        //this is path to the selecte image
                photoUri = data?.data

                post_wrt_img_picture.setImageURI(photoUri)
            } else {
        //Exit the addPhotoActivity if you leave 사진촬영 취소시
                finish()
            }
        }
    }
    fun contentUpload(){
//make filename

        //파일명 중복되지 않게 날짜로 이름생성
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("image")?.child(imageFileName)


        // compress image before uploading
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, photoUri)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val data = baos.toByteArray()

//업로드 방식은 2가지 Promise method와 Callback method, 구글에서 Promis method를 더 선호

        //Promise method
        storageRef?.putBytes(data)?.continueWithTask{task: Task<UploadTask.TaskSnapshot>->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener{uri->
            var contentDTO = ContentDTO()

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val docRef = FirebaseFirestore.getInstance().collection("users").document(uid!!)
            docRef.get()
                .addOnSuccessListener{documentSnapshot->
                    if (documentSnapshot.exists()) {
                        val nickname = documentSnapshot.getString("nickname")
                        Log.d("Name", " $nickname")
                        contentDTO.usernickname = nickname.toString()

                        //Insert downloadUrl of image
                        contentDTO.imageUri = uri.toString()

                        //Insert uid of user
                        contentDTO.uid = auth?.currentUser?.uid

                        contentDTO.userId = auth?.currentUser?.email

                        contentDTO.title = post_wrt_edittext_title.text.toString()

                        contentDTO.explain = post_wrt_edittext_content.text.toString()

                        contentDTO.timestamp = System.currentTimeMillis()

                        contentDTO.price =post_wrt_edittext_price.text.toString() + "원"

                        val postId = firestore?.collection("images")?.document()?.id
                        contentDTO.id = postId

                        firestore?.collection("images")?.document(postId.toString())?.set(contentDTO)

                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener{exception->
                    Log.d(TAG, "get failed with ", exception)
                }
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}