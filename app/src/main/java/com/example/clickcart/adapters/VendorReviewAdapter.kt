package com.example.clickcart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clickcart.R
import com.example.clickcart.models.VendorReview
import java.text.SimpleDateFormat
import java.util.Locale

class VendorReviewAdapter : RecyclerView.Adapter<VendorReviewAdapter.ReviewViewHolder>() {
    private var reviews = listOf<VendorReview>()
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    private val displayFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val reviewerName: TextView = view.findViewById(R.id.reviewerName)
        private val reviewDate: TextView = view.findViewById(R.id.reviewDate)
        private val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        private val reviewText: TextView = view.findViewById(R.id.reviewText)

        fun bind(review: VendorReview, displayFormatter: SimpleDateFormat, dateFormatter: SimpleDateFormat) {
            // Format customer ID to show only 5 digits
            val shortCustomerId = "user - ${review.customerId.take(10)}"
            reviewerName.text = shortCustomerId

            // Format date to show only the date part
            try {
                val date = dateFormatter.parse(review.createdAt)
                reviewDate.text = date?.let { displayFormatter.format(it) }
            } catch (e: Exception) {
                reviewDate.text = review.createdAt
            }

            ratingBar.rating = review.rating.toFloat()
            reviewText.text = review.comment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vendor_view_item, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position], displayFormatter, dateFormatter)
    }

    override fun getItemCount() = reviews.size

    fun updateReviews(newReviews: List<VendorReview>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}