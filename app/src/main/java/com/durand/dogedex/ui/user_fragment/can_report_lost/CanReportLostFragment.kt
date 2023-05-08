package com.durand.dogedex.ui.user_fragment.can_report_lost

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.durand.dogedex.R

class CanReportLostFragment : Fragment() {

    companion object {
        fun newInstance() = CanReportLostFragment()
    }

    private lateinit var viewModel: CanReportLostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_can_report_lost, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CanReportLostViewModel::class.java)
        // TODO: Use the ViewModel
    }

}