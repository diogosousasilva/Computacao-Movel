package com.diogo.geminiimageapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.diogo.geminiimageapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

/**
 * MainActivity — Gemini Image Processing App.
 *
 * Displays three images (cakes/cookies) and allows the user to send
 * a text prompt along with the selected image to the Gemini AI for
 * processing (e.g., request a recipe, suggest a name, etc.).
 *
 * Extra feature: Response history with timestamps is displayed below.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: GeminiViewModel by viewModels()

    // The three sample images loaded from drawable resources
    private val images = mutableListOf<Bitmap>()
    private var selectedImageIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load the three sample images from drawable resources
        loadImages()

        // Set up image selection click listeners
        setupImageSelection()

        // Select the first image by default
        selectImage(0)

        // Set up the send button
        binding.sendButton.setOnClickListener {
            val prompt = binding.promptEditText.text.toString().trim()
            if (prompt.isEmpty()) {
                binding.promptEditText.error = "Please enter a prompt"
                return@setOnClickListener
            }

            sendPrompt(prompt)
        }

        // Set up clear history button
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        // Observe ViewModel state
        observeViewModel()
    }

    /**
     * Loads three sample cake/cookie images from drawable resources.
     */
    private fun loadImages() {
        val imageResIds = listOf(
            R.drawable.cake_1,
            R.drawable.cake_2,
            R.drawable.cake_3
        )

        for (resId in imageResIds) {
            val bitmap = BitmapFactory.decodeResource(resources, resId)
            images.add(bitmap)
        }

        // Set the images to the ImageViews
        binding.image1.setImageBitmap(images[0])
        binding.image2.setImageBitmap(images[1])
        binding.image3.setImageBitmap(images[2])
    }

    /**
     * Sets up click listeners for image selection.
     */
    private fun setupImageSelection() {
        binding.image1.setOnClickListener { selectImage(0) }
        binding.image2.setOnClickListener { selectImage(1) }
        binding.image3.setOnClickListener { selectImage(2) }
    }

    /**
     * Highlights the selected image and deselects others.
     */
    private fun selectImage(index: Int) {
        selectedImageIndex = index

        // Reset all borders
        binding.image1Card.strokeWidth = 0
        binding.image2Card.strokeWidth = 0
        binding.image3Card.strokeWidth = 0

        // Highlight selected
        val selectedCard = when (index) {
            0 -> binding.image1Card
            1 -> binding.image2Card
            2 -> binding.image3Card
            else -> binding.image1Card
        }
        selectedCard.strokeWidth = 6
        selectedCard.strokeColor = getColor(R.color.selected_border)
    }

    /**
     * Sends the prompt + selected image to the Gemini API via ViewModel.
     */
    private fun sendPrompt(prompt: String) {
        val selectedBitmap = images[selectedImageIndex]
        viewModel.sendPrompt(prompt, selectedBitmap)
    }

    /**
     * Observes ViewModel LiveData for response updates.
     */
    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.sendButton.isEnabled = !isLoading
            binding.responseCard.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.response.observe(this) { response ->
            if (response != null) {
                binding.responseTextView.text = response
                binding.responseCard.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }

        // Observe history for the extra feature
        viewModel.history.observe(this) { historyList ->
            if (historyList.isNullOrEmpty()) {
                binding.historyTextView.visibility = View.GONE
                binding.historyHeader.visibility = View.GONE
            } else {
                binding.historyHeader.visibility = View.VISIBLE
                binding.historyTextView.visibility = View.VISIBLE
                val historyText = historyList.joinToString("\n\n") { entry ->
                    "🕐 ${entry.timestamp}\n📝 Prompt: ${entry.prompt}\n🤖 Response: ${entry.response}"
                }
                binding.historyTextView.text = historyText
            }
        }
    }
}
