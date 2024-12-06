package com.autoever.bluelink.models

data class Car(
    var id: String = "",  // 'var'로 변경하여 재할당 가능
    val owner: String,
    val model: String,
    val number: String,
    val year: Int,
    val fuelType: FuelType
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
