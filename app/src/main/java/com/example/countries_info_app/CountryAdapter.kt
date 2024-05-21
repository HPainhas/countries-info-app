package com.example.countries_info_app

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.countries_info_app.api.Coordinates
import com.example.countries_info_app.api.Country
import com.example.countries_info_app.api.Currency
import com.example.countries_info_app.databinding.ItemCountryBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class CountryAdapter(
    private val context: Context,
    private val countries: List<Country>
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var expandedPosition = -1

    override fun getItemCount(): Int = countries.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = ItemCountryBinding.inflate(LayoutInflater.from(context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country = countries[position]
        holder.bind(country)

        val isExpanded = position == expandedPosition
        holder.showDetails(isExpanded)

        holder.itemView.setOnClickListener {
            expandedPosition = if (isExpanded) -1 else position
            notifyItemChanged(position)
        }
    }

    inner class CountryViewHolder(
        private val binding: ItemCountryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(country: Country) {
            with(binding) {
                progressBar.visibility = View.VISIBLE

                Picasso
                    .get()
                    .load(country.flag?.flagUrlPng)
                    .error(R.drawable.error)
                    .fit()
                    .into(itemCountryFlag, object: Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                            Log.e("CountryAdapter", "Successfully loaded country flag image")
                        }

                        override fun onError(e: Exception?) {
                            progressBar.visibility = View.GONE
                            Log.e("CountryAdapter", "Error loading country flag image", e)
                        }

                    })

                itemCountryName.text = if (country.name.isNullOrEmpty()) "N/A" else country.name
                itemCountryCapitalContent.text = if (country.capital.isNullOrEmpty()) "N/A" else country.capital
                itemCountryPopulationContent.text = formatPopulation(country.population)
                itemCountryCurrencyContent.text = formatCurrencies(country.currencies)
                itemCountryLanguagesContent.text = formatLanguages(country.languages)
                itemCountryContinentsContent.text = formatContinents(country.continents)
                itemCountryCoordinatesContent.text = formatCoordinates(country.coordinates)

                itemCountryLocateCoordinatesButton.setOnClickListener {
                    val coordinates = country.coordinates

                    if (coordinates?.latitude != null && coordinates.longitude != null) {
                        (context as MainActivity).locateCountry(coordinates)
                    }
                }
            }
        }

        fun showDetails(show: Boolean) {
            with(binding) {
                val expandIcon = when (show) {
                    true -> R.drawable.arrow_drop_up_filled_24
                    false -> R.drawable.arrow_drop_down_filled_24
                }

                itemCountryExpandIcon.setImageResource(expandIcon)

                itemCountryCapitalHeader.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryCapitalContent.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryPopulationHeader.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryPopulationContent.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryCurrencyHeader.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryCurrencyContent.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryLanguagesHeader.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryLanguagesContent.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryContinentsHeader.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryContinentsContent.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryCoordinatesHeader.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryCoordinatesContent.visibility = if (show) View.VISIBLE else View.GONE
                itemCountryLocateCoordinatesButton.visibility = if (show) View.VISIBLE else View.GONE
            }
        }

        private fun formatPopulation(population: Int?): String {
            if (population == null || population == 0) return "N/A"

            val formattedPopulation = StringBuilder(population.toString())
                .reverse()
                .toString()
                .chunked(3)
                .joinToString(",")
                .reversed()

            return formattedPopulation
        }

        private fun formatCurrencies(currencies: List<Currency>?): String {
            if (currencies.isNullOrEmpty()) return "N/A"

            return currencies.joinToString(", ") {
                "${it.currencyName} (${it.currencySign}) - ${it.currencySymbol}"
            }
        }

        private fun formatLanguages(languages: List<String>?): String? {
            if (languages.isNullOrEmpty()) return null

            return languages.joinToString(", ")
        }

        private fun formatContinents(continents: List<String>?): String? {
            if (continents.isNullOrEmpty()) return null

            return continents.joinToString(", ")
        }

        private fun formatCoordinates(coordinates: Coordinates?): String {
            if (coordinates?.latitude == null || coordinates.longitude == null) return "N/A"

            return "[${coordinates.latitude}, ${coordinates.longitude}]"
        }
    }
}