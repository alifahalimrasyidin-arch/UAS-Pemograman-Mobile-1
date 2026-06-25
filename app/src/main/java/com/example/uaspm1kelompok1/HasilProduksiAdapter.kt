package com.example.uaspm1kelompok1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HasilProduksiAdapter(

    private var listData:
    List<DashboardActivity.HasilProduksi>,

    private val onKirimQC:
        (DashboardActivity.HasilProduksi) -> Unit

) : RecyclerView.Adapter<HasilProduksiAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val tvSpId: TextView =
            view.findViewById(R.id.tvSpId)

        val tvJenisKain: TextView =
            view.findViewById(R.id.tvJenisKain)

        val tvQuantity: TextView =
            view.findViewById(R.id.tvQuantity)

        val tvTanggal: TextView =
            view.findViewById(R.id.tvTanggal)

        val tvPetugas: TextView =
            view.findViewById(R.id.tvPetugas)

        val tvStatusQC: TextView =
            view.findViewById(R.id.tvStatusQC)

        val btnKirimQC: Button =
            view.findViewById(R.id.btnKirimQC)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_hasil_produksi,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val data =
            listData[position]

        holder.tvSpId.text =
            data.spId

        holder.tvJenisKain.text =
            data.productName

        holder.tvQuantity.text =
            "${data.quantity} ${data.unit}"

        holder.tvTanggal.text =
            "Tanggal Produksi : ${data.tanggalSelesai}"

        holder.tvPetugas.text =
            "Petugas : ${data.petugasProduksi}"

        holder.tvStatusQC.text =
            "Status QC : ${data.statusQC}"

        if (data.statusQC == "Sudah Kirim QC") {

            holder.btnKirimQC.text =
                "SUDAH DIKIRIM QC"

            holder.btnKirimQC.isEnabled =
                false

            holder.btnKirimQC.setBackgroundColor(
                android.graphics.Color.parseColor(
                    "#2E7D32"
                )
            )

        } else {

            holder.btnKirimQC.text =
                "KIRIM KE QC"

            holder.btnKirimQC.isEnabled =
                true

            holder.btnKirimQC.setBackgroundColor(
                android.graphics.Color.parseColor(
                    "#D32F2F"
                )
            )

            holder.btnKirimQC.setOnClickListener {

                onKirimQC(data)
            }
        }
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun updateData(
        newData:
        List<DashboardActivity.HasilProduksi>
    ) {

        listData = newData

        notifyDataSetChanged()
    }
}