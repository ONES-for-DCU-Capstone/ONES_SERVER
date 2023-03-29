package com.example.ones_02.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.ones_02.R
import com.example.ones_02.navigation.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
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
        setContentView(R.layout.activity_add_photo)

        //Intitiate Storge 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //Open the album
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMSGE_FROM_ALBUM)

        //add image upload event
        val addphoto_btn_upload = findViewById<Button>(R.id.addphoto_btn_upload)

        addphoto_btn_upload.setOnClickListener{
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMSGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                //this is path to the selecte image
                photoUri = data?.data

                val addphoto_image = findViewById<ImageView>(R.id.addphoto_image)
                addphoto_image.setImageURI(photoUri)
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

            //업로드 방식은 2가지 Promise method와 Callback method, 구글에서 Promis method를 더 선호

            //Promise method
            storageRef?.putFile(photoUri!!)?.continueWithTask{ task: Task<UploadTask.TaskSnapshot> ->
                return@continueWithTask storageRef.downloadUrl
            }?.addOnSuccessListener { uri ->
                var contentDTO = ContentDTO()

                //Insert downloadUrl of image
                contentDTO.imageUri = uri.toString()

                //Insert uid of user
                contentDTO.uid = auth?.currentUser?.uid

                contentDTO.userId = auth?.currentUser?.email

                contentDTO.title = addphoto_title.text.toString()

                contentDTO.explain = addphoto_edit_explain.text.toString()

                contentDTO.timestamp = System.currentTimeMillis()

                firestore?.collection("images")?.document()?.set(contentDTO)

                setResult(Activity.RESULT_OK)
                finish()
            }

            //FileUpload, Callback method
//            storageRef?.putFile(photoUri!!)?.addOnSuccessListener{
//                Toast.makeText(this,getString(R.string.upload_success),Toast.LENGTH_LONG).show()
//                storageRef.downloadUrl.addOnSuccessListener { uri ->
//                    var contentDTO = ContentDTO()
//
//                    //Insert downloadUrl of image
//                    contentDTO.imageUri = uri.toString()
//
//                    //Insert uid of user
//                    contentDTO.uid = auth?.currentUser?.uid
//
//                    contentDTO.userId = auth?.currentUser?.email
//
//                    contentDTO.explain = addphoto_edit_explain.text.toString()
//
//                    contentDTO.timestamp = System.currentTimeMillis()
//
//                    firestore?.collection("images")?.document()?.set(contentDTO)
//
//                    setResult(Activity.RESULT_OK)
//                    finish()
//                }
//            }
        }

}