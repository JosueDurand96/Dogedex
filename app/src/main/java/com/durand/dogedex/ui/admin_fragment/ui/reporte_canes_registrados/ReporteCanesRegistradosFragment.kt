package com.durand.dogedex.ui.admin_fragment.ui.reporte_canes_registrados

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.api.User
import com.durand.dogedex.api.response.list_mascotas.ListaMascotas
import com.durand.dogedex.databinding.FragmentReportRegisterCanBinding

class ReporteCanesRegistradosFragment : Fragment() {

    private var _binding: FragmentReportRegisterCanBinding? = null
    private lateinit var viewModel: ReporteCanesRegistradosViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ReporteCanesRegistradosViewModel::class.java)

        _binding = FragmentReportRegisterCanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.startReportCanesRegistrados()
        viewModel.listCanes.observe(requireActivity()) {
            listCan(it)
        }

        return root
    }

    private fun listCan(list: List<ListaMascotas>) {
        _binding!!.canReportLostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ReporteCanesRegistradosAdapter(list)
        _binding!!.canReportLostRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}