package com.autoever.bluelink.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.autoever.bluelink.R
import com.autoever.bluelink.models.Gender
import com.autoever.bluelink.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class SignUpActivity : AppCompatActivity() {
    private lateinit var buttonDatePicker: TextView
    private var selectedBirth: String = "" // 생년월일을 클래스 변수로 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setUI()
    }

    private fun setUI() {
        // EditText 초기화
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextName = findViewById<EditText>(R.id.editTextName)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)

        // 라디오 그룹 초기화 (성별 선택)
        val radioGroupGender = findViewById<RadioGroup>(R.id.radioGroupGender)

        // 생년월일 선택 버튼
        buttonDatePicker = findViewById(R.id.buttonDatePicker)

        // "가입하기" 버튼
        val textViewComplete = findViewById<TextView>(R.id.textViewComplete)

        // 생년월일 선택 이벤트
        buttonDatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                // 선택된 날짜를 텍스트와 클래스 변수에 저장
                selectedBirth = "${selectedYear}년 ${selectedMonth + 1}월 ${selectedDay}일"
                buttonDatePicker.text = selectedBirth
            }, year, month, day)

            datePickerDialog.show()
        }

        // 가입하기 버튼 클릭 이벤트
        textViewComplete.setOnClickListener {
            // 입력값 검증
            val email = editTextEmail.text.toString().trim()
            val name = editTextName.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || selectedBirth.isEmpty()) {
                Toast.makeText(this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 성별 선택 확인
            val selectedGenderId = radioGroupGender.checkedRadioButtonId
            val gender = if (selectedGenderId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedGenderId)
                when (selectedRadioButton.text.toString()) {
                    "남성" -> Gender.MALE
                    "여성" -> Gender.FEMALE
                    else -> {
                        Toast.makeText(this, "올바른 성별을 선택해주세요.", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            } else {
                Toast.makeText(this, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // User 객체 생성
            val user = User(
                id = "",
                email = email,
                name = name,
                birth = selectedBirth,
                gender = gender
            )

            // 가입 처리
            signUp(user, password)
        }
    }

    // 회원가입 처리 함수
    private fun signUp(user: User, password: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 회원가입 성공 시, Firebase Authentication에서 생성된 UID 가져오기
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // User 객체에 ID 설정
                        val updatedUser = user.copy(id = userId) // 새로운 User 객체 생성
                        // Firestore에 사용자 데이터 저장
                        saveUserData(updatedUser)
                    }
                } else {
                    // 에러 처리
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    // Firestore에 사용자 데이터 저장
    private fun saveUserData(user: User) {
        val firestore = FirebaseFirestore.getInstance()

        // 사용자 UID를 Firestore 문서 ID로 사용하여 저장
        firestore.collection("users")
            .document(user.id) // UID를 문서 ID로 사용
            .set(user) // 사용자 객체를 저장
            .addOnSuccessListener {
                Log.d("SignUpActivity", "User data successfully written!")

                // 메인 화면으로 이동
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e("SignUpActivity", "Error writing document", e)
            }
    }
}
