package com.example.myburrito.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myburrito.Burrito
import com.example.myburrito.R
import kotlin.reflect.KFunction0

class CartItemAdapter(
    private val context: Context,
    public var cartItems: List<Burrito>,
    private val onAddQuantityClickListener: (Burrito) -> Unit,
    private val onMinusQuantityClickListener: (Burrito) -> Unit,
) : RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = cartItems[position]

        // Set data to views
        Glide.with(context).load(currentItem.image).centerCrop().into(holder.CartItemImage)
        holder.tvCartItemName.text = currentItem.name
        holder.tvCartItemType.text =
            if (currentItem.customizedIngredients == null) "Standard" else "Custom"
        holder.tvUnitPrice.text = String.format("%.2f$", currentItem.price)
        holder.tvItemQuantity.text = currentItem.quantity.toString()

        holder.ivAddButton.setOnClickListener {
            onAddQuantityClickListener(currentItem)
            currentItem.quantity += 1
            holder.tvItemQuantity.text = currentItem.quantity.toString()

        }

        holder.ivMinusButton.setOnClickListener {

            onMinusQuantityClickListener(currentItem)
            currentItem.quantity--
            holder.tvItemQuantity.text = currentItem.quantity.toString()

        }

    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val CartItemImage: ImageView = itemView.findViewById(R.id.imageView_cart_item)
        val tvCartItemName: TextView = itemView.findViewById(R.id.tv_cart_item_name)
        val tvCartItemType: TextView = itemView.findViewById(R.id.tv_cart_item_type)
        val tvUnitPrice: TextView = itemView.findViewById(R.id.tv_unit_price)
        val tvItemQuantity: TextView = itemView.findViewById(R.id.tv_item_quantity)
        val ivAddButton: ImageView = itemView.findViewById(R.id.iv_add_button)
        val ivMinusButton: ImageView = itemView.findViewById(R.id.iv_minus_button)

    }
}
