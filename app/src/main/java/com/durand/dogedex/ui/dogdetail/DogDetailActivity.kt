package com.durand.dogedex.ui.dogdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.durand.dogedex.R
import com.durand.dogedex.api.response.Dog
import com.durand.dogedex.databinding.ActivityDogDetailBinding
import com.durand.dogedex.databinding.ActivityDogListBinding

class DogDetailActivity : AppCompatActivity() {

    companion object{
        const val DOG_KEY = "dog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dog = intent?.extras?.getParcelable<Dog>(DOG_KEY)

        if (dog == null){
            Toast.makeText(this, "Dog not found!", Toast.LENGTH_SHORT).show()
            finish()
        }
        binding.dog = dog
    }
}