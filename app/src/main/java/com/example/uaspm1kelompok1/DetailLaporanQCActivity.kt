package com.example.uaspm1kelompok1

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
class DetailLaporanQCActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_detail_laporan_qc
        )

        val noQC =
            intent.getStringExtra("NO_QC")

        val qc =
            DashboardActivity.dikirimKeQC.find {
                it.noQC == noQC
            }

        if (qc == null) {
            finish()
            return
        }

        findViewById<TextView>(
            R.id.tvNoQC
        ).text =
            "No QC : ${qc.noQC}"

        val tvGrade =
            findViewById<TextView>(R.id.tvGrade)

        tvGrade.text =
            "GRADE ${qc.grade}"

        when(qc.grade){

            "A" -> {
                tvGrade.setBackgroundResource(
                    R.drawable.bg_grade_a
                )
            }

            "B" -> {
                tvGrade.setBackgroundResource(
                    R.drawable.bg_grade_b
                )
            }

            "C" -> {
                tvGrade.setBackgroundResource(
                    R.drawable.bg_grade_c
                )
            }
        }

        findViewById<TextView>(
            R.id.tvSpId
        ).text =
            "SP ID : ${qc.spId}"

        findViewById<TextView>(
            R.id.tvJenisKain
        ).text =
            "Jenis Kain : ${qc.productName}"

        findViewById<TextView>(
            R.id.tvStatus
        ).text =
            "Status : ${qc.statusKain}"

        findViewById<TextView>(
            R.id.tvDetail
        ).text =
            "Petugas QC : ${qc.petugasQC}\n\n" +

            "Tanggal Kirim : ${qc.tanggalKirim}\n\n" +

                    "Tanggal QC : ${qc.tanggalQC}\n\n" +

                    "Uji Cuci : ${
                        if (qc.ujiCuci) "✓ Sudah"
                        else "✗ Belum"
                    }\n" +

                    "Uji Daya Tahan : ${
                        if (qc.ujiDayaTahan) "✓ Sudah"
                        else "✗ Belum"
                    }\n" +

                    "Uji Suhu Panas : ${
                        if (qc.ujiSuhuPanas) "✓ Sudah"
                        else "✗ Belum"
                    }\n\n" +

                    "Hasil Warna : ${qc.hasilWarna}\n" +
                    "Hasil Jahitan : ${qc.hasilJahitan}\n" +
                    "Hasil Ukuran : ${qc.hasilUkuran}\n\n" +

                    "Ukuran Akhir : ${qc.ukuranAkhir} Meter"

        findViewById<ImageView>(
            R.id.imgSebelum
        ).setImageBitmap(
            qc.fotoSebelumBitmap
        )

        findViewById<ImageView>(
            R.id.imgSesudah
        ).setImageBitmap(
            qc.fotoSesudahBitmap
        )

        findViewById<ImageView>(
            R.id.imgTambahan
        ).setImageBitmap(
            qc.fotoTambahanBitmap
        )
    }
}