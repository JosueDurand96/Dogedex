package com.durand.dogedex.ui.user_fragment.my_can_lost

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.databinding.FragmentCanPerdidoBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class CanPerdidoFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentCanPerdidoBinding? = null
    private lateinit var mMap: GoogleMap
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(FragmentCanPerdidoViewModel::class.java)

        _binding = FragmentCanPerdidoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.activoAutoCompleteTextView
        val itemsEspecie = listOf("Activo", "Inactivo")
        val adapterEspecie = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsEspecie)
        _binding!!.activoAutoCompleteTextView.setAdapter(adapterEspecie)
        return root
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-12.014190, -77.030687)
        mMap.addMarker(MarkerOptions().position(sydney).title("Holiiii"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}