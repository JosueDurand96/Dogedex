package com.durand.dogedex.ui.admin_fragment.ui.reportar_can_agresor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.durand.dogedex.R
import com.durand.dogedex.data.response.consultar_mascota_dni.ListMascotaDni

interface OnPlaceClickListener {
    fun onPlaceClick(place: String?)
}

class AddCanAgresorAdapter(private val mList: List<ListMascotaDni>) : RecyclerView.Adapter<AddCanAgresorAdapter.ViewHolder>() {

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
        holder.nombreTextView.text = "Nombre: "+item.nombre +" - Mascota "+item.nombreMascota
        holder.nombreTextView.setOnClickListener {
            mOnClickSelected?.onSelected(
                mList[position].nombre!!,
                mList[position].nombreMascota!!
            )
        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreTextView)
    }

    fun setListenerItemSelected(setOnClickSelected: OnClickSelected) {
        mOnClickSelected = setOnClickSelected
    }

    interface OnClickSelected {
        fun onSelected(
            nombre: String,
            nombreMascota: String
        )
    }
}