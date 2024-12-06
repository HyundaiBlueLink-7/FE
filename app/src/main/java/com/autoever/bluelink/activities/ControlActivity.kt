package com.autoever.bluelink.activities

import android.content.Intent
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
    private lateinit var closeButton: ImageView


    // 버튼 상태 관리 (true: 활성화, false: 비활성화)
    private val buttonStates = mutableMapOf<ImageView, Boolean>()

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
        closeButton = findViewById(R.id.close)

        // 버튼 상태 초기화
        buttonStates[lockButton] = false
        buttonStates[unlockButton] = false
        buttonStates[startButton] = false
        buttonStates[stopButton] = false
        buttonStates[lightButton] = false
        buttonStates[sirenButton] = false

        // 버튼 클릭 이벤트 설정
        lightButton.setOnClickListener { toggleButton(lightButton) }
        sirenButton.setOnClickListener { toggleButton(sirenButton) }

        startButton.setOnClickListener {
            toggleButton(startButton)
            if (buttonStates[startButton] == true) {
                disableOtherButton(stopButton)
            }
        }

        stopButton.setOnClickListener {
            toggleButton(stopButton)
            if (buttonStates[stopButton] == true) {
                disableOtherButton(startButton)
            }
        }

        lockButton.setOnClickListener {
            toggleButton(lockButton)
            if (buttonStates[lockButton] == true) {
                disableOtherButton(unlockButton)
            }
        }

        unlockButton.setOnClickListener {
            toggleButton(unlockButton)
            if (buttonStates[unlockButton] == true) {
                disableOtherButton(lockButton)
            }
        }

        closeButton.setOnClickListener( {
            toMain()
        })
    }

    // 현재 온도를 TextView에 표시하는 함수
    private fun updateTemperatureDisplay() {
        tvCurrentTemp.text = String.format("%.1f℃", currentTemp)
    }

    // 버튼 상태를 토글하는 함수
    private fun toggleButton(button: ImageView) {
        val isActive = buttonStates[button] ?: false
        if (isActive) {
            // 버튼이 활성화된 상태 -> 비활성화로 변경
            button.setBackgroundResource(R.drawable.circular_background) // 기본 배경으로 변경
            buttonStates[button] = false
        } else {
            // 버튼이 비활성화된 상태 -> 활성화로 변경
            button.setBackgroundResource(R.drawable.btn_shadow) // 활성화 배경으로 변경
            buttonStates[button] = true
        }
    }

    private fun toMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    // 다른 버튼 비활성화 (시동 켜기/끄기 전용)
    private fun disableOtherButton(button: ImageView) {
        button.setBackgroundResource(R.drawable.circular_background) // 기본 배경으로 설정
        buttonStates[button] = false
    }

}
