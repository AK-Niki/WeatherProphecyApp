package ru.netology.weatherprophecyapp.repository

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.netology.weatherprophecyapp.network.WeatherApiService

class WeatherRepository {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.weatherapi.com/v1/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val api: WeatherApiService = retrofit.create(WeatherApiService::class.java)

    suspend fun fetchWeatherData(apiKey: String, city: String): String? {
        val response: Response<ResponseBody> = api.getForecast(apiKey, city)
        return if (response.isSuccessful) response.body()?.string() else null
    }
}

