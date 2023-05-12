package com.durand.dogedex.ui.user_fragment.my_can_register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.durand.dogedex.R
import com.durand.dogedex.api.User
import com.durand.dogedex.databinding.FragmentMyCanRegisterBinding
import com.durand.dogedex.ui.user_fragment.can_report_lost.ItemsViewModel
import com.durand.dogedex.util.createLoadingDialog

class MyCanRegisterFragment : Fragment() {

    private var _binding: FragmentMyCanRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val vm: MyCanRegisterViewModel by viewModels()

    private val adapter by lazy {
        MyCanRegisterAdapter()
    }

    private val loading by lazy {
        requireContext().createLoadingDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCanRegisterBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // this creates a vertical layout Manager

        // ArrayList of class ItemsViewModel
//        val data = ArrayList<ItemsViewModel>()

        // This loop will create 20 Views containing
        // the image with the count of view
//        for (i in 1..20) {
//            data.add(ItemsViewModel(R.drawable.dog_logo, "Item " + i))
//        }

        // This will pass the ArrayList to our Adapter

        // Setting the Adapter with the recyclerview
        _binding!!.canReportLostRecyclerView.adapter = adapter
        initObservers()
        getUserProfile()
        vm.executeConsultarMascotasPorId()
        return root
    }

    private fun initObservers() {
        vm.list.observe(viewLifecycleOwner) {
            adapter.add(it.map { can -> ItemsViewModel(R.drawable.dog_logo, can.nombre, can.nombre) })
        }

        vm.loading.observe(viewLifecycleOwner) {
            if (it) {
                loading.show()
            } else loading.dismiss()
        }
    }

    private fun getUserProfile() {
        val loggedInUser: User? = User.getLoggedInUser(requireActivity())
        vm.setUserProfile(loggedInUser)
    }
}