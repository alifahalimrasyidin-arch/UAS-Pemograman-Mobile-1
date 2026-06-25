package com.example.uaspm1kelompok1

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class LaporanProduksiAdapter(

    private var listData:
    List<DashboardActivity.SuratPerintah>,

    private val onClick:
        (DashboardActivity.SuratPerintah) -> Unit

) : RecyclerView.Adapter<LaporanProduksiAdapter.ViewHolder>() {

    class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        val card:
                CardView =
            view.findViewById(R.id.cardLaporan)

        val tvSpId:
                TextView =
            view.findViewById(R.id.tvSpId)

        val tvJenis:
                TextView =
            view.findViewById(R.id.tvJenisKain)

        val tvJumlah:
                TextView =
            view.findViewById(R.id.tvJumlah)

        val tvDeadline:
                TextView =
            view.findViewById(R.id.tvDeadline)

        val tvStatus:
                TextView =
            view.findViewById(R.id.tvStatus)

        val tvTanggal:
                TextView =
            view.findViewById(R.id.tvTanggalProduksi)

        val tvPetugas:
                TextView =
            view.findViewById(R.id.tvPetugas)

        val imgStatus:
                ImageView =
            view.findViewById(R.id.imgStatus)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_laporan_produksi,
                    parent,
                    false
                )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        return listData.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val sp =
            listData[position]

        holder.tvSpId.text =
            sp.id

        holder.tvJenis.text =
            sp.productName

        holder.tvJumlah.text =
            "${sp.quantity} ${sp.unit}"

        holder.tvDeadline.text =
            "Deadline : ${sp.deadline}"

        if (
            sp.status ==
            "Selesai Produksi"
        ) {

            holder.tvStatus.text =
                "SELESAI PRODUKSI"

            holder.tvStatus.setBackgroundResource(
                R.drawable.bg_status_hijau
            )

            holder.tvStatus.setTextColor(
                Color.WHITE
            )


            holder.imgStatus.setImageResource(
                android.R.drawable.checkbox_on_background
            )

            val hasil =
                DashboardActivity
                    .hasilProduksi
                    .find {

                        it.spId ==
                                sp.id
                    }

            if (hasil != null) {

                holder.tvTanggal.visibility =
                    View.VISIBLE

                holder.tvPetugas.visibility =
                    View.VISIBLE

                holder.tvTanggal.text =
                    "Tanggal : ${hasil.tanggalSelesai}"

                holder.tvPetugas.text =
                    "Petugas : ${hasil.petugasProduksi}"

            } else {

                holder.tvTanggal.visibility =
                    View.GONE

                holder.tvPetugas.visibility =
                    View.GONE
            }

        } else {

            holder.tvStatus.text =
                "BELUM PRODUKSI"

            holder.tvStatus.setBackgroundResource(
                R.drawable.bg_status_merah
            )

            holder.tvStatus.setTextColor(
                Color.WHITE
            )
            holder.imgStatus.setImageResource(
                android.R.drawable.ic_dialog_alert)

            holder.tvTanggal.visibility =
                View.GONE

            holder.tvPetugas.visibility =
                View.GONE
        }

        holder.itemView.setOnClickListener {

            onClick(sp)
        }
    }

    fun updateData(

        newData:
        List<DashboardActivity.SuratPerintah>

    ) {

        listData =
            newData

        notifyDataSetChanged()
    }
}