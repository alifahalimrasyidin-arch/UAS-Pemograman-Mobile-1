package com.example.uaspm1kelompok1

data class UserData(
    val name: String,
    val email: String,
    val phone: String,
    val gender: String,
    val role: String,  // staff_produksi, kepala_gudang, quality_control
    val password: String
)