package com.autoever.bluelink.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.autoever.bluelink.R
import java.util.Calendar

class SignUpActivity : AppCompatActivity() {
    private lateinit var buttonDatePicker: TextView
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // 날짜 선택 버튼 초기화
        buttonDatePicker = findViewById(R.id.buttonDatePicker)

        // 날짜 선택 TextView 클릭 이벤트
        buttonDatePicker.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog 생성
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // 선택된 날짜를 TextView 텍스트로 설정
            selectedDate = "${selectedYear}년 ${selectedMonth + 1}월 ${selectedDay}일"
            buttonDatePicker.text = selectedDate
        }, year, month, day)

        // 다이얼로그 표시
        datePickerDialog.show()
    }
}
