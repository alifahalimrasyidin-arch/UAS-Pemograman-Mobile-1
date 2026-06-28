package com.example.uaspm1kelompok1

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uaspm1kelompok1.database.DatabaseHelper
import com.example.uaspm1kelompok1.database.DatabaseContract
class DetailProduksiActivity : AppCompatActivity() {

    private lateinit var tvSpId: TextView
    private lateinit var tvJenis: TextView
    private lateinit var tvJumlah: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvPetugas: TextView
    private lateinit var tvStatusQC: TextView
    private lateinit var tvCatatan: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_detail_produksi
        )

        supportActionBar?.hide()

        initView()
        dbHelper = DatabaseHelper(this)
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

        val spId = intent.getStringExtra("SP_ID") ?: return

        val cursor = dbHelper.getHasilProduksiBySpId(spId)

        if (!cursor.moveToFirst()) {

            cursor.close()

            Toast.makeText(
                this,
                "Data hasil produksi tidak ditemukan",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }

        tvSpId.text =
            cursor.getString(
                cursor.getColumnIndexOrThrow(
                    DatabaseContract.HasilProduksiTable.SP_ID
                )
            )

        tvJenis.text =
            cursor.getString(
                cursor.getColumnIndexOrThrow(
                    DatabaseContract.HasilProduksiTable.JENIS_KAIN
                )
            )

        tvJumlah.text =
            "${cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.HasilProduksiTable.JUMLAH))} ${
                cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.HasilProduksiTable.SATUAN))
            }"

        tvTanggal.text =
            cursor.getString(
                cursor.getColumnIndexOrThrow(
                    DatabaseContract.HasilProduksiTable.TANGGAL_PRODUKSI
                )
            )

        tvPetugas.text =
            cursor.getString(
                cursor.getColumnIndexOrThrow(
                    DatabaseContract.HasilProduksiTable.PETUGAS_PRODUKSI
                )
            )

        tvStatusQC.text =
            cursor.getString(
                cursor.getColumnIndexOrThrow(
                    DatabaseContract.HasilProduksiTable.STATUS_QC
                )
            )

        val catatan =
            cursor.getString(
                cursor.getColumnIndexOrThrow(
                    DatabaseContract.HasilProduksiTable.CATATAN
                )
            )

        tvCatatan.text =
            if (catatan.isBlank()) "-" else catatan

        cursor.close()
    }
}