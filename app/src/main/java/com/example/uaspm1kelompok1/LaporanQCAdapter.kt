package com.example.uaspm1kelompok1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LaporanQCAdapter(
    private var listQC: List<DashboardActivity.KirimQC>,
    private val onClick: (DashboardActivity.KirimQC) -> Unit
) : RecyclerView.Adapter<LaporanQCAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvNoQC: TextView =
            view.findViewById(R.id.tvNoQC)

        val tvGrade: TextView =
            view.findViewById(R.id.tvGrade)

        val tvSPID: TextView =
            view.findViewById(R.id.tvSPID)

        val tvJenisKain: TextView =
            view.findViewById(R.id.tvJenisKain)

        val tvTanggalQC: TextView =
            view.findViewById(R.id.tvTanggalQC)

        val tvStatus: TextView =
            view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_laporan_qc,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val qc =
            listQC[position]

        holder.tvNoQC.text =
            qc.noQC

        holder.tvGrade.text =
            qc.grade
        when (qc.grade) {

            "A" -> {
                holder.tvGrade.setBackgroundResource(
                    R.drawable.bg_grade_a
                )
            }

            "B" -> {
                holder.tvGrade.setBackgroundResource(
                    R.drawable.bg_grade_b
                )
            }

            "C" -> {
                holder.tvGrade.setBackgroundResource(
                    R.drawable.bg_grade_c
                )
            }
        }

        holder.tvSPID.text =
            qc.spId

        holder.tvJenisKain.text =
            qc.productName

        holder.tvTanggalQC.text =
            qc.tanggalQC

        holder.tvStatus.text =
            "Status : Selesai Inspeksi"

        holder.itemView.setOnClickListener {

            onClick(qc)
        }
    }

    override fun getItemCount(): Int {
        return listQC.size
    }

    fun updateData(
        newData: List<DashboardActivity.KirimQC>
    ) {

        listQC = newData

        notifyDataSetChanged()
    }
}