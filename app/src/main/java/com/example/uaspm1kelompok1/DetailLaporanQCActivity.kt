package com.example.uaspm1kelompok1

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import com.example.uaspm1kelompok1.database.DatabaseHelper
import com.example.uaspm1kelompok1.database.DatabaseContract
import android.graphics.BitmapFactory
class DetailLaporanQCActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_detail_laporan_qc
        )
        dbHelper = DatabaseHelper(this)
        val noQC = intent.getStringExtra("NO_QC") ?: return

        val cursor = dbHelper.getQualityControlByNoQC(noQC)

        if (!cursor.moveToFirst()) {
            cursor.close()
            finish()
            return
        }
        val grade =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.GRADE))
        val spId =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.SP_ID))
        val jenisKain =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.JENIS_KAIN))
        val status =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.STATUS_KAIN))
        val petugasQC =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.PETUGAS_QC))
        val tanggalKirim =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.TANGGAL_KIRIM))
        val tanggalQC =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.TANGGAL_QC))
        val ujiCuci =
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.UJI_CUCI)) == 1
        val ujiDayaTahan =
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.UJI_DAYA_TAHAN)) == 1
        val ujiSuhu =
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.UJI_SUHU)) == 1
        val hasilWarna =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.HASIL_WARNA))
        val hasilJahitan =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.HASIL_JAHITAN))
        val hasilUkuran =
            cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.HASIL_UKURAN))
        val ukuranAkhir =
            cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseContract.QualityControlTable.UKURAN_AKHIR))
        findViewById<TextView>(
            R.id.tvNoQC
        ).text =
            "No QC : ${noQC}"

        val tvGrade =
            findViewById<TextView>(R.id.tvGrade)

        tvGrade.text =
            "GRADE ${grade}"

        when (grade) {

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
            "SP ID : ${spId}"

        findViewById<TextView>(
            R.id.tvJenisKain
        ).text =
            "Jenis Kain : ${jenisKain}"

        findViewById<TextView>(
            R.id.tvStatus
        ).text =
            "Status : ${status}"

        findViewById<TextView>(
            R.id.tvDetail
        ).text =
            "Petugas QC : ${petugasQC}\n\n" +

                    "Tanggal Kirim : ${tanggalKirim}\n\n" +

                    "Tanggal QC : ${tanggalQC}\n\n" +

                    "Uji Cuci : ${
                        if (ujiCuci) "✓ Sudah"
                        else "✗ Belum"
                    }\n" +

                    "Uji Daya Tahan : ${
                        if (ujiDayaTahan) "✓ Sudah"
                        else "✗ Belum"
                    }\n" +

                    "Uji Suhu Panas : ${
                        if (ujiSuhu) "✓ Sudah"
                        else "✗ Belum"
                    }\n\n" +

                    "Hasil Warna : ${hasilWarna}\n" +
                    "Hasil Jahitan : ${hasilJahitan}\n" +
                    "Hasil Ukuran : ${hasilUkuran}\n\n" +

                    "Ukuran Akhir : ${ukuranAkhir} Meter"
        cursor.getBlob(
            cursor.getColumnIndexOrThrow(
                DatabaseContract.QualityControlTable.FOTO_SEBELUM
            )
        )?.let {

            findViewById<ImageView>(
                R.id.imgSebelum
            ).setImageBitmap(
                BitmapFactory.decodeByteArray(
                    it,
                    0,
                    it.size
                )
            )
        }

        cursor.getBlob(
            cursor.getColumnIndexOrThrow(
                DatabaseContract.QualityControlTable.FOTO_SESUDAH
            )
        )?.let {

            findViewById<ImageView>(
                R.id.imgSesudah
            ).setImageBitmap(
                BitmapFactory.decodeByteArray(
                    it,
                    0,
                    it.size
                )
            )
        }

        cursor.getBlob(
            cursor.getColumnIndexOrThrow(
                DatabaseContract.QualityControlTable.FOTO_TAMBAHAN
            )
        )?.let {

            findViewById<ImageView>(
                R.id.imgTambahan
            ).setImageBitmap(
                BitmapFactory.decodeByteArray(
                    it,
                    0,
                    it.size
                )
            )
        }

        cursor.close()
    }
}