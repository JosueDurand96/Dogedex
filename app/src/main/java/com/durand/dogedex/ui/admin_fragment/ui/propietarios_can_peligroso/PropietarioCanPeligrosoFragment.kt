package com.durand.dogedex.ui.admin_fragment.ui.propietarios_can_peligroso

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.durand.dogedex.databinding.FragmentPropietarioCanPeligrosoBinding

class PropietarioCanPeligrosoFragment : Fragment() {

    private var _binding: FragmentPropietarioCanPeligrosoBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       // val galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentPropietarioCanPeligrosoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textGallery
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}