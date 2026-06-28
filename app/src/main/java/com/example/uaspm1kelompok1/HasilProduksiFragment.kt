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
import com.example.uaspm1kelompok1.database.DatabaseContract
import com.example.uaspm1kelompok1.database.DatabaseHelper

class HasilProduksiFragment : Fragment() {

    companion object {
        var refreshData = false
    }

    private lateinit var spFilter: Spinner

    private lateinit var tvJumlahData: TextView

    private lateinit var rvHasilProduksi: RecyclerView

    private lateinit var adapter:
            HasilProduksiAdapter

    private lateinit var dbHelper:DatabaseHelper

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
                requireContext())
                        dbHelper=DatabaseHelper(requireContext())


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
    private fun loadDataDatabase(){
        dataTampil.clear()
        val cursor=dbHelper.getAllHasilProduksi()
        if(cursor.moveToFirst()){
            do{
                dataTampil.add(
                    DashboardActivity.HasilProduksi(
                        spId=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.HasilProduksiTable.SP_ID)),
                        productName=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.HasilProduksiTable.JENIS_KAIN)),
                        quantity=cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.HasilProduksiTable.JUMLAH)),
                        unit=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.HasilProduksiTable.SATUAN)),
                        tanggalSelesai=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.HasilProduksiTable.TANGGAL_PRODUKSI)),
                        petugasProduksi=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.HasilProduksiTable.PETUGAS_PRODUKSI)),
                        statusQC=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.HasilProduksiTable.STATUS_QC)),
                        notes=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.HasilProduksiTable.CATATAN))
                    )
                )
            }while(cursor.moveToNext())
        }
        cursor.close()
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

    private fun loadSemuaData(){
        loadDataDatabase()
        updateTampilan()
    }

    private fun filterBelumQC(){
        loadDataDatabase()
        val hasil=dataTampil.filter{
            it.statusQC=="Belum Kirim QC"
        }
        dataTampil.clear()
        dataTampil.addAll(hasil)
        updateTampilan()
    }
    private fun filterSudahQC(){
        loadDataDatabase()
        val hasil=dataTampil.filter{
            it.statusQC=="Sudah Kirim QC"
        }
        dataTampil.clear()
        dataTampil.addAll(hasil)
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
                    "QC" + System.currentTimeMillis().toString().takeLast(6)
                val berhasil=dbHelper.insertQualityControl(
                    data.spId,
                    data.productName,
                    data.quantity,
                    data.unit,
                    nomorQC,
                    data.tanggalSelesai,
                    "",
                    "",
                    "",
                    "Menunggu QC",
                    false,
                    false,
                    false,
                    "",
                    "",
                    "",
                    0.0,
                    null,
                    null,
                    null,
                    ""
                )

                if(!berhasil){
                    Toast.makeText(
                        requireContext(),
                        "Gagal mengirim ke QC",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }

                data.statusQC =
                    "Sudah Kirim QC"
                dbHelper.updateStatusQC(
                    data.spId,
                    "Sudah Kirim QC"
                )

                loadSemuaData()

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