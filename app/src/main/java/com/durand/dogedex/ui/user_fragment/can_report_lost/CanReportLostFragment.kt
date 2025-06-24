package com.durand.dogedex.ui.user_fragment.can_report_lost

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CanReportLostViewModel::class.java)

        _binding = FragmentCanReportLostBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
        }
        viewModel.listar()
        viewModel.list.observe(requireActivity()) {
            Log.d("josue", "nombre: " + it[0].nombre)
            Log.d("josue", "complete: " + it)

            binding.canReportLostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.canReportLostRecyclerView. adapter = CanReportLostAdapter(it)

        }
        return root
    }

}