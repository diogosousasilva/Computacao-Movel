package com.diogo.countryinfo.ui

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.diogo.countryinfo.databinding.ActivityMainBinding
import com.diogo.countryinfo.util.NetworkResult
import java.text.NumberFormat
import java.util.Locale

/**
 * Main (and only) Activity for the Country Info app.
 * Provides a search input and displays country information in a card.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CountryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSearchButton()
        setupKeyboardSearch()
        observeResults()
    }

    /**
     * Trigger search when the button is pressed.
     */
    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            performSearch()
        }
    }

    /**
     * Trigger search when the user presses Enter/Search on the keyboard.
     */
    private fun setupKeyboardSearch() {
        binding.editCountry.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun performSearch() {
        val query = binding.editCountry.text?.toString() ?: ""
        hideKeyboard()
        viewModel.searchCountry(query)
    }

    /**
     * Observe ViewModel state and update UI accordingly.
     */
    private fun observeResults() {
        viewModel.countryResult.observe(this) { result ->
            when (result) {
                is NetworkResult.Idle -> showIdle()
                is NetworkResult.Loading -> showLoading()
                is NetworkResult.Success -> showResult(result)
                is NetworkResult.Error -> showError(result.message)
            }
        }
    }

    private fun showIdle() {
        binding.welcomeLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.cardResult.visibility = View.GONE
        binding.cardError.visibility = View.GONE
    }

    private fun showLoading() {
        binding.welcomeLayout.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.cardResult.visibility = View.GONE
        binding.cardError.visibility = View.GONE
    }

    private fun showResult(result: NetworkResult.Success<com.diogo.countryinfo.data.model.CountryResponse>) {
        val country = result.data
        binding.welcomeLayout.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.cardError.visibility = View.GONE
        binding.cardResult.visibility = View.VISIBLE

        // Load flag image
        Glide.with(this)
            .load(country.flags.png)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.imageFlag)

        // Populate text fields
        binding.textCountryName.text = country.name.common
        binding.textCapital.text = country.capital?.firstOrNull() ?: "N/A"
        binding.textPopulation.text = NumberFormat.getNumberInstance(Locale.getDefault())
            .format(country.population)
        binding.textRegion.text = country.region
    }

    private fun showError(message: String) {
        binding.welcomeLayout.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.cardResult.visibility = View.GONE
        binding.cardError.visibility = View.VISIBLE
        binding.textError.text = message
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
