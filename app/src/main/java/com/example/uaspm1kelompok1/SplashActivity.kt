package com.example.uaspm1kelompok1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var logo: ImageView
    private lateinit var fillAnimation: Animation

    companion object {
        private const val SPLASH_DURATION = 5000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        initViews()
        loadAnimation()
        startFillAnimation()
        startSplashTimer()
    }

    private fun initViews() {
        logo = findViewById(R.id.ivLogoSplash)
    }

    private fun loadAnimation() {
        fillAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
    }

    private fun startFillAnimation() {
        logo.startAnimation(fillAnimation)
    }

    private fun startSplashTimer() {
        Handler(Looper.getMainLooper()).postDelayed({
            logo.clearAnimation()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DURATION)
    }
}