package com.example.uaspm1kelompok1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QCAdapter(
    private var listQC: List<DashboardActivity.KirimQC>,
    private val onClick: (DashboardActivity.KirimQC) -> Unit
) : RecyclerView.Adapter<QCAdapter.QCViewHolder>() {

    class QCViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvNoQC: TextView =
            view.findViewById(R.id.tvNoQC)

        val tvSpId: TextView =
            view.findViewById(R.id.tvSpId)

        val tvJenisKain: TextView =
            view.findViewById(R.id.tvJenisKain)

        val tvJumlahKain: TextView =
            view.findViewById(R.id.tvJumlahKain)

        val tvStatusQC: TextView =
            view.findViewById(R.id.tvStatusQC)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QCViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_qc_card,
                    parent,
                    false
                )

        return QCViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: QCViewHolder,
        position: Int
    ) {

        val qc = listQC[position]

        holder.tvNoQC.text =
            qc.noQC

        holder.tvSpId.text =
            qc.spId

        holder.tvJenisKain.text =
            qc.productName

        holder.tvJumlahKain.text =
            "${qc.quantity} Meter"

        holder.tvStatusQC.text =
            qc.statusKain

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