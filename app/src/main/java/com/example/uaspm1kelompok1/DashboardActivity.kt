package com.example.uaspm1kelompok1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.edit
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.text.Editable
import android.text.TextWatcher
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager


class DashboardActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserRole: TextView
    private lateinit var btnLogout: Button
    private lateinit var tvDashboardTitle: TextView
    private lateinit var menuContainer: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private var userRole: String = ""
    private lateinit var rootDashboard: androidx.constraintlayout.widget.ConstraintLayout

    companion object {
        private const val PREFS_NAME = "TIASA_PREFS"
        private const val KEY_IS_LOGIN = "is_login"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"

        val suratPerintah = mutableListOf<SuratPerintah>()

        val hasilProduksi = mutableListOf<HasilProduksi>()

        val dikirimKeQC = mutableListOf<KirimQC>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        if (!sharedPreferences.getBoolean(KEY_IS_LOGIN, false)) {
            goToLogin()
            return
        }

        userRole = sharedPreferences.getString(KEY_USER_ROLE, "staff_produksi") ?: "staff_produksi"
        when (userRole) {

            "kepala_gudang" -> {
                window.decorView.setBackgroundResource(
                    R.drawable.bg_dashboard_kepalagudang
                )
            }

            "staff_produksi" -> {
                window.decorView.setBackgroundResource(
                    R.drawable.bg_dashboard_staffproduksi
                )
            }

            "quality_control" -> {
                window.decorView.setBackgroundResource(
                    R.drawable.bg_dashboard_qc
                )
            }
        }
        initViews()
        when (userRole) {

            "kepala_gudang" -> {
                rootDashboard.setBackgroundResource(
                    R.drawable.bg_dashboard_kepalagudang
                )
            }

            "staff_produksi" -> {
                rootDashboard.setBackgroundResource(
                    R.drawable.bg_dashboard_staffproduksi
                )
            }

            "quality_control" -> {
                rootDashboard.setBackgroundResource(
                    R.drawable.bg_dashboard_qc
                )
            }
        }
        displayUserInfo()
        setupMenu()
        setupListeners()
    }

    private fun initViews() {
        tvUserName = findViewById(R.id.tvUserName)
        tvUserEmail = findViewById(R.id.tvUserEmail)
        tvUserRole = findViewById(R.id.tvUserRole)
        btnLogout = findViewById(R.id.btnLogout)
        menuContainer = findViewById(R.id.menuContainer)
        tvDashboardTitle = findViewById(R.id.tvDashboardTitle)
        rootDashboard = findViewById(R.id.rootDashboard)
    }

    private fun displayUserInfo() {
        val userName = sharedPreferences.getString(KEY_USER_NAME, "User")
        val userEmail = sharedPreferences.getString(KEY_USER_EMAIL, "")

        tvUserName.text = userName
        tvUserEmail.text = userEmail

        val roleDisplay = when (userRole) {
            "kepala_gudang" -> "Kepala Gudang"
            "quality_control" -> "Quality Control"
            "staff_produksi" -> "Staff Produksi"
            else -> userRole.replace("_", " ").split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
        }
        tvUserRole.text = roleDisplay
        tvUserRole.visibility = View.VISIBLE
        tvDashboardTitle.text = when (userRole) {

            "kepala_gudang" ->
                "DASHBOARD KEPALA GUDANG"

            "staff_produksi" ->
                "DASHBOARD STAFF PRODUKSI"

            "quality_control" ->
                "DASHBOARD QUALITY CONTROL"

            else ->
                "DASHBOARD"
        }
    }
    private fun setupMenu() {

        menuContainer.removeAllViews()

        if (userRole == "kepala_gudang") {

            val btnSP = Button(this).apply {

                text = ""

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(160)
                ).apply {
                    bottomMargin = dpToPx(16)
                }

                background = AppCompatResources.getDrawable(
                    this@DashboardActivity,
                    R.drawable.bg_tombol_sp_kepala
                )

                setTextColor(getColor(android.R.color.white))
                textSize = 18f

                setOnClickListener {

                    startActivity(
                        Intent(
                            this@DashboardActivity,
                            SuratPerintahActivity::class.java
                        )
                    )

                }
            }
            menuContainer.addView(btnSP)

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val btnProduksi = Button(this).apply {

                text = ""

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    dpToPx(140),
                    1f
                ).apply {
                    marginEnd = dpToPx(8)
                }

                background = AppCompatResources.getDrawable(
                    this@DashboardActivity,
                    R.drawable.bg_tombol_laporanproduksi
                )

                setTextColor(getColor(android.R.color.white))
                textSize = 16f

                setOnClickListener {
                    lihatLaporanProduksi()
                }
            }

            val btnQC = Button(this).apply {

                text = ""

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    dpToPx(140),
                    1f
                ).apply {
                    marginStart = dpToPx(8)
                }

                background = AppCompatResources.getDrawable(
                    this@DashboardActivity,
                    R.drawable.bg_tombol_laporanqc
                )

                setTextColor(getColor(android.R.color.white))
                textSize = 16f

                setOnClickListener {
                    lihatLaporanQC()
                }
            }

            row.addView(btnProduksi)
            row.addView(btnQC)

            menuContainer.addView(row)

            return
        }
        if (userRole == "staff_produksi") {

            val btnSP = Button(this).apply {

                text = ""

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(160)
                ).apply {
                    bottomMargin = dpToPx(16)
                }

                background = AppCompatResources.getDrawable(
                    this@DashboardActivity,
                    R.drawable.bg_tombol_lihatsp
                )

                setOnClickListener {
                    lihatSuratPerintahBaru()
                }
            }

            menuContainer.addView(btnSP)

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            val btnProduksi = Button(this).apply {

                text = ""

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    dpToPx(140),
                    1f
                ).apply {
                    marginEnd = dpToPx(8)
                }

                background = AppCompatResources.getDrawable(
                    this@DashboardActivity,
                    R.drawable.bg_tombol_mulaiproduksi
                )

                setOnClickListener {
                    mulaiProduksiBaru()
                }
            }

            val btnHasil = Button(this).apply {

                text = ""

                layoutParams = LinearLayout.LayoutParams(
                    0,
                    dpToPx(140),
                    1f
                ).apply {
                    marginStart = dpToPx(8)
                }

                background = AppCompatResources.getDrawable(
                    this@DashboardActivity,
                    R.drawable.bg_tombol_hasilproduksi
                )

                setOnClickListener {
                    hasilProduksiBaru()
                }
            }

            row.addView(btnProduksi)
            row.addView(btnHasil)

            menuContainer.addView(row)

            return
        }
        val menus = when (userRole) {

            "quality_control" -> listOf(

                MenuItem("Inspeksi Kain") {

                    startActivity(
                        Intent(
                            this,
                            QCActivity::class.java
                        )
                    )
                },

                MenuItem("Show Hasil QC") {
                    showHasilQC()
                }
            )


            else -> listOf(
                MenuItem("Lihat Surat Perintah") { lihatSuratPerintahBaru() },
                MenuItem("Mulai Produksi") { mulaiProduksiBaru() },
                MenuItem("Hasil Produksi") { hasilProduksiBaru() }
            )
        }

        for (menu in menus) {

            val button = Button(this).apply {

                text = menu.title

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dpToPx(56)
                ).apply {
                    bottomMargin = dpToPx(12)
                }

                background = AppCompatResources.getDrawable(
                    this@DashboardActivity,
                    R.drawable.bg_button
                )

                setTextColor(getColor(android.R.color.white))
                textSize = 16f

                setOnClickListener {
                    menu.action()
                }
            }

            menuContainer.addView(button)
        }
    }

    private fun setupListeners() {
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }


    //MENU STAFFGUDANG
    private fun lihatSuratPerintahBaru() {

        val view = layoutInflater.inflate(
            R.layout.dialog_lihat_sp,
            null
        )

        val rvSP =
            view.findViewById<RecyclerView>(
                R.id.rvSP
            )

        val btnSemua =
            view.findViewById<Button>(
                R.id.btnSemua
            )

        val btnMingguIni =
            view.findViewById<Button>(
                R.id.btnMingguIni
            )

        rvSP.layoutManager =
            LinearLayoutManager(this)

        val adapter =
            SPAdapter(
                suratPerintah.filter {
                    it.status == "Menunggu Proses"
                }
            ) { sp ->

                val detailView =
                    layoutInflater.inflate(
                        R.layout.dialog_detail_sp,
                        null
                    )

                detailView.findViewById<TextView>(
                    R.id.tvDetailId
                ).text = sp.id

                detailView.findViewById<TextView>(
                    R.id.tvDetailJenis
                ).text = sp.productName

                detailView.findViewById<TextView>(
                    R.id.tvDetailJumlah
                ).text = "${sp.quantity} Meter"

                detailView.findViewById<TextView>(
                    R.id.tvDetailDeadline
                ).text = sp.deadline

                detailView.findViewById<TextView>(
                    R.id.tvDetailStatus
                ).text = sp.status

                MaterialAlertDialogBuilder(this)
                    .setView(detailView)
                    .setPositiveButton(
                        "TUTUP",
                        null
                    )
                    .show()
            }

        rvSP.adapter = adapter

        btnSemua.setOnClickListener {

            adapter.updateData(
                suratPerintah.filter {
                    it.status == "Menunggu Proses"
                }
            )
        }

        btnMingguIni.setOnClickListener {

            val sdf =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            val today = Date()

            val sevenDays =
                Calendar.getInstance().apply {

                    time = today

                    add(
                        Calendar.DAY_OF_YEAR,
                        7
                    )

                }.time

            val filtered =
                suratPerintah.filter {

                    it.status == "Menunggu Proses" &&

                            try {

                                val deadline =
                                    sdf.parse(it.deadline)

                                deadline != null &&
                                        deadline.after(today) &&
                                        deadline.before(sevenDays)

                            } catch (e: Exception) {

                                false
                            }
                }

            adapter.updateData(filtered)
        }

        val dialog =
            MaterialAlertDialogBuilder(this)
                .setView(view)
                .create()

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            (resources.displayMetrics.heightPixels * 0.95).toInt()
        )
    }
    private fun mulaiProduksiBaru() {

        val spMenunggu = suratPerintah.filter {
            it.status == "Menunggu Proses"
        }

        if (spMenunggu.isEmpty()) {

            Toast.makeText(
                this,
                "Tidak ada surat perintah yang siap diproduksi",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val view = layoutInflater.inflate(
            R.layout.dialog_lihat_sp,
            null
        )

        val rvSP =
            view.findViewById<RecyclerView>(
                R.id.rvSP
            )

        rvSP.layoutManager =
            LinearLayoutManager(this)

        val btnSemua =
            view.findViewById<Button>(
                R.id.btnSemua
            )

        val btnMingguIni =
            view.findViewById<Button>(
                R.id.btnMingguIni
            )

        fun loadData(
            data: List<SuratPerintah>
        ) {

            rvSP.adapter =
                SPAdapter(data) { sp ->

                    showFormProduksi(sp)
                }
        }

        loadData(spMenunggu)

        btnSemua.setOnClickListener {

            loadData(spMenunggu)
        }

        btnMingguIni.setOnClickListener {

            val sdf =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            val today = Date()

            val sevenDays =
                Calendar.getInstance().apply {

                    time = today

                    add(
                        Calendar.DAY_OF_YEAR,
                        7
                    )

                }.time

            val filtered =
                suratPerintah.filter {

                    it.status == "Menunggu Proses" &&

                            try {

                                val deadline =
                                    sdf.parse(it.deadline)

                                deadline != null &&
                                        deadline.after(today) &&
                                        deadline.before(sevenDays)

                            } catch (e: Exception) {

                                false
                            }
                }

            loadData(filtered)
        }

        val dialog =
            MaterialAlertDialogBuilder(this)
                .setView(view)
                .create()

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            (resources.displayMetrics.heightPixels * 0.95).toInt()
        )
    }
    private fun showFormProduksi(
        selectedSP: SuratPerintah
    ) {

        val view = layoutInflater.inflate(
            R.layout.dialog_mulai_produksi,
            null
        )

        val tvSpId =
            view.findViewById<TextView>(
                R.id.tvSpId
            )

        val tvJenisKainInfo =
            view.findViewById<TextView>(
                R.id.tvJenisKainInfo
            )

        val tvPanjangInfo =
            view.findViewById<TextView>(
                R.id.tvPanjangInfo
            )

        val tvDeadlineInfo =
            view.findViewById<TextView>(
                R.id.tvDeadlineInfo
            )

        val spJenisProduksi =
            view.findViewById<Spinner>(
                R.id.spJenisProduksi
            )

        val etPanjangProduksi =
            view.findViewById<EditText>(
                R.id.etPanjangProduksi
            )

        val tvJenisError =
            view.findViewById<TextView>(
                R.id.tvJenisError
            )

        val tvPanjangError =
            view.findViewById<TextView>(
                R.id.tvPanjangError
            )

        val etCatatan =
            view.findViewById<EditText>(
                R.id.etCatatan
            )

        tvSpId.text =
            selectedSP.id

        tvJenisKainInfo.text =
            selectedSP.productName

        tvPanjangInfo.text =
            "${selectedSP.quantity} Meter"

        tvDeadlineInfo.text =
            selectedSP.deadline

        val jenisKain = arrayOf(
            "Katun",
            "Denim",
            "Polyester"
        )

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                jenisKain
            )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spJenisProduksi.adapter = adapter
        val posisiAwal =
            jenisKain.indexOf(
                selectedSP.productName
            )

        if (posisiAwal >= 0) {
            spJenisProduksi.setSelection(
                posisiAwal
            )
        }
        // VALIDASI REALTIME

        etPanjangProduksi.addTextChangedListener(
            object : TextWatcher {

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
                ) {

                    val input =
                        s.toString().toIntOrNull()

                    if (input == null) {

                        tvPanjangError.visibility =
                            View.GONE

                        etPanjangProduksi.setBackgroundResource(
                            R.drawable.bg_edittext
                        )

                        return
                    }

                    when {

                        input < selectedSP.quantity -> {

                            tvPanjangError.visibility =
                                View.VISIBLE

                            tvPanjangError.text =
                                "Panjang kain kurang dari ketentuan surat perintah"

                            etPanjangProduksi.setBackgroundResource(
                                R.drawable.bg_error
                            )
                        }

                        input > selectedSP.quantity + 10 -> {

                            tvPanjangError.visibility =
                                View.VISIBLE

                            tvPanjangError.text =
                                "Panjang kain melebihi toleransi 10 meter"

                            etPanjangProduksi.setBackgroundResource(
                                R.drawable.bg_error
                            )
                        }

                        else -> {

                            tvPanjangError.visibility =
                                View.GONE

                            etPanjangProduksi.setBackgroundResource(
                                R.drawable.bg_edittext
                            )
                        }
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )

        val dialog =
            MaterialAlertDialogBuilder(this)
                .setView(view)
                .setNegativeButton(
                    "KEMBALI",
                    null
                )
                .setPositiveButton(
                    "SIMPAN PRODUKSI",
                    null
                )
                .create()

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            (resources.displayMetrics.heightPixels * 0.90).toInt()
        )

        dialog.getButton(
            android.app.AlertDialog.BUTTON_POSITIVE
        ).setOnClickListener {

            val jenisDipilih =
                spJenisProduksi.selectedItem.toString()

            val panjangProduksi =
                etPanjangProduksi.text
                    .toString()
                    .toIntOrNull()

            var valid = true

            if (
                jenisDipilih.lowercase()
                !=
                selectedSP.productName.lowercase()
            ) {

                tvJenisError.visibility =
                    View.VISIBLE

                valid = false
            }
            else {

                tvJenisError.visibility =
                    View.GONE
            }

            if (panjangProduksi == null) {

                tvPanjangError.visibility =
                    View.VISIBLE

                tvPanjangError.text =
                    "Panjang kain wajib diisi"

                etPanjangProduksi.setBackgroundResource(
                    R.drawable.bg_error
                )

                valid = false
            }

            if (!valid) {
                return@setOnClickListener
            }

            if (
                panjangProduksi!! <
                selectedSP.quantity
            ) {
                return@setOnClickListener
            }

            if (
                panjangProduksi >
                selectedSP.quantity + 10
            ) {
                return@setOnClickListener
            }

            // STATUS SELESAI PRODUKSI

            selectedSP.status =
                "Selesai Produksi"

            // SIMPAN HASIL PRODUKSI

            hasilProduksi.add(
                HasilProduksi(
                    spId = selectedSP.id,
                    productName = selectedSP.productName,
                    quantity = panjangProduksi,
                    unit = "Meter",
                    tanggalSelesai = getCurrentDate(),
                    statusQC = "Menunggu QC",
                    notes = etCatatan.text.toString()
                )
            )

            Toast.makeText(
                this,
                "Produksi berhasil disimpan",
                Toast.LENGTH_LONG
            ).show()

            dialog.dismiss()
        }
    }
    private fun hasilProduksiBaru() {

        if (hasilProduksi.isEmpty()) {
            Toast.makeText(
                this,
                "Belum ada hasil produksi",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val daftarHasil = hasilProduksi.map {
            "${it.spId} - ${it.productName} (${it.quantity} Meter)"
        }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("Hasil Produksi")
            .setItems(daftarHasil) { _, which ->

                val hasil = hasilProduksi[which]

                MaterialAlertDialogBuilder(this)
                    .setTitle(hasil.spId)
                    .setMessage(
                        "Jenis Kain : ${hasil.productName}\n" +
                                "Panjang : ${hasil.quantity} Meter\n" +
                                "Tanggal : ${hasil.tanggalSelesai}\n" +
                                "Status QC : ${hasil.statusQC}"
                    )
                    .setPositiveButton("KIRIM KE QC") { _, _ ->

                        if (hasil.statusQC == "Dikirim ke QC") {

                            Toast.makeText(
                                this,
                                "Data sudah dikirim ke QC",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@setPositiveButton
                        }

                        hasil.statusQC = "Dikirim ke QC"

                        dikirimKeQC.add(
                            KirimQC(
                                spId = hasil.spId,
                                productName = hasil.productName,
                                quantity = hasil.quantity,
                                unit = hasil.unit,
                                tanggalKirim = getCurrentDate(),
                                noQC = "QC${String.format("%03d", dikirimKeQC.size + 1)}",

                                grade = "-",
                                detailQC = "-",
                                statusKain = "Menunggu QC"
                            )
                        )

                        Toast.makeText(
                            this,
                            "Berhasil dikirim ke QC",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .setNegativeButton("TUTUP", null)
                    .show()
            }
            .setNegativeButton("TUTUP", null)
            .show()
    }
    // MENU KEPALA GUDANG

    private fun buatSuratPerintahProduksi() {

        val inputView =
            layoutInflater.inflate(
                R.layout.dialog_surat_perintah,
                null
            )

        val tvSpId =
            inputView.findViewById<TextView>(R.id.tvSpId)

        val spJenisKain =
            inputView.findViewById<Spinner>(R.id.spJenisKain)

        val etPanjangKain =
            inputView.findViewById<EditText>(R.id.etPanjangKain)

        val etDeadline =
            inputView.findViewById<EditText>(R.id.etDeadline)

        val tvPanjangError =
            inputView.findViewById<TextView>(R.id.tvPanjangError)

        val spId =
            "SP${String.format("%03d", suratPerintah.size + 1)}"

        tvSpId.text = "ID Surat : $spId"

        val jenisKain = arrayOf(
            "Katun",
            "Denim",
            "Polyester"
        )

        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                jenisKain
            )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spJenisKain.adapter = adapter

        // VALIDASI REALTIME

        etPanjangKain.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    val panjang =
                        s.toString().toIntOrNull()

                    when {

                        panjang == null -> {

                            tvPanjangError.visibility =
                                View.GONE

                            etPanjangKain.setBackgroundResource(
                                R.drawable.bg_edittext
                            )
                        }

                        panjang < 100 -> {

                            tvPanjangError.visibility =
                                View.VISIBLE

                            tvPanjangError.text =
                                "Anda tidak bisa membuat kain dibawah 100 meter"

                            etPanjangKain.setBackgroundResource(
                                R.drawable.bg_error
                            )
                        }

                        panjang > 300 -> {

                            tvPanjangError.visibility =
                                View.VISIBLE

                            tvPanjangError.text =
                                "Anda tidak bisa membuat kain di atas 300 meter"

                            etPanjangKain.setBackgroundResource(
                                R.drawable.bg_error
                            )
                        }

                        else -> {

                            tvPanjangError.visibility =
                                View.GONE

                            etPanjangKain.setBackgroundResource(
                                R.drawable.bg_edittext
                            )
                        }
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        // DATE PICKER

        etDeadline.setOnClickListener {

            val calendar = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->

                    etDeadline.setText(
                        String.format(
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            day
                        )
                    )
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val dialog =
            MaterialAlertDialogBuilder(this)
                .setView(inputView)
                .setNegativeButton("KEMBALI", null)
                .setPositiveButton("BUAT SURAT", null)
                .create()

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            (resources.displayMetrics.heightPixels * 0.90).toInt()
        )

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener {

                val panjangStr =
                    etPanjangKain.text.toString()

                val deadline =
                    etDeadline.text.toString()

                var valid = true

                if (panjangStr.isEmpty()) {

                    etPanjangKain.setBackgroundResource(
                        R.drawable.bg_error
                    )

                    tvPanjangError.visibility =
                        View.VISIBLE

                    tvPanjangError.text =
                        "Panjang kain wajib diisi"

                    valid = false
                }

                if (deadline.isEmpty()) {

                    etDeadline.setBackgroundResource(
                        R.drawable.bg_error
                    )

                    valid = false
                }

                if (!valid) {
                    return@setOnClickListener
                }

                val panjang =
                    panjangStr.toInt()

                if (panjang < 100 || panjang > 300) {
                    return@setOnClickListener
                }

                val konfirmasiView =
                    layoutInflater.inflate(
                        R.layout.dialog_konfirmasi_sp,
                        null
                    )

                konfirmasiView.findViewById<TextView>(
                    R.id.tvKonfirmasiId
                ).text =
                    "ID Surat : $spId"

                konfirmasiView.findViewById<TextView>(
                    R.id.tvKonfirmasiJenis
                ).text =
                    "Jenis Kain : ${spJenisKain.selectedItem}"

                konfirmasiView.findViewById<TextView>(
                    R.id.tvKonfirmasiJumlah
                ).text =
                    "Jumlah Produksi : $panjang Meter"

                konfirmasiView.findViewById<TextView>(
                    R.id.tvKonfirmasiDeadline
                ).text =
                    "Deadline Produksi : $deadline"

                val konfirmasiDialog =
                    MaterialAlertDialogBuilder(this)
                        .setView(konfirmasiView)
                        .setNegativeButton("BATAL", null)
                        .setPositiveButton("SIMPAN") { _, _ ->

                            val dataBaru =
                                SuratPerintah(
                                    id = spId,
                                    productName = spJenisKain.selectedItem.toString(),
                                    quantity = panjang,
                                    unit = "Meter",
                                    deadline = deadline,
                                    status = "Menunggu Proses"
                                )

                            suratPerintah.add(dataBaru)

                            val successView =
                                layoutInflater.inflate(
                                    R.layout.dialog_sukses_sp,
                                    null
                                )

                            successView.findViewById<TextView>(
                                R.id.tvSuccessSpId
                            ).text =
                                "ID Surat : $spId"

                            val successDialog =
                                MaterialAlertDialogBuilder(this)
                                    .setView(successView)
                                    .setPositiveButton("TUTUP") { _, _ ->
                                        dialog.dismiss()
                                    }
                                    .create()

                            successDialog.show()

                            successDialog.window?.setLayout(
                                (resources.displayMetrics.widthPixels * 0.90).toInt(),
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        }
                        .create()

                konfirmasiDialog.show()

                konfirmasiDialog.window?.setLayout(
                    (resources.displayMetrics.widthPixels * 0.90).toInt(),
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
    }
    private fun showHasilQC() {
        val listQC = dikirimKeQC.filter { it.statusKain != "Menunggu QC" }

        if (listQC.isEmpty()) {
            Toast.makeText(this, "Belum ada hasil inspeksi QC", Toast.LENGTH_SHORT).show()
            return
        }

        var message = ""
        for (qc in listQC) {
            message += "No QC: ${qc.noQC}\n"
            message += "SP ID: ${qc.spId}\n"
            message += "Produk: ${qc.productName}\n"
            message += "Grade: ${qc.grade}\n"
            message += "Status: ${qc.statusKain}\n"
            message += "━━━━━━━━━━━━━━━━━━\n\n"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Hasil Inspeksi QC")
            .setMessage(message)
            .setPositiveButton("TUTUP", null)
            .show()
    }

    private fun lihatLaporanProduksi() {
        if (suratPerintah.isEmpty()) {
            Toast.makeText(this, "Belum ada data surat perintah", Toast.LENGTH_SHORT).show()
            return
        }

        var message = "LAPORAN PRODUKSI\n━━━━━━━━━━━━━━━━━━━━━━\n\n"

        for (sp in suratPerintah) {
            message += " ${sp.id}\n"
            message += " ${sp.productName}\n"
            message += " ${sp.quantity} ${sp.unit}\n"
            message += " ${sp.deadline}\n"
            message += " ${sp.status}\n\n"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Laporan Produksi")
            .setMessage(message)
            .setPositiveButton("TUTUP", null)
            .show()
    }

    private fun lihatLaporanQC() {

        val dataQC =
            dikirimKeQC.filter {
                it.statusKain == "Selesai QC"
            }

        if (dataQC.isEmpty()) {

            Toast.makeText(
                this,
                "Belum ada data hasil QC",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        var totalA = 0
        var totalB = 0
        var totalC = 0

        var message =
            "LAPORAN QUALITY CONTROL\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━\n\n"

        for (qc in dataQC) {

            when (qc.grade) {

                "A" -> totalA++

                "B" -> totalB++

                "C" -> totalC++
            }

            message +=
                "NO QC : ${qc.noQC}\n" +
                        "SP ID : ${qc.spId}\n" +
                        "JENIS KAIN : ${qc.productName}\n" +
                        "PANJANG : ${qc.quantity} Meter\n" +
                        "TANGGAL KIRIM : ${qc.tanggalKirim}\n" +
                        "TANGGAL QC : ${qc.tanggalQC}\n\n" +

                        "UJI SAMPLE\n" +
                        "- Uji Cuci : ${if (qc.ujiCuci) "✓" else "✗"}\n" +
                        "- Uji Daya Tahan : ${if (qc.ujiDayaTahan) "✓" else "✗"}\n" +
                        "- Uji Suhu Panas : ${if (qc.ujiSuhuPanas) "✓" else "✗"}\n\n" +

                        "HASIL INSPEKSI\n" +
                        "- Warna : ${qc.hasilWarna}\n" +
                        "- Jahitan : ${qc.hasilJahitan}\n" +
                        "- Ukuran : ${qc.hasilUkuran}\n" +
                        "- Ukuran Akhir : ${qc.ukuranAkhir} Meter\n\n" +

                        "DOKUMENTASI QC\n" +
                        "- Foto Sebelum : ${if (qc.fotoSebelum.isNotEmpty()) "Ada" else "Tidak Ada"}\n" +
                        "- Foto Sesudah : ${if (qc.fotoSesudah.isNotEmpty()) "Ada" else "Tidak Ada"}\n" +
                        "- Foto Tambahan : ${if (qc.fotoTambahan.isNotEmpty()) "Ada" else "Tidak Ada"}\n\n" +

                        "GRADE : ${qc.grade}\n" +
                        "STATUS : ${qc.statusKain}\n"

            if (qc.grade == "C") {

                message +=
                    "PENURUNAN HARGA : ${qc.persentasePenurunan}%\n"
            }

            message +=
                "\n━━━━━━━━━━━━━━━━━━━━━━\n\n"
        }

        message +=
            "RINGKASAN HASIL QC\n\n" +
                    "Grade A : $totalA\n" +
                    "Grade B : $totalB\n" +
                    "Grade C : $totalC\n\n" +
                    "Total Kain QC : ${dataQC.size}"

        MaterialAlertDialogBuilder(this)
            .setTitle("Laporan QC")
            .setMessage(message)
            .setPositiveButton(
                "TUTUP",
                null
            )
            .show()
    }
    //MENU QUALITY CONTROL

    private fun showInspeksiDialogQC(qc: KirimQC) {

        val view =
            layoutInflater.inflate(
                R.layout.dialog_inspeksi_qc,
                null
            )

        val tvInfoQC =
            view.findViewById<TextView>(R.id.tvInfoQC)

        val spGrade =
            view.findViewById<Spinner>(R.id.spGrade)

        val etDetailQC =
            view.findViewById<EditText>(R.id.etDetailQC)

        tvInfoQC.text =
            "No QC : ${qc.noQC}\n" +
                    "Jenis Kain : ${qc.productName}\n" +
                    "Panjang : ${qc.quantity} Meter"

        val gradeList = arrayOf(
            "A",
            "B",
            "C"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            gradeList
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spGrade.adapter = adapter

        MaterialAlertDialogBuilder(this)
            .setTitle("Inspeksi Kain")
            .setView(view)
            .setPositiveButton("SIMPAN") { _, _ ->

                val grade =
                    spGrade.selectedItem.toString()

                val detail =
                    etDetailQC.text.toString()

                qc.grade = grade
                qc.detailQC = detail

                qc.statusKain =
                    if (
                        grade == "A" ||
                        grade == "B"
                    ) {
                        "Lulus"
                    } else {
                        "Gagal"
                    }

                Toast.makeText(
                    this,
                    "Data QC berhasil disimpan",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton("BATAL", null)
            .show()
    }

    private fun inspeksiKain() {
        val listQC = dikirimKeQC.filter { it.statusKain == "Menunggu QC" }

        if (listQC.isEmpty()) {
            Toast.makeText(this, "Tidak ada data kain yang perlu diperiksa", Toast.LENGTH_SHORT).show()
            return
        }

        val items = listQC.map { "${it.noQC} - ${it.productName}" }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("Pilih Kain untuk Inspeksi")
            .setItems(items) { _, which ->
                showInspeksiDialogQC(listQC[which])
            }
            .setNegativeButton("BATAL", null)
            .show()
    }




    // ==================== UTILITY ====================

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("🚪 KONFIRMASI LOGOUT")
            .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            .setPositiveButton("✅ YA, LOGOUT") { _, _ ->
                performLogout()
            }
            .setNegativeButton("❌ BATAL", null)
            .show()
    }

    private fun performLogout() {
        sharedPreferences.edit { clear() }
        Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show()
        goToLogin()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    data class MenuItem(val title: String, val action: () -> Unit)

    data class SuratPerintah(
        val id: String,
        var productName: String,
        var quantity: Int,
        var unit: String,
        var deadline: String,
        var status: String
    )

    data class HasilProduksi(
        val spId: String,
        val productName: String,
        val quantity: Int,
        val unit: String,
        val tanggalSelesai: String,
        var statusQC: String,
        val notes: String = ""
    )

    data class KirimQC(
        val spId: String,
        val productName: String,
        val quantity: Int,
        val unit: String,
        val tanggalKirim: String,
        val noQC: String,

        var ujiCuci: Boolean = false,
        var ujiDayaTahan: Boolean = false,
        var ujiSuhuPanas: Boolean = false,

        var cekWarna: Int = 0,
        var cekJahitan: Int = 0,
        var cekUkuran: Int = 0,

        // HASIL DESKRIPTIF QC
        var hasilWarna: String = "-",
        var hasilJahitan: String = "-",
        var hasilUkuran: String = "-",

        var ukuranAkhir: Int = 0,

        var fotoSebelum: String = "",
        var fotoSesudah: String = "",
        var fotoTambahan: String = "",

        var grade: String = "-",
        var detailQC: String = "-",

        var persentasePenurunan: Int = 0,

        var tanggalQC: String = "-",

        var statusKain: String = "Menunggu QC"
    )
}
