package com.example.clickcart.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.clickcart.LoginActivity
import com.example.clickcart.R
import com.example.clickcart.api.RetrofitClient
import com.example.clickcart.api.UserApiService
import com.example.clickcart.data.UserResponse
import com.example.clickcart.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Account : Fragment() {

    private lateinit var logoutButton: Button
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = TokenManager.getUserId() // Retrieve the user ID from the TokenManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // Initialize UI components
        userNameTextView = view.findViewById(R.id.userName)
        userEmailTextView = view.findViewById(R.id.userEmail)
        logoutButton = view.findViewById(R.id.signOutButton)

        logoutButton.setOnClickListener { performLogout() } // Set click listener

        // Fetch user details only once
        if (userId != null) {
            fetchUserDetails(userId!!)
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun fetchUserDetails(userId: String) {
        val service = RetrofitClient.create().create(UserApiService::class.java)
        service.getUserDetails(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { userDetails ->
                        userNameTextView.text = userDetails.name
                        userEmailTextView.text = userDetails.email
                    } ?: run {
                        Toast.makeText(requireContext(), "User details not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error fetching user details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun performLogout() {
        // Clear the stored token
        TokenManager.clearToken() // Ensure this method exists in your TokenManager

        // Navigate to the login screen
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear the activity stack
        startActivity(intent)

        // Optional: Show a logout success message
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}
