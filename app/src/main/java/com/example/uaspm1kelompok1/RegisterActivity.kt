package com.example.uaspm1kelompok1

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

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

                if (
                    email.isNotEmpty() &&
                    (!email.contains("@") || !email.lowercase().endsWith(".com"))
                ) {

                    etEmail.background =
                        ContextCompat.getDrawable(
                            this@RegisterActivity,
                            R.drawable.bg_bordersalah
                        )

                } else {

                    etEmail.background =
                        ContextCompat.getDrawable(
                            this@RegisterActivity,
                            R.drawable.bg_edittext
                        )
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {}
        })

        // Validasi Phone
        etPhone.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {}

            override fun afterTextChanged(s: Editable?) {

                val phone = s.toString()

                if (
                    phone.isNotEmpty() &&
                    !phone.matches(Regex("^08[0-9]{8,12}$"))
                ) {

                    etPhone.background =
                        ContextCompat.getDrawable(
                            this@RegisterActivity,
                            R.drawable.bg_bordersalah
                        )

                    tvPhoneError.visibility = View.VISIBLE

                } else {

                    etPhone.background =
                        ContextCompat.getDrawable(
                            this@RegisterActivity,
                            R.drawable.bg_edittext
                        )

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
            if (etFullName.text.isEmpty() || etEmail.text.isEmpty() || etPhone.text.isEmpty() ||
                etPassword.text.isEmpty() || !cbTerms.isChecked || selectedGender.isEmpty()) {
                showCustomDialog("Perhatian", "Silahkan lengkapi data Anda!", false) { }
            } else {
                showCustomDialog("Konfirmasi", "Apakah data Anda telah sesuai?", true) {
                    showSuccessDialog()
                }
            }
        }
        tvBackToLogin.setOnClickListener { finish() }
    }

    private fun showCustomDialog(title: String, message: String, isConfirm: Boolean, onConfirm: () -> Unit) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_custom)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val tvMsg = dialog.findViewById<TextView>(R.id.tvMessage)
        val btnPositive = dialog.findViewById<Button>(R.id.btnPositive)
        val btnNegative = dialog.findViewById<Button>(R.id.btnNegative)

        tvMsg.text = message
        btnPositive.text = if (isConfirm) "Lanjutkan" else "OK"
        if (!isConfirm) btnNegative.visibility = View.GONE

        btnPositive.setOnClickListener {
            onConfirm()
            dialog.dismiss()
        }
        btnNegative.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showSuccessDialog() {
        Toast.makeText(this, "Selamat, Anda telah terdaftar!", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun setupPasswordStrengthChecker() {
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { updatePasswordStrength(s.toString()) }
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
        val colors = listOf(android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light, android.R.color.holo_green_dark)
        val bars = listOf(strengthBar1, strengthBar2, strengthBar3, strengthBar4)
        for (i in 0 until strength) {
            bars[i].setBackgroundColor(ContextCompat.getColor(this, colors[i]))
        }
        tvPasswordStrength.text = when(strength) { 1 -> "Lemah"; 2 -> "Sedang"; 3 -> "Kuat"; 4 -> "Sangat Kuat"; else -> "" }
    }

    private fun resetStrengthBars() {
        val color = ContextCompat.getColor(this, android.R.color.darker_gray)
        listOf(strengthBar1, strengthBar2, strengthBar3, strengthBar4).forEach { it.setBackgroundColor(color) }
    }

    private fun setupPasswordToggles() {
        btnTogglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            etPassword.inputType = if (isPasswordVisible) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            btnTogglePassword.setImageResource(if (isPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_close)
            etPassword.setSelection(etPassword.text.length)
        }
        btnToggleConfirmPassword.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            etConfirmPassword.inputType = if (isConfirmPasswordVisible) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            btnToggleConfirmPassword.setImageResource(if (isConfirmPasswordVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_close)
            etConfirmPassword.setSelection(etConfirmPassword.text.length)
        }
    }

    private fun setupRoleSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, listOf("Staff Produksi", "Kepala Gudang", "Quality Control"))
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRole.adapter = adapter
        spRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>, p1: View?, p2: Int, p3: Long) { selectedRole = listOf("staff_produksi", "kepala_gudang", "quality_control")[p2] }
            override fun onNothingSelected(p0: AdapterView<*>) {}
        }
    }
}