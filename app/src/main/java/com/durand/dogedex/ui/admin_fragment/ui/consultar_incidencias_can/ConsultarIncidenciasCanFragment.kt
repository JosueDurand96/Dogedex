package com.durand.dogedex.ui.admin_fragment.ui.consultar_incidencias_can

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.data.request.DniRequest
import com.durand.dogedex.data.response.consultar_can_agresivo_dni.ConsultarCanAgresivoDni
import com.durand.dogedex.databinding.FragmentConsultarIncidenciasCanBinding

class ConsultarIncidenciasCanFragment : Fragment() {


    private var _binding: FragmentConsultarIncidenciasCanBinding? = null
    private lateinit var adapter: ConsultarIncidenciasCanAdapter
    private lateinit var viewModel: ConsultarIncidenciasCanViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ConsultarIncidenciasCanViewModel::class.java)

        _binding = FragmentConsultarIncidenciasCanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _binding!!.searchAppCompatButton.setOnClickListener {
            val dni = binding.dniTextInputEditText.text.toString()
            Log.d("josue", "dni: $dni")
            viewModel.consultaCanesAgresivoXDni(DniRequest(dni))
        }
        viewModel.listCanesPorDni.observe(requireActivity()) {
            dataDniRecycler(it)
        }


        return root
    }

    private fun dataDniRecycler(list: List<ConsultarCanAgresivoDni>) {
        adapter = ConsultarIncidenciasCanAdapter(list)
        binding.dniRecyclerView.adapter = adapter
        binding.dniRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setListenerItemSelected(object :
            ConsultarIncidenciasCanAdapter.OnClickSelected {
            override fun onSelected(
                nombre: String,
                nombreMascota: String,
                apellidos: String,
                caracter: String,
                color: String,
                descripcionAgresion: String,
                descripcionRaza: String,
                fechaHora: String,
                genero: String,
                idAgresion: Int,
                idMascota: Int,
                idRaza: Int,
                idUsuario: Int,
                numeroDocumento: String,
                pelaje: String,
                tamano: String
            ) {
                binding.nombreTextView.text = nombre
                binding.apellidosTextView.text = apellidos
                binding.nombreMacotaTextView.text = idMascota.toString()
                binding.razaTextView.text = descripcionRaza
                binding.caracterTextView.text = caracter
                binding.generoTextView.text = genero
                binding.fechaTextView.text = fechaHora
                binding.descriptionTextView.text = descripcionAgresion
            }
        })

    }


}