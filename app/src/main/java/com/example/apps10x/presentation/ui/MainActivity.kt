package com.example.apps10x.presentation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apps10x.databinding.ActivityMainBinding
import com.example.apps10x.presentation.adapter.ForecastAdapter
import com.example.apps10x.presentation.model.ForecastData
import com.example.apps10x.presentation.state.ForecastUiState
import com.example.apps10x.presentation.state.WeatherUiState
import com.example.apps10x.presentation.viewmodel.WeatherViewModel
import com.example.apps10x.utils.Constants
import com.example.apps10x.utils.Messages
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var forecastAdapter: ForecastAdapter
    private val weatherViewModel by viewModels<WeatherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpViews()
        fetchWeatherData()
        fetchForecastData()
    }

    private fun setUpViews() {
        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun fetchWeatherData() {
        weatherViewModel.weatherData.observe(this) { result ->
            when (result) {
                is WeatherUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.weatherLayout.visibility = View.INVISIBLE
                }

                is WeatherUiState.Empty -> {
                    Snackbar.make(binding.root, Messages.EMPTY_DATA, Snackbar.LENGTH_LONG).show()
                }

                is WeatherUiState.Success -> {
                    binding.currentTemperature.text = "${result.data}${Constants.DEGREE_SYMBOL}"
                    binding.progressBar.visibility = View.GONE
                    binding.weatherLayout.visibility = View.VISIBLE
                }

                is WeatherUiState.Error -> {
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG)
                        .setAction(Constants.RETRY) {}.show()
                }
            }
        }
    }

    private fun fetchForecastData() {
        lifecycleScope.launch {
            weatherViewModel.forecastData.collect { state ->
                when (state) {
                    is ForecastUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.weatherLayout.visibility = View.INVISIBLE
                    }

                    is ForecastUiState.Empty -> {
                        Snackbar.make(binding.root, Messages.EMPTY_DATA, Snackbar.LENGTH_LONG)
                            .show()
                    }

                    is ForecastUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.weatherLayout.visibility = View.VISIBLE
                        populateData(state.data)
                    }

                    is ForecastUiState.Error -> {
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
                            .setAction(Constants.RETRY) {}.show()
                    }
                }
            }
        }
    }

    private fun populateData(forecastList: List<ForecastData>) {
        forecastAdapter = ForecastAdapter(forecastList)
        binding.recyclerView.adapter = forecastAdapter
    }
}