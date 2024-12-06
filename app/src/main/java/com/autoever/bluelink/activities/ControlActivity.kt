package com.autoever.bluelink.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.autoever.bluelink.R

class ControlActivity : AppCompatActivity() {

    private lateinit var tvCurrentTemp: TextView
    private lateinit var seekBarTemperature: SeekBar

    private lateinit var lockButton: ImageView
    private lateinit var unlockButton: ImageView
    private lateinit var startButton: ImageView
    private lateinit var stopButton: ImageView
    private lateinit var lightButton: ImageView
    private lateinit var sirenButton: ImageView

    private var currentTemp = 24.0f // 초기 온도 설정
    private val minTemp = 16.0f // 최소 온도
    private val maxTemp = 30.0f // 최대 온도

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        Log.d("msg", "here")

        // 온도 조절 UI 초기화
        tvCurrentTemp = findViewById(R.id.tvCurrentTemp)
        seekBarTemperature = findViewById(R.id.seekBarTemperature)
        val btnDecreaseTemp: Button = findViewById(R.id.btnDecreaseTemp)
        val btnIncreaseTemp: Button = findViewById(R.id.btnIncreaseTemp)

        // 초기 온도 설정
        seekBarTemperature.max = (maxTemp - minTemp).toInt()
        seekBarTemperature.progress = (currentTemp - minTemp).toInt()
        updateTemperatureDisplay()

        // 온도 감소 버튼
        btnDecreaseTemp.setOnClickListener {
            if (currentTemp > minTemp) {
                currentTemp -= 0.5f
                seekBarTemperature.progress = (currentTemp - minTemp).toInt()
                updateTemperatureDisplay()
            }
        }

        // 온도 증가 버튼
        btnIncreaseTemp.setOnClickListener {
            if (currentTemp < maxTemp) {
                currentTemp += 0.5f
                seekBarTemperature.progress = (currentTemp - minTemp).toInt()
                updateTemperatureDisplay()
            }
        }

        // SeekBar 변경 이벤트
        seekBarTemperature.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentTemp = minTemp + progress
                updateTemperatureDisplay()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // 버튼 UI 초기화
        lockButton = findViewById(R.id.lock)
        unlockButton = findViewById(R.id.unlock)
        startButton = findViewById(R.id.power)
        stopButton = findViewById(R.id.poweroff)
        lightButton = findViewById(R.id.light)
        sirenButton = findViewById(R.id.siren)

        // 버튼 클릭 이벤트 설정
        lockButton.setOnClickListener { handleButtonClick(lockButton) }
        unlockButton.setOnClickListener { handleButtonClick(unlockButton) }
        lightButton.setOnClickListener { handleButtonClick(lightButton) }
        sirenButton.setOnClickListener { handleButtonClick(sirenButton) }

        // "시동 켜기" 버튼
        startButton.setOnClickListener {
            handleButtonClick(startButton)
            disableOtherButton(stopButton)
        }

        // "시동 끄기" 버튼
        stopButton.setOnClickListener {
            handleButtonClick(stopButton)
            disableOtherButton(startButton)
        }
    }

    // 현재 온도를 TextView에 표시하는 함수
    private fun updateTemperatureDisplay() {
        tvCurrentTemp.text = String.format("%.1f℃", currentTemp)
    }

    // 버튼 클릭 시 배경 변경 처리
    private fun handleButtonClick(button: ImageView) {
        button.setBackgroundResource(R.drawable.btn_shadow) // 배경 변경
    }

    // 클릭된 버튼을 제외한 나머지 버튼 초기화
    private fun resetOtherButtons(activeButton: ImageView) {
        val buttons = listOf(lockButton, unlockButton, startButton, stopButton, lightButton, sirenButton)
        buttons.filter { it != activeButton }
            .forEach { it.setBackgroundResource(0) } // 배경 초기화
    }

    // "시동 켜기"와 "시동 끄기" 동시 활성화 방지
    private fun disableOtherButton(button: ImageView) {
        button.setBackgroundResource(0) // 배경 초기화
    }
}
