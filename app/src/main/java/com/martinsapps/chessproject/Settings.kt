package com.martinsapps.chessproject

import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.martinsapps.chessproject.databinding.ActivityMainBinding
import java.io.IOException

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
        //    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        //    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
        //    insets
        //}

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.panel)
        supportActionBar?.hide()

        val screenWidth = getScreenWidth()
        val screenHeight = getScreenHeight()

        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish()
        }

        val dbHandler = DbHandler(applicationContext, "openings.db", null, 1)



        val legalMovesSwitch = findViewById<SwitchCompat>(R.id.switch_legal_moves)
        val soundEffectsSwitch = findViewById<SwitchCompat>(R.id.switch_sound_effects)

        val settings = dbHandler.getSettings() ?: throw IOException()
       // val theme = settings["chessboard"]
        val legalMoves = settings["legal_moves"]
        val soundEffects = settings["sound_effects"]

        if (legalMoves == 1){
            legalMovesSwitch.isChecked = true
        }
        if (soundEffects == 1){
            soundEffectsSwitch.isChecked = true
        }



        legalMovesSwitch.setOnClickListener{
            val settings = dbHandler.getSettings() ?: throw IOException()
            val theme = settings["chessboard"].toString()
            var legalMoves = settings["legal_moves"].toString().toInt()
            val soundEffects = settings["sound_effects"].toString().toInt()
            legalMoves = if (legalMoves == 1){
                0
            }else{
                1
            }

            dbHandler.updateSettings(theme, legalMoves, soundEffects)
        }
        soundEffectsSwitch.setOnClickListener{
            val settings = dbHandler.getSettings() ?: throw IOException()
            val theme = settings["chessboard"].toString()
            val legalMoves = settings["legal_moves"].toString().toInt()
            var soundEffects = settings["sound_effects"].toString().toInt()
            soundEffects = if (soundEffects == 1){
                0
            }else{
                1
            }

            dbHandler.updateSettings(theme, legalMoves, soundEffects)
        }

    }
    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Modern API: WindowMetrics
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
            val navBarInsets = insets.getInsets(WindowInsets.Type.navigationBars())
            windowMetrics.bounds.height() - navBarInsets.bottom
        } else {
            // Legacy API: DisplayMetrics
            val display = windowManager.defaultDisplay
            val usableSize = Point()
            val realSize = Point()

            display.getSize(usableSize)
            display.getRealSize(realSize)

            usableSize.y // Return the usable screen height
        }
        //return Resources.getSystem().displayMetrics.heightPixels
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.popup_enter, R.anim.popup_exit)
    }
}