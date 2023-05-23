package com.durand.dogedex.ui.admin_fragment.ui.reporte_canes_registrados

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.durand.dogedex.R
import com.durand.dogedex.data.response.list_mascotas.ListaMascotas
import com.durand.dogedex.data.response.list_mascotas.MascotaResponse


//data class ItemsViewModelReporte(val image: String, val text: String) {
//}

class ReporteCanesRegistradosAdapter(private val mList: List<MascotaResponse>) : RecyclerView.Adapter<ReporteCanesRegistradosAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reporte_canes_registrados, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        //holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.nameTextView.text = ItemsViewModel.nombre
        holder.fechaNacimientoTextView.text = ItemsViewModel.fechaNacimiento
        holder.razaTextView.text = ItemsViewModel.pelaje
        holder.caracterTextView.text = ItemsViewModel.caracter
        holder.colorTextView.text = ItemsViewModel.color
        holder.distritoTextView.text = ItemsViewModel.distrito

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val fechaNacimientoTextView: TextView = itemView.findViewById(R.id.fechaNacimientoTextView)
        val razaTextView: TextView = itemView.findViewById(R.id.razaTextView)
        val caracterTextView: TextView = itemView.findViewById(R.id.caracterTextView)
        val colorTextView: TextView = itemView.findViewById(R.id.colorTextView)
        val distritoTextView: TextView = itemView.findViewById(R.id.distritoTextView)



    }
}