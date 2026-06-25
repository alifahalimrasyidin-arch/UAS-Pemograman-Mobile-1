package com.example.uaspm1kelompok1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ProduksiFragment : Fragment() {

    companion object {

        var refreshData = false
    }

    private lateinit var rvProduksi: RecyclerView
    private lateinit var btnSemua: Button
    private lateinit var btnMingguIni: Button
    private lateinit var tvJumlahData: TextView

    private lateinit var adapter: SPAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_produksi,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(
            view,
            savedInstanceState
        )

        rvProduksi =
            view.findViewById(R.id.rvProduksi)

        btnSemua =
            view.findViewById(R.id.btnSemua)

        btnMingguIni =
            view.findViewById(R.id.btnMingguIni)

        tvJumlahData =
            view.findViewById(R.id.tvJumlahData)

        rvProduksi.layoutManager =
            LinearLayoutManager(requireContext())

        tampilSemuaSP()

        btnSemua.setOnClickListener {

            tampilSemuaSP()
        }

        btnMingguIni.setOnClickListener {

            filterMingguIni()
        }
    }

    override fun onResume() {
        super.onResume()

        if (refreshData) {

            refreshData = false

            tampilSemuaSP()
        }
    }

    private fun tampilSemuaSP() {

        val data =
            DashboardActivity.suratPerintah.filter {

                it.status != "Selesai Produksi"
            }

        adapter =
            SPAdapter(data) { sp ->

                val intent =
                    Intent(
                        requireContext(),
                        InputProduksiActivity::class.java
                    )

                intent.putExtra(
                    "ID_SP",
                    sp.id
                )

                startActivity(intent)
            }

        rvProduksi.adapter =
            adapter

        tvJumlahData.text =
            "Total SP : ${data.size}"
    }

    private fun filterMingguIni() {

        val sdf =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        val today =
            Date()

        val sevenDays =
            Calendar.getInstance().apply {

                time = today

                add(
                    Calendar.DAY_OF_YEAR,
                    7
                )

            }.time

        val hasil =
            DashboardActivity.suratPerintah.filter {

                try {

                    val deadline =
                        sdf.parse(it.deadline)

                    deadline != null &&
                            deadline.after(today) &&
                            deadline.before(sevenDays) &&
                            it.status != "Selesai Produksi"

                } catch (e: Exception) {

                    false
                }
            }

        adapter =
            SPAdapter(hasil) { sp ->

                val intent =
                    Intent(
                        requireContext(),
                        InputProduksiActivity::class.java
                    )

                intent.putExtra(
                    "ID_SP",
                    sp.id
                )

                startActivity(intent)
            }

        rvProduksi.adapter =
            adapter

        tvJumlahData.text =
            "Total SP : ${hasil.size}"
    }
}