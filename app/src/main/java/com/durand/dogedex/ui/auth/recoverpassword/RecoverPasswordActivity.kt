package com.durand.dogedex.ui.auth.recoverpassword

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.R
import com.durand.dogedex.data.request.oficial.RegistrarCodigoRequest
import com.durand.dogedex.data.request.oficial.ValidarCodigoRequest
import com.durand.dogedex.databinding.ActivityRecoverPasswordBinding
import com.durand.dogedex.ui.auth.viewmodels.RegistrarCodigoViewModel
import com.durand.dogedex.ui.auth.viewmodels.ValidarCodigoViewModel

class RecoverPasswordActivity : AppCompatActivity() {

    private lateinit var registrarCodViewModel: RegistrarCodigoViewModel
    private lateinit var validateCodViewModel: ValidarCodigoViewModel
    private lateinit var binding: ActivityRecoverPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRecoverPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root) // Use binding.root here
        binding.otpContainer.visibility = View.GONE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        validateCodViewModel = ViewModelProvider(this).get(ValidarCodigoViewModel::class.java)
        registrarCodViewModel = ViewModelProvider(this).get(RegistrarCodigoViewModel::class.java)

        registrarCodViewModel.isLoading.observe(this) { isLoading ->
           // binding.sendButton.isEnabled = !isLoading
            binding.progressBarEmail.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        binding.sendButton.setOnClickListener{
            val email = binding.etEmail.text.toString()
            Log.d("josue", "email: $email")

            registrarCodViewModel.registrarCodigo(RegistrarCodigoRequest(correoUsuario = email))
        }

        registrarCodViewModel.list.observe(this) { it ->
            binding.otpContainer.visibility = View.VISIBLE
            Log.d("josue", "HUBO EXITO: "+it.codigo)
        }

        validateCodViewModel.list.observe(this) { it ->
            Log.d("josue", "HUBO EXITO: "+it.codigo)
            val intent = Intent(this, ChangePasswordActivity::class.java)
            intent.putExtra("email", binding.etEmail.text.toString())
            startActivity(intent)
        }

        binding.verifyOtpButton.setOnClickListener {
            validateCodViewModel.registrarCodigo(ValidarCodigoRequest(
                correoUsuario = binding.etEmail.text.toString(),
                codigo = binding.etOtpCode.text.toString()
            ))
        }
    }

}