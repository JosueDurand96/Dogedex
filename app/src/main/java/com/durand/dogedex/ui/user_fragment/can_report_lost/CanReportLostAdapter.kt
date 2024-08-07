package com.durand.dogedex.ui.user_fragment.can_report_lost

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.durand.dogedex.R


data class ItemsViewModel(val image: Int, val text: String, val description: String = "") {
}

class CanReportLostAdapter(private val mList: MutableList<ItemsViewModel> = mutableListOf()) :
    RecyclerView.Adapter<CanReportLostAdapter.ViewHolder>() {

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

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.text
        holder.description.text = ItemsViewModel.description

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    fun add(new: List<ItemsViewModel>) {
        mList.clear()
        mList.addAll(new)
        notifyDataSetChanged()
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.photoImageView)
        val textView: TextView = itemView.findViewById(R.id.titleTextView)
        val description: TextView = itemView.findViewById(R.id.descriptionTextView)
    }
}