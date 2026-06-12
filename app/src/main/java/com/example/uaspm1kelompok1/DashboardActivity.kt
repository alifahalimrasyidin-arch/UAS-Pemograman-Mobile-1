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
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserRole: TextView
    private lateinit var btnLogout: Button
    private lateinit var menuContainer: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private var userRole: String = ""

    // akses kamera
    private val barcodeLauncher =
        registerForActivityResult(
            ScanContract()
        ) { result ->
            if (result.contents != null) {
                Toast.makeText(
                    this,
                    "Barcode: ${result.contents}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    // Data Surat Perintah Produksi
    private val suratPerintah = mutableListOf(
        SuratPerintah("SP-001", "Kain Katun Premium", 1000, "meter", "2024-06-20", "Menunggu Produksi"),
        SuratPerintah("SP-002", "Kain Polyester", 800, "meter", "2024-06-22", "Sedang Produksi"),
        SuratPerintah("SP-003", "Kain Denim", 500, "meter", "2024-06-25", "Menunggu Produksi"),
        SuratPerintah("SP-004", "Kain Sutra", 300, "meter", "2024-06-28", "Selesai")
    )

    // Data Hasil Produksi
    private val hasilProduksi = mutableListOf(
        HasilProduksi("SP-001", "Kain Katun Premium", 1000, "meter", "2024-06-18", "Menunggu QC", "-"),
        HasilProduksi("SP-003", "Kain Denim", 500, "meter", "2024-06-20", "Menunggu QC", "-")
    )

    // Data yang sudah dikirim ke QC
    private val dikirimKeQC = mutableListOf(
        KirimQC("SP-001", "Kain Katun Premium", 1000, "meter", "2024-06-18", "QC-001", "Menunggu Inspeksi"),
        KirimQC("SP-003", "Kain Denim", 500, "meter", "2024-06-20", "QC-002", "Menunggu Inspeksi")
    )

    companion object {
        private const val PREFS_NAME = "TIASA_PREFS"
        private const val KEY_IS_LOGIN = "is_login"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ROLE = "user_role"
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
                MenuItem("📊 Dashboard Gudang") { showDashboardGudang() },
                MenuItem("📈 Laporan Stok") { showLaporanStok() },
                MenuItem("👥 Kelola Staff") { kelolaStaff() },
                MenuItem("🚚 Kelola Order Armada") { kelolaOrderArmada() },
                MenuItem("📦 Kelola Pengiriman") { kelolaPengiriman() },
                MenuItem("📊 Laporan Bulanan") { laporanBulanan() },
                MenuItem("✏️ Edit Data Laporan") { editLaporan() },
                MenuItem("📋 Monitoring Produksi") { monitoringProduksi() }
            )
            "quality_control" -> listOf(
                MenuItem("🔍 Inspeksi Kain") { inspeksiKain() },
                MenuItem("📷 Scan Barcode") { startBarcodeScanner() },
                MenuItem("📋 Laporan QC") { showLaporanQC() },
                MenuItem("✅ Kain Lulus QC") { showKainLulus() },
                MenuItem("❌ Kain Gagal QC") { showKainGagal() }
            )
            else -> listOf(
                MenuItem("📄 Kelola Surat Perintah") { manageSuratPerintah() },
                MenuItem("🏭 Kelola Produksi") { manageProduksi() },
                MenuItem("📊 Kelola Hasil Produksi") { manageHasilProduksi() }
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

    // ==================== MENU STAFF PRODUKSI ====================

    private fun manageSuratPerintah() {
        val options = arrayOf(
            "📝 Buat Surat Perintah Baru",
            "📋 Lihat Semua Surat Perintah",
            "🔄 Update Status Surat Perintah"
        )

        MaterialAlertDialogBuilder(this)
            .setTitle("📄 KELOLA SURAT PERINTAH")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> buatSuratPerintah()
                    1 -> lihatSemuaSuratPerintah()
                    2 -> updateStatusSuratPerintah()
                }
            }
            .setNegativeButton("TUTUP", null)
            .show()
    }

    private fun kelolaOrderArmada() {
        MaterialAlertDialogBuilder(this)
            .setTitle("🚚 Kelola Order Armada")
            .setMessage(
                "Armada:\n\n" +
                        "• Truk A\n" +
                        "• Truk B\n" +
                        "• Truk C"
            )
            .setPositiveButton("TUTUP", null)
            .show()
    }

    private fun kelolaPengiriman() {
        MaterialAlertDialogBuilder(this)
            .setTitle("📦 Kelola Pengiriman")
            .setMessage(
                "Status Pengiriman:\n\n" +
                        "• Pesanan #001 - Dikirim\n" +
                        "• Pesanan #002 - Dalam Perjalanan"
            )
            .setPositiveButton("TUTUP", null)
            .show()
    }

    private fun laporanBulanan() {
        MaterialAlertDialogBuilder(this)
            .setTitle("📊 Laporan Bulanan")
            .setMessage(
                "Produksi Bulan Ini\n\n" +
                        "Total Produksi : 5000 Meter\n" +
                        "Total Pengiriman : 4800 Meter"
            )
            .setPositiveButton("TUTUP", null)
            .show()
    }

    private fun editLaporan() {
        Toast.makeText(
            this,
            "Fitur Edit Data Laporan",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun buatSuratPerintah() {
        val inputView = layoutInflater.inflate(R.layout.dialog_surat_perintah, null)
        val etProductName = inputView.findViewById<EditText>(R.id.etProductName)
        val etQuantity = inputView.findViewById<EditText>(R.id.etQuantity)
        val etUnit = inputView.findViewById<EditText>(R.id.etUnit)
        val etDeadline = inputView.findViewById<EditText>(R.id.etDeadline)

        MaterialAlertDialogBuilder(this)
            .setTitle("BUAT SURAT PERINTAH")
            .setView(inputView)
            .setPositiveButton("SIMPAN") { _, _ ->
                val productName = etProductName.text.toString()
                val quantityStr = etQuantity.text.toString()
                val unit = etUnit.text.toString()
                val deadline = etDeadline.text.toString()

                if (productName.isNotEmpty() && quantityStr.isNotEmpty()) {
                    val newSP = SuratPerintah(
                        id = "SP-${String.format(Locale.US, "%03d", suratPerintah.size + 1)}",
                        productName = productName,
                        quantity = quantityStr.toIntOrNull() ?: 0,
                        unit = unit.ifEmpty { "meter" },
                        deadline = deadline,
                        status = "Menunggu Produksi"
                    )
                    suratPerintah.add(newSP)
                    Toast.makeText(this, "✅ Surat Perintah ${newSP.id} berhasil dibuat", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "❌ Nama produk dan jumlah harus diisi", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("BATAL", null)
            .show()
    }

    private fun lihatSemuaSuratPerintah() {
        if (suratPerintah.isEmpty()) {
            Toast.makeText(this, "Belum ada surat perintah", Toast.LENGTH_SHORT).show()
            return
        }

        var message = "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
        for (sp in suratPerintah) {
            val statusIcon = when (sp.status) {
                "Selesai" -> "✅"
                "Sedang Produksi" -> "🏭"
                "Menunggu Produksi" -> "⏳"
                else -> "📋"
            }
            message += "$statusIcon ${sp.id}\n"
            message += "   🧵 ${sp.productName}\n"
            message += "   📏 ${sp.quantity} ${sp.unit}\n"
            message += "   📅 ${sp.deadline}\n"
            message += "   📊 ${sp.status}\n"
            message += "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("📋 DAFTAR SURAT PERINTAH")
            .setMessage(message)
            .setPositiveButton("TUTUP", null)
            .show()
    }

    private fun updateStatusSuratPerintah() {
        if (suratPerintah.isEmpty()) {
            Toast.makeText(this, "Belum ada surat perintah", Toast.LENGTH_SHORT).show()
            return
        }

        val spList = suratPerintah.map { "${it.id} - ${it.productName} (${it.status})" }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("🔄 UPDATE STATUS SURAT PERINTAH")
            .setItems(spList) { _, which ->
                val selectedSP = suratPerintah[which]
                showStatusUpdateDialog(selectedSP)
            }
            .setNegativeButton("BATAL", null)
            .show()
    }

    private fun showStatusUpdateDialog(sp: SuratPerintah) {
        val statuses = arrayOf("⏳ Menunggu Produksi", "🏭 Sedang Produksi", "✅ Selesai")

        MaterialAlertDialogBuilder(this)
            .setTitle("🔄 UPDATE STATUS")
            .setMessage("SP: ${sp.id}\nProduk: ${sp.productName}\n\nStatus saat ini: ${sp.status}")
            .setItems(statuses) { _, which ->
                sp.status = when (which) {
                    0 -> "Menunggu Produksi"
                    1 -> "Sedang Produksi"
                    2 -> "Selesai"
                    else -> sp.status
                }
                Toast.makeText(this, "✅ Status ${sp.id} diupdate menjadi ${sp.status}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("BATAL", null)
            .show()
    }

    private fun manageProduksi() {
        val options = arrayOf("Mulai Produksi", "Laporkan Hasil Produksi Harian", "Monitoring Produksi")

        MaterialAlertDialogBuilder(this)
            .setTitle("🏭 KELOLA PRODUKSI")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> mulaiProduksi()
                    1 -> laporHasilProduksiHarian()
                    2 -> monitoringProduksi()
                }
            }
            .setNegativeButton("TUTUP", null)
            .show()
    }

    private fun mulaiProduksi() {
        val spMenunggu = suratPerintah.filter { it.status == "Menunggu Produksi" }
        if (spMenunggu.isEmpty()) {
            Toast.makeText(this, "Tidak ada surat perintah yang menunggu produksi", Toast.LENGTH_SHORT).show()
            return
        }

        val spList = spMenunggu.map { "${it.id} - ${it.productName} (${it.quantity} ${it.unit})" }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("🚀 PILIH SURAT PERINTAH")
            .setItems(spList) { _, which ->
                val selectedSP = spMenunggu[which]
                selectedSP.status = "Sedang Produksi"
                Toast.makeText(this, "✅ Produksi ${selectedSP.id} dimulai", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("BATAL", null)
            .show()
    }

    private fun laporHasilProduksiHarian() {
        val inputView = layoutInflater.inflate(R.layout.dialog_lapor_produksi, null)
        val etSPId = inputView.findViewById<EditText>(R.id.etSPId)
        val etQuantity = inputView.findViewById<EditText>(R.id.etQuantity)
        val etNotes = inputView.findViewById<EditText>(R.id.etNotes)

        MaterialAlertDialogBuilder(this)
            .setTitle("📝 LAPOR HASIL PRODUKSI HARIAN")
            .setView(inputView)
            .setPositiveButton("LAPORKAN") { _, _ ->
                val spId = etSPId.text.toString()
                val quantity = etQuantity.text.toString()
                val notes = etNotes.text.toString()

                val sp = suratPerintah.find { it.id.equals(spId, ignoreCase = true) }
                if (sp != null && quantity.isNotEmpty()) {
                    Toast.makeText(this, "✅ Laporan untuk $spId: $quantity ${sp.unit}\nCatatan: ${notes.ifEmpty { "-" }}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "❌ SP ID tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("BATAL", null)
            .show()
    }

    private fun monitoringProduksi() {
        val spProduksi = suratPerintah.filter { it.status == "Sedang Produksi" }
        if (spProduksi.isEmpty()) {
            Toast.makeText(this, "Tidak ada produksi yang sedang berjalan", Toast.LENGTH_SHORT).show()
            return
        }

        var message = "🏭 MONITORING PRODUKSI\n━━━━━━━━━━━━━━━━━━━━━━\n\n"
        for (sp in spProduksi) {
            message += "📄 ${sp.id} - ${sp.productName}\n"
            message += "   🎯 Target: ${sp.quantity} ${sp.unit}\n"
            message += "   📅 Deadline: ${sp.deadline}\n\n"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("📊 MONITORING PRODUKSI")
            .setMessage(message)
            .setPositiveButton("REFRESH", null)
            .setNegativeButton("TUTUP", null)
            .show()
    }

    private fun manageHasilProduksi() {
        val options = arrayOf("📦 Lihat Hasil Produksi Selesai", "📤 Kirim ke QC")

        MaterialAlertDialogBuilder(this)
            .setTitle("📊 KELOLA HASIL PRODUKSI")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> lihatHasilProduksiSelesai()
                    1 -> kirimKeQC()
                }
            }
            .setNegativeButton("TUTUP", null)
            .show()
    }

    private fun lihatHasilProduksiSelesai() {
        val spSelesai = suratPerintah.filter { it.status == "Selesai" }
        if (spSelesai.isEmpty()) {
            Toast.makeText(this, "Belum ada produksi yang selesai", Toast.LENGTH_SHORT).show()
            return
        }

        val spList = spSelesai.map { "${it.id} - ${it.productName} (${it.quantity} ${it.unit})" }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("📦 HASIL PRODUKSI SELESAI")
            .setItems(spList) { _, which ->
                val selectedSP = spSelesai[which]
                showHasilProduksiDetail(selectedSP)
            }
            .setNegativeButton("TUTUP", null)
            .show()
    }

    private fun showHasilProduksiDetail(sp: SuratPerintah) {
        val existingResult = hasilProduksi.find { it.spId == sp.id }

        val message = if (existingResult != null) {
            "✅ Produksi telah dicatat\n\n" +
                    "🆔 SP ID: ${sp.id}\n" +
                    "🧵 Produk: ${sp.productName}\n" +
                    "📏 Jumlah: ${existingResult.quantity} ${sp.unit}\n" +
                    "📅 Tanggal Selesai: ${existingResult.tanggalSelesai}\n" +
                    "📊 Status QC: ${existingResult.statusQC}"
        } else {
            "📦 Hasil Produksi:\n\n" +
                    "🆔 SP ID: ${sp.id}\n" +
                    "🧵 Produk: ${sp.productName}\n" +
                    "🎯 Target: ${sp.quantity} ${sp.unit}\n\n" +
                    "Apakah Anda ingin mencatat hasil produksi ini?"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("📊 DETAIL HASIL PRODUKSI")
            .setMessage(message)
            .setPositiveButton("📝 CATAT HASIL") { _, _ ->
                catatHasilProduksi(sp)
            }
            .setNeutralButton("📤 KIRIM KE QC") { _, _ ->
                if (existingResult != null) {
                    confirmKirimKeQC(existingResult)
                } else {
                    Toast.makeText(this, "Catat hasil produksi terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("TUTUP", null)
            .show()
    }

    private fun catatHasilProduksi(sp: SuratPerintah) {
        val inputView = layoutInflater.inflate(R.layout.dialog_catat_produksi, null)
        val etQuantity = inputView.findViewById<EditText>(R.id.etQuantity)
        val etNotes = inputView.findViewById<EditText>(R.id.etNotes)

        etQuantity.setText(sp.quantity.toString())
        etQuantity.hint = "Jumlah yang diproduksi (${sp.unit})"

        MaterialAlertDialogBuilder(this)
            .setTitle("📝 CATAT HASIL PRODUKSI: ${sp.id}")
            .setView(inputView)
            .setPositiveButton("SIMPAN") { _, _ ->
                val quantityStr = etQuantity.text.toString()
                val notes = etNotes.text.toString()

                if (quantityStr.isNotEmpty()) {
                    val existing = hasilProduksi.find { it.spId == sp.id }
                    if (existing == null) {
                        hasilProduksi.add(
                            HasilProduksi(
                                spId = sp.id,
                                productName = sp.productName,
                                quantity = quantityStr.toIntOrNull() ?: 0,
                                unit = sp.unit,
                                tanggalSelesai = getCurrentDate(),
                                statusQC = "Menunggu QC",
                                notes = notes
                            )
                        )
                        Toast.makeText(this, "✅ Hasil produksi untuk ${sp.id} berhasil dicatat", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Hasil produksi sudah dicatat sebelumnya", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "❌ Jumlah harus diisi", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("BATAL", null)
            .show()
    }

    private fun kirimKeQC() {
        val menungguQC = hasilProduksi.filter { it.statusQC == "Menunggu QC" }
        if (menungguQC.isEmpty()) {
            Toast.makeText(this, "Tidak ada hasil produksi yang menunggu dikirim ke QC", Toast.LENGTH_SHORT).show()
            return
        }

        val qcList = menungguQC.map { "${it.spId} - ${it.productName} (${it.quantity} ${it.unit})" }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("📤 KIRIM KE QC")
            .setItems(qcList) { _, which ->
                val selected = menungguQC[which]
                confirmKirimKeQC(selected)
            }
            .setNegativeButton("BATAL", null)
            .show()
    }

    private fun confirmKirimKeQC(hasil: HasilProduksi) {
        MaterialAlertDialogBuilder(this)
            .setTitle("📤 KONFIRMASI KIRIM KE QC")
            .setMessage("Kirim ${hasil.productName} (${hasil.quantity} ${hasil.unit}) ke Quality Control?")
            .setPositiveButton("✅ YA, KIRIM") { _, _ ->
                hasil.statusQC = "Dikirim ke QC"

                dikirimKeQC.add(
                    KirimQC(
                        spId = hasil.spId,
                        productName = hasil.productName,
                        quantity = hasil.quantity,
                        unit = hasil.unit,
                        tanggalKirim = getCurrentDate(),
                        noQC = "QC-${String.format(Locale.US, "%03d", dikirimKeQC.size + 1)}",
                        status = "Menunggu Inspeksi"
                    )
                )

                Toast.makeText(this, "✅ ${hasil.productName} berhasil dikirim ke QC", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("❌ BATAL", null)
            .show()
    }

    // ==================== MENU KEPALA GUDANG ====================

    private fun showDashboardGudang() {
        val totalSP = suratPerintah.size
        val selesai = suratPerintah.count { it.status == "Selesai" }
        val produksiBerjalan = suratPerintah.count { it.status == "Sedang Produksi" }
        val totalHasil = hasilProduksi.sumOf { it.quantity }

        val message = """
            📊 DASHBOARD GUDANG
            
            📦 TOTAL SURAT PERINTAH: $totalSP
            ✅ SELESAI: $selesai
            🏭 SEDANG PRODUKSI: $produksiBerjalan
            📏 TOTAL HASIL PRODUKSI: $totalHasil meter
            📤 DIKIRIM KE QC: ${dikirimKeQC.size}
        """.trimIndent()

        MaterialAlertDialogBuilder(this)
            .setTitle("Dashboard Gudang")
            .setMessage(message)
            .setPositiveButton("TUTUP", null)
            .show()
    }

    private fun showLaporanStok() {
        var message = "📈 LAPORAN STOK KAIN\n━━━━━━━━━━━━━━━━━━━━━━\n\n"
        for (sp in suratPerintah) {
            message += "🧵 ${sp.productName}\n"
            message += "   📏 Target: ${sp.quantity} ${sp.unit}\n"
            message += "   📊 Status: ${sp.status}\n\n"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Laporan Stok")
            .setMessage(message)
            .setPositiveButton("TUTUP", null)
            .show()
    }

    private fun kelolaStaff() {
        MaterialAlertDialogBuilder(this)
            .setTitle("👥 Kelola Staff")
            .setMessage("Fitur kelola staff akan segera hadir.\n\nStaff saat ini:\n• Staff Produksi\n• Quality Control\n• Logistik")
            .setPositiveButton("TUTUP", null)
            .show()
    }

    // ==================== MENU QUALITY CONTROL ====================

    private fun inspeksiKain() {
        val spList = suratPerintah.map { "${it.id} - ${it.productName} (${it.quantity} ${it.unit})" }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("🔍 PILIH PRODUK UNTUK INSPEKSI")
            .setItems(spList) { _, which ->
                val selectedSP = suratPerintah[which]
                showInspeksiDialog(selectedSP)
            }
            .setNegativeButton("BATAL", null)
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

    private fun showKainLulus() {
        val lulus = dikirimKeQC.filter { it.status == "Menunggu Inspeksi" }
        if (lulus.isEmpty()) {
            Toast.makeText(this, "Belum ada kain yang lulus QC", Toast.LENGTH_SHORT).show()
            return
        }

        var message = "✅ KAIN LULUS QC\n━━━━━━━━━━━━━━━━━━━━━━\n\n"
        for (item in lulus) {
            message += "🧵 ${item.productName}\n"
            message += "   📏 ${item.quantity} ${item.unit}\n"
            message += "   🔢 ${item.noQC}\n\n"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle("Kain Lulus QC")
            .setMessage(message)
            .setPositiveButton("TUTUP", null)
            .show()
    }

    private fun showKainGagal() {
        MaterialAlertDialogBuilder(this)
            .setTitle("❌ Kain Gagal QC")
            .setMessage("Belum ada data kain yang gagal QC.\n\nSemua produk yang diinspeksi dinyatakan lulus.")
            .setPositiveButton("TUTUP", null)
            .show()
    }

    private fun startBarcodeScanner() {
        val options = ScanOptions()
        options.setPrompt("Arahkan kamera ke barcode")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        barcodeLauncher.launch(options)
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
