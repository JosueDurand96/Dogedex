package com.durand.dogedex.ui.user_fragment.list_my_can_register

import android.content.Context
import android.os.Bundle
import android.util.Log
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
    private var idUsuario: Long? = 0

    private lateinit var adapter: MyCanRegisterAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(MyCanRegisterViewModel::class.java)
        val sharedPref = activity?.getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        idUsuario = sharedPref?.getLong("idUsuario", -1) // -1 es el valor por defecto si no existe

        _binding = FragmentMyCanRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        // Configurar RecyclerView una sola vez
        adapter = MyCanRegisterAdapter(emptyList())
        binding.canReportLostRecyclerView.adapter = adapter
        
        // Asegurar que el layoutManager esté configurado (aunque esté en XML, a veces es necesario configurarlo en código)
        if (binding.canReportLostRecyclerView.layoutManager == null) {
            binding.canReportLostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            Log.d("MyCanRegisterFragment", "LayoutManager configurado manualmente")
        }
        
        Log.d("MyCanRegisterFragment", "RecyclerView configurado")
        Log.d("MyCanRegisterFragment", "  - adapter: ${binding.canReportLostRecyclerView.adapter}")
        Log.d("MyCanRegisterFragment", "  - layoutManager: ${binding.canReportLostRecyclerView.layoutManager}")
        Log.d("MyCanRegisterFragment", "  - visibility: ${binding.canReportLostRecyclerView.visibility}")
        Log.d("MyCanRegisterFragment", "  - width: ${binding.canReportLostRecyclerView.width}, height: ${binding.canReportLostRecyclerView.height}")
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.list.observe(viewLifecycleOwner) { list ->
            Log.d("MyCanRegisterFragment", "=== Observer activado ===")
            Log.d("MyCanRegisterFragment", "Lista recibida con ${list} elementos")
            if (list.isNotEmpty()) {
                Log.d("MyCanRegisterFragment", "Primer elemento: ${list[0].nombre}, Raza: ${list[0].raza}")
                Log.d("MyCanRegisterFragment", "Todos los elementos:")
                list.forEachIndexed { index, item ->
                    Log.d("MyCanRegisterFragment", "  [$index] ${item.nombre} - ${item.raza} - ID: ${item.id}")
                }
            } else {
                Log.w("MyCanRegisterFragment", "La lista está vacía")
            }
            
            // SOLUCIÓN: Recrear el adapter con la nueva lista en lugar de actualizar
            Log.d("MyCanRegisterFragment", "Recreando adapter con ${list.size} elementos")
            adapter = MyCanRegisterAdapter(list)
            binding.canReportLostRecyclerView.adapter = adapter
            
            Log.d("MyCanRegisterFragment", "Adapter recreado y asignado")
            Log.d("MyCanRegisterFragment", "RecyclerView adapter: ${binding.canReportLostRecyclerView.adapter}")
            Log.d("MyCanRegisterFragment", "RecyclerView itemCount: ${binding.canReportLostRecyclerView.adapter?.itemCount}")
            Log.d("MyCanRegisterFragment", "RecyclerView visibility: ${binding.canReportLostRecyclerView.visibility}")
            Log.d("MyCanRegisterFragment", "RecyclerView layoutManager: ${binding.canReportLostRecyclerView.layoutManager}")
            
            // Forzar invalidación del layout
            binding.canReportLostRecyclerView.invalidate()
            binding.canReportLostRecyclerView.requestLayout()
            Log.d("MyCanRegisterFragment", "RecyclerView invalidado y layout solicitado")
        }
        
        Log.d("MyCanRegisterFragment", "onCreateView - idUsuario: $idUsuario")
        
        // Cargar datos solo si idUsuario es válido
        if (idUsuario != null && idUsuario != -1L) {
            viewModel.listar(idUsuario!!)
        } else {
            Log.e("MyCanRegisterFragment", "Error: idUsuario no válido: $idUsuario")
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}