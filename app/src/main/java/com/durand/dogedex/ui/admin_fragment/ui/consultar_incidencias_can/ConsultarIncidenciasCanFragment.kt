package com.durand.dogedex.ui.admin_fragment.ui.consultar_incidencias_can

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.durand.dogedex.R
import com.durand.dogedex.databinding.FragmentConsultarIncidenciasCanBinding
import com.durand.dogedex.databinding.FragmentEditarRolesSistemaBinding

class ConsultarIncidenciasCanFragment : Fragment() {


    private var _binding: FragmentConsultarIncidenciasCanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentConsultarIncidenciasCanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }




}