package com.example.stylishadmin.model.items

data class ItemDetail(
    val itemId: String = "",       // Default empty string
    val title: String = "",        // Default empty string
    val price: Float = 0.0f,       // Default price 0.0
    val imageUrl: List<String> = emptyList(),  // Default to an empty list
    val quantity: Int = 0,         // Default quantity 0
    val size: String = "",         // Default empty string
    var isOutOfStock: Boolean = false // Default to false
) {
    // No-argument constructor for Firebase
    constructor() : this(
        itemId = "",
        title = "",
        price = 0.0f,
        imageUrl = emptyList(),
        quantity = 0,
        size = "",
        isOutOfStock = false
    )
}
