package com.autoever.bluelink.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.autoever.bluelink.R
import com.google.firebase.auth.FirebaseAuth

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // 로그인된 상태라면 MainActivity로 이동
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // IntroActivity 종료
        } else {
            // 로그인되지 않은 상태에서는 IntroActivity 표시
            setContentView(R.layout.activity_intro)

            // 회원가입 버튼 클릭 시 SignUpActivity로 이동
            findViewById<View>(R.id.textViewSignUp).setOnClickListener {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            // 로그인 버튼 클릭 시 LoginActivity로 이동
            findViewById<View>(R.id.textViewLogin).setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
