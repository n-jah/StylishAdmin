package com.example.stylishadmin.model.items

data class Brand(
    val brandName: String = "",
    val imgIcon: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this("", "")
}
