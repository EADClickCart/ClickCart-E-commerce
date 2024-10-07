package com.example.clickcart.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
    private lateinit var manageAccountCard: CardView
    private var userId: String? = null
    private var userRole: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = TokenManager.getUserId()
        userRole = TokenManager.getUserRole()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        userNameTextView = view.findViewById(R.id.userName)
        userEmailTextView = view.findViewById(R.id.userEmail)
        logoutButton = view.findViewById(R.id.signOutButton)
        manageAccountCard = view.findViewById(R.id.manageAccountCard)

        logoutButton.setOnClickListener { performLogout() }
        manageAccountCard.setOnClickListener { showEditProfileDialog() }

        if (userId != null) {
            fetchUserDetails(userId!!)
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun showEditProfileDialog() {
        val dialog = Dialog(requireContext())
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null)
        dialog.setContentView(dialogView)

        val editTextName: EditText = dialogView.findViewById(R.id.editTextName)
        val editTextEmail: EditText = dialogView.findViewById(R.id.editTextEmail)
        val btnSave: Button = dialogView.findViewById(R.id.btnSave)

        editTextName.setText(userNameTextView.text)
        editTextEmail.setText(userEmailTextView.text)

        btnSave.setOnClickListener {
            val newName = editTextName.text.toString()
            val newEmail = editTextEmail.text.toString()
            updateUserProfile(newName, newEmail)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateUserProfile(newName: String, newEmail: String) {
        val userId = TokenManager.getUserId() ?: return
        val userRole = TokenManager.getUserRole() ?: return
        val service = RetrofitClient.create().create(UserApiService::class.java)
        val updateBody = mapOf(
            "name" to newName,
            "email" to newEmail,
            "role" to userRole
        )

        service.updateUserDetails(userId, updateBody).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    fetchUserDetails(userId) // Refresh user details
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
        TokenManager.clearToken()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
    }
}