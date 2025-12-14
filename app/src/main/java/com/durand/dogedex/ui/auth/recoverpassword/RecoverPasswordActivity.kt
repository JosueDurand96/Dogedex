package com.durand.dogedex.ui.auth.recoverpassword

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
        setContentView(binding.root)
        binding.otpContainer.visibility = View.GONE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        validateCodViewModel = ViewModelProvider(this).get(ValidarCodigoViewModel::class.java)
        registrarCodViewModel = ViewModelProvider(this).get(RegistrarCodigoViewModel::class.java)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        registrarCodViewModel.isLoading.observe(this) { isLoading ->
            binding.sendButton.isEnabled = !isLoading
            binding.progressBarEmail.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        registrarCodViewModel.list.observe(this) { response ->
            binding.otpContainer.visibility = View.VISIBLE
            Log.d("josue", "Código enviado - codigo: ${response.codigo}, mensaje: ${response.mensaje}")
            Toast.makeText(this, response.mensaje, Toast.LENGTH_SHORT).show()
        }

        validateCodViewModel.isLoading.observe(this) { isLoading ->
            binding.verifyOtpButton.isEnabled = !isLoading
            binding.progressBarEmail.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        validateCodViewModel.list.observe(this) { response ->
            Log.d("josue", "Código validado - codigo: ${response.codigo}, mensaje: ${response.mensaje}")
            Toast.makeText(this, response.mensaje, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ChangePasswordActivity::class.java)
            intent.putExtra("email", binding.etEmail.text.toString())
            startActivity(intent)
            finish()
        }
    }

    private fun setupClickListeners() {
        binding.sendButton.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese su correo electrónico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            Log.d("josue", "Enviando código a email: $email")
            binding.sendButton.isEnabled = false
            registrarCodViewModel.registrarCodigo(RegistrarCodigoRequest(correoUsuario = email))
        }

        binding.verifyOtpButton.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val codigo = binding.etOtpCode.text.toString().trim()
            
            if (codigo.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese el código de verificación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            Log.d("josue", "Validando código para email: $email, código: $codigo")
            binding.verifyOtpButton.isEnabled = false
            validateCodViewModel.registrarCodigo(
                ValidarCodigoRequest(
                    correoUsuario = email,
                    codigo = codigo
                )
            )
        }
    }
}