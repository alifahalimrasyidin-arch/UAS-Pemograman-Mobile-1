package com.example.uaspm1kelompok1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import android.text.Editable
import android.text.TextWatcher
class LaporanQCFragment : Fragment() {

    private lateinit var rvLaporanQC: RecyclerView
    private lateinit var etCariQC: EditText

    private lateinit var btnCariQC: Button
    private lateinit var btnSemuaLaporan: Button
    private lateinit var btnMingguIniLaporan: Button
    private lateinit var btnBulanIniLaporan: Button

    private lateinit var tvJumlahLaporanQC: TextView

    private lateinit var adapter: LaporanQCAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_laporan_qc,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        rvLaporanQC =
            view.findViewById(R.id.rvLaporanQC)

        etCariQC =
            view.findViewById(R.id.etCariQC)

        btnCariQC =
            view.findViewById(R.id.btnCariQC)

        btnSemuaLaporan =
            view.findViewById(R.id.btnSemuaLaporan)

        btnMingguIniLaporan =
            view.findViewById(R.id.btnMingguIniLaporan)

        btnBulanIniLaporan =
            view.findViewById(R.id.btnBulanIniLaporan)

        tvJumlahLaporanQC =
            view.findViewById(R.id.tvJumlahLaporanQC)

        rvLaporanQC.layoutManager =
            LinearLayoutManager(requireContext())

        val dataQC =
            DashboardActivity.dikirimKeQC.filter {
                it.statusKain == "Selesai QC"
            }

        adapter =
            LaporanQCAdapter(dataQC) { qc ->

                val intent =
                    android.content.Intent(
                        requireContext(),
                        DetailLaporanQCActivity::class.java
                    )

                intent.putExtra(
                    "NO_QC",
                    qc.noQC
                )

                startActivity(intent)
            }

        rvLaporanQC.adapter =
            adapter
        etCariQC.addTextChangedListener(
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

                    val keyword =
                        s.toString().trim()

                    val hasilCari =
                        DashboardActivity.dikirimKeQC.filter {

                            it.statusKain == "Selesai QC" &&

                                    (
                                            it.noQC.contains(
                                                keyword,
                                                ignoreCase = true
                                            ) ||

                                                    it.spId.contains(
                                                        keyword,
                                                        ignoreCase = true
                                                    )
                                            )
                        }

                    adapter.updateData(
                        hasilCari
                    )

                    updateJumlahData(
                        hasilCari.size
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {}
            }
        )
        updateJumlahData(dataQC.size)

        btnSemuaLaporan.setOnClickListener {

            val semuaData =
                DashboardActivity.dikirimKeQC.filter {
                    it.statusKain == "Selesai QC"
                }

            adapter.updateData(semuaData)

            updateJumlahData(
                semuaData.size
            )
        }

        btnCariQC.setOnClickListener {

            val keyword =
                etCariQC.text
                    .toString()
                    .trim()

            val hasilCari =
                DashboardActivity.dikirimKeQC.filter {

                    it.statusKain == "Selesai QC" &&

                            (
                                    it.noQC.contains(
                                        keyword,
                                        ignoreCase = true
                                    ) ||

                                            it.spId.contains(
                                                keyword,
                                                ignoreCase = true
                                            )
                                    )
                }

            if (hasilCari.isEmpty()) {

                MaterialAlertDialogBuilder(
                    requireContext()
                )
                    .setTitle("Data Tidak Ditemukan")
                    .setMessage(
                        "ID QC atau ID SP yang dicari tidak ditemukan."
                    )
                    .setPositiveButton(
                        "TUTUP",
                        null
                    )
                    .show()
            }

            adapter.updateData(
                hasilCari
            )

            updateJumlahData(
                hasilCari.size
            )
        }

        btnMingguIniLaporan.setOnClickListener {

            filterHari(7)
        }

        btnBulanIniLaporan.setOnClickListener {

            filterHari(30)
        }

    }

    private fun filterHari(
        jumlahHari: Int
    ) {

        val sdf =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        val batasTanggal =
            Calendar.getInstance().apply {

                add(
                    Calendar.DAY_OF_YEAR,
                    -jumlahHari
                )

            }.time

        val filtered =
            DashboardActivity.dikirimKeQC.filter {

                it.statusKain == "Selesai QC" &&

                        try {

                            val tanggalQC =
                                sdf.parse(
                                    it.tanggalQC
                                )

                            tanggalQC != null &&
                                    tanggalQC.after(
                                        batasTanggal
                                    )

                        } catch (e: Exception) {

                            false
                        }
            }

        adapter.updateData(
            filtered
        )

        updateJumlahData(
            filtered.size
        )
    }

    private fun updateJumlahData(
        jumlah: Int
    ) {

        tvJumlahLaporanQC.text =
            "Total Data QC : $jumlah"
    }

    private fun tampilDetailQC(
        qc: DashboardActivity.KirimQC
    ) {

        val detail =
            "NO QC : ${qc.noQC}\n\n" +

                    "SP ID : ${qc.spId}\n" +
                    "Jenis Kain : ${qc.productName}\n" +
                    "Jumlah : ${qc.quantity} Meter\n\n" +

                    "Tanggal Kirim : ${qc.tanggalKirim}\n" +
                    "Tanggal QC : ${qc.tanggalQC}\n\n" +

                    "UJI SAMPLE\n" +
                    "- Uji Cuci : ${if (qc.ujiCuci) "Sudah Dilakukan" else "Belum"}\n" +
                    "- Uji Daya Tahan : ${if (qc.ujiDayaTahan) "Sudah Dilakukan" else "Belum"}\n" +
                    "- Uji Suhu Panas : ${if (qc.ujiSuhuPanas) "Sudah Dilakukan" else "Belum"}\n\n"

                    "HASIL INSPEKSI\n" +
                    "Warna : ${qc.hasilWarna}\n" +
                    "Jahitan : ${qc.hasilJahitan}\n" +
                    "Ukuran : ${qc.hasilUkuran}\n" +
                    "Ukuran Akhir : ${qc.ukuranAkhir} Meter\n\n" +

                    "GRADE : ${qc.grade}\n" +
                    "STATUS : Selesai Inspeksi"

        MaterialAlertDialogBuilder(
            requireContext()
        )
            .setTitle(
                "Detail Laporan QC"
            )
            .setMessage(detail)
            .setPositiveButton(
                "TUTUP",
                null
            )
            .show()
    }
}