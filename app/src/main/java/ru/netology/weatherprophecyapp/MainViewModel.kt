package ru.netology.weatherprophecyapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import org.json.JSONObject
import ru.netology.weatherprophecyapp.adapters.HourItem
import ru.netology.weatherprophecyapp.adapters.WeatherModel
import ru.netology.weatherprophecyapp.repository.WeatherRepository

class MainViewModel : ViewModel() {
    val liveDataCurrent = MutableLiveData<WeatherModel>()
    val liveDataList = MutableLiveData<List<WeatherModel>>()
    private val repository = WeatherRepository()

    fun fetchWeatherData(city: String, apiKey: String) {
        viewModelScope.launch {
            val result = repository.fetchWeatherData(apiKey, city)
            result?.let {
                parseWeatherData(it)
            }
        }
    }

    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentData(mainObject, list[0])
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        val gson = Gson()
        for (i in 0 until daysArray.length()) {
            val day = daysArray.getJSONObject(i)
            val hoursJson = day.getJSONArray("hour").toString()
            val type = object : TypeToken<List<HourItem>>() {}.type
            val hours: List<HourItem> = gson.fromJson(hoursJson, type)
            val item = WeatherModel(
                city = name,
                time = day.getString("date"),
                condition = day.getJSONObject("day")
                    .getJSONObject("condition").getString("text"),
                currentTemp = "",
                maxTemp = day.getJSONObject("day").getString("maxtemp_c")
                    .toFloat().toInt().toString(),
                minTemp = day.getJSONObject("day").getString("mintemp_c")
                    .toFloat().toInt().toString(),
                imageUrl = day.getJSONObject("day")
                    .getJSONObject("condition").getString("icon"),
                hours = hours
            )
            list.add(item)
        }
        liveDataList.value = list
        return list
    }

    private fun parseCurrentData(mainObject: JSONObject, weatherItem: WeatherModel) {
        val current = mainObject.getJSONObject("current")
        val condition = current.getJSONObject("condition")
        val item = WeatherModel(
            city = mainObject.getJSONObject("location").getString("name"),
            time = current.getString("last_updated"),
            condition = condition.getString("text"),
            currentTemp = current.getString("temp_c"),
            maxTemp = weatherItem.maxTemp,
            minTemp = weatherItem.minTemp,
            imageUrl = condition.getString("icon"),
            hours = weatherItem.hours
        )
        liveDataCurrent.value = item
    }
}




