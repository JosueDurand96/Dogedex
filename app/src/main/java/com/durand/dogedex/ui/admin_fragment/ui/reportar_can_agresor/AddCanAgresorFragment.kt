package com.durand.dogedex.ui.admin_fragment.ui.reportar_can_agresor

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.data.Request.DniRequest
import com.durand.dogedex.data.response.consultar_mascota_dni.ListMascotaDni
import com.durand.dogedex.databinding.FragmentReportCanAgresorBinding

class AddCanAgresorFragment : Fragment() {

    private var _binding: FragmentReportCanAgresorBinding? = null

    private lateinit var adapter: AddCanAgresorAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: AddCanAgresorViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AddCanAgresorViewModel::class.java)

        _binding = FragmentReportCanAgresorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.dniTextInputEditText.text.toString()

        viewModel.startReportCanesRegistrados(DniRequest("10880423"))

        viewModel.listCanes.observe(requireActivity()){
            dataDniRecyler(it)
        }
        return root
    }


    private fun dataDniRecyler(list: List<ListMascotaDni>){
        adapter = AddCanAgresorAdapter( list)
        binding.dniRecyclerView.adapter = adapter
        binding.dniRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter.setListenerItemSelected(object :
            AddCanAgresorAdapter.OnClickSelected {
            override fun onSelected(nombre: String, nombreMascota: String) {
                binding.resultCanTextView.visibility = View.VISIBLE
                binding.resultCanTextView.text  = "Nombre: $nombre - Mascota $nombreMascota"
            }
        })
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
