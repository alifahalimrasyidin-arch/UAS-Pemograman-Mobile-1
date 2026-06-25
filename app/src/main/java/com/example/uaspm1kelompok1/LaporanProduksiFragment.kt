package com.example.uaspm1kelompok1

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class LaporanProduksiFragment : Fragment() {

    private lateinit var btnSemua: Button
    private lateinit var btnMingguIni: Button
    private lateinit var btnBulanIni: Button

    private lateinit var spStatus: Spinner

    private lateinit var tvJumlahData: TextView

    private lateinit var rvLaporan: RecyclerView

    private lateinit var adapter: LaporanProduksiAdapter

    private val dataTampil =
        mutableListOf<DashboardActivity.SuratPerintah>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_laporan_produksi,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        btnSemua =
            view.findViewById(R.id.btnSemua)

        btnMingguIni =
            view.findViewById(R.id.btnMingguIni)

        btnBulanIni =
            view.findViewById(R.id.btnBulanIni)

        spStatus =
            view.findViewById(R.id.spStatus)

        tvJumlahData =
            view.findViewById(R.id.tvJumlahData)

        rvLaporan =
            view.findViewById(R.id.rvLaporanProduksi)

        rvLaporan.layoutManager =
            LinearLayoutManager(requireContext())

        adapter =
            LaporanProduksiAdapter(
                dataTampil
            ){ sp ->

                klikCard(sp)
            }

        rvLaporan.adapter =
            adapter

        setupSpinner()

        tampilSemua()

        btnSemua.setOnClickListener {

            tampilSemua()
        }

        btnMingguIni.setOnClickListener {

            filterMinggu()
        }

        btnBulanIni.setOnClickListener {

            filterBulan()
        }
    }

    private fun setupSpinner(){

        val listStatus = arrayOf(

            "Semua",

            "Belum Produksi",

            "Selesai Produksi"
        )

        val spinnerAdapter =
            ArrayAdapter(

                requireContext(),

                android.R.layout.simple_spinner_item,

                listStatus
            )

        spinnerAdapter.setDropDownViewResource(

            android.R.layout.simple_spinner_dropdown_item
        )

        spStatus.adapter =
            spinnerAdapter

        spStatus.onItemSelectedListener =
            object :
                AdapterView.OnItemSelectedListener{

                override fun onItemSelected(

                    parent: AdapterView<*>?,

                    view: View?,

                    position: Int,

                    id: Long
                ){

                    when(position){

                        0->tampilSemua()

                        1->filterBelumProduksi()

                        2->filterSelesaiProduksi()
                    }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ){}
            }
    }

    private fun tampilSemua(){

        dataTampil.clear()

        dataTampil.addAll(

            DashboardActivity.suratPerintah
        )

        adapter.notifyDataSetChanged()

        tvJumlahData.text =
            "Total SP : ${dataTampil.size}"
    }

    private fun filterBelumProduksi(){

        dataTampil.clear()

        dataTampil.addAll(

            DashboardActivity.suratPerintah.filter{

                it.status!="Selesai Produksi"
            }
        )

        adapter.notifyDataSetChanged()

        tvJumlahData.text =
            "Total SP : ${dataTampil.size}"
    }
    private fun filterSelesaiProduksi(){

        dataTampil.clear()

        dataTampil.addAll(

            DashboardActivity.suratPerintah.filter{

                it.status == "Selesai Produksi"
            }
        )

        adapter.notifyDataSetChanged()

        tvJumlahData.text =
            "Total SP : ${dataTampil.size}"
    }

    private fun filterMinggu(){

        val sdf =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        val calendar =
            Calendar.getInstance()

        val hariIni =
            calendar.time

        calendar.add(
            Calendar.DAY_OF_YEAR,
            7
        )

        val mingguDepan =
            calendar.time

        dataTampil.clear()

        dataTampil.addAll(

            DashboardActivity.suratPerintah.filter{

                try{

                    val deadline =
                        sdf.parse(
                            it.deadline
                        )

                    deadline != null &&
                            deadline.after(hariIni) &&
                            deadline.before(mingguDepan)

                }catch (e:Exception){

                    false
                }
            }
        )

        adapter.notifyDataSetChanged()

        tvJumlahData.text =
            "Total SP : ${dataTampil.size}"
    }

    private fun filterBulan() {

        val bulanSekarang =
            Calendar.getInstance().get(Calendar.MONTH)

        val tahunSekarang =
            Calendar.getInstance().get(Calendar.YEAR)

        val sdf =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        dataTampil.clear()

        dataTampil.addAll(

            DashboardActivity.suratPerintah.filter { sp ->

                try {

                    val deadline =
                        sdf.parse(sp.deadline)

                    if (deadline == null) {

                        false

                    } else {

                        val cal =
                            Calendar.getInstance()

                        cal.time = deadline

                        cal.get(Calendar.MONTH) == bulanSekarang &&
                                cal.get(Calendar.YEAR) == tahunSekarang
                    }

                } catch (e: Exception) {

                    false
                }
            }
        )

        adapter.notifyDataSetChanged()

        tvJumlahData.text =
            "Total SP : ${dataTampil.size}"
    }
    private fun klikCard(

        sp:
        DashboardActivity.SuratPerintah

    ){

        if(
            sp.status
            !=
            "Selesai Produksi"
        ){

            AlertDialog.Builder(
                requireContext()
            )
                .setTitle(
                    "Informasi"
                )
                .setMessage(
                    "Staff Produksi belum melakukan produksi."
                )
                .setPositiveButton(
                    "OK",
                    null
                )
                .show()

            return
        }

        val hasil =
            DashboardActivity
                .hasilProduksi
                .find{

                    it.spId ==
                            sp.id
                }

        if(
            hasil == null
        ){

            Toast.makeText(

                requireContext(),

                "Data hasil produksi tidak ditemukan",

                Toast.LENGTH_SHORT

            ).show()

            return
        }

        val intent =
            Intent(

                requireContext(),

                DetailProduksiActivity::class.java

            )

        intent.putExtra(
            "SP_ID",
            hasil.spId
        )

        startActivity(
            intent
        )
    }
}