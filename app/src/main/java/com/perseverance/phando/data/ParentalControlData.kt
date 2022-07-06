package com.perseverance.phando.data

class ParentalControlData(
    val `data`: Data,
    val message: String,
    val status: String
)

data class Data(
    val all_age: AllAge,
    val isPinSet: Int,
    val maturity_rating: ArrayList<MaturityRating>
)

data class AllAge(
    val setting_default_value: String,
    val setting_item_id: Int,
    val setting_name: String,
    var setting_value: Int
)

data class MaturityRating(
    val setting_default_value: String,
    val setting_item_id: Int,
    val setting_name: String,
    var setting_value: Int
)