package com.example.apprecetas

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser != null) {
            openMain()
            return
        }
        setContentView(R.layout.activity_login)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val emailValue = email.text.toString().trim()
            val passwordValue = password.text.toString()
            when {
                emailValue.isBlank() || passwordValue.isBlank() -> showMessage(R.string.auth_empty_fields)
                !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() -> showMessage(R.string.auth_invalid_email)
                else -> viewModel.login(emailValue, passwordValue)
            }
        }
        findViewById<TextView>(R.id.tvGoRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        lifecycleScope.launch {
            viewModel.result.collectLatest { result ->
                findViewById<ProgressBar>(R.id.progressAuth).visibility =
                    if (result is AuthResult.Loading) View.VISIBLE else View.GONE
                when (result) {
                    AuthResult.Success -> openMain()
                    is AuthResult.Error -> {
                        Toast.makeText(
                            this@LoginActivity,
                            authErrorMessageRes(result.cause, R.string.auth_login_error),
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.reset()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showMessage(message: Int) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
