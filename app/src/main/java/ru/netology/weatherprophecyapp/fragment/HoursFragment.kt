package ru.netology.weatherprophecyapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.weatherprophecyapp.adapters.HourAdapter
import ru.netology.weatherprophecyapp.databinding.FragmentHoursBinding
import androidx.fragment.app.activityViewModels
import ru.netology.weatherprophecyapp.MainViewModel

class HoursFragment : Fragment() {
    private var binding: FragmentHoursBinding? = null
    private var adapter: HourAdapter? = null
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.liveDataCurrent.observe(viewLifecycleOwner) { weatherModel ->
            adapter?.submitList(weatherModel.hours)
        }
    }

    private fun initRcView() {
        binding?.let {
            it.rcView.layoutManager = LinearLayoutManager(activity)
            adapter = HourAdapter()
            it.rcView.adapter = adapter
        }
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

