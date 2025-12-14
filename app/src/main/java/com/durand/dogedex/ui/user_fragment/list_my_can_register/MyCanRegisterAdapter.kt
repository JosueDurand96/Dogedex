package com.durand.dogedex.ui.user_fragment.list_my_can_register

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.durand.dogedex.R
import com.durand.dogedex.data.response.oficial.ListarCanResponse

class MyCanRegisterAdapter(private var mList: List<ListarCanResponse> = mutableListOf()) :
    RecyclerView.Adapter<MyCanRegisterAdapter.ViewHolder>() {

    fun updateList(newList: List<ListarCanResponse>) {
        Log.d("MyCanRegisterAdapter", "=== updateList INICIADO ===")
        Log.d("MyCanRegisterAdapter", "Lista anterior: ${mList.size} elementos")
        Log.d("MyCanRegisterAdapter", "Nueva lista: ${newList.size} elementos")
        
        if (newList.isEmpty()) {
            Log.w("MyCanRegisterAdapter", "La nueva lista está vacía")
        } else {
            Log.d("MyCanRegisterAdapter", "Primer elemento de nueva lista: ${newList[0].nombre}")
        }
        
        mList = newList
        Log.d("MyCanRegisterAdapter", "mList actualizado a ${mList.size} elementos")
        
        // Siempre notificar cambios
        notifyDataSetChanged()
        Log.d("MyCanRegisterAdapter", "notifyDataSetChanged() llamado")
        Log.d("MyCanRegisterAdapter", "=== updateList COMPLETADO ===")
    }

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
        Log.d("MyCanRegisterAdapter", "onBindViewHolder llamado para posición $position, tamaño lista: ${mList.size}")
        if (position >= mList.size) {
            Log.e("MyCanRegisterAdapter", "Error: posición $position fuera de rango (tamaño: ${mList.size})")
            return
        }
        val model = mList[position]
        Log.d("MyCanRegisterAdapter", "Binding item: ${model.nombre}, Raza: ${model.raza}")

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
        Log.d("josue", "tamano: ${model.tamanio}")
        val tamanoPrefix = "Tamaño: "
        val tamanofullText = tamanoPrefix + model.tamanio
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
        val count = mList.size
        Log.d("MyCanRegisterAdapter", "getItemCount: $count")
        return count
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