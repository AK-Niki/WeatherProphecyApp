package ru.netology.weatherprophecyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.weatherprophecyapp.databinding.ActivityMainBinding
import ru.netology.weatherprophecyapp.fragment.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.placeHolder, MainFragment.newInstance())
            .commit()
    }
}


