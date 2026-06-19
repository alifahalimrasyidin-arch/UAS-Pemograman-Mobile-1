package com.example.uaspm1kelompok1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SuratPerintahActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surat_perintah)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SuratPerintahFragment())
                .commit()
        }
    }
}
