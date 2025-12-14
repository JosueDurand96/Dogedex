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
import com.durand.dogedex.data.request.oficial.ActualizarClaveRequest
import com.durand.dogedex.databinding.ActivityChangePasswordBinding
import com.durand.dogedex.ui.auth.LoginActivity
import com.durand.dogedex.ui.auth.viewmodels.ChangePasswordViewModel

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var email: String
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        email = intent.getStringExtra("email") ?: ""
        if (email.isEmpty()) {
            Toast.makeText(this, "Error: Email no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        viewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)
        
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.buttonChangePassword.isEnabled = !isLoading
            // ProgressBar no está disponible en este layout
        }

        viewModel.list.observe(this) { response ->
            Log.d("josue", "Contraseña actualizada - codigo: ${response.codigo}, mensaje: ${response.mensaje}")
            Toast.makeText(this, response.mensaje, Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupClickListeners() {
        binding.buttonChangePassword.setOnClickListener {
            val nuevaClave = binding.editTextNewPassword.text.toString().trim()
            val confirmarClave = binding.editTextConfirmPassword.text.toString().trim()
            
            if (nuevaClave.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese la nueva contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (confirmarClave.isEmpty()) {
                Toast.makeText(this, "Por favor confirme la contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (nuevaClave != confirmarClave) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (nuevaClave.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            Log.d("josue", "Actualizando contraseña para email: $email")
            binding.buttonChangePassword.isEnabled = false
            viewModel.registrarCodigo(
                ActualizarClaveRequest(
                    correoUsuario = email,
                    clave = nuevaClave
                )
            )
        }
    }
}