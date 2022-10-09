package com.durand.dogedex.ui.dogdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.durand.dogedex.R
import com.durand.dogedex.databinding.ActivityDogDetailBinding
import com.durand.dogedex.databinding.ActivityDogListBinding

class DogDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}