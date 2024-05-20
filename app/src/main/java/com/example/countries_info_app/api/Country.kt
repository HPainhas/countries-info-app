package com.example.countries_info_app.api

data class Country(
    val name: String?,
    val capital: String?,
    val population: Int?,
    val flag: Flag?,
    val currency: List<Currency>?,
    val languages: List<String>?,
    val continents: List<String>?,
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