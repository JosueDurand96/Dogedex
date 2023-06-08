package com.durand.projecttesisupc.di.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.durand.projecttesisupc.R

class ListAdapter(
    private val names: List<String>,
    private val clickListener: TestRecyclerClickListener
) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.name_list_item, parent, false)

        return ListViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int = names.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) =
        holder.onBind(names[position])

    class ListViewHolder(
        private val view: View,
        private val clickListener: TestRecyclerClickListener
    ) : RecyclerView.ViewHolder(view) {

        fun onBind(name: String) {
            val txtName = view.findViewById<TextView>(R.id.txtName)
            txtName.text = name
            txtName.setOnClickListener {
                clickListener.onClick(name)
            }
        }
    }
}

