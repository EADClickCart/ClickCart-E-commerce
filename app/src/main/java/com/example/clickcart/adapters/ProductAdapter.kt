package com.example.clickcart.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.clickcart.R
import com.example.clickcart.models.Product

class ProductAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private var products: MutableList<Product> = mutableListOf()
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var onItemClickListener: ((Product) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(product)
        }
    }

    override fun getItemCount(): Int = products.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Product) -> Unit) {
        this.onItemClickListener = listener
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.cartProductName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val vendorName: TextView = itemView.findViewById(R.id.textViewForVendorName)
        private val rating: TextView = itemView.findViewById(R.id.rating)

        fun bind(product: Product) {
            productName.text = product.name
            productPrice.text = "${product.price}$"
            vendorName.text = product.vendorName ?: "Unknown Vendor"
            rating.text = product.rating?.let { String.format("%.1f", it) } ?: "N/A"
        }
    }
}