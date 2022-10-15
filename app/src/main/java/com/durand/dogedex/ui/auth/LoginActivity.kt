package com.durand.dogedex.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.durand.dogedex.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}