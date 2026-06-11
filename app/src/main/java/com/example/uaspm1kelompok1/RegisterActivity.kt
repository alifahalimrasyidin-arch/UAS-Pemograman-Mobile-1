package com.example.uaspm1kelompok1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RegisterActivity : AppCompatActivity() {

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

    private var selectedGender: String = ""
    private var selectedRole: String = "staff_produksi"
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupListeners()
        setupPasswordStrengthChecker()
        setupPhoneNumberFormatting()
        setupPasswordToggles()
        setupRoleSpinner()
    }

    private fun initViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
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

    private fun setupListeners() {
        btnRegister.setOnClickListener { performRegistration() }
        tvBackToLogin.setOnClickListener { finish() }
    }

    private fun setupPasswordToggles() {
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

        btnToggleConfirmPassword.setOnClickListener {
            if (isConfirmPasswordVisible) {
                etConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_close)
                isConfirmPasswordVisible = false
            } else {
                etConfirmPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                btnToggleConfirmPassword.setImageResource(R.drawable.ic_eye_open)
                isConfirmPasswordVisible = true
            }
            etConfirmPassword.setSelection(etConfirmPassword.text.length)
        }
    }

    private fun setupPasswordStrengthChecker() {
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                updatePasswordStrength(password)
            }
        })
    }

    private fun setupPhoneNumberFormatting() {
        etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString()
                if (phone.isNotEmpty() && !phone.matches(Regex("^08[0-9]{8,12}$"))) {
                    etPhone.error = "Nomor harus dimulai dengan 08 (minimal 10 digit)"
                } else {
                    etPhone.error = null
                }
            }
        })
    }

    private fun updatePasswordStrength(password: String) {
        val strength = checkPasswordStrength(password)
        resetStrengthBars()

        when (strength) {
            0 -> {
                tvPasswordStrength.text = "⚠️ Password terlalu lemah"
                tvPasswordStrength.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }
            1 -> {
                strengthBar1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_light))
                tvPasswordStrength.text = "🔴 Password lemah"
                tvPasswordStrength.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            }
            2 -> {
                strengthBar1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_light))
                strengthBar2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_light))
                tvPasswordStrength.text = "🟠 Password sedang"
                tvPasswordStrength.setTextColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
            }
            3 -> {
                strengthBar1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
                strengthBar2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
                strengthBar3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light))
                tvPasswordStrength.text = "🟢 Password kuat"
                tvPasswordStrength.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            }
            4 -> {
                strengthBar1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                strengthBar2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                strengthBar3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                strengthBar4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                tvPasswordStrength.text = "✅ Password sangat kuat"
                tvPasswordStrength.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
            }
        }
    }

    private fun resetStrengthBars() {
        val color = ContextCompat.getColor(this, android.R.color.darker_gray)
        strengthBar1.setBackgroundColor(color)
        strengthBar2.setBackgroundColor(color)
        strengthBar3.setBackgroundColor(color)
        strengthBar4.setBackgroundColor(color)
    }

    private fun checkPasswordStrength(password: String): Int {
        var strength = 0
        if (password.length >= 6) strength++
        if (password.length >= 8) strength++
        if (password.contains(Regex("[0-9]"))) strength++
        if (password.contains(Regex("[A-Z]"))) strength++
        if (password.contains(Regex("[^a-zA-Z0-9]"))) strength++
        return when {
            password.isEmpty() -> 0
            strength <= 2 -> 1
            strength == 3 -> 2
            strength == 4 -> 3
            else -> 4
        }
    }

    private fun performRegistration() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        if (fullName.isEmpty()) {
            etFullName.error = "Nama lengkap harus diisi"
            etFullName.requestFocus()
            return
        }
        if (fullName.length < 3) {
            etFullName.error = "Nama minimal 3 karakter"
            etFullName.requestFocus()
            return
        }
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
        if (selectedGender.isEmpty()) {
            Toast.makeText(this, "Pilih jenis kelamin terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        if (phone.isEmpty()) {
            etPhone.error = "Nomor WhatsApp harus diisi"
            etPhone.requestFocus()
            return
        }
        if (!phone.matches(Regex("^08[0-9]{8,12}$"))) {
            etPhone.error = "Nomor harus dimulai dengan 08 (minimal 10 digit)"
            etPhone.requestFocus()
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
        val strength = checkPasswordStrength(password)
        if (strength <= 1) {
            showWeakPasswordDialog()
            return
        }
        if (password != confirmPassword) {
            etConfirmPassword.error = "Password tidak cocok"
            etConfirmPassword.requestFocus()
            return
        }
        if (!cbTerms.isChecked) {
            Toast.makeText(this, "Anda harus menyetujui syarat & ketentuan", Toast.LENGTH_SHORT).show()
            return
        }

        setLoadingState(true)
        Handler(Looper.getMainLooper()).postDelayed({
            setLoadingState(false)

            val roleName = when (selectedRole) {
                "kepala_gudang" -> "Kepala Gudang"
                "quality_control" -> "Quality Control"
                else -> "Staff Produksi"
            }

            MaterialAlertDialogBuilder(this)
                .setTitle("Registrasi Berhasil! 🎉")
                .setMessage("Akun Anda telah berhasil didaftarkan sebagai $roleName.\n\nEmail: $email\n\nSilakan login untuk melanjutkan.")
                .setPositiveButton("Login Sekarang") { _, _ ->
                    finish()
                }
                .show()
        }, 1500)
    }

    private fun showWeakPasswordDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Password Lemah")
            .setMessage("Password Anda terlalu lemah. Gunakan kombinasi:\n\n• Minimal 8 karakter\n• Huruf besar dan kecil\n• Angka\n• Simbol (!@#$%)\n\nApakah tetap ingin menggunakan password ini?")
            .setPositiveButton("Tetap Gunakan") { _, _ ->
                cbTerms.isChecked = true
                performRegistration()
            }
            .setNegativeButton("Perbaiki", null)
            .show()
    }

    private fun setLoadingState(isLoading: Boolean) {
        btnRegister.isEnabled = !isLoading
        btnRegister.text = if (isLoading) "" else getString(R.string.register_button)
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