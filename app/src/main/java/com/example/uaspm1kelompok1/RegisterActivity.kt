package com.example.uaspm1kelompok1

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.uaspm1kelompok1.database.DatabaseHelper
class RegisterActivity : AppCompatActivity() {

    // Semua View
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLoading: TextView
    private lateinit var tvBackToLogin: TextView
    private lateinit var cbTerms: CheckBox
    private lateinit var tvPasswordStrength: TextView
    private lateinit var strengthBar1: View
    private lateinit var strengthBar2: View
    private lateinit var strengthBar3: View
    private lateinit var strengthBar4: View
    private lateinit var rgGender: RadioGroup
    private lateinit var spRole: Spinner
    private lateinit var btnTogglePassword: ImageButton
    private lateinit var btnToggleConfirmPassword: ImageButton
    private lateinit var tvPhoneError: TextView
    private lateinit var tvEmailError: TextView

    // Variabel state
    private var selectedGender: String = ""
    private var selectedRole: String = "staff_produksi"
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private lateinit var dbHelper: DatabaseHelper

    companion object {
        private const val USER_DATA_PREFS = "USER_DATA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        dbHelper = DatabaseHelper(this)
        initViews()
        setupRealTimeValidation()
        setupListeners()
        setupPasswordStrengthChecker()
        setupPasswordToggles()
        setupRoleSpinner()
    }

    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        tvPhoneError = findViewById(R.id.tvPhoneError)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)
        tvLoading = findViewById(R.id.tvLoading)
        tvBackToLogin = findViewById(R.id.tvBackToLogin)
        cbTerms = findViewById(R.id.cbTerms)
        tvPasswordStrength = findViewById(R.id.tvPasswordStrength)
        strengthBar1 = findViewById(R.id.strengthBar1)
        strengthBar2 = findViewById(R.id.strengthBar2)
        strengthBar3 = findViewById(R.id.strengthBar3)
        strengthBar4 = findViewById(R.id.strengthBar4)
        rgGender = findViewById(R.id.rgGender)
        spRole = findViewById(R.id.spRole)
        btnTogglePassword = findViewById(R.id.btnTogglePassword)
        btnToggleConfirmPassword = findViewById(R.id.btnToggleConfirmPassword)

        rgGender.setOnCheckedChangeListener { _, checkedId ->
            selectedGender = when (checkedId) {
                R.id.rbMale -> "Laki-laki"
                R.id.rbFemale -> "Perempuan"
                else -> ""
            }
        }
    }

    private fun setupRealTimeValidation() {
        // Validasi Email
        etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                        .matches()
                ) {
                    etEmail.background =
                        ContextCompat.getDrawable(this@RegisterActivity, R.drawable.bg_bordersalah)
                    etEmail.error = "Format email tidak valid"
                } else {
                    etEmail.background =
                        ContextCompat.getDrawable(this@RegisterActivity, R.drawable.bg_edittext)
                    etEmail.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Validasi Phone
        etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString()
                if (phone.isNotEmpty() && !phone.matches(Regex("^08[0-9]{8,12}$"))) {
                    etPhone.background =
                        ContextCompat.getDrawable(this@RegisterActivity, R.drawable.bg_bordersalah)
                    tvPhoneError.visibility = View.VISIBLE
                } else {
                    etPhone.background =
                        ContextCompat.getDrawable(this@RegisterActivity, R.drawable.bg_edittext)
                    tvPhoneError.visibility = View.GONE
                }
            }
        })

        // Validasi Confirm Password
        etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != etPassword.text.toString()) {
                    etConfirmPassword.error = "Password tidak cocok"
                } else {
                    etConfirmPassword.error = null
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupListeners() {
        btnRegister.setOnClickListener {
            if (!validateForm()) {
                return@setOnClickListener
            }
            performRegistration()
        }
        tvBackToLogin.setOnClickListener { finish() }
    }

    /**
     * Validasi semua form sebelum register
     */
    private fun validateForm(): Boolean {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (fullName.isEmpty()) {
            etFullName.error = "Nama lengkap harus diisi"
            etFullName.requestFocus()
            return false
        }
        if (fullName.length < 3) {
            etFullName.error = "Nama minimal 3 karakter"
            etFullName.requestFocus()
            return false
        }
        if (email.isEmpty()) {
            etEmail.error = "Email harus diisi"
            etEmail.requestFocus()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Format email tidak valid"
            etEmail.requestFocus()
            return false
        }
        if (selectedGender.isEmpty()) {
            Toast.makeText(this, "Pilih jenis kelamin", Toast.LENGTH_SHORT).show()
            return false
        }
        if (phone.isEmpty()) {
            etPhone.error = "Nomor WhatsApp harus diisi"
            etPhone.requestFocus()
            return false
        }
        if (!phone.matches(Regex("^08[0-9]{8,12}$"))) {
            etPhone.error = "Nomor harus 08 (minimal 10 digit)"
            etPhone.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "Password harus diisi"
            etPassword.requestFocus()
            return false
        }
        if (password.length < 6) {
            etPassword.error = "Password minimal 6 karakter"
            etPassword.requestFocus()
            return false
        }
        if (password != confirmPassword) {
            etConfirmPassword.error = "Password tidak cocok"
            etConfirmPassword.requestFocus()
            return false
        }
        if (!cbTerms.isChecked) {
            Toast.makeText(this, "Setujui syarat & ketentuan", Toast.LENGTH_SHORT).show()
            return false
        }

        // CEK APAKAH EMAIL SUDAH TERDAFTAR
        if (isEmailRegistered(email)) {
            etEmail.error = "Email sudah terdaftar!"
            etEmail.requestFocus()
            Toast.makeText(this, "Email sudah terdaftar, gunakan email lain", Toast.LENGTH_SHORT)
                .show()
            return false
        }

        return true
    }

    /**
     * Cek apakah email sudah terdaftar
     */
    private fun isEmailRegistered(
        email: String
    ): Boolean {

        return dbHelper.isEmailExist(
            email
        )
    }

    /**
     * Simpan data user ke SharedPreferences
     */


    private fun performRegistration() {

        val fullName =
            etFullName.text.toString().trim()

        val email =
            etEmail.text.toString().trim()

        val phone =
            etPhone.text.toString().trim()

        val password =
            etPassword.text.toString().trim()

        setLoadingState(true)

        Handler(Looper.getMainLooper()).postDelayed({

            val berhasil =
                dbHelper.insertUser(
                    nama = fullName,
                    email = email,
                    noHp = phone,
                    gender = selectedGender,
                    password = password,
                    role = selectedRole
                )

            setLoadingState(false)

            if (berhasil) {
                showSuccessDialog()

            } else {
                Toast.makeText(
                    this,

                    "Registrasi gagal",
                    Toast.LENGTH_SHORT

                ).show()
            }

        },1500)
    }

    private fun showSuccessDialog() {
        val roleName = when (selectedRole) {
            "kepala_gudang" -> "Kepala Gudang"
            "quality_control" -> "Quality Control"
            else -> "Staff Produksi"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("🎉 Registrasi Berhasil!")
            .setMessage("Akun Anda telah berhasil didaftarkan sebagai $roleName.\n\nSilakan login untuk melanjutkan.")
            .setPositiveButton("Login Sekarang") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun setupPasswordStrengthChecker() {
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                updatePasswordStrength(password)
                // Cek kecocokan password dengan konfirmasi
                if (etConfirmPassword.text.toString().isNotEmpty()) {
                    if (password != etConfirmPassword.text.toString()) {
                        etConfirmPassword.error = "Password tidak cocok"
                    } else {
                        etConfirmPassword.error = null
                    }
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updatePasswordStrength(password: String) {
        val strength = if (password.isEmpty()) 0 else {
            var s = 0
            if (password.length >= 6) s++
            if (password.contains(Regex("[0-9]"))) s++
            if (password.contains(Regex("[A-Z]"))) s++
            if (password.contains(Regex("[^a-zA-Z0-9]"))) s++
            s
        }
        resetStrengthBars()
        val colors = listOf(
            android.R.color.holo_red_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_green_light,
            android.R.color.holo_green_dark
        )
        val bars = listOf(strengthBar1, strengthBar2, strengthBar3, strengthBar4)
        for (i in 0 until strength.coerceAtMost(4)) {
            bars[i].setBackgroundColor(ContextCompat.getColor(this, colors[i]))
        }
        tvPasswordStrength.text = when(strength) {
            0 -> ""
            1 -> "⚠️ Lemah"
            2 -> "🟠 Sedang"
            3 -> "🟢 Kuat"
            4 -> "✅ Sangat Kuat"
            else -> ""
        }
    }

    private fun resetStrengthBars() {
        val color = ContextCompat.getColor(this, android.R.color.darker_gray)
        listOf(strengthBar1, strengthBar2, strengthBar3, strengthBar4).forEach { it.setBackgroundColor(color) }
    }

    private fun setupPasswordToggles() {
        btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            etPassword.inputType = if (isPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            btnTogglePassword.setImageResource(if (isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_close)
            etPassword.setSelection(etPassword.text.length)
        }
        btnToggleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            etConfirmPassword.inputType = if (isConfirmPasswordVisible) {
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            btnToggleConfirmPassword.setImageResource(if (isConfirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_close)
            etConfirmPassword.setSelection(etConfirmPassword.text.length)
        }
    }

    private fun setupRoleSpinner() {
        val roles = listOf("Staff Produksi", "Kepala Gudang", "Quality Control")
        val roleValues = listOf("staff_produksi", "kepala_gudang", "quality_control")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRole.adapter = adapter
        spRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedRole = roleValues[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedRole = "staff_produksi"
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        btnRegister.isEnabled = !isLoading
        btnRegister.text = if (isLoading) "" else "DAFTAR"
        progressBar.visibility = if (isLoading) ProgressBar.VISIBLE else ProgressBar.GONE
        tvLoading.visibility = if (isLoading) TextView.VISIBLE else TextView.GONE
        etFullName.isEnabled = !isLoading
        etEmail.isEnabled = !isLoading
        etPhone.isEnabled = !isLoading
        etPassword.isEnabled = !isLoading
        etConfirmPassword.isEnabled = !isLoading
        cbTerms.isEnabled = !isLoading
        rgGender.isEnabled = !isLoading
        spRole.isEnabled = !isLoading
        btnTogglePassword.isEnabled = !isLoading
        btnToggleConfirmPassword.isEnabled = !isLoading
    }
}