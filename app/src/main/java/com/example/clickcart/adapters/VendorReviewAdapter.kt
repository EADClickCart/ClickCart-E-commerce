package com.example.clickcart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clickcart.R
import com.example.clickcart.models.VendorReview

class VendorReviewAdapter : RecyclerView.Adapter<VendorReviewAdapter.ReviewViewHolder>() {
    private var reviews = listOf<VendorReview>()

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val reviewerName: TextView = view.findViewById(R.id.reviewerName)
        private val reviewDate: TextView = view.findViewById(R.id.reviewDate)
        private val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        private val reviewText: TextView = view.findViewById(R.id.reviewText)

        fun bind(review: VendorReview) {
            reviewerName.text = review.customerId // You might want to get actual customer name
            reviewDate.text = review.createdAt
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
        holder.bind(reviews[position])
    }

    override fun getItemCount() = reviews.size

    fun updateReviews(newReviews: List<VendorReview>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}