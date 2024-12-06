package com.autoever.bluelink.models

data class Car(
    var id: String = "",             // 'var'로 변경하여 재할당 가능
    val owner: String,               // 차량 소유자 (사용자 ID)
    val model: String,               // 차량 모델명
    val number: String,              // 차량 번호
    val year: Int,                   // 제조 연도
    val fuelType: FuelType,          // 연료 타입
    var nickname: String, // 차량 별칭 (기본값: "My Car")
    var isEngineOn: Boolean = false, // 시동 여부 (기본값 false)
    var isDoorOpen: Boolean = false, // 문열림 여부 (기본값 false)
    var currentFuel: Int = 232       // 현재 주유량 (기본값 232km)
)

enum class FuelType(val displayName: String) {
    GASOLINE("Gasoline"),
    DIESEL("Diesel"),
    ELECTRIC("Electric");

    companion object {
        fun fromDisplayName(name: String): FuelType? {
            return values().find { it.displayName == name }
        }
    }
}
