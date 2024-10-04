package com.example.clickcart.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.clickcart.LoginActivity
import com.example.clickcart.R
import com.example.clickcart.utils.TokenManager

class Account : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        logoutButton = view.findViewById(R.id.signOutButton) // Initialize your logout button
        logoutButton.setOnClickListener { performLogout() } // Set click listener

        return view
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

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Account().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
