package com.durand.dogedex.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.data.Request.oficial.LoginRequest
import com.durand.dogedex.data.User
import com.durand.dogedex.data.dto.AddLoginDTO
import com.durand.dogedex.databinding.FragmentLoginBinding
import com.durand.dogedex.ui.admin_fragment.AdminHome
import com.durand.dogedex.ui.admin_fragment.ui.reportar_can_agresor.AddCanAgresorViewModel
import com.durand.dogedex.ui.auth.oficial.LoginViewModel
import com.durand.dogedex.ui.user_fragment.UserHome

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

        viewModel.login.observe(requireActivity()) {
            Toast.makeText(requireContext(), "Bienvenido ${it.nombre}!", Toast.LENGTH_SHORT).show()
            Log.d("josue", "apellido: " + it.apellido.toString())
            Log.d("josue", "nombre: " + it.nombre)
//            if (it.tipoUsuario == "U") {
//                val intent = Intent(requireContext(), UserHome::class.java)
//                startActivity(intent)
//            } else if (it.tipoUsuario == "A") {
//                val intent = Intent(requireContext(), AdminHome::class.java)
//                startActivity(intent)
//            }
        }
        return binding.root
    }

    private fun validateFields() {
        binding.emailInput.error = ""
        binding.passwordInput.error = ""
        val email = binding.emailEdit.text.toString()
//        if (!isValidEmail(email)){
//            binding.emailInput.error = "Email is not valid!"
//            return
//        }

        val password = binding.passwordEdit.text.toString()
        if (password.isEmpty()) {
            binding.passwordInput.error = "Password is not valid!"
            return
        }
        Log.d("login", "Probando login")
        Log.d("login", "email: $email")
        Log.d("login", "password: $password")
        Log.d("login", "viewModel: 1")
        viewModel.login(LoginRequest(email, password))
        Log.d("login", "viewModel: 2")
        //loginFragmentActions.onLoginFieldsValidated("josue@gmail.com", "12345678")
        Log.d("login", "viewModel: 3")
    }
}