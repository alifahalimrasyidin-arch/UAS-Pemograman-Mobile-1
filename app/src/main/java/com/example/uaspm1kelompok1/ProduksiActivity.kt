package com.example.uaspm1kelompok1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ProduksiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_produksi
        )

        supportActionBar?.hide()

        if (savedInstanceState == null) {

            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    ProduksiFragment()
                )
                .commit()
        }
    }
}