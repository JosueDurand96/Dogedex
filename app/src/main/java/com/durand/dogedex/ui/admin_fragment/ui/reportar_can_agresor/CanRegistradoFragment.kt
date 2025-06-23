package com.durand.dogedex.ui.admin_fragment.ui.reportar_can_agresor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.data.request.AddAggressionPetRequest
import com.durand.dogedex.data.request.DniRequest
import com.durand.dogedex.data.response.consultar_mascota_dni.ListMascotaDni
import com.durand.dogedex.databinding.FragmentCanRegistradoBinding


class CanRegistradoFragment : Fragment() {
    private var _binding: FragmentCanRegistradoBinding? = null

    private lateinit var adapter: AddCanAgresorAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: AddCanAgresorViewModel
    private var idMascota: Int? = null
    private var idUsuario: Int? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(AddCanAgresorViewModel::class.java)

        _binding = FragmentCanRegistradoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.searchAppCompatButton.setOnClickListener {
            val dni = binding.dniTextInputEditText.text.toString()
            Log.d("josue", "dni: $dni")
            viewModel.startReportCanesRegistrados(DniRequest(dni))
        }

        viewModel.listCanes.observe(requireActivity()) {
            dataDniRecyler(it)
        }

        viewModel.addCan.observe(requireActivity()){
            if (it == true){
                Toast.makeText(requireContext(), "Se registro correctamente!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "No se pudo registrar!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.registrarAppCompatButton.setOnClickListener {
            val descripcion = binding.descriptionTextInputEditText.text.toString()
            val fecha = binding.fechaTextInputEditText.text.toString()
            viewModel.addPerroPeligroso(
                AddAggressionPetRequest(
                    descripcion, "0",
                    fecha,
                    idMascota!!,
                    idUsuario!!,
                )
            )
        }
        return root
    }

    private fun dataDniRecyler(list: List<ListMascotaDni>) {
        adapter = AddCanAgresorAdapter(list)
        binding.dniRecyclerView.adapter = adapter
        binding.dniRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setListenerItemSelected(object :
            AddCanAgresorAdapter.OnClickSelected {
            override fun onSelected(nombre: String, nombreMascota: String, id_mascota:Int, id_usuario:Int) {
                idMascota = id_mascota
                idUsuario = id_usuario
                binding.resultCanTextView.visibility = View.VISIBLE
                binding.resultCanTextView.text = "Nombre: $nombre - Mascota $nombreMascota"
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}