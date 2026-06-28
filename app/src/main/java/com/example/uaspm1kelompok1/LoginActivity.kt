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
import com.example.uaspm1kelompok1.database.DatabaseHelper
import com.example.uaspm1kelompok1.database.SessionManager
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
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var sessionManager: SessionManager
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
         dbHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)
        initViews()
        loadSavedCredentials()
        setupListeners()
        setupPasswordToggle()

        if (sessionManager.isLogin()) {
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


    /**
     * Ambil data user dari SharedPreferences berdasarkan email
     * @return UserData atau null jika tidak ditemukan
     */


    /**
     * Cek login dari data register
     * @return role jika berhasil, null jika gagal
     */


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

            // =============================
            // LOGIN HARDCODE
            // =============================

            when {

                email.equals(
                    "staffproduksi@tiasa.com",
                    true
                ) && password == "staffproduksi123" -> {

                    sessionManager.saveLogin(

                        email,

                        "Staff Produksi",

                        "staff_produksi"
                    )

                    goToDashboard()

                    return@postDelayed
                }

                email.equals(
                    "kepalagudang@tiasa.com",
                    true
                ) && password == "kepalagudang123" -> {

                    sessionManager.saveLogin(

                        email,

                        "Kepala Gudang",

                        "kepala_gudang"
                    )

                    goToDashboard()

                    return@postDelayed
                }

                email.equals(
                    "qualitycontrol@tiasa.com",
                    true
                ) && password == "qualitycontrol123" -> {

                    sessionManager.saveLogin(

                        email,

                        "Quality Control",

                        "quality_control"
                    )

                    goToDashboard()

                    return@postDelayed
                }
            }

            // =============================
            // LOGIN SQLITE
            // =============================

            val cursor = dbHelper.loginUser(

                email,

                password

            )

            if (cursor.moveToFirst()) {

                val nama = cursor.getString(

                    cursor.getColumnIndexOrThrow(
                        "nama"
                    )
                )

                val role = cursor.getString(

                    cursor.getColumnIndexOrThrow(
                        "role"
                    )
                )

                sessionManager.saveLogin(
                    email,
                    nama,
                    role
                )

                cursor.close()
                Toast.makeText(

                    this,
                    "Login berhasil",
                    Toast.LENGTH_SHORT

                ).show()
                goToDashboard()
            } else {

                cursor.close()
                setLoadingState(false)
                Toast.makeText(

                    this,
                    "Email atau Password salah",
                    Toast.LENGTH_SHORT

                ).show()
            }
        },1500)
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