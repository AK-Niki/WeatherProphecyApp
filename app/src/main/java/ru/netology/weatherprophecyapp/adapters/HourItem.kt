package ru.netology.weatherprophecyapp.adapters

import com.google.gson.annotations.SerializedName

data class HourItem(
    val time: String,
    @SerializedName("temp_c") val tempC: String,
    val condition: Condition
)

data class Condition(
    val text: String,
    val icon: String
)



