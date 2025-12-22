package com.durand.dogedex.ui.auth

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.data.request.oficial.LoginRequest
import com.durand.dogedex.databinding.FragmentLoginBinding
import com.durand.dogedex.ui.auth.oficial.LoginViewModel
import com.durand.dogedex.ui.auth.recoverpassword.RecoverPasswordActivity
import com.durand.dogedex.ui.user_fragment.UserHome
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    interface LoginFragmentActions {
        fun onRegisterButtonClick()
        fun onLoginFieldsValidated(email: String, password: String)
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var loginFragmentActions: LoginFragmentActions
    private lateinit var binding: FragmentLoginBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginFragmentActions = try {
            context as LoginFragmentActions
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must be implement LoginFragmentActions")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding = FragmentLoginBinding.inflate(layoutInflater)
        binding.loginRegisterButton.setOnClickListener {
            loginFragmentActions.onRegisterButtonClick()
        }
        binding.loginButton.setOnClickListener {
            validateFields()
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loginButton.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessageId ->
            errorMessageId?.let {
                binding.loginButton.isEnabled = true
                showErrorSnackbar(getString(it))
            }
        }
        
        binding.forgotPassword.setOnClickListener{
            val intent = Intent(requireContext(), RecoverPasswordActivity::class.java)
            startActivity(intent)
        }
        viewModel.login.observe(viewLifecycleOwner) {
            val sharedPref = activity?.getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
            val editor: SharedPreferences.Editor = sharedPref!!.edit()
            editor.putLong("idUsuario", it.id)
            // Guardar nombre del usuario
            editor.putString("nombreUsuario", it.nombre)
            // Guardar timestamp del login (en milisegundos)
            editor.putLong("loginTimestamp", System.currentTimeMillis())
            editor.apply()
            editor.commit()
            binding.loginButton.isEnabled = true
            Toast.makeText(requireContext(), "Bienvenido ${it.nombre}!", Toast.LENGTH_SHORT).show()
            Log.d("josue", "apellido: " + it.apellido)
            Log.d("josue", "nombre: " + it.nombre)
            Log.d("josue", "Login timestamp guardado: ${System.currentTimeMillis()}")
            val intent = Intent(requireContext(), UserHome::class.java)
            intent.putExtra("id", it.id)
            intent.putExtra("nombre", it.nombre)
            startActivity(intent)
            requireActivity().finish()
        }
        return binding.root
    }

    private fun validateFields() {
        binding.emailInput.error = ""
        binding.passwordInput.error = ""
        val email = binding.emailEdit.text.toString()
        val password = binding.passwordEdit.text.toString()
        if (password.isEmpty()) {
            binding.passwordInput.error = "Password is not valid!"
            return
        }
        Log.d("login", "Probando login")
        Log.d("login", "email: $email")
        Log.d("login", "password: $password")
        binding.loginButton.isEnabled = false
        viewModel.login(LoginRequest(email, password))
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Cerrar") { }
            .show()
    }
}