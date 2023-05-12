package com.durand.dogedex.ui.user_fragment.can_report_lost

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.R
import com.durand.dogedex.databinding.FragmentCanReportLostBinding
import com.durand.dogedex.util.createLoadingDialog

class CanReportLostFragment : Fragment() {


    private var _binding: FragmentCanReportLostBinding? = null

    private val vm: CanReportLostViewModel by viewModels()
    private val binding get() = _binding!!

    private val adapter by lazy {
        CanReportLostAdapter()
    }

    private val loading by lazy {
        requireContext().createLoadingDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentCanReportLostBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _binding!!.canReportLostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        _binding!!.canReportLostRecyclerView.adapter = adapter
        initObservers()
        return root
    }

    private fun initObservers() {
        vm.list.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.add(it.map { pet -> ItemsViewModel(R.drawable.dog_logo, pet.petId.toString(), pet.description) })
            }
        }

        vm.loading.observe(viewLifecycleOwner) {
            if (it) {
                loading.show()
            } else loading.dismiss()
        }
    }
}