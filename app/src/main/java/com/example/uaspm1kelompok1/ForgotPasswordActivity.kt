package com.example.uaspm1kelompok1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnSend: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLoading: TextView
    private lateinit var tvBackToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        btnSend = findViewById(R.id.btnSend)
        progressBar = findViewById(R.id.progressBar)
        tvLoading = findViewById(R.id.tvLoading)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)
    }

    private fun setupListeners() {
        btnSend.setOnClickListener { sendResetLink() }
        tvBackToLogin.setOnClickListener { finish() }
    }

    private fun sendResetLink() {
        val email = etEmail.text.toString().trim()

        // Validasi email
        if (email.isEmpty()) {
            etEmail.error = "Email harus diisi"
            etEmail.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Format email tidak valid"
            etEmail.requestFocus()
            return
        }

        // Tampilkan loading
        setLoadingState(true)

        // Simulasi pengiriman email reset password
        Handler(Looper.getMainLooper()).postDelayed({
            setLoadingState(false)

            // Cek apakah email terdaftar (simulasi)
            if (email == "admin@tiasa.com") {
                showSuccessDialog(email)
            } else {
                showEmailNotFoundDialog(email)
            }
        }, 2000)
    }

    private fun showSuccessDialog(email: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("✅ Berhasil!")
            .setMessage("konfirmasi reset password telah dikirim ke:\n\n$email\n\nSilakan cek email Anda untuk melanjutkan.")
            .setPositiveButton("OK") { _, _ ->
                finish() // Kembali ke login
            }
            .setCancelable(false)
            .show()
    }

    private fun showEmailNotFoundDialog(email: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("⚠️ Email Tidak Ditemukan")
            .setMessage("Email $email tidak terdaftar di sistem TIASA.\n\nSilakan daftar terlebih dahulu atau gunakan email yang berbeda.")
            .setPositiveButton("Coba Lagi") { _, _ ->
                etEmail.requestFocus()
            }
            .setNegativeButton("Daftar Sekarang") { _, _ ->
                // back to halaman register
                startActivity(android.content.Intent(this, RegisterActivity::class.java))
            }
            .show()
    }

    private fun setLoadingState(isLoading: Boolean) {
        btnSend.isEnabled = !isLoading
        if (isLoading) {
            btnSend.text = ""
        } else {
            btnSend.text = getString(R.string.send_button)
        }
        progressBar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
        tvLoading.visibility = if (isLoading) TextView.VISIBLE else TextView.GONE
        etEmail.isEnabled = !isLoading
    }
}