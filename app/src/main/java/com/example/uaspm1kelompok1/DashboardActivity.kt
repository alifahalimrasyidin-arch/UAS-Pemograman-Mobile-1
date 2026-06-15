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

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserRole: TextView
    private lateinit var btnLogout: Button
    private lateinit var menuContainer: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private var userRole: String = ""


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

        initViews()
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
    }

    private fun setupMenu() {
        menuContainer.removeAllViews()

        val menus = when (userRole) {
            "kepala_gudang" -> listOf(
                MenuItem("Buat Surat Perintah Produksi") { buatSuratPerintahProduksi() },
                MenuItem("Laporan Produksi") { lihatLaporanProduksi() },
                MenuItem("Laporan QC") { lihatLaporanQC() }
            )

            "quality_control" -> listOf(
                MenuItem("Inspeksi Kain") { inspeksiKain() },
                MenuItem("Laporan QC") { showLaporanQC() },
                MenuItem("Show Hasil QC") { showHasilQC() },
            )
            else -> listOf(
                MenuItem(" Lihat Surat Perintah") { lihatSuratPerintahBaru() },
                MenuItem(" Mulai Produksi") { mulaiProduksiBaru() },
                MenuItem(" Hasil Produksi") { hasilProduksiBaru() }
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
                background = AppCompatResources.getDrawable(this@DashboardActivity, R.drawable.bg_button)
                setTextColor(getColor(android.R.color.white))
                textSize = 16f
                setOnClickListener { menu.action() }
            }
            menuContainer.addView(button)
        }
    }

    private fun setupListeners() {
        btnLogout.setOnClickListener { showLogoutDialog() }
    }

    //menu staffgudang
    private fun lihatSuratPerintahBaru() {

        val daftarSP = suratPerintah.filter {
            it.status == "Menunggu Proses"
        }

        if (daftarSP.isEmpty()) {
            Toast.makeText(
                this,
                "Belum ada surat perintah yang menunggu proses",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        var message = ""

        for (sp in daftarSP) {

            message += " ${sp.id}\n"
            message += " ${sp.productName}\n"
            message += " ${sp.quantity} Meter\n"
            message += " ${sp.deadline}\n"
            message += " ${sp.status}\n"
            message += "━━━━━━━━━━━━━━━━━━\n\n"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(" SURAT PERINTAH PRODUKSI")
            .setMessage(message)
            .setPositiveButton("TUTUP", null)
            .show()
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

        val daftarSP = spMenunggu.map {
            "${it.id} - ${it.productName}"
        }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("Pilih Surat Perintah")
            .setItems(daftarSP) { _, which ->

                val selectedSP = spMenunggu[which]

                val view = layoutInflater.inflate(
                    R.layout.dialog_mulai_produksi,
                    null
                )

                val tvInfoSP =
                    view.findViewById<TextView>(R.id.tvInfoSP)

                val spJenisProduksi =
                    view.findViewById<Spinner>(R.id.spJenisProduksi)

                val etPanjangProduksi =
                    view.findViewById<EditText>(R.id.etPanjangProduksi)

                val tvJenisError =
                    view.findViewById<TextView>(R.id.tvJenisError)

                val tvPanjangError =
                    view.findViewById<TextView>(R.id.tvPanjangError)

                val etCatatan =
                    view.findViewById<EditText>(R.id.etCatatan)

                tvInfoSP.text =
                    "ID SP : ${selectedSP.id}\n" +
                            "Jenis Kain : ${selectedSP.productName}\n" +
                            "Panjang SP : ${selectedSP.quantity} Meter\n" +
                            "Deadline : ${selectedSP.deadline}"

                val jenisKain = arrayOf(
                    "Katun",
                    "Denim",
                    "Polyester"
                )

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_item,
                    jenisKain
                )

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                spJenisProduksi.adapter = adapter

                // VALIDASI REALTIME PANJANG

                etPanjangProduksi.addTextChangedListener(
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

                            val input =
                                s.toString().toIntOrNull()

                            if (input == null) {
                                tvPanjangError.visibility =
                                    View.GONE
                                return
                            }

                            when {

                                input < selectedSP.quantity -> {

                                    etPanjangProduksi.setBackgroundColor(
                                        Color.argb(
                                            40,
                                            255,
                                            0,
                                            0
                                        )
                                    )

                                    tvPanjangError.visibility =
                                        View.VISIBLE

                                    tvPanjangError.text =
                                        "Panjang kain kurang dari ketentuan surat perintah"
                                }

                                input > selectedSP.quantity + 10 -> {

                                    etPanjangProduksi.setBackgroundColor(
                                        Color.argb(
                                            40,
                                            255,
                                            0,
                                            0
                                        )
                                    )

                                    tvPanjangError.visibility =
                                        View.VISIBLE

                                    tvPanjangError.text =
                                        "Panjang kain melebihi ketentuan surat perintah"
                                }

                                else -> {

                                    etPanjangProduksi.setBackgroundColor(
                                        Color.TRANSPARENT
                                    )

                                    tvPanjangError.visibility =
                                        View.GONE
                                }
                            }
                        }

                        override fun afterTextChanged(
                            s: Editable?
                        ) {
                        }
                    }
                )

                MaterialAlertDialogBuilder(this)
                    .setTitle("Mulai Produksi")
                    .setView(view)
                    .setPositiveButton("SIMPAN") { _, _ ->

                        val jenisDipilih =
                            spJenisProduksi.selectedItem.toString()

                        val panjangProduksi =
                            etPanjangProduksi.text
                                .toString()
                                .toIntOrNull()

                        if (
                            jenisDipilih.lowercase() !=
                            selectedSP.productName.lowercase()
                        ) {

                            tvJenisError.visibility =
                                View.VISIBLE

                            Toast.makeText(
                                this,
                                "Jenis kain tidak sesuai SP",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@setPositiveButton
                        }

                        if (panjangProduksi == null) {

                            Toast.makeText(
                                this,
                                "Panjang kain harus diisi",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@setPositiveButton
                        }

                        if (
                            panjangProduksi <
                            selectedSP.quantity
                        ) {

                            Toast.makeText(
                                this,
                                "Panjang kain kurang dari SP",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@setPositiveButton
                        }

                        if (
                            panjangProduksi >
                            selectedSP.quantity + 10
                        ) {

                            Toast.makeText(
                                this,
                                "Panjang kain melebihi batas toleransi",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@setPositiveButton
                        }

                        // UPDATE SP

                        selectedSP.quantity =
                            panjangProduksi

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
                    }
                    .setNegativeButton("BATAL", null)
                    .show()
            }
            .setNegativeButton("TUTUP", null)
            .show()
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
                                status = "Menunggu Inspeksi"
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
    // ==================== MENU KEPALA GUDANG ====================

    private fun buatSuratPerintahProduksi() {

        val inputView = layoutInflater.inflate(R.layout.dialog_surat_perintah, null)

        val tvSpId = inputView.findViewById<TextView>(R.id.tvSpId)
        val spJenisKain = inputView.findViewById<android.widget.Spinner>(R.id.spJenisKain)
        val etPanjangKain = inputView.findViewById<EditText>(R.id.etPanjangKain)
        val etDeadline = inputView.findViewById<EditText>(R.id.etDeadline)

        val spId = "SP${String.format("%03d", suratPerintah.size + 1)}"
        tvSpId.text = "ID Surat: $spId"

        val jenisKain = arrayOf(
            "Katun",
            "Denim",
            "Polyester"
        )

        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            jenisKain
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spJenisKain.adapter = adapter

        etDeadline.setOnClickListener {

            val calendar = Calendar.getInstance()

            android.app.DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->

                    val tanggal =
                        String.format(
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            dayOfMonth
                        )

                    etDeadline.setText(tanggal)

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Surat Perintah Produksi")
            .setView(inputView)
            .setPositiveButton("SIMPAN") { _, _ ->

                val panjangStr = etPanjangKain.text.toString()
                val deadline = etDeadline.text.toString()

                if (panjangStr.isEmpty() || deadline.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Semua data harus diisi",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val panjang = panjangStr.toInt()

                if (panjang < 100 || panjang > 300) {
                    Toast.makeText(
                        this,
                        "Panjang kain harus 100 - 300 meter",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val dataBaru = SuratPerintah(
                    id = spId,
                    productName = spJenisKain.selectedItem.toString(),
                    quantity = panjang,
                    unit = "Meter",
                    deadline = deadline,
                    status = "Menunggu Proses"
                )

                suratPerintah.add(dataBaru)

                Toast.makeText(
                    this,
                    "Surat Perintah $spId berhasil dibuat",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton("BATAL", null)
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
        MaterialAlertDialogBuilder(this)
            .setTitle("Laporan QC")
            .setMessage(
                "Menampilkan seluruh hasil produksi yang sudah diperiksa QC.\n\n" +
                        "Status:\n" +
                        "• Menunggu QC\n" +
                        "• Selesai QC\n\n" +
                        "Fitur detail QC akan dibuat setelah modul QC selesai."
            )
            .setPositiveButton("TUTUP", null)
            .show()
    }

    // ==================== MENU QUALITY CONTROL ====================

    private fun inspeksiKain() {
        val spList = dikirimKeQC.map {
            "${it.noQC} - ${it.productName}"
        }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("🔍 PILIH PRODUK UNTUK INSPEKSI")
            .setItems(spList) { _, which ->
                val selectedQC = dikirimKeQC[which]
                showInspeksiDialogQC(selectedQC)
            }
            .setNegativeButton("BATAL", null)
            .show()
    }
    private fun showInspeksiDialogQC(qc: KirimQC) {

        MaterialAlertDialogBuilder(this)
            .setTitle("Inspeksi Kain")
            .setMessage(
                "No QC : ${qc.noQC}\n" +
                        "Jenis Kain : ${qc.productName}\n" +
                        "Panjang : ${qc.quantity} Meter\n\n" +
                        "Data berhasil diterima QC."
            )
            .setPositiveButton("TUTUP", null)
            .show()
    }
    private fun showInspeksiDialog(sp: SuratPerintah) {
        val inputView = layoutInflater.inflate(R.layout.dialog_inspection, null)
        val etQuantity = inputView.findViewById<EditText>(R.id.etInspectQuantity)
        val etNotes = inputView.findViewById<EditText>(R.id.etInspectNotes)
        val rgResult = inputView.findViewById<android.widget.RadioGroup>(R.id.rgInspectResult)

        etQuantity.hint = "Jumlah yang diinspeksi (${sp.unit})"

        MaterialAlertDialogBuilder(this)
            .setTitle("🔍 INSPEKSI: ${sp.productName}")
            .setView(inputView)
            .setPositiveButton("SIMPAN") { _, _ ->
                val quantity = etQuantity.text.toString()
                val notes = etNotes.text.toString()
                val result = if (rgResult.checkedRadioButtonId == R.id.rbPass) "Lulus" else "Gagal"

                Toast.makeText(this, "✅ Inspeksi selesai: $result\nCatatan: ${notes.ifEmpty { "-" }}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("BATAL", null)
            .show()
    }

    private fun showLaporanQC() {
        var message = "📋 LAPORAN QC\n━━━━━━━━━━━━━━━━━━━━━━\n\n"
        var lulus = 0
        var gagal = 0

        for (qc in dikirimKeQC) {
            message += "🧵 ${qc.productName}\n"
            message += "   📏 Jumlah: ${qc.quantity} ${qc.unit}\n"
            message += "   🔢 No. QC: ${qc.noQC}\n"
            message += "   📊 Status: ${qc.status}\n\n"

            if (qc.status == "Menunggu Inspeksi") lulus++
            else gagal++
        }

        if (dikirimKeQC.isEmpty()) {
            message += "Belum ada data QC"
        } else {
            message += "━━━━━━━━━━━━━━━━━━━━━━\n"
            message += "✅ Menunggu Inspeksi: $lulus\n"
            message += "❌ Gagal: $gagal"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Laporan QC")
            .setMessage(message)
            .setPositiveButton("TUTUP", null)
            .show()
    }

    private fun showHasilQC() {
        var message = "HASIL QC\n\n"

        val lulus = dikirimKeQC.filter { it.status == "Lulus" }
        val gagal = dikirimKeQC.filter { it.status == "Gagal" }

        message += "KAIN LULUS: ${lulus.size}\n"
        message += "KAIN GAGAL: ${gagal.size}\n"

        MaterialAlertDialogBuilder(this)
            .setTitle("Hasil QC")
            .setMessage(message)
            .setPositiveButton("TUTUP", null)
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
        val status: String
    )
}
