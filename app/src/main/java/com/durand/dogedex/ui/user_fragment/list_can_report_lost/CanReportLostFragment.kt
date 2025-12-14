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
        viewModel.list.observe(viewLifecycleOwner) { list ->
            Log.d("CanReportLostFragment", "=== Observer activado ===")
            Log.d("CanReportLostFragment", "Lista recibida con ${list.size} elementos")
            if (list.isNotEmpty()) {
                Log.d("CanReportLostFragment", "Primer elemento: ${list[0].nombre}, Fecha pérdida: ${list[0].fechaPerdida}")
                list.forEachIndexed { index, item ->
                    Log.d("CanReportLostFragment", "  [$index] ${item.nombre} - ${item.lugarPerdida} - Fecha: ${item.fechaPerdida}")
                }
            } else {
                Log.w("CanReportLostFragment", "La lista está vacía")
            }

            if (binding.canReportLostRecyclerView.layoutManager == null) {
                binding.canReportLostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                Log.d("CanReportLostFragment", "LayoutManager configurado")
            }

            binding.canReportLostRecyclerView.adapter = CanReportLostAdapter(list)
            binding.canReportLostRecyclerView.invalidate()
            binding.canReportLostRecyclerView.requestLayout()

            Log.d("CanReportLostFragment", "Adapter actualizado con ${list.size} elementos")
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.listar()
    }

}