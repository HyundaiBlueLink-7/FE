package com.autoever.bluelink.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.autoever.bluelink.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var carOnImageView: ImageView
    private lateinit var powerTextView: TextView
    private lateinit var powerIndicatorImageView: ImageView
    private lateinit var carNameTextView: TextView
    private lateinit var carNicknameTextView: TextView
    private lateinit var carImageView: ImageView
    private lateinit var fuelTextView: TextView // 추가: 연료 정보
    private lateinit var logoutImageView: ImageView
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var carId: String = "" // 차량 ID 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // View 초기화
        carOnImageView = findViewById(R.id.carOn)
        powerTextView = findViewById(R.id.textView2)
        powerIndicatorImageView = findViewById(R.id.imageView2)
        carNameTextView = findViewById(R.id.textViewCarName)
        carNicknameTextView = findViewById(R.id.textViewCarNickname)
        carImageView = findViewById(R.id.imageView6)
        fuelTextView = findViewById(R.id.textView3) // 추가: 연료 정보 초기화
        logoutImageView = findViewById(R.id.imageView5)

        // Firestore에서 차량 정보를 가져와 UI 업데이트
        fetchCarData()

        // 시동 버튼 클릭 이벤트
        carOnImageView.setOnClickListener {
            togglePowerState()
        }

        // 로그아웃 버튼 클릭 이벤트
        logoutImageView.setOnClickListener {
            logout()
        }
    }

    private fun fetchCarData() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("cars")
            .whereEqualTo("owner", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val car = querySnapshot.documents[0]
                    carId = car.id
                    val carModel = car.getString("model") ?: "모델 없음"
                    val carNickname = car.getString("nickname") ?: "별칭 없음"
                    val isPowerOn = car.getBoolean("isPowerOn") ?: false
                    val currentFuel = car.getLong("currentFuel") ?: 0L // 추가: 연료 정보

                    updateCarUI(carModel, carNickname)
                    updateCarImage(carModel)
                    updateFuelUI(currentFuel)
                    updatePowerStateUI(isPowerOn)
                } else {
                    Toast.makeText(this, "등록된 차량이 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "데이터를 가져오지 못했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateCarUI(model: String, nickname: String) {
        carNameTextView.text = model
        carNicknameTextView.text = nickname
    }

    private fun updateCarImage(model: String) {
        val imageRes = when (model.lowercase()) {
            "sonata" -> R.drawable.sonata
            "avante" -> R.drawable.avante
            "santa fe" -> R.drawable.santa_fe
            "tucson" -> R.drawable.tucson
            "palisade" -> R.drawable.palisade
            else -> android.R.color.darker_gray // 회색 배경
        }
        carImageView.setImageResource(imageRes)
    }

    private fun updateFuelUI(currentFuel: Long) {
        fuelTextView.text = "${currentFuel}km" // 연료 정보 업데이트
    }

    private fun updatePowerStateUI(isPowerOn: Boolean) {
        if (isPowerOn) {
            powerTextView.text = "켜짐"
            powerTextView.setTextColor(getColor(R.color.aeef_blue)) // #00AEEF
            carOnImageView.setImageResource(R.drawable.power_on)
            carOnImageView.setBackgroundResource(R.drawable.circular_background_blue)
            powerIndicatorImageView.setImageResource(R.drawable.power_on)
        } else {
            powerTextView.text = "꺼짐"
            powerTextView.setTextColor(getColor(R.color.black))
            carOnImageView.setImageResource(R.drawable.power_off)
            carOnImageView.setBackgroundResource(R.drawable.circular_background)
            powerIndicatorImageView.setImageResource(R.drawable.power_off)
        }
    }

    private fun togglePowerState() {
        if (carId.isEmpty()) {
            Toast.makeText(this, "차량 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("cars").document(carId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val currentPowerState = documentSnapshot.getBoolean("isPowerOn") ?: false
                val newPowerState = !currentPowerState

                firestore.collection("cars").document(carId)
                    .update("isPowerOn", newPowerState)
                    .addOnSuccessListener {
                        updatePowerStateUI(newPowerState)
                        Toast.makeText(
                            this,
                            if (newPowerState) "시동이 켜졌습니다." else "시동이 꺼졌습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "상태를 업데이트하지 못했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "데이터를 가져오지 못했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logout() {
        auth.signOut() // Firebase 로그아웃
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()

        // 로그인 화면으로 이동
        val intent = Intent(this, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) // 이전 액티비티 스택 제거
        startActivity(intent)
        finish()
    }
}
