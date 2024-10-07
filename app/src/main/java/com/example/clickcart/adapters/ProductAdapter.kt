package com.example.clickcart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.clickcart.R
import com.example.clickcart.data.VendorDataManager
import com.example.clickcart.models.Product
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class ProductAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val products: MutableList<Product> = mutableListOf()
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var onItemClickListener: ((Product) -> Unit)? = null
    private val viewHolderJobs = mutableMapOf<Int, Job>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        // Cancel any existing job for this position
        viewHolderJobs[position]?.cancel()

        val product = products[position]
        // Start a new job for this position
        val job = lifecycleOwner.lifecycleScope.launch {
            holder.bind(product)
        }
        viewHolderJobs[position] = job

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(product)
        }
    }

    override fun onViewRecycled(holder: ProductViewHolder) {
        super.onViewRecycled(holder)
        val position = holder.bindingAdapterPosition
        if (position != RecyclerView.NO_POSITION) {
            viewHolderJobs[position]?.cancel()
            viewHolderJobs.remove(position)
        }
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products.clear()
        products.addAll(newProducts)
        notifyDataSetChanged()
        // Clear all jobs when updating products
        viewHolderJobs.forEach { (_, job) -> job.cancel() }
        viewHolderJobs.clear()
    }

    fun setOnItemClickListener(listener: (Product) -> Unit) {
        this.onItemClickListener = listener
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.cartProductName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val vendorName: TextView = itemView.findViewById(R.id.textViewForVendorName)
        private val rating: TextView = itemView.findViewById(R.id.rating)

        suspend fun bind(product: Product) {
            productName.text = product.name
            productPrice.text = "${product.price}$"

            try {
                val vendor = VendorDataManager.getVendorDetails(product.vendorId)
                vendorName.text = vendor.name

                val vendorRating = VendorDataManager.getVendorRating(product.vendorId)
                rating.text = String.format("%.1f", vendorRating)
            } catch (e: Exception) {
                vendorName.text = "Unknown Vendor"
                rating.text = "N/A"
            }
        }
    }
}