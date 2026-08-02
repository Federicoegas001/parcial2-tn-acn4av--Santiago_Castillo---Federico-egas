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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val name = findViewById<EditText>(R.id.etName)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            val nameValue = name.text.toString().trim()
            val emailValue = email.text.toString().trim()
            val passwordValue = password.text.toString()
            when {
                nameValue.isBlank() || emailValue.isBlank() || passwordValue.isBlank() -> showMessage(R.string.auth_empty_fields)
                !Patterns.EMAIL_ADDRESS.matcher(emailValue).matches() -> showMessage(R.string.auth_invalid_email)
                passwordValue.length < 6 -> showMessage(R.string.auth_short_password)
                else -> viewModel.register(nameValue, emailValue, passwordValue)
            }
        }
        findViewById<TextView>(R.id.tvGoLogin).setOnClickListener { finish() }

        lifecycleScope.launch {
            viewModel.result.collectLatest { result ->
                findViewById<ProgressBar>(R.id.progressAuth).visibility =
                    if (result is AuthResult.Loading) View.VISIBLE else View.GONE
                when (result) {
                    AuthResult.Success -> {
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finishAffinity()
                    }
                    is AuthResult.Error -> {
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.auth_register_error_detail, result.cause.localizedMessage ?: result.cause.javaClass.simpleName),
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
}
