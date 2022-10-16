package com.durand.dogedex.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.durand.dogedex.MainActivity
import com.durand.dogedex.R
import com.durand.dogedex.api.ApiResponseStatus
import com.durand.dogedex.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentActions, SignUpFragment.SignUpFragmentActions{

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.status.observe(this) { status ->

            when (status) {
                is ApiResponseStatus.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    showErrorDialog(status.message)
                }
                is ApiResponseStatus.Loading -> binding.loadingWheel.visibility = View.VISIBLE
                is ApiResponseStatus.Success -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(this, "Se cargaron los datos correctamente!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModel.user.observe(this){
            user ->
            if(user != null){
                startMainActivity()
            }
        }
    }

    private fun startMainActivity(){
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun showErrorDialog(messageId: Int){
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(messageId)
            .setPositiveButton(android.R.string.ok) { _, _ ->}
            .create()
            .show()
    }

    override fun onRegisterButtonClick() {
        findNavController(R.id.nav_host_fragment).navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }

    override fun onSignUpFieldsValidated(
        email: String,
        password: String,
        passwordConfirmation: String
    ) {
        Log.d("josue","email: $email")
        Log.d("josue","password: $password")
        Log.d("josue","passwordConfirmation: $passwordConfirmation")
        viewModel.onSignUp(email, password, passwordConfirmation)
    }
}