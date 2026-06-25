package com.example.uaspm1kelompok1

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailProduksiActivity : AppCompatActivity() {

    private lateinit var tvSpId: TextView
    private lateinit var tvJenis: TextView
    private lateinit var tvJumlah: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvPetugas: TextView
    private lateinit var tvStatusQC: TextView
    private lateinit var tvCatatan: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_detail_produksi
        )

        supportActionBar?.hide()

        initView()

        loadData()
    }

    private fun initView() {

        tvSpId =
            findViewById(R.id.tvSpId)

        tvJenis =
            findViewById(R.id.tvJenis)

        tvJumlah =
            findViewById(R.id.tvJumlah)

        tvTanggal =
            findViewById(R.id.tvTanggal)

        tvPetugas =
            findViewById(R.id.tvPetugas)

        tvStatusQC =
            findViewById(R.id.tvStatusQC)

        tvCatatan =
            findViewById(R.id.tvCatatan)
    }

    private fun loadData() {

        val spId =
            intent.getStringExtra("SP_ID")

        val hasil =
            DashboardActivity
                .hasilProduksi
                .find {

                    it.spId == spId
                }

        if (hasil == null) {

            Toast.makeText(
                this,
                "Data hasil produksi tidak ditemukan",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }

        tvSpId.text =
            hasil.spId

        tvJenis.text =
            hasil.productName

        tvJumlah.text =
            "${hasil.quantity} ${hasil.unit}"

        tvTanggal.text =
            hasil.tanggalSelesai

        tvPetugas.text =
            hasil.petugasProduksi

        tvStatusQC.text =
            hasil.statusQC

        tvCatatan.text =
            if (hasil.notes.isBlank())
                "-"
            else
                hasil.notes
    }
}