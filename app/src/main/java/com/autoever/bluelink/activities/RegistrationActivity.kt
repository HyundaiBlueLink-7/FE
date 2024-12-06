package com.autoever.bluelink.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.autoever.bluelink.R

class RegistrationActivity : AppCompatActivity() {

    private lateinit var carImageView: ImageView
    private lateinit var carModelSpinner: Spinner
    private lateinit var carNumberEditText: EditText
    private lateinit var registerCarButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // View 초기화
        carImageView = findViewById(R.id.carImageView)
        carModelSpinner = findViewById(R.id.carModelSpinner)
        carNumberEditText = findViewById(R.id.carNumberEditText)
        registerCarButton = findViewById(R.id.registerCarButton)

        // 차량 모델 목록
        val carModels = listOf("선택하세요", "Sonata", "Avante", "Santa Fe", "Tucson", "Palisade")

        // Spinner 어댑터 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, carModels)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        carModelSpinner.adapter = adapter

        // Spinner 선택 이벤트 처리
        carModelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val selectedCar = carModels[position]
                updateCarImage(selectedCar)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 기본 이미지 유지
                carImageView.setImageResource(android.R.color.darker_gray)
            }
        }

        // 등록 버튼 클릭 이벤트 처리
        registerCarButton.setOnClickListener {
            val selectedCar = carModelSpinner.selectedItem.toString()
            val carNumber = carNumberEditText.text.toString().trim()

            if (selectedCar == "선택하세요" || carNumber.isEmpty()) {
                Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "차량 등록 완료: $selectedCar ($carNumber)", Toast.LENGTH_SHORT).show()
                // 여기서 Firebase 또는 서버로 데이터 전송 가능
            }
        }
    }

    // 선택된 차량 모델에 따라 이미지를 업데이트하는 함수
    private fun updateCarImage(carModel: String) {
        when (carModel) {
            "Sonata" -> carImageView.setImageResource(R.drawable.sonata) // Sonata 이미지
            "Avante" -> carImageView.setImageResource(R.drawable.avante) // Avante 이미지
            "Santa Fe" -> carImageView.setImageResource(R.drawable.santa_fe) // Santa Fe 이미지
            "Tucson" -> carImageView.setImageResource(R.drawable.tucson) // Tucson 이미지
            "Palisade" -> carImageView.setImageResource(R.drawable.palisade) // Palisade 이미지
            else -> carImageView.setImageResource(android.R.color.darker_gray) // 기본 이미지
        }
    }
}
