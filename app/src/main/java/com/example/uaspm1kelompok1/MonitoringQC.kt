package com.example.uaspm1kelompok1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MonitoringQC : AppCompatActivity() {

    private lateinit var etCariQC: EditText
    private lateinit var btnCariQC: Button
    private lateinit var spGradeFilter: Spinner
    private lateinit var tvJumlahLaporanQC: TextView
    private lateinit var tvTotalGrade: TextView
    private lateinit var rvLaporanQC: RecyclerView

    private lateinit var adapter: LaporanQCAdapter

    private val semuaData =
        mutableListOf<DashboardActivity.KirimQC>()

    private val dataTampil =
        mutableListOf<DashboardActivity.KirimQC>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitoring_qc)

        initView()
        loadData()
        setupSpinner()
        setupSearch()
    }

    private fun initView() {

        etCariQC =
            findViewById(R.id.etCariQC)

        btnCariQC =
            findViewById(R.id.btnCariQC)

        spGradeFilter =
            findViewById(R.id.spGradeFilter)

        tvJumlahLaporanQC =
            findViewById(R.id.tvJumlahLaporanQC)

        tvTotalGrade =
            findViewById(R.id.tvTotalGrade)

        rvLaporanQC =
            findViewById(R.id.rvLaporanQC)

        rvLaporanQC.layoutManager =
            LinearLayoutManager(this)
    }

    private fun loadData() {

        semuaData.clear()

        semuaData.addAll(
            DashboardActivity.dikirimKeQC.filter {
                it.statusKain == "Selesai QC"
            }
        )

        dataTampil.clear()
        dataTampil.addAll(semuaData)

        adapter =
            LaporanQCAdapter(
                dataTampil
            ) { qc ->

                val intent =
                    Intent(
                        this,
                        DetailLaporanQCActivity::class.java
                    )

                intent.putExtra(
                    "NO_QC",
                    qc.noQC
                )

                startActivity(intent)
            }

        rvLaporanQC.adapter = adapter

        updateInfo()
    }

    private fun setupSpinner() {

        val gradeList = arrayOf(
            "Semua Grade",
            "Grade A",
            "Grade B",
            "Grade C"
        )

        val spinnerAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                gradeList
            )

        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spGradeFilter.adapter =
            spinnerAdapter

        spGradeFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    filterGrade(
                        gradeList[position]
                    )
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {
                }
            }
    }

    private fun filterGrade(
        grade: String
    ) {

        dataTampil.clear()

        when (grade) {

            "Semua Grade" -> {

                dataTampil.addAll(
                    semuaData
                )

                tvTotalGrade.text =
                    "Semua Grade : ${semuaData.size}"
            }

            "Grade A" -> {

                val hasil =
                    semuaData.filter {
                        it.grade == "A"
                    }

                dataTampil.addAll(hasil)

                tvTotalGrade.text =
                    "Grade A : ${hasil.size}"
            }

            "Grade B" -> {

                val hasil =
                    semuaData.filter {
                        it.grade == "B"
                    }

                dataTampil.addAll(hasil)

                tvTotalGrade.text =
                    "Grade B : ${hasil.size}"
            }

            "Grade C" -> {

                val hasil =
                    semuaData.filter {
                        it.grade == "C"
                    }

                dataTampil.addAll(hasil)

                tvTotalGrade.text =
                    "Grade C : ${hasil.size}"
            }
        }

        adapter.notifyDataSetChanged()

        updateInfo()
    }

    private fun setupSearch() {

        btnCariQC.setOnClickListener {

            val keyword =
                etCariQC.text
                    .toString()
                    .trim()

            if (keyword.isEmpty()) {

                filterGrade(
                    spGradeFilter.selectedItem.toString()
                )

                return@setOnClickListener
            }

            val hasil =
                semuaData.filter {

                    it.noQC.contains(
                        keyword,
                        true
                    ) ||

                            it.spId.contains(
                                keyword,
                                true
                            )
                }

            dataTampil.clear()
            dataTampil.addAll(hasil)

            adapter.notifyDataSetChanged()

            tvJumlahLaporanQC.text =
                "Total Data QC : ${hasil.size}"
        }
    }

    private fun updateInfo() {

        tvJumlahLaporanQC.text =
            "Total Data QC : ${dataTampil.size}"
    }
}