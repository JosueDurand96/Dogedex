package com.durand.dogedex.ui.user_fragment.list_my_can_register

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.location.Geocoder
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.durand.dogedex.R
import com.durand.dogedex.data.response.oficial.ListarCanResponse
import com.durand.dogedex.databinding.FragmentMyCanRegisterDetailBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MyCanRegisterDetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMyCanRegisterDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var can: ListarCanResponse
    private var mMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyCanRegisterDetailBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Obtener los argumentos
        can = arguments?.getSerializable("can") as? ListarCanResponse
            ?: run {
                Log.e("MyCanRegisterDetailFragment", "Error: No se recibió can")
                findNavController().navigateUp()
                return root
            }

        setupUI()
        setupBackButton()
        setupMap()

        return root
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupUI() {
        // Cargar imagen
        loadImage()

        // Configurar textos con formato
        setupTexts()
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Configurar ubicación en el mapa usando el distrito
        setLocationOnMap(can.distrito)
    }

    private fun setLocationOnMap(distrito: String) {
        if (distrito.isBlank()) {
            // Si no hay distrito, centrar en Lima, Perú por defecto
            val defaultLocation = LatLng(-12.0464, -77.0428) // Lima, Perú
            mMap?.addMarker(MarkerOptions().position(defaultLocation).title("Ubicación del can"))
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
            return
        }

        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            // Buscar usando el distrito en Lima, Perú
            val searchQuery = "$distrito, Lima, Perú"
            val addresses = geocoder.getFromLocationName(searchQuery, 1)
            
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val location = LatLng(address.latitude, address.longitude)
                
                mMap?.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title("Distrito: $distrito")
                )
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
            } else {
                // Si no se encuentra la ubicación, centrar en Lima, Perú
                val defaultLocation = LatLng(-12.0464, -77.0428) // Lima, Perú
                mMap?.addMarker(
                    MarkerOptions()
                        .position(defaultLocation)
                        .title("Ubicación aproximada")
                )
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
                Log.w("MyCanRegisterDetailFragment", "No se pudo geocodificar: $distrito")
            }
        } catch (e: Exception) {
            Log.e("MyCanRegisterDetailFragment", "Error al geocodificar ubicación: ${e.message}", e)
            // En caso de error, centrar en Lima, Perú
            val defaultLocation = LatLng(-12.0464, -77.0428) // Lima, Perú
            mMap?.addMarker(
                MarkerOptions()
                    .position(defaultLocation)
                    .title("Ubicación aproximada")
            )
            mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
        }
    }

    private fun loadImage() {
        if (can.foto.isNotEmpty()) {
            try {
                val imageBytes = Base64.decode(can.foto, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                if (bitmap != null) {
                    binding.canImageView.load(bitmap) {
                        crossfade(true)
                        placeholder(R.drawable.dog_logo)
                        error(R.drawable.dog_logo)
                    }
                } else {
                    binding.canImageView.setImageResource(R.drawable.dog_logo)
                }
            } catch (e: Exception) {
                Log.e("MyCanRegisterDetailFragment", "Error al decodificar imagen: ${e.message}", e)
                binding.canImageView.setImageResource(R.drawable.dog_logo)
            }
        } else {
            binding.canImageView.setImageResource(R.drawable.dog_logo)
        }
    }

    private fun setupTexts() {
        // Nombre
        binding.nombreTextView.text = formatBoldText("Nombre: ", can.nombre)

        // Datos del can
        binding.razaTextView.text = formatBoldText("Raza: ", can.raza)
        binding.especieTextView.text = formatBoldText("Especie: ", can.especie)
        binding.generoTextView.text = formatBoldText("Género: ", can.genero)
        binding.tamanioTextView.text = formatBoldText("Tamaño: ", can.tamanio)
        binding.colorTextView.text = formatBoldText("Color: ", can.color)
        binding.pelajeTextView.text = formatBoldText("Pelaje: ", can.pelaje)
        binding.caracterTextView.text = formatBoldText("Carácter: ", can.caracter)
        binding.esterilizadoTextView.text = formatBoldText("Esterilizado: ", can.esterelizado)
        binding.distritoTextView.text = formatBoldText("Distrito: ", can.distrito)
        binding.obtencionTextView.text = formatBoldText("Obtención: ", can.obtencion)
        binding.tenenciaTextView.text = formatBoldText("Tenencia: ", can.tenencia)
        binding.fechaNacimientoTextView.text = formatBoldText("Fecha de nacimiento: ", can.fechaNacimiento)

        // Datos del propietario
        val nombreCompleto = "${can.nombreUsuario} ${can.apellidoUsuario}".trim()
        binding.propietarioTextView.text = formatBoldText("Propietario: ", nombreCompleto)
    }

    private fun formatBoldText(prefix: String, value: String): SpannableString {
        val fullText = prefix + value
        val spannableString = SpannableString(fullText)
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            prefix.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
