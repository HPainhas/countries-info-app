package com.example.countries_info_app.api

import android.content.Context
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object CountryApiClient {

    private const val BASE_URL = "https://restcountries.com/v3.1/all"

    fun fetchCountries(context: Context, callback: (List<Country>?, Exception?) -> Unit) {
        val request = Request.Builder()
            .url("$BASE_URL?fields=name,capital,population,flags,currencies,languages,continents,capitalInfo")
            .cacheControl(
                CacheControl.Builder()
                    .maxStale(10, TimeUnit.HOURS)
                    .build()
            )
            .get()
            .addHeader("accept", "application/json")
            .build()

        OkHttpClientSingleton.getInstance(context).newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(null, IOException("Unexpected code $response"))
                    return
                }

                response.body?.string()?.let { jsonData ->
                    val countries = JSONArray(jsonData)
                    val countryList = mutableListOf<Country>()

                    for (i in 0 until countries.length()) {
                        val countryData = countries.getJSONObject(i)

                        val name = if (countryData.has("name")) countryData.getJSONObject("name").getString("common") else null
                        val capital = if (countryData.has("capital")) getCountryCapital(countryData.getJSONArray("capital")) else null
                        val population = if (countryData.has("population")) countryData.getInt("population") else null
                        val flag = if (countryData.has("flags")) getCountryFlag(flagsObject = countryData.getJSONObject("flags")) else null
                        val currencies = if (countryData.has("currencies")) getCountryCurrencies(currenciesObject = countryData.getJSONObject("currencies")) else null
                        val languages = if (countryData.has("languages")) getCountryLanguages(languagesObject = countryData.getJSONObject("languages")) else null
                        val continents = if (countryData.has("continents")) getCountryContinents(countryData.getJSONArray("continents")) else null
                        val coordinates = if (countryData.has("capitalInfo")) getCountryCoordinates(countryData.getJSONObject("capitalInfo")) else null

                        val country = Country(
                            name,
                            capital,
                            population,
                            flag,
                            currencies,
                            languages,
                            continents,
                            coordinates
                        )

                        countryList.add(country)
                    }

                    val sortedCountryList = countryList.sortedBy { it.name }

                    callback(sortedCountryList, null)
                } ?: callback(null, IOException("Response body is null"))
            }
        })
    }

    private fun getCountryCapital(capitalsArray: JSONArray): String? {
        return if (capitalsArray.length() == 0) {
            return null
        } else {
            capitalsArray.getString(0)
        }
    }

    private fun getCountryFlag(flagsObject: JSONObject): Flag {
        return Flag(
            flagUrlPng = if (flagsObject.has("png")) flagsObject.getString("png") else null,
            flagUrlSvg = if (flagsObject.has("svg")) flagsObject.getString("svg") else null,
        )
    }

    private fun getCountryCurrencies(currenciesObject: JSONObject): List<Currency> {
        val currencyList = mutableListOf<Currency>()
        val currencyKeys = currenciesObject.keys()

        while (currencyKeys.hasNext()) {
            val currencyKey = currencyKeys.next() as String
            val currencyData = currenciesObject.getJSONObject(currencyKey)
            val currencyName = if (currencyData.has("name")) currencyData.getString("name") else null
            val currencySymbol = if (currencyData.has("symbol")) currencyData.getString("symbol") else null

            val currency = Currency(
                currencySign = currencyKey,
                currencyName = currencyName,
                currencySymbol = currencySymbol
            )

            currencyList.add(currency)
        }

        return currencyList
    }

    private fun getCountryLanguages(languagesObject: JSONObject): List<String> {
        val languageList = mutableListOf<String>()
        val languageKeys = languagesObject.keys()

        while (languageKeys.hasNext()) {
            val languageKey = languageKeys.next() as String
            val languageName = languagesObject.getString(languageKey)
            languageList.add(languageName)
        }

        return languageList
    }

    private fun getCountryContinents(continentsArray: JSONArray): List<String> {
        val continents = mutableListOf<String>()

        for (i in 0 until continentsArray.length()) {
            continents.add(continentsArray.getString(i))
        }

        return continents
    }

    private fun getCountryCoordinates(coordinatesObject: JSONObject): Coordinates? {
        val coordinatesKey = coordinatesObject.keys().next() as String
        val coordinatesData = coordinatesObject.getJSONArray(coordinatesKey)

        return if (coordinatesData.length() > 0) {
            Coordinates(
                latitude = coordinatesData.getDouble(0),
                longitude =  coordinatesData.getDouble(1)
            )
        } else {
            null
        }
    }
}
