package com.durand.dogedex.ui.user_fragment.list_my_can_register

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.databinding.FragmentMyCanRegisterBinding

class MyCanRegisterFragment : Fragment() {

    private var _binding: FragmentMyCanRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MyCanRegisterViewModel
    private var idUsuario: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(MyCanRegisterViewModel::class.java)
        val sharedPref = activity?.getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        idUsuario = sharedPref?.getInt("idUsuario", -1) // -1 es el valor por defecto si no existe

        _binding = FragmentMyCanRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.listar()
        viewModel.list.observe(viewLifecycleOwner) {
            binding.canReportLostRecyclerView.layoutManager = LinearLayoutManager(requireContext()) // o `binding.root.context`
            binding.canReportLostRecyclerView.adapter = MyCanRegisterAdapter(it)
        }

        return root
    }

}