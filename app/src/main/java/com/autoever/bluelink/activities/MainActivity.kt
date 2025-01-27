package com.autoever.bluelink.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
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
    private lateinit var fuelTextView: TextView
    private lateinit var logoutImageView: ImageView
    private lateinit var tempMinusButton: ImageView
    private lateinit var tempPlusButton: ImageView
    private lateinit var tempDisplay: TextView
    private lateinit var tempSeekBar: SeekBar
    private lateinit var btnLock: ImageView
    private lateinit var btnUnlock: ImageView
    private lateinit var btnControl: TextView


    private var currentTemperature = 24.0
    private var isDoorLocked = true // 초기 문 상태

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private var carId: String = ""

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
        fuelTextView = findViewById(R.id.textView3)
        logoutImageView = findViewById(R.id.imageView5)
        tempMinusButton = findViewById(R.id.tempMinus)
        tempPlusButton = findViewById(R.id.tempPlus)
        tempDisplay = findViewById(R.id.tempDisplay)
        tempSeekBar = findViewById(R.id.tempSeekBar)
        btnLock = findViewById(R.id.btn_lock)
        btnUnlock = findViewById(R.id.btn_unlock)
        btnControl = findViewById(R.id.controlButton)

        // Firestore에서 차량 정보를 가져와 UI 업데이트
        fetchCarData()

        // 초기 문 상태 UI 반영
        updateDoorLockUI()

        // 버튼 클릭 이벤트
        carOnImageView.setOnClickListener { togglePowerState() }
        logoutImageView.setOnClickListener { logout() }
        tempMinusButton.setOnClickListener { adjustTemperature(-0.5) }
        tempPlusButton.setOnClickListener { adjustTemperature(0.5) }
        btnLock.setOnClickListener { lockDoors() }
        btnUnlock.setOnClickListener { unlockDoors() }
        btnControl.setOnClickListener { toControl() }

        tempSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentTemperature = 16.0 + (progress / 2.0)
                updateTempDisplay()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 초기 온도 표시
        tempSeekBar.progress = ((currentTemperature - 16) * 2).toInt()
        updateTempDisplay()
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
                    isDoorLocked = car.getBoolean("isDoorLocked") ?: true // 문 상태 가져오기

                    updateCarUI(carModel, carNickname)
                    updateCarImage(carModel)
                    updatePowerStateUI(isPowerOn)
                    updateDoorLockUI() // 문 상태 반영
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

    private fun toControl() {
        val intent = Intent(this, ControlActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun updateCarImage(model: String) {
        val imageRes = when (model.lowercase()) {
            "sonata" -> R.drawable.sonata
            "avante" -> R.drawable.avante
            "santa fe" -> R.drawable.santa_fe
            "tucson" -> R.drawable.tucson
            "palisade" -> R.drawable.palisade
            else -> android.R.color.darker_gray
        }
        carImageView.setImageResource(imageRes)
    }

    private fun updatePowerStateUI(isPowerOn: Boolean) {
        if (isPowerOn) {
            powerTextView.text = "켜짐"
            powerTextView.setTextColor(getColor(R.color.aeef_blue))
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

    private fun lockDoors() {
        if (!isDoorLocked) {
            isDoorLocked = true
            updateDoorLockStateInFirestore()
            updateDoorLockUI()
            Toast.makeText(this, "문이 잠겼습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun unlockDoors() {
        if (isDoorLocked) {
            isDoorLocked = false
            updateDoorLockStateInFirestore()
            updateDoorLockUI()
            Toast.makeText(this, "문이 열렸습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateDoorLockUI() {
        if (isDoorLocked) {
            btnLock.setBackgroundResource(R.drawable.circular_background_blue)
            btnUnlock.setBackgroundResource(R.drawable.circular_background)
        } else {
            btnLock.setBackgroundResource(R.drawable.circular_background)
            btnUnlock.setBackgroundResource(R.drawable.circular_background_blue)
        }
    }

    private fun updateDoorLockStateInFirestore() {
        if (carId.isEmpty()) return

        firestore.collection("cars").document(carId)
            .update("isDoorLocked", isDoorLocked)
            .addOnSuccessListener {
                // 성공 시 로깅 또는 추가 작업 가능
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "문 상태를 업데이트하지 못했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun adjustTemperature(delta: Double) {
        val newTemperature = currentTemperature + delta
        if (newTemperature in 16.0..30.0) {
            currentTemperature = newTemperature
            tempSeekBar.progress = ((currentTemperature - 16) * 2).toInt()
            updateTempDisplay()
        } else {
            Toast.makeText(
                this,
                if (newTemperature < 16.0) "최소 온도는 16.0°C입니다." else "최대 온도는 30.0°C입니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateTempDisplay() {
        tempDisplay.text = String.format("%.1f°C", currentTemperature)
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
        auth.signOut()
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
