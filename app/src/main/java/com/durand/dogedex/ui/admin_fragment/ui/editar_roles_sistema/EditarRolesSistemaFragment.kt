package com.durand.dogedex.ui.admin_fragment.ui.editar_roles_sistema

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.durand.dogedex.R
import com.durand.dogedex.databinding.FragmentEditarRolesSistemaBinding
import com.durand.dogedex.databinding.FragmentPropietarioCanPeligrosoBinding


class EditarRolesSistemaFragment : Fragment() {

    private var _binding: FragmentEditarRolesSistemaBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentEditarRolesSistemaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


}