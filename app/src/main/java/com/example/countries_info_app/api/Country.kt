package com.example.countries_info_app.api

import androidx.compose.ui.layout.LayoutCoordinates

data class Country(
    val name: String?,
    val capital: String?,
    val population: Int?,
    val flag: Flag?,
    val currencies: List<Currency>?,
    val languages: List<String>?,
    val continents: List<String>?,
    val coordinates: Coordinates?
)

data class Flag(
    val flagUrlPng: String?,
    val flagUrlSvg: String?,
)

data class Currency(
    val currencySign: String?,
    val currencyName: String?,
    val currencySymbol: String?,
)

data class Coordinates(
    val latitude: Double?,
    val longitude: Double?
)