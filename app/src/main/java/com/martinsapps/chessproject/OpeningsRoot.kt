package com.martinsapps.chessproject

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
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
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.R.anim.abc_popup_enter
import androidx.appcompat.R.anim.abc_popup_exit
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
        enableEdgeToEdge()
        setContentView(R.layout.activity_openings_root)
        // Set the enter and exit transitions
        overridePendingTransition(
            abc_popup_enter,
            abc_popup_exit
        )
        val dbHandler = DbHandler(applicationContext, "openings.db", null, 1)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.panel)
        supportActionBar?.hide()

        val backButton = findViewById<ImageButton>(R.id.back)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }

        val intent = intent
        //white is 0
        //black is 1
        color = intent.getIntExtra("color", 0)

        val openings = dbHandler.getAllTableNames()

        val screenWidth = getScreenWidth()
        val screenHeight = getScreenHeight()

        //val cl = findViewById<ConstraintLayout>(R.id.constraintLayout)
        //val sv = findViewById<ScrollView>(R.id.scrollView)
        val scrollViewLinearLayout = findViewById<LinearLayout>(R.id.scrollViewLinearLayout)

        //val buttonList = mutableListOf<ImageButton>()

        val titleLayout = findViewById<LinearLayout>(R.id.titleLayout)
        titleLayout.requestLayout()
        titleLayout.layoutParams.width = screenWidth
        titleLayout.layoutParams.height = screenHeight/8

        backButton.requestLayout()
        backButton.layoutParams.width = screenWidth/4
        backButton.layoutParams.height = screenWidth/4

        val title = findViewById<TextView>(R.id.title)
        title.requestLayout()
        title.textSize = 32F
        title.layoutParams.width = screenWidth/2
        title.layoutParams.height = screenHeight/8

        val logo = findViewById<ImageView>(R.id.logo)
        logo.requestLayout()
        logo.layoutParams.width = screenWidth/4
        logo.layoutParams.height = screenWidth/4


        // Loop through items, adding rows of two
        for (i in openings.indices step 2) {
            // Create a horizontal row
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

            // Add first item to the row
            rowLayout.addView(createItemView(openings[i]))

            // Add second item to the row if it exists
            if (i + 1 < openings.size) {
                rowLayout.addView(createItemView(openings[i+1]))
            } else {
                //Add an empty spacer to balance the row
                rowLayout.addView(spacerView(this))
            }

            // Add the row to the container
            scrollViewLinearLayout.addView(rowLayout)
        }
    }

    // Function to create individual item views
    private fun createItemView(dbName: String): View {
        val itemLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 8)
        }

        val imageButton = ImageButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(400, 400)
            setImageResource(getImage(dbName))
            scaleType = ImageView.ScaleType.FIT_CENTER
            if (color == 0){
                setBackgroundResource(android.R.color.white)
            }else{
                setBackgroundResource(android.R.color.black)
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

    // Function to create a spacer view
    private fun spacerView(context: Context): View {
        return View(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
        }
    }



    private fun getName(dbName: String): String {
        val names = mapOf(
            "fried_liver" to "Fried Liver",
            "najdorf" to "Najdorf",
            "giuoco_piano" to "Giuoco Piano",
            "giuoco_piano_center_attack" to "Giuoco Piano Center Attack"
        )
        return names[dbName]!!
    }

    private fun getImage(dbName: String): Int {
        val images = mapOf(
            "fried_liver" to R.drawable.fried_liver,
            "najdorf" to R.drawable.sicilian_najdorf,
            "giuoco_piano" to R.drawable.giuoco_piano,
            "giuoco_piano_center_attack" to R.drawable.giuoco_piano_center_attack
        )
        return images[dbName]!!
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