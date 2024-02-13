package com.example.myburrito.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myburrito.Burrito
import com.example.myburrito.R

class BurritoAdapter(
    private val context: Context,
    private var burritoList: List<Burrito>,
    private val onAddToCartClickListener: (Burrito) -> Unit,
    private val onImageViewClickListerner: (Burrito) -> Unit,
) : RecyclerView.Adapter<BurritoAdapter.BurritoViewHolder>() {

    inner class BurritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        val cardView: CardView = itemView.findViewById(R.id.cardView2)
        val imageView: ImageView = itemView.findViewById(R.id.ImageView_burrito_item)
        val titleTextView: TextView = itemView.findViewById(R.id.tv_item_title)
        val priceTextView: TextView = itemView.findViewById(R.id.tv_item_price)
        val addToCartButton: Button = itemView.findViewById(R.id.button_add_item_to_cart)
    }

    fun updateBurritos(newList: List<Burrito>) {
        burritoList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BurritoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.burrito_item, parent, false)
        return BurritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: BurritoViewHolder, position: Int) {
        val burrito = burritoList[position]
        Glide.with(context).asBitmap()
            .load(burrito.image)
            .centerCrop()
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            onImageViewClickListerner(burrito);
        }
        holder.titleTextView.text = burrito.name
        holder.priceTextView.text = String.format("%.2f$", burrito.price)
        holder.addToCartButton.setOnClickListener {
            // Notify the listener that the user clicked "Add to Cart"
            onAddToCartClickListener.invoke(burrito)
        }
    }

    override fun getItemCount(): Int {
        return burritoList.size
    }
}
