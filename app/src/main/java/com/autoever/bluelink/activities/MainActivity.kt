package com.autoever.bluelink.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.autoever.bluelink.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var logoutImageView: ImageView
    private lateinit var carNameTextView: TextView
    private lateinit var carImageView: ImageView
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // View 초기화
        logoutImageView = findViewById(R.id.imageView5)
        carNameTextView = findViewById(R.id.textViewCarName)
        carImageView = findViewById(R.id.imageView6)

        // 로그아웃 클릭 이벤트
        logoutImageView.setOnClickListener {
            performLogout()
        }

        // 차량 정보 로드
        loadCarInfo()
    }

    private fun performLogout() {
        auth.signOut() // Firebase 인증 로그아웃
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

        // IntroActivity로 이동
        val intent = Intent(this, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun loadCarInfo() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid

        // Firestore에서 차량 정보 가져오기
        firestore.collection("cars")
            .whereEqualTo("owner", userId) // 현재 사용자 ID와 일치하는 차량 검색
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val carData = querySnapshot.documents[0] // 첫 번째 차량 정보 가져오기
                    val carName = carData.getString("model") ?: "차량 정보 없음"
                    carNameTextView.text = carName // 차량 이름 표시
                    updateCarImage(carName) // 차량 이미지 업데이트
                } else {
                    Toast.makeText(this, "등록된 차량이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "차량 정보를 가져오는 데 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateCarImage(carName: String) {
        when (carName) {
            "Sonata" -> carImageView.setImageResource(R.drawable.sonata)
            "Avante" -> carImageView.setImageResource(R.drawable.avante)
            "Santa Fe" -> carImageView.setImageResource(R.drawable.santa_fe)
            "Tucson" -> carImageView.setImageResource(R.drawable.tucson)
            "Palisade" -> carImageView.setImageResource(R.drawable.palisade)
            else -> carImageView.setImageResource(android.R.color.darker_gray) // 기본 이미지
        }
    }
}
