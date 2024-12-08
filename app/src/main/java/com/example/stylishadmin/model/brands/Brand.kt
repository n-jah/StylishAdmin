package com.example.stylishadmin.model.brands

data class Brand(
    val brandName: String = "",
    val imgIcon: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this("", "")
}
