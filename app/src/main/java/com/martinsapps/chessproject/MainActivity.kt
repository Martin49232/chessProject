package com.martinsapps.chessproject

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import com.martinsapps.chessproject.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.exp

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

        val dbHandler = DbHandler(applicationContext, "openings.db", null, 1)
        if (!checkDB()) {
            copyDataBase()
            val milliseconds = System.currentTimeMillis()
            dbHandler.updateStreak(1, milliseconds)
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
        val settings = findViewById<ImageButton>(R.id.settings)
        val lastPlayed = findViewById<ImageButton>(R.id.lastPlayed)
        val streak = findViewById<ImageButton>(R.id.fire)

        /*titleLayout.requestLayout()
        titleLayout.layoutParams.width = screenWidth
        titleLayout.layoutParams.height = screenHeight/8

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


        streak.requestLayout()
        streak.layoutParams.width = screenWidth/4
        streak.layoutParams.height = screenWidth/4

        val title = findViewById<TextView>(R.id.title)
        title.requestLayout()
        title.textSize = 32F
        title.layoutParams.width = screenWidth/2
        title.layoutParams.height = screenHeight/8

        val logo = findViewById<ImageView>(R.id.logo)
        logo.requestLayout()
        logo.layoutParams.width = screenWidth/4
        logo.layoutParams.height = screenWidth/4
        */



        val timeStamp = dbHandler.getLastTimeStamp()


        val now = LocalDateTime.now()

        val yearNow = now.year
        val datOfMonthNow = now.dayOfMonth
        val monthOfYearNow = now.month

        val instant = Instant.ofEpochMilli(timeStamp)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())

        val year = zonedDateTime.year
        val monthOfYear = zonedDateTime.monthValue
        val dayOfMonth = zonedDateTime.dayOfMonth


        val startDate = LocalDate.of(year, monthOfYear, dayOfMonth)
        val endDate = LocalDate.of(yearNow, monthOfYearNow, datOfMonthNow)

        val daysBetween = ChronoUnit.DAYS.between(startDate, endDate)

        if (daysBetween == 1L){
            var streakCurrent = dbHandler.getStreak()
            streakCurrent +=1
            val milliseconds = System.currentTimeMillis()
            dbHandler.updateStreak(streakCurrent, milliseconds)
        }
        else if (daysBetween > 1){
            val milliseconds = System.currentTimeMillis()
            dbHandler.updateStreak(1, milliseconds)
        }else if (daysBetween < 0){
            val milliseconds = System.currentTimeMillis()
            dbHandler.updateStreak(1, milliseconds)
        }

        val currentStreak = dbHandler.getStreak()
        changeSvgFillColor(streak, R.drawable.streak_flame, 255,255,255, (255 * (1 - exp((-0.05* currentStreak)))).toInt())



        settings.setOnClickListener {
            //Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Settings::class.java)
            this.startActivity(intent)
            finish()
        }

        lastPlayed.setOnClickListener {
            //Toast.makeText(this, "Last Trained Openings", Toast.LENGTH_SHORT).show()
            val opening = dbHandler.getLastPlayed()
            if (opening == null) {
                Toast.makeText(this, "You need to play an opening first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val dbName = opening["table_name"].toString()
            val color = opening["color"].toString().toInt()
            val name = opening["name"].toString()

            val intent = Intent(this, Openings::class.java)
            intent.putExtra("opening", dbName)
            intent.putExtra("color", color)
            intent.putExtra("name", name)

            this.startActivity(intent)
            finish()

        }

        streak.setOnClickListener {
            val streakNumber = dbHandler.getStreak()

            val string = "Your current streak is $streakNumber days"
            Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeSvgFillColor(imageButton: ImageButton, svgDrawableRes: Int, red: Int, green: Int, blue: Int, alpha: Int) {
        //Get the SVG drawable from resources
        val drawable: Drawable? = AppCompatResources.getDrawable(imageButton.context, svgDrawableRes)

        if (drawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(drawable)
            val color = Color.argb(alpha, red, green, blue)

            DrawableCompat.setTint(wrappedDrawable, color)

            imageButton.setImageDrawable(wrappedDrawable)
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