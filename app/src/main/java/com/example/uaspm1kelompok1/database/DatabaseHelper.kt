package com.example.uaspm1kelompok1.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(
    context: Context
) : SQLiteOpenHelper(

    context,

    DatabaseContract.DATABASE_NAME,

    null,

    DatabaseContract.DATABASE_VERSION

) {

    override fun onCreate(db: SQLiteDatabase) {

        // ===========================
        // TABEL USERS
        // ===========================

        db.execSQL(
            """
            CREATE TABLE ${DatabaseContract.UserTable.TABLE_NAME}(
            
            ${DatabaseContract.UserTable.ID}
            INTEGER PRIMARY KEY AUTOINCREMENT,

            ${DatabaseContract.UserTable.NAMA}
            TEXT NOT NULL,

            ${DatabaseContract.UserTable.EMAIL}
            TEXT UNIQUE NOT NULL,

            ${DatabaseContract.UserTable.NO_HP}
            TEXT NOT NULL,

            ${DatabaseContract.UserTable.GENDER}
            TEXT NOT NULL,

            ${DatabaseContract.UserTable.PASSWORD}
            TEXT NOT NULL,

            ${DatabaseContract.UserTable.ROLE}
            TEXT NOT NULL
            )
            """.trimIndent()
        )

        // ===========================
        // TABEL SURAT PERINTAH
        // ===========================

        db.execSQL(
            """
            CREATE TABLE ${DatabaseContract.SuratPerintahTable.TABLE_NAME}(
            
            ${DatabaseContract.SuratPerintahTable.ID}
            TEXT PRIMARY KEY,
            
            ${DatabaseContract.SuratPerintahTable.JENIS_KAIN}
            TEXT NOT NULL,
            
            ${DatabaseContract.SuratPerintahTable.JUMLAH}
            INTEGER NOT NULL,
            
            ${DatabaseContract.SuratPerintahTable.SATUAN}
            TEXT NOT NULL,
            
            ${DatabaseContract.SuratPerintahTable.DEADLINE}
            TEXT NOT NULL,
            
            ${DatabaseContract.SuratPerintahTable.STATUS}
            TEXT NOT NULL
            
            )
            """.trimIndent()
        )

        // ===========================
        // TABEL HASIL PRODUKSI
        // ===========================

        db.execSQL(
            """
            CREATE TABLE ${DatabaseContract.HasilProduksiTable.TABLE_NAME}(
            
            ${DatabaseContract.HasilProduksiTable.ID}
            INTEGER PRIMARY KEY AUTOINCREMENT,
            
            ${DatabaseContract.HasilProduksiTable.SP_ID}
            TEXT NOT NULL,
            
            ${DatabaseContract.HasilProduksiTable.JENIS_KAIN}
            TEXT NOT NULL,
            
            ${DatabaseContract.HasilProduksiTable.JUMLAH}
            INTEGER NOT NULL,
            
            ${DatabaseContract.HasilProduksiTable.SATUAN}
            TEXT NOT NULL,
            
            ${DatabaseContract.HasilProduksiTable.TANGGAL_PRODUKSI}
            TEXT NOT NULL,
            
            ${DatabaseContract.HasilProduksiTable.PETUGAS_PRODUKSI}
            TEXT NOT NULL,
            
            ${DatabaseContract.HasilProduksiTable.STATUS_QC}
            TEXT NOT NULL,
            
            ${DatabaseContract.HasilProduksiTable.CATATAN}
            TEXT
            
            )
            """.trimIndent()
        )

        // ===========================
        // TABEL QUALITY CONTROL
        // ===========================

        db.execSQL(
            """
            CREATE TABLE ${DatabaseContract.QualityControlTable.TABLE_NAME}(
            ${DatabaseContract.QualityControlTable.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${DatabaseContract.QualityControlTable.SP_ID} TEXT NOT NULL,
            ${DatabaseContract.QualityControlTable.JENIS_KAIN} TEXT NOT NULL,
            ${DatabaseContract.QualityControlTable.JUMLAH} INTEGER NOT NULL,
            ${DatabaseContract.QualityControlTable.SATUAN} TEXT NOT NULL,
            ${DatabaseContract.QualityControlTable.NOMOR_QC} TEXT NOT NULL,
            ${DatabaseContract.QualityControlTable.TANGGAL_KIRIM} TEXT NOT NULL,
            ${DatabaseContract.QualityControlTable.TANGGAL_QC} TEXT NOT NULL,
            ${DatabaseContract.QualityControlTable.PETUGAS_QC} TEXT NOT NULL,
            ${DatabaseContract.QualityControlTable.GRADE} TEXT,
            ${DatabaseContract.QualityControlTable.STATUS_KAIN} TEXT,
            ${DatabaseContract.QualityControlTable.UJI_CUCI} INTEGER DEFAULT 0,
            ${DatabaseContract.QualityControlTable.UJI_DAYA_TAHAN} INTEGER DEFAULT 0,
            ${DatabaseContract.QualityControlTable.UJI_SUHU} INTEGER DEFAULT 0,
            ${DatabaseContract.QualityControlTable.HASIL_WARNA} TEXT,
            ${DatabaseContract.QualityControlTable.HASIL_JAHITAN} TEXT,
            ${DatabaseContract.QualityControlTable.HASIL_UKURAN} TEXT,
            ${DatabaseContract.QualityControlTable.UKURAN_AKHIR} REAL,
            ${DatabaseContract.QualityControlTable.FOTO_SEBELUM} BLOB,
            ${DatabaseContract.QualityControlTable.FOTO_SESUDAH} BLOB,
            ${DatabaseContract.QualityControlTable.FOTO_TAMBAHAN} BLOB,
            ${DatabaseContract.QualityControlTable.CATATAN} TEXT
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        db.execSQL(
            "DROP TABLE IF EXISTS ${DatabaseContract.UserTable.TABLE_NAME}"
        )

        db.execSQL(
            "DROP TABLE IF EXISTS ${DatabaseContract.SuratPerintahTable.TABLE_NAME}"
        )

        db.execSQL(
            "DROP TABLE IF EXISTS ${DatabaseContract.HasilProduksiTable.TABLE_NAME}"
        )

        db.execSQL(
            "DROP TABLE IF EXISTS ${DatabaseContract.QualityControlTable.TABLE_NAME}"
        )

        onCreate(db)
    }

// ===========================
// INSERT USER
// ===========================

    fun insertUser(

        nama: String,

        email: String,

        noHp: String,

        gender: String,

        password: String,

        role: String

    ): Boolean {

        val db = writableDatabase

        val values = android.content.ContentValues()

        values.put(
            DatabaseContract.UserTable.NAMA,
            nama
        )

        values.put(
            DatabaseContract.UserTable.EMAIL,
            email
        )

        values.put(
            DatabaseContract.UserTable.NO_HP,
            noHp
        )

        values.put(
            DatabaseContract.UserTable.GENDER,
            gender
        )

        values.put(
            DatabaseContract.UserTable.PASSWORD,
            password
        )

        values.put(
            DatabaseContract.UserTable.ROLE,
            role
        )

        val result = db.insert(
            DatabaseContract.UserTable.TABLE_NAME,
            null,
            values
        )

        db.close()

        return result != -1L
    }
// ===========================
// LOGIN USER
// ===========================

    fun loginUser(

        email: String,

        password: String

    ): android.database.Cursor {

        val db = readableDatabase

        return db.rawQuery(

            """
        SELECT *
        FROM ${DatabaseContract.UserTable.TABLE_NAME}
        WHERE
        ${DatabaseContract.UserTable.EMAIL}=?
        AND
        ${DatabaseContract.UserTable.PASSWORD}=?
        """,

            arrayOf(
                email,
                password
            )
        )
    }
// ===========================
// CEK EMAIL
// ===========================

    fun isEmailExist(

        email: String

    ): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(

            """
        SELECT *
        FROM ${DatabaseContract.UserTable.TABLE_NAME}
        WHERE
        ${DatabaseContract.UserTable.EMAIL}=?
        """,

            arrayOf(email)
        )

        val ada = cursor.count > 0

        cursor.close()

        db.close()

        return ada
    }
// ===========================
// GET ALL USER
// ===========================

    fun getAllUser(): android.database.Cursor {

        val db =
            readableDatabase

        return db.rawQuery(

            """
        SELECT *
        FROM ${DatabaseContract.UserTable.TABLE_NAME}
        ORDER BY ${DatabaseContract.UserTable.NAMA}
        ASC
        """,

            null
        )
    }
// ===========================
// DELETE USER
// ===========================

    fun deleteUser(

        email: String

    ): Boolean {

        val db = writableDatabase

        val result = db.delete(

            DatabaseContract.UserTable.TABLE_NAME,

            "${DatabaseContract.UserTable.EMAIL}=?",

            arrayOf(email)

        )

        db.close()

        return result > 0
    }
// ===========================
// UPDATE PASSWORD
// ===========================

    fun updatePassword(

        email: String,

        passwordBaru: String

    ): Boolean {

        val db = writableDatabase

        val values = android.content.ContentValues()

        values.put(

            DatabaseContract.UserTable.PASSWORD,

            passwordBaru

        )

        val result = db.update(

            DatabaseContract.UserTable.TABLE_NAME,

            values,

            "${DatabaseContract.UserTable.EMAIL}=?",

            arrayOf(email)

        )

        db.close()

        return result > 0
    }
    // ===========================
// INSERT SURAT PERINTAH
// ===========================

    fun insertSuratPerintah(

        id: String,

        jenisKain: String,

        jumlah: Int,

        satuan: String,

        deadline: String,

        status: String

    ): Boolean {

        val db =
            writableDatabase

        val values =
            android.content.ContentValues()

        values.put(
            DatabaseContract.SuratPerintahTable.ID,
            id
        )

        values.put(
            DatabaseContract.SuratPerintahTable.JENIS_KAIN,
            jenisKain
        )

        values.put(
            DatabaseContract.SuratPerintahTable.JUMLAH,
            jumlah
        )

        values.put(
            DatabaseContract.SuratPerintahTable.SATUAN,
            satuan
        )

        values.put(
            DatabaseContract.SuratPerintahTable.DEADLINE,
            deadline
        )

        values.put(
            DatabaseContract.SuratPerintahTable.STATUS,
            status
        )

        val result =
            db.insert(

                DatabaseContract.SuratPerintahTable.TABLE_NAME,

                null,

                values
            )

        db.close()

        return result != -1L
    }
    // ===========================
// GET ALL SURAT PERINTAH
// ===========================

    fun getAllSuratPerintah():

            android.database.Cursor {

        val db =
            readableDatabase

        return db.rawQuery(

            """
        SELECT *
        FROM ${DatabaseContract.SuratPerintahTable.TABLE_NAME}
        ORDER BY ${DatabaseContract.SuratPerintahTable.ID}
        ASC
        """,

            null
        )
    }
    fun getSuratPerintahById(id:String):android.database.Cursor{
        val db=readableDatabase
        return db.rawQuery(
            """
        SELECT *
        FROM ${DatabaseContract.SuratPerintahTable.TABLE_NAME}
        WHERE ${DatabaseContract.SuratPerintahTable.ID}=?
        """,
            arrayOf(id)
        )
    }
    // ===========================
// UPDATE STATUS SP
// ===========================

    fun updateStatusSuratPerintah(

        id: String,

        status: String

    ): Boolean {

        val db =
            writableDatabase

        val values =
            android.content.ContentValues()

        values.put(

            DatabaseContract.SuratPerintahTable.STATUS,

            status
        )

        val result =
            db.update(

                DatabaseContract.SuratPerintahTable.TABLE_NAME,

                values,

                "${DatabaseContract.SuratPerintahTable.ID}=?",

                arrayOf(id)
            )

        db.close()

        return result > 0
    }
    // ===========================
// DELETE SURAT PERINTAH
// ===========================

    fun deleteSuratPerintah(

        id: String

    ): Boolean {

        val db =
            writableDatabase

        val result =
            db.delete(

                DatabaseContract.SuratPerintahTable.TABLE_NAME,

                "${DatabaseContract.SuratPerintahTable.ID}=?",

                arrayOf(id)
            )

        db.close()

        return result > 0
    }
    // ===========================
// INSERT HASIL PRODUKSI
// ===========================

    fun insertHasilProduksi(

        spId: String,

        jenisKain: String,

        jumlah: Int,

        satuan: String,

        tanggalProduksi: String,

        petugasProduksi: String,

        statusQC: String,

        catatan: String

    ): Boolean {

        val db =
            writableDatabase

        val values =
            android.content.ContentValues()

        values.put(
            DatabaseContract.HasilProduksiTable.SP_ID,
            spId
        )

        values.put(
            DatabaseContract.HasilProduksiTable.JENIS_KAIN,
            jenisKain
        )

        values.put(
            DatabaseContract.HasilProduksiTable.JUMLAH,
            jumlah
        )

        values.put(
            DatabaseContract.HasilProduksiTable.SATUAN,
            satuan
        )

        values.put(
            DatabaseContract.HasilProduksiTable.TANGGAL_PRODUKSI,
            tanggalProduksi
        )

        values.put(
            DatabaseContract.HasilProduksiTable.PETUGAS_PRODUKSI,
            petugasProduksi
        )

        values.put(
            DatabaseContract.HasilProduksiTable.STATUS_QC,
            statusQC
        )

        values.put(
            DatabaseContract.HasilProduksiTable.CATATAN,
            catatan
        )

        val result =
            db.insert(

                DatabaseContract.HasilProduksiTable.TABLE_NAME,

                null,

                values
            )

        db.close()

        return result != -1L
    }
    // ===========================
// GET ALL HASIL PRODUKSI
// ===========================

    fun getAllHasilProduksi():

            android.database.Cursor {

        val db =
            readableDatabase

        return db.rawQuery(

            """
        SELECT *
        FROM ${DatabaseContract.HasilProduksiTable.TABLE_NAME}
        ORDER BY
        ${DatabaseContract.HasilProduksiTable.ID}
        DESC
        """,

            null
        )
    }
    // ===========================
// GET HASIL PRODUKSI BY SP
// ===========================

    fun getHasilProduksiBySP(

        spId: String

    ): android.database.Cursor {

        val db =
            readableDatabase

        return db.rawQuery(

            """
        SELECT *
        FROM ${DatabaseContract.HasilProduksiTable.TABLE_NAME}
        WHERE
        ${DatabaseContract.HasilProduksiTable.SP_ID}=?
        """,

            arrayOf(spId)
        )
    }
    // ===========================
// UPDATE STATUS QC
// ===========================

    fun updateStatusQC(

        spId: String,

        statusQC: String

    ): Boolean {

        val db =
            writableDatabase

        val values =
            android.content.ContentValues()

        values.put(

            DatabaseContract.HasilProduksiTable.STATUS_QC,

            statusQC
        )

        val result =
            db.update(

                DatabaseContract.HasilProduksiTable.TABLE_NAME,

                values,

                "${DatabaseContract.HasilProduksiTable.SP_ID}=?",

                arrayOf(spId)
            )

        db.close()

        return result > 0
    }
    // ===========================
// INSERT QUALITY CONTROL
// ===========================
    fun insertQualityControl(   spId:String,
                                jenisKain:String,
                                jumlah:Int,
                                satuan:String,
                                nomorQC:String,
                                tanggalKirim:String,
                                tanggalQC:String,
                                petugasQC:String,
                                grade:String,
                                statusKain:String,
                                ujiCuci:Boolean,
                                ujiDayaTahan:Boolean,
                                ujiSuhu:Boolean,
                                hasilWarna:String,
                                hasilJahitan:String,
                                hasilUkuran:String,
                                ukuranAkhir:Double,
                                fotoSebelum:ByteArray?,
                                fotoSesudah:ByteArray?,
                                fotoTambahan:ByteArray?,
                                catatan:String
    ):Boolean{
        val db=writableDatabase
        val values=android.content.ContentValues()
        values.put(DatabaseContract.QualityControlTable.SP_ID,spId)
        values.put(DatabaseContract.QualityControlTable.JENIS_KAIN,jenisKain)
        values.put(DatabaseContract.QualityControlTable.JUMLAH,jumlah)
        values.put(DatabaseContract.QualityControlTable.SATUAN,satuan)
        values.put(DatabaseContract.QualityControlTable.NOMOR_QC,nomorQC)
        values.put(DatabaseContract.QualityControlTable.TANGGAL_KIRIM,tanggalKirim)
        values.put(DatabaseContract.QualityControlTable.TANGGAL_QC,tanggalQC)
        values.put(DatabaseContract.QualityControlTable.PETUGAS_QC,petugasQC)
        values.put(DatabaseContract.QualityControlTable.GRADE,grade)
        values.put(DatabaseContract.QualityControlTable.STATUS_KAIN,statusKain)
        values.put(DatabaseContract.QualityControlTable.UJI_CUCI,if(ujiCuci)1 else 0)
        values.put(DatabaseContract.QualityControlTable.UJI_DAYA_TAHAN,if(ujiDayaTahan)1 else 0)
        values.put(DatabaseContract.QualityControlTable.UJI_SUHU,if(ujiSuhu)1 else 0)
        values.put(DatabaseContract.QualityControlTable.HASIL_WARNA,hasilWarna)
        values.put(DatabaseContract.QualityControlTable.HASIL_JAHITAN,hasilJahitan)
        values.put(DatabaseContract.QualityControlTable.HASIL_UKURAN,hasilUkuran)
        values.put(DatabaseContract.QualityControlTable.UKURAN_AKHIR,ukuranAkhir)
        values.put(DatabaseContract.QualityControlTable.FOTO_SEBELUM,fotoSebelum)
        values.put(DatabaseContract.QualityControlTable.FOTO_SESUDAH,fotoSesudah)
        values.put(DatabaseContract.QualityControlTable.FOTO_TAMBAHAN,fotoTambahan)
        values.put(DatabaseContract.QualityControlTable.CATATAN,catatan)
        val result=db.insert(DatabaseContract.QualityControlTable.TABLE_NAME,null,values)
        db.close()
        return result!=-1L
    }

    // ===========================
// GET ALL QUALITY CONTROL
// ===========================

    fun getAllQualityControl():

            android.database.Cursor {

        val db =
            readableDatabase

        return db.rawQuery(

            """
        SELECT *
        FROM ${DatabaseContract.QualityControlTable.TABLE_NAME}
        ORDER BY
        ${DatabaseContract.QualityControlTable.ID}
        DESC
        """,

            null
        )
    }
    fun getQualityControlByNoQC(noQC:String):android.database.Cursor{

        val db=readableDatabase

        return db.rawQuery(
            """
        SELECT *
        FROM ${DatabaseContract.QualityControlTable.TABLE_NAME}
        WHERE ${DatabaseContract.QualityControlTable.NOMOR_QC}=?
        LIMIT 1
        """,
            arrayOf(noQC)
        )
    }

    fun getHasilProduksiBySpId(spId: String): android.database.Cursor {

        val db = readableDatabase

        return db.rawQuery(
            """
        SELECT *
        FROM ${DatabaseContract.HasilProduksiTable.TABLE_NAME}
        WHERE ${DatabaseContract.HasilProduksiTable.SP_ID}=?
        """.trimIndent(),
            arrayOf(spId)
        )
    }
    // ===========================
// GET QC BERDASARKAN SP
// ===========================

    fun getQCBySP(

        spId: String

    ): android.database.Cursor {

        val db =
            readableDatabase

        return db.rawQuery(

            """
        SELECT *
        FROM ${DatabaseContract.QualityControlTable.TABLE_NAME}
        WHERE
        ${DatabaseContract.QualityControlTable.SP_ID}=?
        """,

            arrayOf(spId)
        )
    }
    // ===========================
// UPDATE QUALITY CONTROL
// ===========================

    fun updateQualityControl(spId:String,tanggalQC:String,petugasQC:String,grade:String,statusKain:String,ujiCuci:Boolean,ujiDayaTahan:Boolean,ujiSuhu:Boolean,hasilWarna:String,hasilJahitan:String,hasilUkuran:String,ukuranAkhir:Double,fotoSebelum:ByteArray?,fotoSesudah:ByteArray?,fotoTambahan:ByteArray?,catatan:String):Boolean{
        val db=writableDatabase
        val values=android.content.ContentValues()
        values.put(DatabaseContract.QualityControlTable.TANGGAL_QC,tanggalQC)
        values.put(DatabaseContract.QualityControlTable.PETUGAS_QC,petugasQC)
        values.put(DatabaseContract.QualityControlTable.GRADE,grade)
        values.put(DatabaseContract.QualityControlTable.STATUS_KAIN,statusKain)
        values.put(DatabaseContract.QualityControlTable.UJI_CUCI,if(ujiCuci)1 else 0)
        values.put(DatabaseContract.QualityControlTable.UJI_DAYA_TAHAN,if(ujiDayaTahan)1 else 0)
        values.put(DatabaseContract.QualityControlTable.UJI_SUHU,if(ujiSuhu)1 else 0)
        values.put(DatabaseContract.QualityControlTable.HASIL_WARNA,hasilWarna)
        values.put(DatabaseContract.QualityControlTable.HASIL_JAHITAN,hasilJahitan)
        values.put(DatabaseContract.QualityControlTable.HASIL_UKURAN,hasilUkuran)
        values.put(DatabaseContract.QualityControlTable.UKURAN_AKHIR,ukuranAkhir)
        values.put(DatabaseContract.QualityControlTable.FOTO_SEBELUM,fotoSebelum)
        values.put(DatabaseContract.QualityControlTable.FOTO_SESUDAH,fotoSesudah)
        values.put(DatabaseContract.QualityControlTable.FOTO_TAMBAHAN,fotoTambahan)
        values.put(DatabaseContract.QualityControlTable.CATATAN,catatan)
        val result=db.update(DatabaseContract.QualityControlTable.TABLE_NAME,values,"${DatabaseContract.QualityControlTable.SP_ID}=?",arrayOf(spId))
        db.close()
        return result>0
    }
    fun generateSuratPerintahId():String{
        val db=readableDatabase
        val cursor=db.rawQuery("SELECT ${DatabaseContract.SuratPerintahTable.ID} FROM ${DatabaseContract.SuratPerintahTable.TABLE_NAME} ORDER BY ${DatabaseContract.SuratPerintahTable.ID} DESC LIMIT 1",null)
        var nomor=1
        if(cursor.moveToFirst()){
            val lastId=cursor.getString(0)
            nomor=lastId.replace("SP","").toInt()+1
        }
        cursor.close()
        db.close()
        return "SP"+String.format("%03d",nomor)
    }
    // ===========================
// DELETE QUALITY CONTROL
// ===========================

    fun deleteQualityControl(

        spId: String

    ): Boolean {

        val db =
            writableDatabase

        val result =
            db.delete(

                DatabaseContract.QualityControlTable.TABLE_NAME,

                "${DatabaseContract.QualityControlTable.SP_ID}=?",

                arrayOf(spId)
            )

        db.close()

        return result > 0
    }
}
