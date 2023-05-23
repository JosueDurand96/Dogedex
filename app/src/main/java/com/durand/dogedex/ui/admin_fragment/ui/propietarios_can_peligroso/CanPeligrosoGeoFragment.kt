package com.durand.dogedex.ui.admin_fragment.ui.propietarios_can_peligroso

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.durand.dogedex.data.response.can_perdido.ListCanPerdido
import com.durand.dogedex.databinding.FragmentCanPerdidosGeoBinding
import com.durand.dogedex.ui.admin_fragment.ui.reporte_canes_registrados.ReporteCanesRegistradosViewModel

class CanPeligrosoGeoFragment : Fragment() {

    private var _binding: FragmentCanPerdidosGeoBinding? = null
    private lateinit var viewModel: ListCanPeligrosoPerdidas

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ListCanPeligrosoPerdidas::class.java)

        _binding = FragmentCanPerdidosGeoBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.startReportCanesRegistrados()
        viewModel.listCanes.observe(requireActivity()){
            mapGoogleApi(it)
        }
        val textView: TextView = binding.textGallery
        return root
    }

    private fun mapGoogleApi(list: List<ListCanPerdido>){
        Log.d("mapGoogleApi", "mapGoogleApi: $list")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}