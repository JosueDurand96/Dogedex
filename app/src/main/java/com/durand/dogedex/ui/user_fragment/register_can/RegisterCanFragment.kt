package com.durand.dogedex.ui.user_fragment.register_can

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.durand.dogedex.data.User
import com.durand.dogedex.databinding.FragmentRegisterCanBinding
import com.durand.dogedex.util.createLoadingDialog

class RegisterCanFragment : Fragment() {

    private var _binding: FragmentRegisterCanBinding? = null

    private val vm: RegisterCanViewModel by viewModels()

    private val loading by lazy {
        requireContext().createLoadingDialog()
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterCanBinding.inflate(inflater, container, false)
        val itemsEspecie = listOf("Perro", "Gato", "Otro")
        val adapterEspecie = ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsEspecie)
        _binding!!.especieAutoCompleteTextView.setAdapter(adapterEspecie)
        val itemsGenero = listOf("Macho", "Hembra")
        val adapterGenero =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsGenero)
        _binding!!.generoAutoCompleteTextView.setAdapter(adapterGenero)

        val itemsRazaCanina = listOf("Labrador", "Pugs", "Pitbull", "Mestizo")
        val adapterRazaCanina =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsRazaCanina)
        _binding!!.razaCaninaAutoCompleteTextView.setAdapter(adapterRazaCanina)


        val itemsTamano = listOf("Labrador", "Pugs", "Pitbull", "Mestizo")
        val adapterTamano =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsTamano)
        _binding!!.tamanoAutoCompleteTextView.setAdapter(adapterTamano)

        val itemsCaracter = listOf("Tímido", "Sociable", "Pasivo", "Agresivo", "Independiente")
        val adapterCaracter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsCaracter)
        _binding!!.caracterAutoCompleteTextView.setAdapter(adapterCaracter)

        val itemsColorDog = listOf("Negro", "Blanco", "Caramelo", "Mostaza", "Otro")
        val adapterColorDog =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsColorDog)
        _binding!!.colorCanAutoCompleteTextView.setAdapter(adapterColorDog)

        val itemsPelaje = listOf("Duro", "Rizado", "Corto", "Largo")
        val adapterPelaje =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsPelaje)
        _binding!!.pelajeAutoCompleteTextView.setAdapter(adapterPelaje)

        val itemsEsterelizado = listOf("Si", "No")
        val adapterEsterelizado =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, itemsEsterelizado)
        _binding!!.esterilizadoAutoCompleteTextView.setAdapter(adapterEsterelizado)

        val itemsDistritoDondeReside = listOf(
            "Lima",
            "San Isidro",
            "Miraflores",
            "San Miguel",
            "San Borja",
            "Surco",
            "Jesus Maria",
            "Rimac",
            "SMP"
        )
        val adapterDistritoDondeReside = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            itemsDistritoDondeReside
        )
        _binding!!.distritoDondeResideAutoCompleteTextView.setAdapter(adapterDistritoDondeReside)

        val itemsmodoDeObtencion =
            listOf("Recogido", "Reubicado", "Regalo", "Nacido en casa", "Compra")
        val adaptermodoDeObtencion = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            itemsmodoDeObtencion
        )
        _binding!!.modoDeObtencionAutoCompleteTextView.setAdapter(adaptermodoDeObtencion)

        val itemsrazonDeTenencia = listOf(
            "Compañía",
            "Asistencia",
            "Terapia",
            "Trabajo",
            "Seguridad",
            "Deporte",
            "Exposición",
            "Reproducción",
            "Caza"
        )
        val adapterrazonDeTenencia = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_dropdown_item,
            itemsrazonDeTenencia
        )
        binding.vm = vm
        binding.lifecycleOwner = viewLifecycleOwner
        _binding!!.razonDeTenenciaAutoCompleteTextView.setAdapter(adapterrazonDeTenencia)
        getUserProfile()
        initObservers()
        return binding.root
    }

    private fun initObservers() {
        vm.event.observe(viewLifecycleOwner) {
            when (it) {
                is RegisterCanEvent.ShowError -> {
                    Toast.makeText(requireContext(), it.msg, Toast.LENGTH_SHORT).show()
                }

                RegisterCanEvent.Success -> {

                }
                RegisterCanEvent.Loading -> {
                    loading.show()
                }
                RegisterCanEvent.DismissLoading -> {
                    loading.dismiss()
                }

                else -> {}
            }
        }

    }

    private fun getUserProfile() {
        val loggedInUser: User? = User.getLoggedInUser(requireActivity())
        vm.setUserProfile(loggedInUser)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}