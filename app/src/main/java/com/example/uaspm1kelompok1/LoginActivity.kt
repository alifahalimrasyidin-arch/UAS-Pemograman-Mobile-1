package com.example.uaspm1kelompok1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLoading: TextView
    private lateinit var cbRememberMe: CheckBox
    private lateinit var tvRegister: TextView
    private lateinit var tvForgotPassword: TextView
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var sharedPreferences: SharedPreferences

    private var isPasswordVisible = false

    companion object {
        private const val PREFS_NAME = "TIASA_PREFS"
        private const val KEY_IS_LOGIN = "is_login"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_SAVED_EMAIL = "saved_email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        initViews()
        loadSavedCredentials()
        setupListeners()
        setupPasswordToggle()

        if (sharedPreferences.getBoolean(KEY_IS_LOGIN, false)) {
            goToDashboard()
        }
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        tvLoading = findViewById(R.id.tvLoading)
        cbRememberMe = findViewById(R.id.cbRememberMe)
        tvRegister = findViewById(R.id.tvRegister)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
    }

    private fun loadSavedCredentials() {
        val rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
        cbRememberMe.isChecked = rememberMe

        if (rememberMe) {
            val savedEmail = sharedPreferences.getString(KEY_SAVED_EMAIL, "")
            etEmail.setText(savedEmail)
        }
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener { performLogin() }
        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun setupPasswordToggle() {
        btnTogglePassword.setOnClickListener {
            if (isPasswordVisible) {
                // Sembunyikan password
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnTogglePassword.setImageResource(R.drawable.ic_eye_close)
                isPasswordVisible = false
            } else {
                // Tampilkan password
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnTogglePassword.setImageResource(R.drawable.ic_eye_open)
                isPasswordVisible = true
            }
            // Pindahkan kursor ke akhir teks
            etPassword.setSelection(etPassword.text.length)
        }
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Email harus diisi"
            etEmail.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email tidak valid"
            etEmail.requestFocus()
            return
        }

        if (password.isEmpty()) {
            etPassword.error = "Password harus diisi"
            etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            etPassword.error = "Password minimal 6 karakter"
            etPassword.requestFocus()
            return
        }

        if (cbRememberMe.isChecked) {
            sharedPreferences.edit().apply {
                putBoolean(KEY_REMEMBER_ME, true)
                putString(KEY_SAVED_EMAIL, email)
                apply()
            }
        } else {
            sharedPreferences.edit().apply {
                putBoolean(KEY_REMEMBER_ME, false)
                remove(KEY_SAVED_EMAIL)
                apply()
            }
        }

        setLoadingState(true)

        Handler(Looper.getMainLooper()).postDelayed({
            when {
                // Staff Produksi
                email.equals("staffproduksi@tiasa.com", ignoreCase = true) && password == "staffproduksi123" -> {
                    saveLoginSession(email, "Staff Produksi", "staff_produksi")
                    Toast.makeText(this, "✅ Login Sebagai Staff Produksi!", Toast.LENGTH_SHORT).show()
                    goToDashboard()
                }
                // Kepala Gudang
                email.equals("kepalagudang@tiasa.com", ignoreCase = true) && password == "kepalagudang123" -> {
                    saveLoginSession(email, "Kepala Gudang", "kepala_gudang")
                    Toast.makeText(this, "✅ Login Sebagai Kepala Gudang!", Toast.LENGTH_SHORT).show()
                    goToDashboard()
                }
                // Quality Control
                email.equals("qualitycontrol@tiasa.com", ignoreCase = true) && password == "qualitycontrol123" -> {
                    saveLoginSession(email, "Quality Control", "quality_control")
                    Toast.makeText(this, "✅ Login Sebagai Quality Control!", Toast.LENGTH_SHORT).show()
                    goToDashboard()
                }
                else -> {
                    Toast.makeText(
                        this,
                        "❌ Email atau Password salah!\n\n📋 Demo Accounts:\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                                "Staff Produksi: staffproduksi@tiasa.com / staffproduksi123\n" +
                                "Kepala Gudang: kepalagudang@tiasa.com / kepalagudang123\n" +
                                "Quality Control: qualitycontrol@tiasa.com / qualitycontrol123",
                        Toast.LENGTH_LONG
                    ).show()
                    setLoadingState(false)
                }
            }
        }, 2000)
    }

    private fun saveLoginSession(email: String, name: String, role: String) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_LOGIN, true)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_ROLE, role)
            apply()
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        btnLogin.isEnabled = !isLoading
        btnLogin.text = if (isLoading) "" else "MASUK"
        progressBar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
        tvLoading.visibility = if (isLoading) TextView.VISIBLE else TextView.GONE
        etEmail.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading
        cbRememberMe.isEnabled = !isLoading
        btnTogglePassword.isEnabled = !isLoading
    }

    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}