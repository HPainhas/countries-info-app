package com.example.countries_info_app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.countries_info_app.api.CountryApiClient

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CountryApiClient.fetchCountries(this) { countries, exception ->
            if (exception != null) {
                Log.e("MainActivity", "Error fetching country data", exception)
            } else if (countries != null) {
                runOnUiThread {
                    countries.forEach { country ->
                        Log.d("COUNTRYINFO", country.toString())
                    }
                }
            }
        }
    }
}
