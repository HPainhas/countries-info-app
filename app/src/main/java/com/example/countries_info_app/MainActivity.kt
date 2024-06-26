package com.example.countries_info_app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.countries_info_app.api.Coordinates
import com.example.countries_info_app.api.CountryApiClient
import com.example.countries_info_app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var countryAdapter: CountryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.countriesRecyclerView.layoutManager = LinearLayoutManager(this)

        CountryApiClient.fetchCountries(this) { countries, exception ->
            if (exception != null) {
                Log.e("MainActivity", "Error fetching country data", exception)
            } else if (countries != null) {
                runOnUiThread {
                    countryAdapter = CountryAdapter(this, countries)
                    binding.countriesRecyclerView.adapter = countryAdapter

                    if (savedInstanceState == null) {
                        val fragment = MapsFragment()
                        supportFragmentManager.beginTransaction()
                            .replace(binding.countriesMapFragmentContainer.id, fragment)
                            .commit()
                    }
                }
            }
        }
    }

    fun locateCountry(coordinates: Coordinates) {
        val mapsFragment = supportFragmentManager.findFragmentById(binding.countriesMapFragmentContainer.id) as MapsFragment
        mapsFragment.updateLocation(coordinates.latitude!!, coordinates.longitude!!)
    }
}
