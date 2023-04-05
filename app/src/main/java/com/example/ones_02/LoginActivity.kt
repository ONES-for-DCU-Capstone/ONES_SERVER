package com.example.ones_02

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    //회원가입
    var auth : FirebaseAuth? = null
    var googleSingInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Firebase 로그인 통합 관리하는 Object 만들기
        auth = FirebaseAuth.getInstance()
        val email_login_button = findViewById<AppCompatButton>(R.id.email_login_button)
        val google_login_button = findViewById<AppCompatButton>(R.id.google_login_button)

        email_login_button.setOnClickListener{
            signinAndsignup()
        }
        google_login_button.setOnClickListener{
            //First step
            googleLogin()
        }

        signup_button.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSingInClient = GoogleSignIn.getClient(this,gso)
    }

    //자동로그인
    override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
    }

    fun googleLogin(){
        var signInIntent = googleSingInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if(result!!.isSuccess){
                var account = result!!.signInAccount
                //Second step
                firebaseAuthWithGoogle(account!!)
            }
        }
    }
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener{
                    task ->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else{
                    //Show the error message
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    fun signinAndsignup(){
        val edittext_id = findViewById<EditText>(R.id.edittext_id)
        val edittext_password = findViewById<EditText>(R.id.edittext_password)

        auth?.createUserWithEmailAndPassword(edittext_id.text.toString(),
                    edittext_password.text.toString())?.addOnCompleteListener{
                        task ->
                            if(task.isSuccessful){
                                //Creating a user account
                                moveMainPage(task.result?.user)
                                                    //result뒤에 ?는 왜 붙일까?
                            }
//                            else if(task.exception?.message.isNullOrEmpty()){
//                                //Show the error message
//                                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
//                            }
                            else{
                                //Login if you have account
                                signinEmail()
                            }
        }
    }

    fun signinEmail(){
        val edittext_id = findViewById<EditText>(R.id.edittext_id)
        val edittext_password = findViewById<EditText>(R.id.edittext_password)

        //이부분이 createUserWithEmailAndPassword와 동일해서 같은 아이디일 경우 로그인이 안됨.. ㅜ
        auth?.signInWithEmailAndPassword(edittext_id.text.toString(),edittext_password.text.toString())
            ?.addOnCompleteListener{
                task ->
            if(task.isSuccessful){
                //Login
                moveMainPage(task.result?.user)
            }else{
                //Show the error message
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    // 로그인 시 다른 화면으로 전환하는 것
    fun moveMainPage(user:FirebaseUser?){
        if(user != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}