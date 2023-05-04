package com.durand.dogedex.ui.dogdetail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.durand.dogedex.R
import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.api.response.Dog
import com.durand.dogedex.databinding.ActivityDogDetailBinding

class DogDetailActivity : AppCompatActivity() {

    companion object {
        const val DOG_KEY = "dog"
        const val IS_RECOGNITION_KEY = "is_recognition"
    }

    private val viewModel: DogDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dog = intent?.extras?.getParcelable<Dog>(DOG_KEY)
        val isRecognition = intent?.extras?.getBoolean(IS_RECOGNITION_KEY, false) ?: false
        if (dog!!.name == "Doberman") {
            binding.canDangerousTextView.visibility = View.VISIBLE
            binding.canNoDangerousTextView.visibility = View.GONE
        } else {
            binding.canDangerousTextView.visibility = View.GONE
            binding.canNoDangerousTextView.visibility = View.VISIBLE
        }

        if (dog == null) {
            Toast.makeText(this, R.string.error_showing_dog_not_found, Toast.LENGTH_SHORT)
                .show()
            finish()
            return
        }

        viewModel.status.observe(this) { status ->

            when (status) {
                is ApiResponseStatus.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                }
                is ApiResponseStatus.Loading -> binding.loadingWheel.visibility = View.VISIBLE
                is ApiResponseStatus.Success -> {
                    binding.loadingWheel.visibility = View.GONE
                    finish()
                }
            }
        }


        binding.dogIndex.text = getString(R.string.dog_index_format, dog.index)
        binding.lifeExpectancy.text =
            getString(R.string.dog_life_expectancy_format, dog.lifeExpectancy)
        binding.dog = dog
        binding.dogImage.load(dog.imageUrl)
        binding.closeButton.setOnClickListener {
            if (isRecognition) {
                viewModel.addDogToUser(dog.id)
            } else {
                finish()
            }
        }
    }
}