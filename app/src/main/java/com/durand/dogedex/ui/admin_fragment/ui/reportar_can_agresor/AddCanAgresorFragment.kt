package com.durand.dogedex.ui.admin_fragment.ui.reportar_can_agresor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.R
import com.durand.dogedex.data.Request.AddAggressionPetRequest
import com.durand.dogedex.data.Request.DniRequest
import com.durand.dogedex.data.response.consultar_mascota_dni.ListMascotaDni
import com.durand.dogedex.databinding.FragmentReportCanAgresorBinding

class AddCanAgresorFragment : Fragment() {

    private var _binding: FragmentReportCanAgresorBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportCanAgresorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.canRegisterAppCompatButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_report_can_agresor_to_nav_can_registrado_agresor)
        }

        binding.canSinRegistrarAppCompatButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_report_can_agresor_to_nav_can_no_registrado_agresor)
        }


        return root
    }



}
