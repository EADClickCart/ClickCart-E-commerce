package com.example.clickcart.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.clickcart.databinding.ItemOrderView1Binding
import com.example.clickcart.models.Order
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(
    private val onReorderClick: (Order) -> Unit,
    private val onReportIssueClick: (Order) -> Unit,
    private val onArrowClick: (Order) -> Unit
) : ListAdapter<Order, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderView1Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding, onReorderClick, onReportIssueClick, onArrowClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(
        private val binding: ItemOrderView1Binding,
        private val onReorderClick: (Order) -> Unit,
        private val onReportIssueClick: (Order) -> Unit,
        private val onArrowClick: (Order) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n", "DefaultLocale")
        fun bind(order: Order) {
            binding.apply {
                orderIdValueTextView.text = order.id
                orderDateValueTextView.text = formatDate(order.createdAt.toString())
                itemsValueTextView.text = order.items.sumOf { it.quantity }.toString()
                totalPriceValueTextView.text = String.format("$%.2f", order.totalOrderPrice)
                statusValueTextView.text = order.orderStatus

                reportIssueButton.setOnClickListener {
                    onReportIssueClick(order)
                }

                reorderButton.setOnClickListener {
                    onReorderClick(order)
                }

                arrowImageView.setOnClickListener {
                    onArrowClick(order)
                }
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }
    }

    private class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
}