package com.example.uaspm1kelompok1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import android.text.Editable
import android.text.TextWatcher
class QCFragment : Fragment() {

    private lateinit var rvQC: RecyclerView
    private lateinit var btnSemuaQC: Button
    private lateinit var btnMingguIniQC: Button
    private lateinit var tvJumlahQC: TextView

    private lateinit var adapter: QCAdapter
    private var targetImageView: ImageView? = null

    private var fotoSebelumBitmap: Bitmap? = null
    private var fotoSesudahBitmap: Bitmap? = null
    private var fotoTambahanBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(
            R.layout.fragment_qc,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        rvQC =
            view.findViewById(R.id.rvQC)

        btnSemuaQC =
            view.findViewById(R.id.btnSemuaQC)

        btnMingguIniQC =
            view.findViewById(R.id.btnMingguIniQC)

        tvJumlahQC =
            view.findViewById(R.id.tvJumlahQC)

        rvQC.layoutManager =
            LinearLayoutManager(requireContext())

        val dataQC =
            DashboardActivity.dikirimKeQC.filter {
                it.statusKain == "Menunggu QC"
            }

        adapter =
            QCAdapter(dataQC) { qc ->

                bukaFormQC(qc)
            }

        rvQC.adapter =
            adapter

        updateJumlahData(
            dataQC.size
        )

        btnSemuaQC.setOnClickListener {

            val semuaData =
                DashboardActivity.dikirimKeQC.filter {
                    it.statusKain == "Menunggu QC"
                }

            adapter.updateData(
                semuaData
            )

            updateJumlahData(
                semuaData.size
            )
        }

        btnMingguIniQC.setOnClickListener {

            val sdf =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            val today = Date()

            val sevenDays =
                Calendar.getInstance().apply {

                    time = today

                    add(
                        Calendar.DAY_OF_YEAR,
                        -7
                    )

                }.time

            val filtered =
                DashboardActivity.dikirimKeQC.filter {

                    try {

                        val tanggal =
                            sdf.parse(it.tanggalKirim)

                        tanggal != null &&
                                tanggal.after(sevenDays)

                    } catch (e: Exception) {

                        false
                    }
                }

            adapter.updateData(
                filtered
            )

            updateJumlahData(
                filtered.size
            )
        }
    }

    private fun updateJumlahData(
        jumlah: Int
    ) {

        tvJumlahQC.text =
            "Total Data QC : $jumlah"
    }

    private fun bukaFormQC(
        qc: DashboardActivity.KirimQC
    ) {

        val view =
            layoutInflater.inflate(
                R.layout.dialog_form_qc,
                null
            )

        val tvNoQC =
            view.findViewById<TextView>(
                R.id.tvNoQC
            )

        val tvSPID =
            view.findViewById<TextView>(
                R.id.tvSPID
            )

        val tvJenisKain =
            view.findViewById<TextView>(
                R.id.tvJenisKain
            )

        val tvJumlahKain =
            view.findViewById<TextView>(
                R.id.tvJumlahKain
            )

        val tvTanggalKirim =
            view.findViewById<TextView>(
                R.id.tvTanggalKirim
            )

        val cbUjiCuci =
            view.findViewById<CheckBox>(
                R.id.cbUjiCuci
            )

        val cbUjiDayaTahan =
            view.findViewById<CheckBox>(
                R.id.cbUjiDayaTahan
            )

        val cbUjiSuhu =
            view.findViewById<CheckBox>(
                R.id.cbUjiSuhu
            )

        val spWarna =
            view.findViewById<Spinner>(
                R.id.spWarna
            )

        val spJahitan =
            view.findViewById<Spinner>(
                R.id.spJahitan
            )

        val spUkuran =
            view.findViewById<Spinner>(
                R.id.spUkuran
            )

        val etUkuranAkhir =
            view.findViewById<EditText>(
                R.id.etUkuranAkhir
            )


        val tvUjiError =
            view.findViewById<TextView>(
                R.id.tvUjiError
            )

        val tvUkuranError =
            view.findViewById<TextView>(
                R.id.tvUkuranError
            )
        etUkuranAkhir.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    val ukuran =
                        s.toString().toIntOrNull()

                    if (ukuran == null) {

                        tvUkuranError.visibility =
                            View.GONE

                        etUkuranAkhir.setBackgroundResource(
                            R.drawable.bg_edittext
                        )

                        return
                    }

                    if (ukuran > qc.quantity) {

                        tvUkuranError.visibility =
                            View.VISIBLE

                        tvUkuranError.text =
                            "Ukuran akhir tidak boleh melebihi hasil produksi (${qc.quantity} Meter)"

                        etUkuranAkhir.setBackgroundResource(
                            R.drawable.bg_error
                        )
                    } else {

                        tvUkuranError.visibility =
                            View.GONE

                        etUkuranAkhir.setBackgroundResource(
                            R.drawable.bg_edittext
                        )
                    }
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        val btnSimpanQC =
            view.findViewById<Button>(
                R.id.btnSimpanQC
            )
        val btnFotoSebelum =
            view.findViewById<Button>(
                R.id.btnFotoSebelum
            )

        val btnFotoSesudah =
            view.findViewById<Button>(
                R.id.btnFotoSesudah
            )

        val btnFotoTambahan =
            view.findViewById<Button>(
                R.id.btnFotoTambahan
            )

        val imgSebelum =
            view.findViewById<ImageView>(
                R.id.imgSebelum
            )

        val imgSesudah =
            view.findViewById<ImageView>(
                R.id.imgSesudah
            )

        val imgTambahan =
            view.findViewById<ImageView>(
                R.id.imgTambahan
            )
        btnFotoSebelum.setOnClickListener {

            targetImageView = imgSebelum

            val intent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            cameraLauncher.launch(intent)
        }

        btnFotoSesudah.setOnClickListener {

            targetImageView = imgSesudah

            val intent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            cameraLauncher.launch(intent)
        }

        btnFotoTambahan.setOnClickListener {

            targetImageView = imgTambahan

            val intent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            cameraLauncher.launch(intent)
        }

        // DATA KAIN

        tvNoQC.text =
            "No QC : ${qc.noQC}"

        tvSPID.text =
            "ID SP : ${qc.spId}"

        tvJenisKain.text =
            "Jenis Kain : ${qc.productName}"

        tvJumlahKain.text =
            "Jumlah : ${qc.quantity} Meter"

        tvTanggalKirim.text =
            "Tanggal Kirim : ${qc.tanggalKirim}"

        // SPINNER WARNA

        val warnaList = arrayOf(
            "Baik tanpa noda",
            "Ada sedikit noda",
            "Noda akibat luntur"
        )

        val warnaAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                warnaList
            )

        warnaAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spWarna.adapter =
            warnaAdapter

        // SPINNER JAHITAN

        val jahitanList = arrayOf(
            "Cutting baik",
            "Ada benang double picks",
            "Mispick / cutting tidak sempurna"
        )

        val jahitanAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                jahitanList
            )

        jahitanAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spJahitan.adapter =
            jahitanAdapter

        // SPINNER UKURAN

        val ukuranList = arrayOf(
            "Size berkurang 1-10m",
            "Size berkurang 10-20m",
            "Size berkurang 20m+"
        )

        val ukuranAdapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                ukuranList
            )

        ukuranAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spUkuran.adapter =
            ukuranAdapter

        val dialog =
            MaterialAlertDialogBuilder(
                requireContext()
            )
                .setView(view)
                .create()

        dialog.show()



        btnSimpanQC.setOnClickListener {
            val ukuranAkhir =
                etUkuranAkhir.text
                    .toString()
                    .toIntOrNull()

            if (!cbUjiCuci.isChecked) {

                showErrorDialog(
                    "Uji Cuci belum dilakukan"
                )

                return@setOnClickListener
            }

            if (!cbUjiDayaTahan.isChecked) {

                showErrorDialog(
                    "Uji Daya Tahan belum dilakukan"
                )

                return@setOnClickListener
            }

            if (!cbUjiSuhu.isChecked) {

                showErrorDialog(
                    "Uji Suhu Panas belum dilakukan"
                )

                return@setOnClickListener
            }



            if (ukuranAkhir == null) {

                tvUkuranError.visibility =
                    View.VISIBLE

                tvUkuranError.text =
                    "Ukuran akhir wajib diisi"

                showErrorDialog(
                    "Ukuran akhir belum diisi"
                )

                return@setOnClickListener
            }

            if (ukuranAkhir > qc.quantity) {

                tvUkuranError.visibility =
                    View.VISIBLE

                tvUkuranError.text =
                    "Ukuran akhir tidak boleh melebihi hasil produksi"

                etUkuranAkhir.setBackgroundResource(
                    R.drawable.bg_error
                )

                return@setOnClickListener
            }

            tvUkuranError.visibility =
                View.GONE

            if (fotoSebelumBitmap == null) {

                showErrorDialog(
                    "Foto sebelum uji belum diambil"
                )

                return@setOnClickListener
            }

            if (fotoSesudahBitmap == null) {

                showErrorDialog(
                    "Foto sesudah uji belum diambil"
                )

                return@setOnClickListener

            }

            // lanjutkan kode grade dan simpan yang lama


            val poinWarna =
                3 - spWarna.selectedItemPosition

            val poinJahitan =
                3 - spJahitan.selectedItemPosition

            val poinUkuran =
                3 - spUkuran.selectedItemPosition

            val totalPoin =
                poinWarna +
                        poinJahitan +
                        poinUkuran

            val grade =
                when {

                    totalPoin >= 8 -> "A"

                    totalPoin >= 5 -> "B"

                    else -> "C"
                }

            MaterialAlertDialogBuilder(
                requireContext()
            )
                .setTitle("Hasil Inspeksi QC")
                .setMessage(
                    "Jenis Kain : ${qc.productName}\n\n" +
                            "Grade : $grade\n" +
                            "Status : Selesai QC\n\n" +
                            "Apakah data ingin disimpan?"
                )
                .setNegativeButton(
                    "BATAL",
                    null
                )
                .setPositiveButton(
                    "SIMPAN"
                ) { _, _ ->

                    qc.ujiCuci =
                        cbUjiCuci.isChecked

                    qc.ujiDayaTahan =
                        cbUjiDayaTahan.isChecked

                    qc.ujiSuhuPanas =
                        cbUjiSuhu.isChecked

                    qc.cekWarna =
                        spWarna.selectedItemPosition + 1

                    qc.cekJahitan =
                        spJahitan.selectedItemPosition + 1

                    qc.cekUkuran =
                        spUkuran.selectedItemPosition + 1

                    qc.hasilWarna =
                        spWarna.selectedItem.toString()

                    qc.hasilJahitan =
                        spJahitan.selectedItem.toString()

                    qc.hasilUkuran =
                        spUkuran.selectedItem.toString()

                    qc.ukuranAkhir =
                        ukuranAkhir

                    qc.grade =
                        grade

                    qc.petugasQC =
                        requireActivity()
                            .getSharedPreferences(
                                "TIASA_PREFS",
                                android.content.Context.MODE_PRIVATE
                            )
                            .getString(
                                "user_name",
                                "Quality Control"
                            ) ?: "Quality Control"

                    qc.tanggalQC =
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(Date())

                    qc.detailQC =
                        "Warna : ${qc.hasilWarna}\n" +
                                "Jahitan : ${qc.hasilJahitan}\n" +
                                "Ukuran : ${qc.hasilUkuran}\n" +
                                "Ukuran Akhir : ${qc.ukuranAkhir} Meter"

                    qc.fotoSebelum = "Ada"
                    qc.fotoSesudah = "Ada"

                    qc.fotoTambahan =
                        if (fotoTambahanBitmap != null)
                            "Ada"
                        else
                            ""
                    qc.fotoSebelumBitmap =
                        fotoSebelumBitmap

                    qc.fotoSesudahBitmap =
                        fotoSesudahBitmap

                    qc.fotoTambahanBitmap =
                        fotoTambahanBitmap

                    qc.statusKain =
                        "Selesai QC"
                    val prefs =
                        requireActivity().getSharedPreferences(
                            "TIASA_PREFS",
                            android.content.Context.MODE_PRIVATE
                        )

                    qc.petugasQC =
                        prefs.getString(
                            "user_name",
                            "-"
                        ) ?: "-"

                    fotoSebelumBitmap = null
                    fotoSesudahBitmap = null
                    fotoTambahanBitmap = null

                    adapter.updateData(
                        DashboardActivity.dikirimKeQC.filter {
                            it.statusKain == "Menunggu QC"
                        }
                    )

                    updateJumlahData(
                        DashboardActivity.dikirimKeQC.count {
                            it.statusKain == "Menunggu QC"
                        }
                    )

                    Toast.makeText(
                        requireContext(),
                        "Inspeksi QC berhasil disimpan",
                        Toast.LENGTH_LONG
                    ).show()

                    dialog.dismiss()
                }
                .show()
        }
    }
    private fun showErrorDialog(
        pesan: String
    ) {

        MaterialAlertDialogBuilder(
            requireContext()
        )
            .setTitle("Data Belum Lengkap")
            .setMessage(
                "$pesan\n\nSilakan lengkapi data terlebih dahulu."
            )
            .setPositiveButton(
                "KEMBALI",
                null
            )
            .show()
    }
    private val cameraLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val bitmap =
                    result.data?.extras?.get("data") as? Bitmap

                bitmap?.let {

                    targetImageView?.setImageBitmap(it)

                    when (targetImageView?.id) {

                        R.id.imgSebelum ->
                            fotoSebelumBitmap = it

                        R.id.imgSesudah ->
                            fotoSesudahBitmap = it

                        R.id.imgTambahan ->
                            fotoTambahanBitmap = it
                    }
                }
            }
        }
}