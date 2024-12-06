package com.autoever.bluelink.activities

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.autoever.bluelink.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Firebase 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setUI()
    }

    fun setUI() {
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

        // "로그인 버튼"
        val textViewContinue = findViewById<TextView>(R.id.textViewContinue)
        textViewContinue.setOnClickListener {
            login(editTextEmail.text.toString(), editTextPassword.text.toString())
        }
    }

    // 로그인 함수
    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        checkCarRegistration(currentUser.uid) // 차량 등록 확인
                    }
                } else {
                    // 로그인 실패
                    Toast.makeText(
                        baseContext,
                        "로그인 실패: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    // 차량 등록 여부 확인
    private fun checkCarRegistration(userId: String) {
        firestore.collection("cars")
            .whereEqualTo("owner", userId) // 현재 사용자가 소유한 차량 검색
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // 차량이 등록되어 있음
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    // 차량이 등록되어 있지 않음
                    val intent = Intent(this, RegistrationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "차량 정보를 확인하는 중 오류가 발생했습니다: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
