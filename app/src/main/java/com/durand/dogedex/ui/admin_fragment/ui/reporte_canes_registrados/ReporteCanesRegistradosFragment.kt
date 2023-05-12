package com.durand.dogedex.ui.admin_fragment.ui.reporte_canes_registrados

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.databinding.FragmentReportRegisterCanBinding

class ReporteCanesRegistradosFragment : Fragment() {

    private var _binding: FragmentReportRegisterCanBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       // val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentReportRegisterCanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // this creates a vertical layout Manager
        _binding!!.canReportLostRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ArrayList of class ItemsViewModel
        val data = ArrayList<ItemsViewModelReporte>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(ItemsViewModelReporte("ss", "Item " + i))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = ReporteCanesRegistradosAdapter(data)

        // Setting the Adapter with the recyclerview
        _binding!!.canReportLostRecyclerView.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}