package com.durand.dogedex.ui.user_fragment.list_my_can_register

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.databinding.FragmentCanAgresivoReportBinding

class CanAgresivoReportFragment : Fragment() {

    private var _binding: FragmentCanAgresivoReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: CanAgresivoReportViewModel
    private var idUsuario: Long? = 0

    private lateinit var adapter: MyCanRegisterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CanAgresivoReportViewModel::class.java)
        val sharedPref = activity?.getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        idUsuario = sharedPref?.getLong("idUsuario", -1) // -1 es el valor por defecto si no existe

        _binding = FragmentCanAgresivoReportBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        // Configurar RecyclerView una sola vez
        adapter = MyCanRegisterAdapter(emptyList())
        binding.canReportLostRecyclerView.adapter = adapter
        
        // Asegurar que el layoutManager esté configurado (aunque esté en XML, a veces es necesario configurarlo en código)
        if (binding.canReportLostRecyclerView.layoutManager == null) {
            binding.canReportLostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            Log.d("CanAgresivoReportFragment", "LayoutManager configurado manualmente")
        }
        
        Log.d("CanAgresivoReportFragment", "RecyclerView configurado")
        Log.d("CanAgresivoReportFragment", "  - adapter: ${binding.canReportLostRecyclerView.adapter}")
        Log.d("CanAgresivoReportFragment", "  - layoutManager: ${binding.canReportLostRecyclerView.layoutManager}")
        Log.d("CanAgresivoReportFragment", "  - visibility: ${binding.canReportLostRecyclerView.visibility}")
        Log.d("CanAgresivoReportFragment", "  - width: ${binding.canReportLostRecyclerView.width}, height: ${binding.canReportLostRecyclerView.height}")
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.list.observe(viewLifecycleOwner) { list ->
            Log.d("CanAgresivoReportFragment", "=== Observer activado ===")
            Log.d("CanAgresivoReportFragment", "Lista recibida con ${list} elementos")
            if (list.isNotEmpty()) {
                Log.d("CanAgresivoReportFragment", "Primer elemento: ${list[0].nombre}, Raza: ${list[0].raza}")
                Log.d("CanAgresivoReportFragment", "Todos los elementos:")
                list.forEachIndexed { index, item ->
                    Log.d("CanAgresivoReportFragment", "  [$index] ${item.nombre} - ${item.raza} - ID: ${item.id}")
                }
            } else {
                Log.w("CanAgresivoReportFragment", "La lista está vacía")
            }
            
            // SOLUCIÓN: Recrear el adapter con la nueva lista en lugar de actualizar
            Log.d("CanAgresivoReportFragment", "Recreando adapter con ${list.size} elementos")
            adapter = MyCanRegisterAdapter(list) { can ->
                // Navegar al fragment de detalle
                val bundle = Bundle().apply {
                    putSerializable("can", can)
                }
                findNavController().navigate(
                    com.durand.dogedex.R.id.action_nav_can_agresivo_report_to_nav_my_can_register_detail,
                    bundle
                )
            }
            binding.canReportLostRecyclerView.adapter = adapter
            
            Log.d("CanAgresivoReportFragment", "Adapter recreado y asignado")
            Log.d("CanAgresivoReportFragment", "RecyclerView adapter: ${binding.canReportLostRecyclerView.adapter}")
            Log.d("CanAgresivoReportFragment", "RecyclerView itemCount: ${binding.canReportLostRecyclerView.adapter?.itemCount}")
            Log.d("CanAgresivoReportFragment", "RecyclerView visibility: ${binding.canReportLostRecyclerView.visibility}")
            Log.d("CanAgresivoReportFragment", "RecyclerView layoutManager: ${binding.canReportLostRecyclerView.layoutManager}")
            
            // Forzar invalidación del layout
            binding.canReportLostRecyclerView.invalidate()
            binding.canReportLostRecyclerView.requestLayout()
            Log.d("CanAgresivoReportFragment", "RecyclerView invalidado y layout solicitado")
        }
        
        Log.d("CanAgresivoReportFragment", "onCreateView - idUsuario: $idUsuario")
        
        // Cargar datos solo si idUsuario es válido
        if (idUsuario != null && idUsuario != -1L) {
            viewModel.listar(idUsuario!!)
        } else {
            Log.e("CanAgresivoReportFragment", "Error: idUsuario no válido: $idUsuario")
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando se vuelve a esta pantalla para mostrar canes recién registrados
        val sharedPref = activity?.getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        val currentIdUsuario = sharedPref?.getLong("idUsuario", -1) ?: -1
        
        if (currentIdUsuario != -1L) {
            Log.d("CanAgresivoReportFragment", "onResume - Recargando datos para idUsuario: $currentIdUsuario")
            viewModel.listar(currentIdUsuario)
        } else {
            Log.e("CanAgresivoReportFragment", "onResume - Error: idUsuario no válido: $currentIdUsuario")
        }
    }

}