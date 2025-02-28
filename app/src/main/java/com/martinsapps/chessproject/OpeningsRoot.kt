package com.martinsapps.chessproject

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.martinsapps.chessproject.databinding.ActivityMainBinding
import kotlin.properties.Delegates

class OpeningsRoot : AppCompatActivity() {

    private var color: Int = 0
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_openings_root)
        // Set the enter and exit transitions
        //overridePendingTransition(
        //    abc_popup_enter,
        //    abc_popup_exit
        //)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@OpeningsRoot, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })




        val dbHandler = DbHandler(applicationContext, "openings.db", null, 1)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.panel)
        supportActionBar?.hide()

        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
            finish()
        }

        val intent = intent
        //white is 0
        //black is 1
        color = intent.getIntExtra("color", 0)

        val openings = dbHandler.getAllTableNames()

        val screenWidth = getScreenWidth()
        val screenHeight = getScreenHeight()

        val scrollViewLinearLayout = findViewById<LinearLayout>(R.id.scrollViewLinearLayout)


        for (i in openings.indices step 2) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    screenWidth,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
                gravity = Gravity.CENTER_HORIZONTAL
            }

            rowLayout.addView(createItemView(openings[i]))

            if (i + 1 < openings.size) {
                rowLayout.addView(createItemView(openings[i+1]))
            } else {
                rowLayout.addView(spacerView(this))
            }

            scrollViewLinearLayout.addView(rowLayout)
        }
    }

    private fun createItemView(dbName: String): View {
        val itemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 8)
        }

        val imageButton = ImageButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(400, 400)
            getImage(dbName)?.let { setImageResource(it) }
            scaleType = ImageView.ScaleType.FIT_CENTER
            if (color == 0){
                setBackgroundResource(android.R.color.white)
            }else{
                setBackgroundResource(R.color.buttonMargin)
            }
            setPadding(20,20,20,20)
            setOnClickListener {
                Toast.makeText(context, "Clicked: ${getName(dbName)}", Toast.LENGTH_SHORT).show()
            }
        }
        imageButton.setOnClickListener {
            val intent = Intent(this, Openings::class.java)
            intent.putExtra("opening", dbName)
            intent.putExtra("color", color)
            intent.putExtra("name", getName(dbName))
            this.startActivity(intent)
            finish()
        }

        val textView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = getName(dbName)
            textSize = 14f
            gravity = Gravity.CENTER
        }

        itemLayout.addView(imageButton)
        itemLayout.addView(textView)

        return itemLayout
    }

    private fun spacerView(context: Context): View {
        return View(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
        }
    }



    private fun getName(dbName: String): String? {
        val names = mapOf(
            "fried_liver" to "Fried Liver",
            "najdorf" to "Najdorf",
            "giuoco_piano" to "Giuoco Piano",
            "giuoco_piano_center_attack" to "Giuoco Piano Center Attack",
            "giuoco_piano_greco_attack" to "Giuoco Piano Greco Attack",
            "giuoco_pianissimo" to "Giuoco Pianissimo",
            "giuoco_piano_second_line" to "Giuoco Piano Second Line",
            "evans_gambit" to "Evans Gambit",
            "evans_accepted_anderssen" to "Evans Gambit Anderssen",
            "evans_declined" to "Evans Declined",
            "classical_nimzo_indian_defence" to "Classical Nimzo Indian Defence"
        )
        return if (names[dbName] != null) {
            names[dbName]
        }
        else{
            "Name not found"
        }
    }

    private fun getImage(dbName: String): Int? {
        val images = mapOf(
            "fried_liver" to R.drawable.fried_liver,
            "najdorf" to R.drawable.sicilian_najdorf,
            "giuoco_piano" to R.drawable.giuoco_piano,
            "giuoco_piano_center_attack" to R.drawable.giuoco_piano_center_attack,
            "giuoco_piano_greco_attack" to R.drawable.giuoco_piano_greco_attack,
            "giuoco_pianissimo" to R.drawable.giuoco_pianissimo,
            "giuoco_piano_second_line" to R.drawable.giuoco_piano_second_line,
            "evans_gambit" to R.drawable.evans_gambit,
            "evans_accepted_anderssen" to R.drawable.evans_accepted_anderssen,
            "evans_declined" to R.drawable.evans_declined,
            "classical_nimzo_indian_defence" to R.drawable.classical_nimzo_indian_defence
        )
        return if (images[dbName] != null) {
            images[dbName]
        }
        else{
            return R.drawable.image_not_found
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