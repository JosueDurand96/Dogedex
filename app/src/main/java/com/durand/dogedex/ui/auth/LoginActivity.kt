package com.durand.dogedex.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.durand.dogedex.R
import com.durand.dogedex.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentActions{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onRegisterButtonClick() {
        findNavController(R.id.nav_host_fragment).navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }
}