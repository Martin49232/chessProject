package com.martinsapps.chessproject

import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Explode
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Set the enter and exit transitions
        //overridePendingTransition(
        //    androidx.appcompat.R.anim.abc_popup_enter,
        //    abc_popup_exit
        //)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.panel)
        supportActionBar?.hide()
        val screenWidth = getScreenWidth()
        val screenHeight = getScreenHeight()


        if (!checkDB()){
            copyDataBase()
        }

        val whiteOpenings = findViewById<ImageButton>(R.id.white)
        whiteOpenings.setOnClickListener {
            val intent = Intent(this, OpeningsRoot::class.java)
            intent.putExtra("color", 0)
            this.startActivity(intent)
            finish()
        }
        val blackOpenings = findViewById<ImageButton>(R.id.black)
        blackOpenings.setOnClickListener {
            val intent = Intent(this, OpeningsRoot::class.java)
            intent.putExtra("color", 1);
            this.startActivity(intent)
            finish()
        }

        val titleLayout = findViewById<LinearLayout>(R.id.titleLayout)
        val bottomLayout = findViewById<LinearLayout>(R.id.bottomLinearLayout)
        val settings = findViewById<Button>(R.id.settings)
        val lastPlayed = findViewById<Button>(R.id.lastPlayed)

        titleLayout.requestLayout()
        titleLayout.layoutParams.width = screenWidth
        titleLayout.layoutParams.height = screenHeight/8


        val title = findViewById<TextView>(R.id.title)
        title.requestLayout()
        title.textSize = 32F
        title.layoutParams.width = screenWidth/2
        title.layoutParams.height = screenHeight/8

        bottomLayout.requestLayout()
        bottomLayout.layoutParams.width = screenWidth
        bottomLayout.layoutParams.height = screenHeight / 8

        settings.requestLayout();
        settings.layoutParams.height = screenHeight / 16
        settings.layoutParams.width = (screenWidth / 2.5).toInt()

        lastPlayed.requestLayout();
        lastPlayed.layoutParams.height = screenHeight / 16
        lastPlayed.layoutParams.width = (screenWidth / 2.5).toInt()

        whiteOpenings.requestLayout();
        whiteOpenings.layoutParams.height = screenWidth / 2
        whiteOpenings.layoutParams.width = screenWidth / 2

        blackOpenings.requestLayout();
        blackOpenings.layoutParams.height = screenWidth / 2
        blackOpenings.layoutParams.width = screenWidth / 2


        settings.setOnClickListener{

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


    private fun checkDB(): Boolean {
        val dbFile: File = applicationContext.getDatabasePath("openings.db")
        return dbFile.exists()
    }

    private fun copyDataBase() {
        try {
            val mInput: InputStream = applicationContext.assets.open("openings.db")
            val outFileName: String =
                "/data/data/com.martinsapps.chessproject/databases/" + "openings.db"
            val mOutput: OutputStream = FileOutputStream(outFileName)
            val mBuffer = ByteArray(1024)
            var mLength: Int
            while (mInput.read(mBuffer).also { mLength = it } > 0) {
                mOutput.write(mBuffer, 0, mLength)
            }
            mOutput.flush()
            mOutput.close()
            mInput.close()
        } catch (e: IOException) {
            println(e)
        }
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.popup_enter, R.anim.popup_exit)
    }
}