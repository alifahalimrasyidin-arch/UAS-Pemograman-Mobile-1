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
        private const val USER_DATA_PREFS = "USER_DATA" // Untuk menyimpan data register
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
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnTogglePassword.setImageResource(R.drawable.ic_eye_close)
                isPasswordVisible = false
            } else {
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnTogglePassword.setImageResource(R.drawable.ic_eye_open)
                isPasswordVisible = true
            }
            etPassword.setSelection(etPassword.text.length)
        }
    }

    /**
     * Cek apakah email sudah terdaftar di data register
     */
    private fun isEmailRegistered(email: String): Boolean {
        val userPrefs = getSharedPreferences(USER_DATA_PREFS, Context.MODE_PRIVATE)
        return userPrefs.contains("user_$email")
    }

    /**
     * Ambil data user dari SharedPreferences berdasarkan email
     * @return UserData atau null jika tidak ditemukan
     */
    private fun getRegisteredUser(email: String): UserData? {
        val userPrefs = getSharedPreferences(USER_DATA_PREFS, Context.MODE_PRIVATE)
        val userDataString = userPrefs.getString("user_$email", null)

        if (userDataString != null) {
            val parts = userDataString.split("|")
            if (parts.size >= 6) {
                return UserData(
                    name = parts[0],
                    email = parts[1],
                    phone = parts[2],
                    gender = parts[3],
                    role = parts[4],
                    password = parts[5]
                )
            }
        }
        return null
    }

    /**
     * Cek login dari data register
     * @return role jika berhasil, null jika gagal
     */
    private fun checkRegisteredUserLogin(email: String, password: String): String? {
        val user = getRegisteredUser(email)
        if (user != null && user.password == password) {
            return user.role
        }
        return null
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
            // CEK APAKAH EMAIL TERDAFTAR DARI REGISTER
            val registeredRole = checkRegisteredUserLogin(email, password)

            when {
                // Staff Produksi (hardcode)
                email.equals("staffproduksi@tiasa.com", ignoreCase = true) && password == "staffproduksi123" -> {
                    saveLoginSession(email, "Staff Produksi", "staff_produksi")
                    Toast.makeText(this, "✅ Login Sebagai Staff Produksi!", Toast.LENGTH_SHORT).show()
                    goToDashboard()
                }
                // Kepala Gudang (hardcode)
                email.equals("kepalagudang@tiasa.com", ignoreCase = true) && password == "kepalagudang123" -> {
                    saveLoginSession(email, "Kepala Gudang", "kepala_gudang")
                    Toast.makeText(this, "✅ Login Sebagai Kepala Gudang!", Toast.LENGTH_SHORT).show()
                    goToDashboard()
                }
                // Quality Control (hardcode)
                email.equals("qualitycontrol@tiasa.com", ignoreCase = true) && password == "qualitycontrol123" -> {
                    saveLoginSession(email, "Quality Control", "quality_control")
                    Toast.makeText(this, "✅ Login Sebagai Quality Control!", Toast.LENGTH_SHORT).show()
                    goToDashboard()
                }
                // CEK DARI DATA REGISTER
                registeredRole != null -> {
                    val user = getRegisteredUser(email)
                    val roleName = when (registeredRole) {
                        "staff_produksi" -> "Staff Produksi"
                        "kepala_gudang" -> "Kepala Gudang"
                        "quality_control" -> "Quality Control"
                        else -> "User"
                    }
                    saveLoginSession(email, user?.name ?: roleName, registeredRole)
                    Toast.makeText(this, "✅ Login Sebagai $roleName!", Toast.LENGTH_SHORT).show()
                    goToDashboard()
                }
                else -> {
                    Toast.makeText(
                        this,
                        "❌ Email atau Password salah!\n\n📋 Demo Accounts:\n" +
                                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                                "👔 Staff Produksi: staffproduksi@tiasa.com / staffproduksi123\n" +
                                "👑 Kepala Gudang: kepalagudang@tiasa.com / kepalagudang123\n" +
                                "🔬 Quality Control: qualitycontrol@tiasa.com / qualitycontrol123\n\n" +
                                "📝 Atau gunakan akun yang sudah terdaftar!",
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