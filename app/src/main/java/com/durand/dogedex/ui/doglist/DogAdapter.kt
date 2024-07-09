package com.durand.dogedex.ui.doglist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.durand.dogedex.R
import com.durand.dogedex.data.response.Dog
import com.durand.dogedex.databinding.DogListItemBinding

class DogAdapter : ListAdapter<Dog, DogAdapter.DogViewHolder>(DiffCallback) {

    companion object DiffCallback: DiffUtil.ItemCallback<Dog>(){
        override fun areItemsTheSame(oldItem: Dog, newItem: Dog): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: Dog, newItem: Dog): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private var onItemClickListener:((Dog) -> Unit)? = null
    fun setOnClickListener(onItemClickListener: (Dog) -> Unit ){
        this.onItemClickListener = onItemClickListener
    }

    private var onLongItemClickListener:((Dog) -> Unit)? = null
    fun setLongItemOnClickListener(onLongItemClickListener: (Dog) -> Unit ){
        this.onLongItemClickListener = onLongItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val binding = DogListItemBinding.inflate(LayoutInflater.from(parent.context))

        return DogViewHolder(binding)
    }

    override fun onBindViewHolder(dogViewHolder: DogViewHolder, position: Int) {
        val dog = getItem(position)
        dogViewHolder.bind(dog)
    }

    inner class DogViewHolder(private val binding: DogListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dog: Dog) {

            if (dog.inCollection){
                binding.dogListItemLayout.setOnLongClickListener {
                    Log.d("josue","setOnLongClickListener")
                    onLongItemClickListener?.invoke(dog)
                    true
                }
                binding.dogListItemLayout.background = ContextCompat.getDrawable(
                    binding.dogName.context,
                    R.drawable.dog_list_item_background
                )
                binding.dogName.visibility = View.VISIBLE
                binding.dogListItemLayout.setOnClickListener {
                    onItemClickListener?.invoke(dog)
                }

                binding.dogTextView.text = dog.name
                binding.dogName.load(dog.imageUrl)

            binding.dogTextView.text = dog.name
            binding.dogName.load(dog.imageUrl)
            binding.dogListItemLayout.setOnClickListener {
                onItemClickListener?.invoke(dog)

            }
        }
    }
}
