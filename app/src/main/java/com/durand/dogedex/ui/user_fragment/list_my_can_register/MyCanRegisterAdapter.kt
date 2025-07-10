package com.durand.dogedex.ui.user_fragment.list_my_can_register

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.durand.dogedex.R
import com.durand.dogedex.data.response.oficial.ListarCanResponse

class MyCanRegisterAdapter(private val mList: List<ListarCanResponse> = mutableListOf()) :
    RecyclerView.Adapter<MyCanRegisterAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_can_register, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = mList[position]

        //NOMBRE
        val nombrePrefix = "Nombre: "
        val namefullText = nombrePrefix + model.nombre
        val nameSpannableString = SpannableString(namefullText)
        nameSpannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Puedes usar Typeface.BOLD o Typeface.DEFAULT_BOLD (desde API 28)
            0, // Inicio del texto a poner en negrita (posición inicial de "Nombre: ")
            nombrePrefix.length, // Fin del texto a poner en negrita (justo después de ": ")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Para que el estilo se aplique solo a esta parte
        )
        holder.titleTextView.text = nameSpannableString
        // RAZA
        val razaPrefix = "Raza: "
        val razafullText = razaPrefix + model.raza
        val razaSpannableString = SpannableString(razafullText)
        razaSpannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Puedes usar Typeface.BOLD o Typeface.DEFAULT_BOLD (desde API 28)
            0, // Inicio del texto a poner en negrita (posición inicial de "Nombre: ")
            razaPrefix.length, // Fin del texto a poner en negrita (justo después de ": ")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Para que el estilo se aplique solo a esta parte
        )
        holder.razaTextView.text = razaSpannableString
        // TAMAÑO
        val tamanoPrefix = "Tamaño: "
        val tamanofullText = tamanoPrefix + model.tamano
        val tamanoSpannableString = SpannableString(tamanofullText)
        tamanoSpannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Puedes usar Typeface.BOLD o Typeface.DEFAULT_BOLD (desde API 28)
            0, // Inicio del texto a poner en negrita (posición inicial de "Nombre: ")
            tamanoPrefix.length, // Fin del texto a poner en negrita (justo después de ": ")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Para que el estilo se aplique solo a esta parte
        )
        holder.tamanoTextView.text = tamanoSpannableString
        // GENERO
        val generoPrefix = "Género: "
        val generofullText = generoPrefix + model.genero
        val generoSpannableString = SpannableString(generofullText)
        generoSpannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Puedes usar Typeface.BOLD o Typeface.DEFAULT_BOLD (desde API 28)
            0, // Inicio del texto a poner en negrita (posición inicial de "Nombre: ")
            generoPrefix.length, // Fin del texto a poner en negrita (justo después de ": ")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Para que el estilo se aplique solo a esta parte
        )
        holder.generoTextView.text = generoSpannableString
        // COLOR
        val colorPrefix = "Color: "
        val colorfullText = colorPrefix + model.color
        val colorSpannableString = SpannableString(colorfullText)
        colorSpannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Puedes usar Typeface.BOLD o Typeface.DEFAULT_BOLD (desde API 28)
            0, // Inicio del texto a poner en negrita (posición inicial de "Nombre: ")
            colorPrefix.length, // Fin del texto a poner en negrita (justo después de ": ")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Para que el estilo se aplique solo a esta parte
        )
        holder.colorTextView.text = colorSpannableString
        // FECHA DE NACIMIENTO
        val fechaPrefix = "Fecha de nacimiento: "
        val fechafullText = fechaPrefix + model.fechaNacimiento
        val fechaSpannableString = SpannableString(fechafullText)
        fechaSpannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Puedes usar Typeface.BOLD o Typeface.DEFAULT_BOLD (desde API 28)
            0, // Inicio del texto a poner en negrita (posición inicial de "Nombre: ")
            fechaPrefix.length, // Fin del texto a poner en negrita (justo después de ": ")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Para que el estilo se aplique solo a esta parte
        )
        holder.fechaNacimientoTextView.text = fechaSpannableString


    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tamanoTextView: TextView = itemView.findViewById(R.id.tamanoTextView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val razaTextView: TextView = itemView.findViewById(R.id.razaTextView)
        val generoTextView: TextView = itemView.findViewById(R.id.generoTextView)
        val fechaNacimientoTextView: TextView = itemView.findViewById(R.id.fechaNacimientoTextView)
        val colorTextView: TextView = itemView.findViewById(R.id.colorTextView)
    }
}