package com.durand.dogedex.ui.forget_password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.durand.dogedex.R
import com.durand.dogedex.databinding.ActivityForgetPasswordBinding
import com.durand.dogedex.databinding.ActivityOnboardingBinding

class ForgetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}