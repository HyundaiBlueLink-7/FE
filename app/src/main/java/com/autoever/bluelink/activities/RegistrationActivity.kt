package com.autoever.bluelink.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.autoever.bluelink.R
import com.autoever.bluelink.models.Car
import com.autoever.bluelink.models.FuelType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationActivity : AppCompatActivity() {

    private lateinit var carImageView: ImageView
    private lateinit var carModelSpinner: Spinner
    private lateinit var fuelTypeSpinner: Spinner
    private lateinit var carNumberEditText: EditText
    private lateinit var yearEditText: EditText
    private lateinit var registerCarButton: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // FirebaseAuth 및 Firestore 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // View 초기화
        carImageView = findViewById(R.id.carImageView)
        carModelSpinner = findViewById(R.id.carModelSpinner)
        fuelTypeSpinner = findViewById(R.id.fuelTypeSpinner)
        carNumberEditText = findViewById(R.id.carNumberEditText)
        yearEditText = findViewById(R.id.yearEditText)
        registerCarButton = findViewById(R.id.registerCarButton)

        // 차량 모델 목록
        val carModels = listOf("선택하세요", "Sonata", "Avante", "Santa Fe", "Tucson", "Palisade")
        // 연료 타입 목록
        val fuelTypes = listOf("선택하세요", "Gasoline", "Diesel", "Electric")

        // Spinner 어댑터 설정
        val carAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, carModels)
        carAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        carModelSpinner.adapter = carAdapter

        val fuelAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fuelTypes)
        fuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        fuelTypeSpinner.adapter = fuelAdapter

        // 차량 모델 Spinner 선택 이벤트 처리
        carModelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: android.view.View,
                position: Int,
                id: Long
            ) {
                val selectedCar = carModels[position]
                updateCarImage(selectedCar)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                carImageView.setImageResource(android.R.color.darker_gray)
            }
        }

        // 등록 버튼 클릭 이벤트 처리
        registerCarButton.setOnClickListener {
            val selectedCar = carModelSpinner.selectedItem.toString()
            val selectedFuel = fuelTypeSpinner.selectedItem.toString()
            val carNumber = carNumberEditText.text.toString().trim()
            val manufactureYear = yearEditText.text.toString().trim()

            // 현재 로그인된 사용자 ID 가져오기
            val currentUser = auth.currentUser
            val userId = currentUser?.uid

            if (userId == null) {
                Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 연료 타입을 FuelType으로 변환
            val fuelType = FuelType.fromDisplayName(selectedFuel)

            if (selectedCar == "선택하세요" || fuelType == null || carNumber.isEmpty() || manufactureYear.isEmpty()) {
                Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val car = Car(
                    id = "", // Firebase에서 생성된 ID
                    owner = userId, // 현재 로그인된 유저 아이디
                    model = selectedCar,
                    number = carNumber,
                    year = manufactureYear.toInt(),
                    fuelType = fuelType
                )
                saveCarToFirestore(car)
            }
        }
    }

    // 선택된 차량 모델에 따라 이미지를 업데이트하는 함수
    private fun updateCarImage(carModel: String) {
        when (carModel) {
            "Sonata" -> carImageView.setImageResource(R.drawable.sonata)
            "Avante" -> carImageView.setImageResource(R.drawable.avante)
            "Santa Fe" -> carImageView.setImageResource(R.drawable.santa_fe)
            "Tucson" -> carImageView.setImageResource(R.drawable.tucson)
            "Palisade" -> carImageView.setImageResource(R.drawable.palisade)
            else -> carImageView.setImageResource(android.R.color.darker_gray)
        }
    }

    // Firestore에 차량 정보 저장
    private fun saveCarToFirestore(car: Car) {
        val carDocument = firestore.collection("cars").document() // 문서 ID 자동 생성
        car.id = carDocument.id // Firestore 문서 ID를 차량 ID로 설정

        carDocument.set(car)
            .addOnSuccessListener {
                Toast.makeText(this, "차량 등록 성공: ${car.model}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "차량 등록 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
