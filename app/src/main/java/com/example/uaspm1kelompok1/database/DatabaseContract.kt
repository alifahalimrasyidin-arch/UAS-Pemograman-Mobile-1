package com.example.uaspm1kelompok1.database

object DatabaseContract {

    const val DATABASE_NAME = "TIASA.db"

    const val DATABASE_VERSION = 3

    // ===========================
    // TABEL USERS
    // ===========================

    object UserTable {

        const val TABLE_NAME = "users"

        const val ID = "id"

        const val NAMA = "nama"

        const val EMAIL = "email"

        const val NO_HP = "no_hp"

        const val GENDER = "gender"

        const val PASSWORD = "password"

        const val ROLE = "role"
    }

    // ===========================
    // TABEL SURAT PERINTAH
    // ===========================

    object SuratPerintahTable {

        const val TABLE_NAME = "surat_perintah"

        const val ID = "id"

        const val JENIS_KAIN = "jenis_kain"

        const val JUMLAH = "jumlah"

        const val SATUAN = "satuan"

        const val DEADLINE = "deadline"

        const val STATUS = "status"
    }

    // ===========================
    // TABEL HASIL PRODUKSI
    // ===========================

    object HasilProduksiTable {

        const val TABLE_NAME = "hasil_produksi"

        const val ID = "id"

        const val SP_ID = "sp_id"

        const val JENIS_KAIN = "jenis_kain"

        const val JUMLAH = "jumlah"

        const val SATUAN = "satuan"

        const val TANGGAL_PRODUKSI = "tanggal_produksi"

        const val PETUGAS_PRODUKSI = "petugas_produksi"

        const val STATUS_QC = "status_qc"

        const val CATATAN = "catatan"
    }

    // ===========================
    // TABEL QUALITY CONTROL
    // ===========================

    object QualityControlTable {
        const val TABLE_NAME = "quality_control"
        const val ID = "id"
        const val SP_ID = "sp_id"
        const val JENIS_KAIN = "jenis_kain"
        const val JUMLAH = "jumlah"
        const val SATUAN = "satuan"
        const val NOMOR_QC = "nomor_qc"
        const val TANGGAL_KIRIM = "tanggal_kirim"
        const val TANGGAL_QC = "tanggal_qc"
        const val PETUGAS_QC = "petugas_qc"
        const val GRADE = "grade"
        const val STATUS_KAIN = "status_kain"
        const val UJI_CUCI = "uji_cuci"
        const val UJI_DAYA_TAHAN = "uji_daya_tahan"
        const val UJI_SUHU = "uji_suhu"
        const val HASIL_WARNA = "hasil_warna"
        const val HASIL_JAHITAN = "hasil_jahitan"
        const val HASIL_UKURAN = "hasil_ukuran"
        const val UKURAN_AKHIR = "ukuran_akhir"
        const val FOTO_SEBELUM = "foto_sebelum"
        const val FOTO_SESUDAH = "foto_sesudah"
        const val FOTO_TAMBAHAN = "foto_tambahan"
        const val CATATAN = "catatan"
    }
}