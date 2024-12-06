package com.autoever.bluelink.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.autoever.bluelink.R
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var logoutImageView: ImageView
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 로그아웃 버튼 초기화
        logoutImageView = findViewById(R.id.imageView5)

        // 로그아웃 클릭 이벤트
        logoutImageView.setOnClickListener {
            performLogout()
        }
    }

    // 로그아웃 처리 함수
    private fun performLogout() {
        auth.signOut() // Firebase 인증 로그아웃
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

        // 로그인 화면으로 이동
        val intent = Intent(this, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // 이전 액티비티 스택 제거
        startActivity(intent)

        // 현재 액티비티 종료
        finish()
    }
}
