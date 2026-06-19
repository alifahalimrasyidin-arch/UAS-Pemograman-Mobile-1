package com.example.uaspm1kelompok1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SPAdapter(
    private var listSP: List<DashboardActivity.SuratPerintah>,
    private val onClick: (DashboardActivity.SuratPerintah) -> Unit
) : RecyclerView.Adapter<SPAdapter.SPViewHolder>() {

    class SPViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvIdSP: TextView =
            view.findViewById(R.id.tvIdSP)

        val tvJenisKain: TextView =
            view.findViewById(R.id.tvJenisKain)

        val tvDeadline: TextView =
            view.findViewById(R.id.tvDeadline)

        val tvStatus: TextView =
            view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SPViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_sp_card,
                    parent,
                    false
                )

        return SPViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SPViewHolder,
        position: Int
    ) {

        val sp = listSP[position]

        holder.tvIdSP.text = sp.id
        holder.tvJenisKain.text = sp.productName
        holder.tvDeadline.text = sp.deadline
        holder.tvStatus.text = sp.status

        holder.itemView.setOnClickListener {
            onClick(sp)
        }
    }

    override fun getItemCount(): Int {
        return listSP.size
    }

    fun updateData(
        newData: List<DashboardActivity.SuratPerintah>
    ) {
        listSP = newData
        notifyDataSetChanged()
    }
}