package com.durand.dogedex.ui.user_fragment.my_can_lost

import android.Manifest
import android.R
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.durand.dogedex.api.User
import com.durand.dogedex.api.response.consultarmascotas.DetalleMascota
import com.durand.dogedex.databinding.FragmentCanPerdidoBinding
import com.durand.dogedex.util.LocationsUtils
import com.durand.dogedex.util.LocationsUtils.Companion.locationFlow
import com.durand.dogedex.util.createLoadingDialog
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class CanPerdidoFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentCanPerdidoBinding? = null
    private lateinit var mMap: GoogleMap
    private val vm: CanPerdidoViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private val loading by lazy {
        requireContext().createLoadingDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCanPerdidoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val itemsEspecie = listOf("Activo", "Inactivo")
        val adapterEspecie = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsEspecie)
        _binding!!.activoAutoCompleteTextView.setAdapter(adapterEspecie)
        binding.vm = vm
        binding.lifecycleOwner = viewLifecycleOwner
        setupObservers()
        getUserProfile()
        return root
    }

    private fun setupObservers() {
        vm.list.observe(viewLifecycleOwner) {
            setMyPetsAdapter(it)
        }

        vm.loading.observe(viewLifecycleOwner) {
            if (it) loading.show() else loading.dismiss()
        }

        vm.viewState.observe(viewLifecycleOwner) {
            when (it) {
                is CanPerdidoViewModel.CanPerdidoEvent.Error -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }

                is CanPerdidoViewModel.CanPerdidoEvent.SuccessAgregarMascotaPerdida -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }

                CanPerdidoViewModel.CanPerdidoEvent.None -> {}
            }
        }
    }

    private fun setMyPetsAdapter(list: List<DetalleMascota>) {
        val adapterEspecie = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, list)
        binding.mascota.setAdapter(adapterEspecie)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissions()
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

    @Suppress("DEPRECATION")
    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 500
            fastestInterval = 250
            maxWaitTime = 500
            numUpdates = 3
        }
    }

    private val requestPermissionLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val permissionDenied = permissions.entries.find { !it.value }
        if (permissionDenied == null) {
            getCurrentPosition()
        } else {
            // NO PERMISSION
        }
    }

    private val requestActiveGPS =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                getCurrentPosition()
            } else {
                activeGPS()
            }
        }

    private fun activeGPS() {
        LocationsUtils.activeGPS(
            locationRequest = locationRequest,
            activity = requireActivity(),
            launchIntentSenderRequest = {
                requestActiveGPS.launch(it)
            }
        )
    }

    private fun getCurrentPosition() {
        if (LocationsUtils.isGPSActive(requireActivity())) {
            callBackFlowCollect()
        } else {
            activeGPS()
        }
    }

    private fun callBackFlowCollect() = safeCollectInViews {
        val location: Location? = fusedLocationClient.locationFlow(locationRequest)
            .firstOrNull { it != null }

        location?.let {
            vm.setCoordinates(latitude = it.latitude.toString(), longitude = it.longitude.toString())
        }
    }

    private fun checkPermissions() {
        val hasPermissions = LocationsUtils.isCurrentPositionAllowed(
            context = requireContext(),
            launchPermissions = {
                requestPermissionLocation.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )
        if (hasPermissions) getCurrentPosition()
    }

    private fun getUserProfile() {
        val loggedInUser: User? = User.getLoggedInUser(requireActivity())
        vm.setUserProfile(loggedInUser)
    }
}

inline fun Fragment.safeCollectInViews(crossinline c: suspend CoroutineScope.() -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            this.c()
        }
    }
}