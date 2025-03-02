package com.martinsapps.chessproject

import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.martinsapps.chessproject.databinding.ActivityMainBinding

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

        /*window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.panel)*/
        supportActionBar?.hide()


        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish()
        }

        val dbHandler = DbHandler(applicationContext, "openings.db", null, 1)



        val legalMovesSwitch = findViewById<SwitchCompat>(R.id.switch_legal_moves)
        val soundEffectsSwitch = findViewById<SwitchCompat>(R.id.switch_sound_effects)

        val settings = dbHandler.getSettings()
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
            val settingsSwitch = dbHandler.getSettings()
            val themeSetting = settingsSwitch["chessboard"].toString()
            var legalMovesSetting = settingsSwitch["legal_moves"].toString().toInt()
            val soundEffectsSetting = settingsSwitch["sound_effects"].toString().toInt()
            legalMovesSetting = if (legalMovesSetting == 1){
                0
            }else{
                1
            }

            dbHandler.updateSettings(themeSetting, legalMovesSetting, soundEffectsSetting)
        }
        soundEffectsSwitch.setOnClickListener{
            val settingsSwitch = dbHandler.getSettings()
            val themeSetting = settingsSwitch["chessboard"].toString()
            val legalMovesSetting = settingsSwitch["legal_moves"].toString().toInt()
            var soundEffectsSetting = settingsSwitch["sound_effects"].toString().toInt()
            soundEffectsSetting = if (soundEffectsSetting == 1){
                0
            }else{
                1
            }

            dbHandler.updateSettings(themeSetting, legalMovesSetting, soundEffectsSetting)
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