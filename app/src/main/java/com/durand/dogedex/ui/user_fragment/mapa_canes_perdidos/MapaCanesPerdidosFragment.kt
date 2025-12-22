package com.durand.dogedex.ui.user_fragment.mapa_canes_perdidos

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.durand.dogedex.R
import com.durand.dogedex.data.response.oficial.ListarCanPerdidoResponse
import com.durand.dogedex.databinding.FragmentMapaCanesPerdidosBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MapaCanesPerdidosFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentMapaCanesPerdidosBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MapaCanesPerdidosViewModel
    private lateinit var mMap: GoogleMap
    private val markers = mutableListOf<Marker>()
    private val lostDogsWithCoords = mutableMapOf<Int, LatLng>()
    private val geocoder: Geocoder by lazy { Geocoder(requireContext(), Locale.getDefault()) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            enableMyLocation()
        } else {
            Toast.makeText(requireContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(MapaCanesPerdidosViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        _binding = FragmentMapaCanesPerdidosBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        setupListeners()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.allLostDogs.observe(viewLifecycleOwner) { dogs ->
            loadAndGeocodeLostDogs(dogs)
        }

        viewModel.loadLostDogs()

        return binding.root
    }

    private fun setupListeners() {
        // Listeners removed - no filters needed
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.setOnMarkerClickListener(this)

        // Center on Lima, Peru by default
        val lima = LatLng(-12.0464, -77.0428)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 12f))

        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            mMap.uiSettings.isMyLocationButtonEnabled = true
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun loadAndGeocodeLostDogs(dogs: List<ListarCanPerdidoResponse>) {
        if (dogs.isEmpty()) {
            // If no lost dogs, ensure Lima is shown
            val lima = LatLng(-12.0464, -77.0428)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 12f))
            return
        }

        // Check which dogs already have coordinates
        val dogsWithCoords = mutableListOf<ListarCanPerdidoResponse>()
        val dogsNeedingGeocode = mutableListOf<ListarCanPerdidoResponse>()

        dogs.forEach { dog ->
            val latitud = dog.latitud
            val longitud = dog.longitud
            if (latitud != null && longitud != null &&
                latitud != 0.0 && longitud != 0.0
            ) {
                dogsWithCoords.add(dog)
                lostDogsWithCoords[dog.hashCode()] = LatLng(latitud, longitud)
            } else if (!dog.lugarPerdida.isNullOrBlank()) {
                dogsNeedingGeocode.add(dog)
            }
        }

        // If all dogs have coordinates, show markers immediately
        if (dogsNeedingGeocode.isEmpty()) {
            updateMapMarkers(dogs)
            return
        }

        // Geocode dogs that need it
        binding.geocodingOverlay.visibility = View.VISIBLE
        geocodeDogs(dogsNeedingGeocode) {
            binding.geocodingOverlay.visibility = View.GONE
            updateMapMarkers(dogs)
        }
    }

    private fun geocodeDogs(
        dogs: List<ListarCanPerdidoResponse>,
        onComplete: () -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            dogs.forEach { dog ->
                try {
                    val lugarPerdida = "${dog.lugarPerdida}, Lima, Peru"
                    val addresses = geocoder.getFromLocationName(lugarPerdida, 1)
                    
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val coords = LatLng(address.latitude, address.longitude)
                        lostDogsWithCoords[dog.hashCode()] = coords
                    }
                } catch (e: Exception) {
                    Log.e("MapaCanesPerdidosFragment", "Error geocoding ${dog.lugarPerdida}: ${e.message}")
                }
            }
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    private fun updateMapMarkers(dogs: List<ListarCanPerdidoResponse>) {
        // Clear existing markers
        markers.forEach { it.remove() }
        markers.clear()

        // Add markers for all lost dogs with coordinates
        dogs.forEach { dog ->
            val coords = lostDogsWithCoords[dog.hashCode()]
            if (coords != null) {
                addMarker(dog, coords)
            }
        }

        // Fit map to show all markers, or keep Lima view if no markers
        if (markers.isNotEmpty()) {
            val builder = com.google.android.gms.maps.model.LatLngBounds.Builder()
            markers.forEach { builder.include(it.position) }
            val bounds = builder.build()
            val padding = 100
            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap.animateCamera(cu)
        } else {
            // If no markers, ensure we're viewing Lima
            val lima = LatLng(-12.0464, -77.0428)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 12f))
        }
    }

    private fun addMarker(dog: ListarCanPerdidoResponse, coords: LatLng) {
        val title = dog.nombre ?: "Sin nombre"

        val marker = mMap.addMarker(
            MarkerOptions()
                .position(coords)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
        )
        marker?.tag = dog
        if (marker != null) {
            markers.add(marker)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val dog = marker.tag as? ListarCanPerdidoResponse
        if (dog != null) {
            val bundle = Bundle().apply {
                putSerializable("canPerdido", dog)
            }
            findNavController().navigate(R.id.action_nav_map_canes_perdidos_to_nav_can_perdido_detail, bundle)
        }
        return true
    }

    private fun centerOnMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    
                    // Add marker for user location
                    mMap.addMarker(
                        MarkerOptions()
                            .position(currentLatLng)
                            .title("📍 Mi ubicación")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    )?.showInfoWindow()
                } ?: run {
                    // Default to Lima if location is not available
                    val lima = LatLng(-12.0464, -77.0428)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 12f))
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

