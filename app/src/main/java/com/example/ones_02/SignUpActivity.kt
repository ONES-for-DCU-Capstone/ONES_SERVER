package com.example.ones_02

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ones_02.navigation.model.UserDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

class SignUpActivity : AppCompatActivity() {

    private lateinit var join_edittext_name : EditText
    private lateinit var join_edittext_nickname : EditText
    private lateinit var join_btn_Nickname_check : Button
    private lateinit var join_edittext_email : EditText
    private lateinit var join_edittext_PW : EditText
    private lateinit var join_edittext_PW_check : EditText
    private lateinit var join_btn_PW_check : Button

    private lateinit var join_edittext_PH : EditText
    private lateinit var join_btn_PH_verify : Button
    private lateinit var join_edittext_PH_verify : EditText
    private lateinit var join_btn_PH_verify_check : Button

    private lateinit var join_btn_Signup : Button

    var auth : FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        join_btn_Nickname_check.setOnClickListener {
            checkNickname()
        }

//        join_btn_PH_verify.setOnClickListener {
//            verifyPhoneNumber()
//        }

        join_btn_PW_check.setOnClickListener{
            checkpassword()
        }

        join_btn_Signup.setOnClickListener {
            signUp()
        }

    }

    private fun checkpassword() {
        val password = join_edittext_PW.text.toString()
        val confirmPassword = join_edittext_PW_check.text.toString()

        // Check if passwords match
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }else{
            Toast.makeText(this, "Password is match", Toast.LENGTH_SHORT).show()
            return

        }
    }

    private fun checkNickname() {
        val nickname = join_edittext_nickname.text.toString()

        // Check if nickname is available in Firestore
        firestore?.collection("users")
            ?.whereEqualTo("nickname", nickname)
            ?.get()
            ?.addOnSuccessListener { result ->
                if (result.isEmpty) {
                    // Nickname is available
                    Toast.makeText(this, "Nickname is available", Toast.LENGTH_SHORT).show()
                } else {
                    // Nickname is already taken
                    Toast.makeText(this, "Nickname is already taken", Toast.LENGTH_SHORT).show()
                }
            }
            ?.addOnFailureListener { exception ->
                Toast.makeText(this, "Error checking nickname: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun signUp() {
        val name = join_edittext_name.text.toString()
        val nickname = join_edittext_nickname.text.toString()
        val email = join_edittext_email.text.toString()
        val password = join_edittext_PW.text.toString()
        val confirmPassword = join_edittext_PW_check.text.toString()
        val phoneNumber = join_edittext_PH.text.toString()

        // Check if all fields are filled out   // || phoneNumber.isEmpty()
        if (name.isEmpty() || nickname.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if nickname is available in Firestore
        firestore?.collection("users")
            ?.whereEqualTo("nickname", nickname)
            ?.get()
            ?.addOnSuccessListener { result ->
                if (result.isEmpty) {
                    // Nickname is available, create a new user account using Firebase Authentication
                    auth?.createUserWithEmailAndPassword(nickname, password)
                        ?.addOnSuccessListener { authResult ->
                            // Save user data to Firestore
                            val user = UserDTO(name, nickname, email, password )
                            firestore?.collection("users")
                                ?.document(authResult.user!!.uid)
                                ?.set(user)
                                ?.addOnSuccessListener {
                                    Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                ?.addOnFailureListener { exception ->
                                    Toast.makeText(this, "Error saving user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        ?.addOnFailureListener { exception ->
                            Toast.makeText(this, "Error creating user account: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Nickname is already taken
                    Toast.makeText(this, "Nickname is already taken", Toast.LENGTH_SHORT).show()
                }
            }
            ?.addOnFailureListener { exception ->
                Toast.makeText(this, "Error checking nickname: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}