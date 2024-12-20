package com.martinsapps.chessproject

import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.martinsapps.chessproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.black)
        supportActionBar?.hide()
        val screenWidth = getScreenWidth()
        val screenHeight = getScreenHeight()

        val whiteOpenings = findViewById<ImageButton>(R.id.white)
        whiteOpenings.setOnClickListener {
            val intent = Intent(this, Openings::class.java)
            intent.putExtra("Color", 0);
            this.startActivity(intent)
        }
        val blackOpenings = findViewById<ImageButton>(R.id.black)
        blackOpenings.setOnClickListener {
            val intent = Intent(this, Openings::class.java)
            intent.putExtra("Color", 1);
            this.startActivity(intent)
        }

        val titleLayout = findViewById<LinearLayout>(R.id.titleLayout)
        val bottomLayout = findViewById<LinearLayout>(R.id.bottomLinearLayout)
        val settings = findViewById<Button>(R.id.settings)
        val lastPlayed = findViewById<Button>(R.id.lastPlayed)

        titleLayout.requestLayout()
        titleLayout.layoutParams.width = screenWidth
        titleLayout.layoutParams.height = screenHeight/8

        bottomLayout.requestLayout()
        bottomLayout.layoutParams.width = screenWidth
        bottomLayout.layoutParams.height = screenHeight/8

        settings.requestLayout();
        settings.layoutParams.height = screenHeight/16
        settings.layoutParams.width = (screenWidth/2.5).toInt()

        lastPlayed.requestLayout();
        lastPlayed.layoutParams.height = screenHeight/16
        lastPlayed.layoutParams.width = (screenWidth/2.5).toInt()

        whiteOpenings.requestLayout();
        whiteOpenings.layoutParams.height = screenWidth/2
        whiteOpenings.layoutParams.width = screenWidth/2

        blackOpenings.requestLayout();
        blackOpenings.layoutParams.height = screenWidth/2
        blackOpenings.layoutParams.width = screenWidth/2
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
}