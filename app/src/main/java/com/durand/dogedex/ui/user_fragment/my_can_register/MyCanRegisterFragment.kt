package com.durand.dogedex.ui.user_fragment.my_can_register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.dogedex.databinding.FragmentMyCanRegisterBinding
import com.durand.dogedex.ui.user_fragment.can_report_lost.CanReportLostAdapter

class MyCanRegisterFragment : Fragment() {

    private var _binding: FragmentMyCanRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MyCanRegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(MyCanRegisterViewModel::class.java)

        _binding = FragmentMyCanRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading -> }

        viewModel.listar()
        viewModel.list.observe(requireActivity()) {

            binding.canReportLostRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.canReportLostRecyclerView. adapter = MyCanRegisterAdapter(it)

        }

        return root
    }


}