package ru.netology.weatherprophecyapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.weatherprophecyapp.MainViewModel
import ru.netology.weatherprophecyapp.adapters.WeatherAdapter
import ru.netology.weatherprophecyapp.adapters.WeatherModel
import ru.netology.weatherprophecyapp.databinding.FragmentDaysBinding

class DaysFragment : Fragment(), WeatherAdapter.Listener {
    private var binding: FragmentDaysBinding? = null
    private var adapter: WeatherAdapter? = null
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        model.liveDataList.observe(viewLifecycleOwner) {
            adapter?.submitList(it.subList(1, it.size))
        }
    }

    private fun init() {
        binding?.let {
            adapter = WeatherAdapter(this)
            it.rcView.layoutManager = LinearLayoutManager(activity)
            it.rcView.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        adapter = null
    }

    override fun onClick(item: WeatherModel) {
        model.liveDataCurrent.value = item
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }
}
