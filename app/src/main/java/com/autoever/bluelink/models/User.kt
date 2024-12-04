package com.autoever.bluelink.models

data class User(
    var id: String = "",
    val email: String = "",
    val name: String = "",
    val birth: String = "",
    val gender: Gender = Gender.MALE,
)

enum class Gender {
    MALE,
    FEMALE
}