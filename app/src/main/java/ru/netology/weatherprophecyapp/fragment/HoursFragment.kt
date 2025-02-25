package ru.netology.weatherprophecyapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.weatherprophecyapp.adapters.WeatherAdapter
import ru.netology.weatherprophecyapp.adapters.WeatherModel
import ru.netology.weatherprophecyapp.databinding.FragmentHoursBinding
import androidx.fragment.app.activityViewModels
import ru.netology.weatherprophecyapp.MainViewModel
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment() {
    private var binding: FragmentHoursBinding? = null
    private var adapter: WeatherAdapter? = null
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            adapter?.submitList(getHoursList(it))
        }
    }

    private fun initRcView() {
        binding?.let {
            it.rcView.layoutManager = LinearLayoutManager(activity)
            adapter = WeatherAdapter(null)
            it.rcView.adapter = adapter
        }
    }

    private fun getHoursList(wItem: WeatherModel): List<WeatherModel> {
        val hoursArray = JSONArray(wItem.hours)
        val list = ArrayList<WeatherModel>()
        for (i in 0 until hoursArray.length()) {
            val item = WeatherModel(
                wItem.city,
                (hoursArray[i] as JSONObject).getString("time"),
                (hoursArray[i] as JSONObject).getJSONObject("condition").getString("text"),
                (hoursArray[i] as JSONObject).getString("temp_c"),
                "",
                "",
                (hoursArray[i] as JSONObject).getJSONObject("condition").getString("icon"),
                ""
            )
            list.add(item)
        }
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        adapter = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}
