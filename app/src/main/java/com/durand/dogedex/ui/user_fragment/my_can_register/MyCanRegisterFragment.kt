package com.durand.dogedex.ui.user_fragment.my_can_register

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.durand.dogedex.R

class MyCanRegisterFragment : Fragment() {

    companion object {
        fun newInstance() = MyCanRegisterFragment()
    }

    private lateinit var viewModel: MyCanRegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_can_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyCanRegisterViewModel::class.java)
        // TODO: Use the ViewModel
    }

}