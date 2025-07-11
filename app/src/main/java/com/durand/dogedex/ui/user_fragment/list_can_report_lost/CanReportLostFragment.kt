package com.durand.dogedex.ui.user_fragment.list_can_report_lost

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.databinding.FragmentCanReportLostBinding

class CanReportLostFragment : Fragment() {

    private var _binding: FragmentCanReportLostBinding? = null
    private lateinit var viewModel: CanReportLostViewModel
    private val binding get() = _binding!!
    private var idUsuario: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CanReportLostViewModel::class.java)

        _binding = FragmentCanReportLostBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        val sharedPref = activity?.getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        idUsuario = sharedPref?.getInt("idUsuario", -1) // -1 es el valor por defecto si no existe
        Log.d("josue", "idUsuario: $idUsuario")
        viewModel.list.observe(viewLifecycleOwner) {
            binding.canReportLostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.canReportLostRecyclerView. adapter = CanReportLostAdapter(it)

        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.listar(1!!)
    }

}