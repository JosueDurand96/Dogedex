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
import com.durand.dogedex.data.request.oficial.RegisterRequest
import com.durand.dogedex.databinding.FragmentSignUpBinding
import com.durand.dogedex.ui.auth.oficial.LoginViewModel
import com.durand.dogedex.ui.auth.oficial.RegisterViewModel
import com.durand.dogedex.ui.user_fragment.UserHome

class SignUpFragment : Fragment() {

    interface SignUpFragmentActions {
        fun onSignUpFieldsValidated(email: String, password: String, passwordConfirmation: String)
    }

    private lateinit var signUpFragmentActions: SignUpFragmentActions
    private lateinit var viewModel: RegisterViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        signUpFragmentActions = try {
            context as SignUpFragmentActions
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must be implement SignUpFragmentActions")
        }
    }

    private lateinit var binding: FragmentSignUpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        setupSignUpButton()
        return binding.root
    }

    private fun setupSignUpButton() {
        binding.signUpButton.setOnClickListener {
            validateFields()
        }
    }

    private fun validateFields() {
        binding.emailInput.error = ""
        binding.passwordInput.error = ""
        binding.confirmPasswordInput.error = ""
        val email = binding.emailEdit.text.toString()
        /*      if (!isValidEmail(email)){
                  binding.emailInput.error = "Email is not valid!"
                  return
              }*/

        val password = binding.passwordEdit.text.toString()
        if (password.isEmpty()) {
            binding.passwordInput.error = "Password is not valid!"
            return
        }

        val passwordConfirmation = binding.confirmPasswordEdit.text.toString()
        if (passwordConfirmation.isEmpty()) {
            binding.confirmPasswordInput.error = "Password is not valid!"
            return
        }

        if (password != passwordConfirmation) {
            binding.passwordInput.error = "Passwords do not match!"
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.signUpButton.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.register.observe(requireActivity()) {
            binding.signUpButton.isEnabled = true
            Log.d("josue", "codigo: " + it.codigo)
            Log.d("josue", "mensaje: " + it.mensaje)
            val intent = Intent(requireContext(), LoginFragment::class.java)
            startActivity(intent)
        }
        viewModel.registerUser(
            RegisterRequest(
                nombre = binding.nameEdit.text.toString(),
                apellidos = binding.apellidosEdit.text.toString(),
                numeroDocumento = binding.dniEdit.text.toString(),
                direccion = binding.direccionEdit.text.toString(),
                distrito =binding.distritoEdit.text.toString(),
                correo = email,
                clave = passwordConfirmation,
            )
        )


    }

}