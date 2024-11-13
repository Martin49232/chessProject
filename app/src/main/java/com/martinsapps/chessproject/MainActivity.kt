package com.martinsapps.chessproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
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
    }
}