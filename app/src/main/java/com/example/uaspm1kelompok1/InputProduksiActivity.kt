package com.example.uaspm1kelompok1

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import com.example.uaspm1kelompok1.database.DatabaseHelper
import com.example.uaspm1kelompok1.database.SessionManager
class InputProduksiActivity : AppCompatActivity() {

    private lateinit var tvSpId: TextView
    private lateinit var tvJenisKainInfo: TextView
    private lateinit var tvPanjangInfo: TextView
    private lateinit var tvDeadlineInfo: TextView

    private lateinit var etPanjangProduksi: EditText
    private lateinit var etCatatan: EditText

    private lateinit var tvPanjangError: TextView

    private lateinit var btnSimpanProduksi: Button
    private lateinit var dbHelper: DatabaseHelper
    private var selectedSP:
            DashboardActivity.SuratPerintah? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_input_produksi
        )

        supportActionBar?.hide()
        dbHelper=DatabaseHelper(this)
        initView()

        loadDataSP()

        setupRealtimeValidation()

        btnSimpanProduksi.setOnClickListener {

            simpanProduksi()
        }
    }

    private fun initView() {

        tvSpId =
            findViewById(R.id.tvSpId)

        tvJenisKainInfo =
            findViewById(R.id.tvJenisKainInfo)

        tvPanjangInfo =
            findViewById(R.id.tvPanjangInfo)

        tvDeadlineInfo =
            findViewById(R.id.tvDeadlineInfo)

        etPanjangProduksi =
            findViewById(R.id.etPanjangProduksi)

        etCatatan =
            findViewById(R.id.etCatatan)

        tvPanjangError =
            findViewById(R.id.tvPanjangError)

        btnSimpanProduksi =
            findViewById(R.id.btnSimpanProduksi)
    }

    private fun loadDataSP() {

        val idSP=intent.getStringExtra("ID_SP")?:return

        val cursor=dbHelper.getSuratPerintahById(idSP)

        if(cursor.moveToFirst()){

            selectedSP=DashboardActivity.SuratPerintah(
                id=cursor.getString(cursor.getColumnIndexOrThrow("id")),
                productName=cursor.getString(cursor.getColumnIndexOrThrow("jenis_kain")),
                quantity=cursor.getInt(cursor.getColumnIndexOrThrow("jumlah")),
                unit=cursor.getString(cursor.getColumnIndexOrThrow("satuan")),
                deadline=cursor.getString(cursor.getColumnIndexOrThrow("deadline")),
                status=cursor.getString(cursor.getColumnIndexOrThrow("status"))
            )

            tvSpId.text=selectedSP!!.id
            tvJenisKainInfo.text=selectedSP!!.productName
            tvPanjangInfo.text="${selectedSP!!.quantity} Meter"
            tvDeadlineInfo.text=selectedSP!!.deadline

        }else{

            Toast.makeText(this,"Data SP tidak ditemukan",Toast.LENGTH_SHORT).show()
            finish()
        }

        cursor.close()
    }

    private fun setupRealtimeValidation() {

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

                    if (selectedSP == null) return

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

                        input < selectedSP!!.quantity -> {

                            tvPanjangError.visibility =
                                View.VISIBLE

                            tvPanjangError.text =
                                "Panjang produksi kurang dari ketentuan SP"

                            etPanjangProduksi.setBackgroundResource(
                                R.drawable.bg_error
                            )
                        }

                        input > selectedSP!!.quantity + 10 -> {

                            tvPanjangError.visibility =
                                View.VISIBLE

                            tvPanjangError.text =
                                "Panjang produksi melebihi toleransi 10 meter"

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
                ) {
                }
            }
        )
    }

    private fun simpanProduksi() {

        val panjangText =
            etPanjangProduksi.text
                .toString()
                .trim()

        if (panjangText.isEmpty()) {

            tvPanjangError.visibility =
                View.VISIBLE

            tvPanjangError.text =
                "Panjang produksi wajib diisi"

            etPanjangProduksi.setBackgroundResource(
                R.drawable.bg_error
            )

            return
        }

        val panjangProduksi =
            panjangText.toInt()

        if (panjangProduksi < selectedSP!!.quantity) {

            tvPanjangError.visibility =
                View.VISIBLE

            tvPanjangError.text =
                "Panjang produksi tidak boleh kurang dari SP"

            etPanjangProduksi.setBackgroundResource(
                R.drawable.bg_error
            )

            return
        }

        if (panjangProduksi > selectedSP!!.quantity + 10) {

            tvPanjangError.visibility =
                View.VISIBLE

            tvPanjangError.text =
                "Maksimal kelebihan 10 meter"

            etPanjangProduksi.setBackgroundResource(
                R.drawable.bg_error
            )

            return
        }

        tvPanjangError.visibility =
            View.GONE

        etPanjangProduksi.setBackgroundResource(
            R.drawable.bg_edittext
        )

        val sessionManager = SessionManager(this)

        val petugasProduksi =
            sessionManager.getNama()

        val berhasil=dbHelper.insertHasilProduksi(
            selectedSP!!.id,
            selectedSP!!.productName,
            panjangProduksi,
            "Meter",
            getCurrentDate(),
            petugasProduksi,
            "Belum Kirim QC",
            etCatatan.text.toString()
        )

        if(!berhasil){
            Toast.makeText(this,"Gagal menyimpan hasil produksi",Toast.LENGTH_LONG).show()
            return
        }



        selectedSP!!.status="Selesai Produksi"
        dbHelper.updateStatusSuratPerintah(
            selectedSP!!.id,
            "Selesai Produksi"
        )

        ProduksiFragment.refreshData =
            true

        Toast.makeText(
            this,
            "Produksi berhasil disimpan",
            Toast.LENGTH_LONG
        ).show()

        finish()
    }

    private fun getCurrentDate(): String {

        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())
    }
}