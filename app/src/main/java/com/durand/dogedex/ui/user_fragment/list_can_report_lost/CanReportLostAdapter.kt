package com.durand.dogedex.ui.user_fragment.list_can_report_lost

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.durand.dogedex.R
import com.durand.dogedex.data.response.oficial.ListarCanPerdidoResponse

class CanReportLostAdapter(
    private val mList: List<ListarCanPerdidoResponse> = mutableListOf(),
    private val onItemClick: (ListarCanPerdidoResponse) -> Unit = {}
) : RecyclerView.Adapter<CanReportLostAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_can_report_lost, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = mList[position]

        // sets the image to the imageview from our itemHolder class
            //   holder.imageView.setImageResource(ItemsViewModel)

        // sets the text to the textview from our itemHolder class
        //NOMBRE
        val nombrePrefix = "Nombre: "
        val nombreFullText = nombrePrefix + model.nombre
        val nombreSpannableString = SpannableString(nombreFullText)
        nombreSpannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Puedes usar Typeface.BOLD o Typeface.DEFAULT_BOLD (desde API 28)
            0, // Inicio del texto a poner en negrita (posición inicial de "Nombre: ")
            nombrePrefix.length, // Fin del texto a poner en negrita (justo después de ": ")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Para que el estilo se aplique solo a esta parte
        )
        holder.titleTextView.text = nombreSpannableString
        //DETALLE
        val detallePrefix = "Detalle: "
        val detalleFullText = detallePrefix + model.comentario
        val detalleSpannableString = SpannableString(detalleFullText)
        detalleSpannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Puedes usar Typeface.BOLD o Typeface.DEFAULT_BOLD (desde API 28)
            0, // Inicio del texto a poner en negrita (posición inicial de "Nombre: ")
            detallePrefix.length, // Fin del texto a poner en negrita (justo después de ": ")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Para que el estilo se aplique solo a esta parte
        )
        holder.detalleTextView.text = detalleSpannableString

        //FECHA DE AGRESION
        val fechaPrefix = "Fecha de agresión: "
        val FECHAfullText = fechaPrefix + model.fechaPerdida
        val fechaSpannableString = SpannableString(FECHAfullText)
        fechaSpannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Puedes usar Typeface.BOLD o Typeface.DEFAULT_BOLD (desde API 28)
            0, // Inicio del texto a poner en negrita (posición inicial de "Nombre: ")
            fechaPrefix.length, // Fin del texto a poner en negrita (justo después de ": ")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Para que el estilo se aplique solo a esta parte
        )
        holder.fechaNacimientoTextView.text = fechaSpannableString

        //DIRECCION
        val direccionPrefix = "Dirección: "
        val direccionfullText = direccionPrefix + model.lugarPerdida
        val direccionSpannableString = SpannableString(direccionfullText)
        direccionSpannableString.setSpan(
            StyleSpan(Typeface.BOLD), // Puedes usar Typeface.BOLD o Typeface.DEFAULT_BOLD (desde API 28)
            0, // Inicio del texto a poner en negrita (posición inicial de "Nombre: ")
            direccionPrefix.length, // Fin del texto a poner en negrita (justo después de ": ")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE // Para que el estilo se aplique solo a esta parte
        )
        holder.direccionTextView.text = direccionSpannableString

        // Cargar imagen desde Base64
        if (model.foto.isNotEmpty()) {
            try {
                val imageBytes = Base64.decode(model.foto, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                if (bitmap != null) {
                    holder.canImageView.load(bitmap) {
                        crossfade(true)
                        placeholder(R.drawable.dog_logo) // Imagen por defecto mientras carga
                        error(R.drawable.dog_logo) // Imagen de error si falla
                    }
                } else {
                    Log.e("CanReportLostAdapter", "Error: bitmap es null para ${model.nombre}")
                    holder.canImageView.setImageResource(R.drawable.dog_logo)
                }
            } catch (e: Exception) {
                Log.e("CanReportLostAdapter", "Error al decodificar imagen Base64 para ${model.nombre}: ${e.message}", e)
                holder.canImageView.setImageResource(R.drawable.dog_logo)
            }
        } else {
            // Si no hay foto, mostrar imagen por defecto
            holder.canImageView.setImageResource(R.drawable.dog_logo)
        }

        // Click listener para el item completo
        holder.itemView.setOnClickListener {
            onItemClick(model)
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val detalleTextView: TextView = itemView.findViewById(R.id.detalleTextView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val direccionTextView: TextView = itemView.findViewById(R.id.direccionTextView)
        val fechaNacimientoTextView: TextView = itemView.findViewById(R.id.fechaNacimientoTextView)
        val canImageView: ImageView = itemView.findViewById(R.id.canImageView)
    }
}