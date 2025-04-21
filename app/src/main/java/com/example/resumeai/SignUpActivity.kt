package com.example.resumeai

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : ComponentActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: Button
    private lateinit var termsCheckBox: CheckBox
    private lateinit var backButton: ImageView
    private lateinit var loginRedirectText: TextView

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        // Initialize views
        firstNameEditText = findViewById(R.id.etFirstName)
        lastNameEditText = findViewById(R.id.etLastName)
        emailEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        confirmPasswordEditText = findViewById(R.id.etConfirmPassword)
        signUpButton = findViewById(R.id.btnSignUp)
        termsCheckBox = findViewById(R.id.cbTerms)
        backButton = findViewById(R.id.btnBack)
        loginRedirectText = findViewById(R.id.tvSignIn)

        // Set listeners
        signUpButton.setOnClickListener { registerUser() }
        backButton.setOnClickListener { finish() }
        loginRedirectText.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish() // Close SignUpActivity
        }
    }

    private fun registerUser() {
        val firstName = firstNameEditText.text.toString().trim()
        val lastName = lastNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        // Input validation
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast("Please fill all fields")
            return
        }

        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return
        }

        if (!termsCheckBox.isChecked) {
            showToast("Please agree to the terms of use")
            return
        }

        // Create user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                saveUserToFirestore(firstName, lastName, email)
            } else {
                showToast("Sign Up Failed: ${task.exception?.message}")
            }
        }
    }

    private fun saveUserToFirestore(firstName: String, lastName: String, email: String) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("SignUpActivity", "User ID is null")
            return
        }

        val user = User(firstName, lastName, email)

        db.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                // Firestore save successful
                Log.d("SignUpActivity", "Firestore save successful")

                // Run UI changes on the main thread
                runOnUiThread {
                    showToast("Sign Up Successful")  // Show the success toast
                    clearInputFields()               // Clear the input fields

                    // Proceed to the next activity after a short delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 1500)
                }
            }
            .addOnFailureListener { e ->
                Log.e("SignUpActivity", "Error saving user: ${e.message}", e)
                showToast("Error saving user: ${e.message}")
            }
    }

    // Helper function to show Toast messages
    private fun showToast(message: String) {
        runOnUiThread {
            try {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("SignUpActivity", "Error showing Toast: ${e.message}", e)
            }
        }
    }

    // Clear input fields after successful sign-up
    private fun clearInputFields() {
        try {
            firstNameEditText.text.clear()
            lastNameEditText.text.clear()
            emailEditText.text.clear()
            passwordEditText.text.clear()
            confirmPasswordEditText.text.clear()
            termsCheckBox.isChecked = false
        } catch (e: Exception) {
            Log.e("SignUpActivity", "Error clearing fields: ${e.message}", e)
        }
    }

    // User data class for Firestore
    data class User(
        val firstName: String = "",
        val lastName: String = "",
        val email: String = ""
    )
}
