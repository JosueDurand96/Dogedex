package com.durand.projecttesisupc.di.fragment.user.my_can_register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.durand.projecttesisupc.R
import com.durand.projecttesisupc.databinding.FragmentCanPerdidoBinding
import com.durand.projecttesisupc.databinding.FragmentMyCanRegisterBinding
import com.durand.projecttesisupc.di.fragment.user.can_report_lost.CanReportLostAdapter
import com.durand.projecttesisupc.di.fragment.user.can_report_lost.ItemsViewModel
import com.durand.projecttesisupc.di.fragment.user.my_can_lost.CanPerdidoViewModel

class MyCanRegisterFragment : Fragment() {

    private var _binding: FragmentMyCanRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: MyCanRegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewModel = ViewModelProvider(this).get(MyCanRegisterViewModel::class.java)

        _binding = FragmentMyCanRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // this creates a vertical layout Manager
        _binding!!.canReportLostRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // ArrayList of class ItemsViewModel
        val data = ArrayList<ItemsViewModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(ItemsViewModel(R.drawable.dog_logo, "Item " + i))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = MyCanRegisterAdapter(data)

        // Setting the Adapter with the recyclerview
        _binding!!.canReportLostRecyclerView.adapter = adapter
        return root
    }



}