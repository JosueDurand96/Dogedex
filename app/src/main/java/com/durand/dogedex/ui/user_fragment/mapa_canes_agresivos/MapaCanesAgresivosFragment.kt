package com.durand.dogedex.ui.user_fragment.mapa_canes_agresivos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
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
import com.durand.dogedex.data.response.oficial.ListarCanResponse
import com.durand.dogedex.databinding.FragmentMapaCanesAgresivosBinding
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

class MapaCanesAgresivosFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentMapaCanesAgresivosBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MapaCanesAgresivosViewModel
    private lateinit var mMap: GoogleMap
    private val markers = mutableListOf<Marker>()
    private val aggressiveDogsWithCoords = mutableMapOf<Int, LatLng>()
    private val geocoder: Geocoder by lazy { Geocoder(requireContext(), Locale.getDefault()) }
    private var idUsuario: Long = -1

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
        viewModel = ViewModelProvider(this).get(MapaCanesAgresivosViewModel::class.java)

        val sharedPref = activity?.getSharedPreferences("idUsuario", Context.MODE_PRIVATE)
        idUsuario = sharedPref?.getLong("idUsuario", -1) ?: -1

        _binding = FragmentMapaCanesAgresivosBinding.inflate(inflater, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.allAggressiveDogs.observe(viewLifecycleOwner) { dogs ->
            loadAndGeocodeAggressiveDogs(dogs)
        }

        if (idUsuario != -1L) {
            viewModel.loadAggressiveDogs(idUsuario)
        } else {
            Log.e("MapaCanesAgresivosFragment", "Error: idUsuario no válido: $idUsuario")
        }

        return binding.root
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

    private fun loadAndGeocodeAggressiveDogs(dogs: List<ListarCanResponse>) {
        if (dogs.isEmpty()) {
            // If no aggressive dogs, ensure Lima is shown
            val lima = LatLng(-12.0464, -77.0428)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima, 12f))
            return
        }

        // Check which dogs already have coordinates
        val dogsWithCoords = mutableListOf<ListarCanResponse>()
        val dogsNeedingGeocode = mutableListOf<ListarCanResponse>()

        dogs.forEach { dog ->
            val latitud = dog.latitud
            val longitud = dog.longitud
            if (latitud != null && longitud != null &&
                latitud != 0.0 && longitud != 0.0
            ) {
                dogsWithCoords.add(dog)
                aggressiveDogsWithCoords[dog.hashCode()] = LatLng(latitud, longitud)
            } else if (!dog.distrito.isNullOrBlank()) {
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
        dogs: List<ListarCanResponse>,
        onComplete: () -> Unit
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            dogs.forEach { dog ->
                try {
                    if (!dog.distrito.isNullOrBlank()) {
                        // Primero intentar usar coordenadas conocidas
                        val knownLocation = getKnownDistrictCoordinates(dog.distrito)
                        if (knownLocation != null) {
                            aggressiveDogsWithCoords[dog.hashCode()] = knownLocation
                            Log.d("MapaCanesAgresivosFragment", "Usando coordenadas conocidas para: ${dog.distrito}")
                        } else {
                            // Si no hay coordenadas conocidas, intentar geocodificar
                            val distrito = "${dog.distrito}, Lima, Peru"
                            val addresses = geocoder.getFromLocationName(distrito, 1)
                            
                            if (addresses != null && addresses.isNotEmpty()) {
                                val address = addresses[0]
                                val coords = LatLng(address.latitude, address.longitude)
                                aggressiveDogsWithCoords[dog.hashCode()] = coords
                                Log.d("MapaCanesAgresivosFragment", "Geocodificado: ${dog.distrito} -> ${coords.latitude}, ${coords.longitude}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MapaCanesAgresivosFragment", "Error geocoding ${dog.distrito}: ${e.message}")
                }
            }
            withContext(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    private fun getKnownDistrictCoordinates(distrito: String): LatLng? {
        // Mapa de coordenadas conocidas de distritos de Lima
        val distritoLower = distrito.lowercase().trim()
        val districtCoordinates = mapOf(
            "rímac" to LatLng(-12.0386, -77.0244),
            "rimac" to LatLng(-12.0386, -77.0244),
            "miraflores" to LatLng(-12.1198, -77.0303),
            "san isidro" to LatLng(-12.0989, -77.0361),
            "san borja" to LatLng(-12.0935, -77.0087),
            "santiago de surco" to LatLng(-12.1490, -76.9938),
            "la molina" to LatLng(-12.0750, -76.9311),
            "san miguel" to LatLng(-12.0775, -77.0936),
            "magdalena del mar" to LatLng(-12.0953, -77.0700),
            "pueblo libre" to LatLng(-12.0719, -77.0625),
            "jesús maría" to LatLng(-12.0844, -77.0486),
            "lince" to LatLng(-12.0864, -77.0389),
            "la victoria" to LatLng(-12.0728, -77.0228),
            "breña" to LatLng(-12.0575, -77.0464),
            "cercado de lima" to LatLng(-12.0464, -77.0428),
            "lima" to LatLng(-12.0464, -77.0428),
            "san juan de lurigancho" to LatLng(-11.9833, -76.9983),
            "san martín de porres" to LatLng(-11.9817, -77.0744),
            "comas" to LatLng(-11.9319, -77.0547),
            "carabayllo" to LatLng(-11.8550, -77.0250),
            "los olivos" to LatLng(-11.9636, -77.0711),
            "independencia" to LatLng(-11.9911, -77.0486),
            "pachacamac" to LatLng(-12.2375, -76.8547),
            "villa el salvador" to LatLng(-12.2014, -76.9569),
            "san juan de miraflores" to LatLng(-12.1686, -76.9917),
            "villa maría del triunfo" to LatLng(-12.1492, -76.9539),
            "chorrillos" to LatLng(-12.1681, -77.0147),
            "barranco" to LatLng(-12.1431, -77.0219),
            "surquillo" to LatLng(-12.1242, -77.0250),
            "chaclacayo" to LatLng(-11.9761, -76.7817),
            "ate" to LatLng(-12.0181, -76.8614),
            "san luis" to LatLng(-12.0744, -77.0069),
            "el agustino" to LatLng(-12.0217, -76.9883)
        )
        return districtCoordinates[distritoLower]
    }

    private fun updateMapMarkers(dogs: List<ListarCanResponse>) {
        // Clear existing markers
        markers.forEach { it.remove() }
        markers.clear()

        // Add markers for all aggressive dogs with coordinates
        dogs.forEach { dog ->
            val coords = aggressiveDogsWithCoords[dog.hashCode()]
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

    private fun addMarker(dog: ListarCanResponse, coords: LatLng) {
        val title = dog.nombre ?: "Sin nombre"

        val marker = mMap.addMarker(
            MarkerOptions()
                .position(coords)
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
        marker?.tag = dog
        if (marker != null) {
            markers.add(marker)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val dog = marker.tag as? ListarCanResponse
        if (dog != null) {
            val bundle = Bundle().apply {
                putSerializable("can", dog)
            }
            findNavController().navigate(R.id.action_nav_map_canes_agresivos_to_nav_my_can_register_detail, bundle)
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

