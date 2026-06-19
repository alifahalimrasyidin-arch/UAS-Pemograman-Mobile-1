package com.example.uaspm1kelompok1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import com.example.uaspm1kelompok1.DashboardActivity.Companion.suratPerintah
import com.example.uaspm1kelompok1.DashboardActivity.SuratPerintah

class SuratPerintahFragment : Fragment() {

    private lateinit var rvSuratPerintah: RecyclerView
    private lateinit var btnSemua: Button
    private lateinit var btnMingguIni: Button
    private lateinit var tvJumlahData: TextView
    private lateinit var fabTambahSP: FloatingActionButton

    private lateinit var adapter: SPAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_surat_perintah,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        rvSuratPerintah =
            view.findViewById(R.id.rvSuratPerintah)

        btnSemua =
            view.findViewById(R.id.btnSemua)

        btnMingguIni =
            view.findViewById(R.id.btnMingguIni)

        tvJumlahData =
            view.findViewById(R.id.tvJumlahData)

        fabTambahSP =
            view.findViewById(R.id.fabTambahSP)

        rvSuratPerintah.layoutManager =
            LinearLayoutManager(requireContext())

        adapter =
            SPAdapter(
                DashboardActivity.suratPerintah
            ) { sp ->

                tampilDetailSP(sp)
            }

        rvSuratPerintah.adapter =
            adapter

        updateJumlahData(
            DashboardActivity.suratPerintah.size
        )

        btnSemua.setOnClickListener {

            adapter.updateData(
                DashboardActivity.suratPerintah
            )

            updateJumlahData(
                DashboardActivity.suratPerintah.size
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
                DashboardActivity.suratPerintah.filter {

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

            updateJumlahData(
                filtered.size
            )
        }

        fabTambahSP.setOnClickListener {

            buatSuratPerintahProduksi()
        }
    }

    private fun updateJumlahData(
        jumlah: Int
    ) {
        tvJumlahData.text =
            "Total SP : $jumlah"
    }

    private fun tampilDetailSP(
        sp: DashboardActivity.SuratPerintah
    ) {

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
        ).text =
            "${sp.quantity} Meter"

        detailView.findViewById<TextView>(
            R.id.tvDetailDeadline
        ).text =
            sp.deadline

        detailView.findViewById<TextView>(
            R.id.tvDetailStatus
        ).text =
            sp.status

        MaterialAlertDialogBuilder(
            requireContext()
        )
            .setView(detailView)
            .setPositiveButton(
                "TUTUP",
                null
            )
            .show()
    }

    private fun buatSuratPerintahProduksi() {

        val inputView =
            layoutInflater.inflate(
                R.layout.dialog_surat_perintah,
                null
            )

        val tvSpId =
            inputView.findViewById<TextView>(
                R.id.tvSpId
            )

        val spJenisKain =
            inputView.findViewById<Spinner>(
                R.id.spJenisKain
            )

        val etPanjangKain =
            inputView.findViewById<EditText>(
                R.id.etPanjangKain
            )

        val etDeadline =
            inputView.findViewById<EditText>(
                R.id.etDeadline
            )

        val tvPanjangError =
            inputView.findViewById<TextView>(
                R.id.tvPanjangError
            )

        val spId =
            "SP${
                String.format(
                    "%03d",
                    DashboardActivity.suratPerintah.size + 1
                )
            }"

        tvSpId.text =
            "ID Surat : $spId"

        val jenisKain =
            arrayOf(
                "Katun",
                "Denim",
                "Polyester"
            )

        val adapterSpinner =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                jenisKain
            )

        adapterSpinner.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spJenisKain.adapter =
            adapterSpinner

        etDeadline.setOnClickListener {

            val calendar =
                Calendar.getInstance()

            DatePickerDialog(
                requireContext(),
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
                requireContext(),
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
            MaterialAlertDialogBuilder(requireContext())
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
                    MaterialAlertDialogBuilder(requireContext())
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

                            adapter.updateData(
                                DashboardActivity.suratPerintah
                            )

                            updateJumlahData(
                                DashboardActivity.suratPerintah.size
                            )
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
                                MaterialAlertDialogBuilder(requireContext())
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
}