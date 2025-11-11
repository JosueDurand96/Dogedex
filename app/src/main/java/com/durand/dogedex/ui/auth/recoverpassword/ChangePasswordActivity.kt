package com.durand.dogedex.ui.auth.recoverpassword

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.R
import com.durand.dogedex.data.request.oficial.ActualizarClaveRequest
import com.durand.dogedex.databinding.ActivityChangePasswordBinding
import com.durand.dogedex.databinding.ActivityRecoverPasswordBinding
import com.durand.dogedex.ui.auth.LoginActivity
import com.durand.dogedex.ui.auth.viewmodels.ChangePasswordViewModel
import com.durand.dogedex.ui.auth.viewmodels.RegistrarCodigoViewModel

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var email: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root) // Use binding.root here
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        email = intent.getStringExtra("email").toString()
        viewModel = ViewModelProvider(this).get(ChangePasswordViewModel::class.java)

        viewModel.list.observe(this) { it ->
            Log.d("josue", "HUBO EXITO: "+it.codigo)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.buttonChangePassword.setOnClickListener {
            viewModel.registrarCodigo(
                ActualizarClaveRequest(
                    correoUsuario = email,
                    clave = binding.editTextConfirmPassword.text.toString()
                )
            )
        }
    }
}