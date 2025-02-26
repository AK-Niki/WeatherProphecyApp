package ru.netology.weatherprophecyapp.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import ru.netology.weatherprophecyapp.MainViewModel
import ru.netology.weatherprophecyapp.adapters.VpAdapter
import ru.netology.weatherprophecyapp.databinding.FragmentMainBinding
import ru.netology.weatherprophecyapp.DialogManager

const val API_KEY = ""

class MainFragment : Fragment() {
    private var binding: FragmentMainBinding? = null
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private val model: MainViewModel by activityViewModels()

    private val fList = listOf(
        HoursFragment.newInstance(),
        DaysFragment.newInstance()
    )
    private val tList = listOf("Hours", "Days")

    override fun onCreateView(inflater: android.view.LayoutInflater, container: android.view.ViewGroup?, savedInstanceState: Bundle?): android.view.View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
        updateCurrentCard()
    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun init() {
        binding?.let { bindingNonNull ->
            locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    requestWeatherData("${location.latitude},${location.longitude}")
                    locationManager.removeUpdates(this)
                }
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }
            val adapter = VpAdapter(activity as FragmentActivity, fList)
            bindingNonNull.vp.adapter = adapter
            TabLayoutMediator(bindingNonNull.tabLayout, bindingNonNull.vp) { tab, pos ->
                tab.text = tList[pos]
            }.attach()
            bindingNonNull.ibSync.setOnClickListener {
                bindingNonNull.tabLayout.selectTab(bindingNonNull.tabLayout.getTabAt(0))
                checkLocation()
            }
            bindingNonNull.ibSearch.setOnClickListener {
                DialogManager.searchByNameDialog(requireContext(), object : DialogManager.Listener {
                    override fun onClick(name: String?) {
                        name?.let { requestWeatherData(it) }
                    }
                })
            }
        }
    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val location = gpsLocation ?: networkLocation
        if (location != null) {
            requestWeatherData("${location.latitude},${location.longitude}")
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, locationListener)
        }
    }

    private fun updateCurrentCard() {
        binding?.let { bindingNonNull ->
            model.liveDataCurrent.observe(viewLifecycleOwner) {
                val maxMinTemp = "${it.maxTemp}ºC / ${it.minTemp}ºC"
                bindingNonNull.tvData.text = it.time
                bindingNonNull.tvCity.text = it.city
                bindingNonNull.tvCurrentTemp.text = if (it.currentTemp.isEmpty()) maxMinTemp else it.currentTemp
                bindingNonNull.tvCondition.text = it.condition
                bindingNonNull.tvMaxMin.text = if (it.currentTemp.isEmpty()) "" else maxMinTemp
                Picasso.get().load("https:" + it.imageUrl).into(bindingNonNull.imWeather)
            }
        }
    }

    private fun permissionListener() {
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            Toast.makeText(activity, "Permission is $it", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionListener()
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(city: String) {
        model.fetchWeatherData(city, API_KEY)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}

