package com.example.uaspm1kelompok1

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HasilProduksiFragment : Fragment() {

    companion object {
        var refreshData = false
    }

    private lateinit var spFilter: Spinner

    private lateinit var tvJumlahData: TextView

    private lateinit var rvHasilProduksi: RecyclerView

    private lateinit var adapter:
            HasilProduksiAdapter

    private val dataTampil =
        mutableListOf<
                DashboardActivity.HasilProduksi>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_hasil_produksi,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        spFilter =
            view.findViewById(
                R.id.spFilterStatusQC
            )

        tvJumlahData =
            view.findViewById(
                R.id.tvJumlahData
            )

        rvHasilProduksi =
            view.findViewById(
                R.id.rvHasilProduksi
            )

        rvHasilProduksi.layoutManager =
            LinearLayoutManager(
                requireContext()
            )

        adapter =
            HasilProduksiAdapter(
                dataTampil
            ) { data ->

                kirimKeQC(data)
            }

        rvHasilProduksi.adapter =
            adapter

        setupSpinner()

        loadSemuaData()
    }

    override fun onResume() {
        super.onResume()

        if (refreshData) {

            refreshData = false

            when (
                spFilter.selectedItemPosition
            ) {

                0 -> loadSemuaData()

                1 -> filterBelumQC()

                2 -> filterSudahQC()
            }
        }
    }

    private fun setupSpinner() {

        val filterList = arrayOf(

            "Semua",

            "Belum Kirim QC",

            "Sudah Kirim QC"
        )

        val spinnerAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                filterList
            )

        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spFilter.adapter =
            spinnerAdapter

        spFilter.onItemSelectedListener =
            object :
                AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    when (position) {

                        0 -> loadSemuaData()

                        1 -> filterBelumQC()

                        2 -> filterSudahQC()
                    }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun loadSemuaData() {

        dataTampil.clear()

        dataTampil.addAll(
            DashboardActivity.hasilProduksi
        )

        updateTampilan()
    }

    private fun filterBelumQC() {

        dataTampil.clear()

        dataTampil.addAll(

            DashboardActivity
                .hasilProduksi
                .filter {

                    it.statusQC ==
                            "Belum Kirim QC"
                }
        )

        updateTampilan()
    }

    private fun filterSudahQC() {

        dataTampil.clear()

        dataTampil.addAll(

            DashboardActivity
                .hasilProduksi
                .filter {

                    it.statusQC ==
                            "Sudah Kirim QC"
                }
        )

        updateTampilan()
    }

    private fun updateTampilan() {

        adapter.notifyDataSetChanged()

        tvJumlahData.text =
            "Total Produksi : ${dataTampil.size}"
    }

    private fun kirimKeQC(
        data: DashboardActivity.HasilProduksi
    ) {

        AlertDialog.Builder(requireContext())
            .setTitle("Kirim ke QC")
            .setMessage(
                "Apakah yakin ingin menghubungi Quality Control?"
            )
            .setPositiveButton(
                "Ya"
            ) { _, _ ->

                val nomorQC =
                    "QC" + String.format(
                        "%03d",
                        DashboardActivity.dikirimKeQC.size + 1
                    )

                DashboardActivity.dikirimKeQC.add(

                    DashboardActivity.KirimQC(

                        spId =
                            data.spId,

                        productName =
                            data.productName,

                        quantity =
                            data.quantity,

                        unit =
                            data.unit,

                        tanggalKirim =
                            data.tanggalSelesai,

                        noQC =
                            nomorQC
                    )
                )

                data.statusQC =
                    "Sudah Kirim QC"

                adapter.notifyDataSetChanged()

                Toast.makeText(
                    requireContext(),
                    "Berhasil Menghubungi QC",
                    Toast.LENGTH_LONG
                ).show()

                when (
                    spFilter.selectedItemPosition
                ) {

                    0 -> loadSemuaData()

                    1 -> filterBelumQC()

                    2 -> filterSudahQC()
                }
            }
            .setNegativeButton(
                "Batal",
                null
            )
            .show()
    }
}