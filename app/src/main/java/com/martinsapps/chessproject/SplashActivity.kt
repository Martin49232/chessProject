package com.martinsapps.chessproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.panel)
        supportActionBar?.hide()
        val logo = findViewById<ImageView>(R.id.logo)

        //loads animation from anim folder

        val animation = AnimationUtils.loadAnimation(this, R.anim.logo_enter)
        logo.startAnimation(animation)

        //Delays the start of mainActivity

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1000)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.popup_enter, R.anim.popup_exit)
    }
}