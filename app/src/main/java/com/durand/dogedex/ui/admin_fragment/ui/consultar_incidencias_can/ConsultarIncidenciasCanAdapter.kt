package com.durand.dogedex.ui.admin_fragment.ui.consultar_incidencias_can

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.durand.dogedex.R
import com.durand.dogedex.data.response.consultar_can_agresivo_dni.ConsultarCanAgresivoDni


class ConsultarIncidenciasCanAdapter(private val mList: List<ConsultarCanAgresivoDni>) :
    RecyclerView.Adapter<ConsultarIncidenciasCanAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_can_dni, parent, false)

        return ViewHolder(view)
    }

    private var mOnClickSelected: OnClickSelected? = null

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = mList[position]

        // sets the image to the imageview from our itemHolder class
        //holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.nombreTextView.text = "Nombre: " + item.nombre
        holder.nombreMacotaTextView.text = "Mascota: " + item.idMascota.toString()
        holder.principalLinearLayout.setOnClickListener {
            mOnClickSelected?.onSelected(
                mList[position].nombre!!,
                mList[position].nombreMascota,
                mList[position].apellidos!!,
                mList[position].caracter!!,
                mList[position].color!!,
                mList[position].descripcionAgresion!!,
                mList[position].descripcionRaza!!,
                mList[position].fechaHora!!,
                mList[position].genero!!,
                mList[position].idAgresion!!,
                mList[position].idMascota!!,
                mList[position].idRaza!!,
                mList[position].idUsuario!!,
                mList[position].numeroDocumento!!,
                mList[position].pelaje!!,
                mList[position].tamano!!,
            )
        }
    }

    interface OnClickSelected {
        fun onSelected(
            nombre: String,
            nombreMascota: String,
            apellidos: String,
            caracter: String,
            color: String,
            descripcionAgresion: String,
            descripcionRaza: String,
            fechaHora: String,
            genero: String,
            idAgresion: Int,
            idMascota: Int,
            idRaza: Int,
            idUsuario: Int,
            numeroDocumento: String,
            pelaje: String,
            tamano: String
        )
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
        val nombreMacotaTextView: TextView = itemView.findViewById(R.id.nombreMacotaTextView)
        val principalLinearLayout: LinearLayout = itemView.findViewById(R.id.principalLinearLayout)
    }

    fun setListenerItemSelected(setOnClickSelected: OnClickSelected) {
        mOnClickSelected = setOnClickSelected
    }


}