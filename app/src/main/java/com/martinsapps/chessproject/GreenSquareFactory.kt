package com.martinsapps.chessproject

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.io.IOException


class GreenSquareFactory(context: Context, constraintLayout: ConstraintLayout, chessBoard: ChessBoard) {
    private var greenSquareList = mutableListOf<ImageView>()
    private val context: Context
    private val constraintLayout: ConstraintLayout
    private val chessBoard: ChessBoard
    init {
        this.greenSquareList = mutableListOf<ImageView>()
        this.context = context
        this.constraintLayout = constraintLayout
        this.chessBoard = chessBoard
    }

    /*
    the class is called GreenSquareFactory because at first there was only the green square. Than I added the other sprites. All the function
    just create imageViews given contexty and constraintLayout instance. Add these Views into greenSquareList (ironic) that is needed for deletion.
     */

    fun addHollowCircle(squareNumber: Int){
        val hollowCircleImageView = ImageView(context)
        hollowCircleImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_circle_green))
        hollowCircleImageView.y = this.chessBoard.squareCoordinates[squareNumber][1].toFloat()
        hollowCircleImageView.x = this.chessBoard.squareCoordinates[squareNumber][0].toFloat()
        hollowCircleImageView.setAlpha(60)
        constraintLayout.addView(hollowCircleImageView)
        hollowCircleImageView.layoutParams.height = chessBoard.squareSize
        hollowCircleImageView.layoutParams.width = chessBoard.squareSize
        greenSquareList.add(hollowCircleImageView)
    }

    fun addDot(squareNumber: Int){
        val settings = chessBoard.dbHandler.getSettings() ?: throw IOException()
        val legalMoves = settings["legal_moves"].toString().toInt()
        if (legalMoves == 1){
            val dotImageView = ImageView(context)
            dotImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dot_green))
            dotImageView.y = this.chessBoard.squareCoordinates[squareNumber][1].toFloat()+chessBoard.squareSize/3
            dotImageView.x = this.chessBoard.squareCoordinates[squareNumber][0].toFloat()+chessBoard.squareSize/3
            dotImageView.setAlpha(60)
            constraintLayout.addView(dotImageView)
            dotImageView.layoutParams.height = chessBoard.squareSize/3
            dotImageView.layoutParams.width = chessBoard.squareSize/3
            greenSquareList.add(dotImageView)
        }
    }

    fun addRedSquare(coordinates: FloatArray){
        val greenSquareImageView = ImageView(context)
        greenSquareImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.red_square))
        greenSquareImageView.x = coordinates[0]
        greenSquareImageView.y = coordinates[1]
        constraintLayout.addView(greenSquareImageView)
        greenSquareImageView.layoutParams.height = chessBoard.squareSize
        greenSquareImageView.layoutParams.width = chessBoard.squareSize

        /*Handler(Looper.getMainLooper()).postDelayed({
            // Hide the ImageView
            constraintLayout.removeView(greenSquareImageView)
        }, 500)*/

            val fadeOut = AlphaAnimation(1f, 0f).apply {
                interpolator = AccelerateInterpolator()
                duration = 500
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        greenSquareImageView.visibility = View.GONE
                        constraintLayout.removeView(greenSquareImageView)
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}

                    override fun onAnimationStart(animation: Animation?) {}
                })
            }
        greenSquareImageView.startAnimation(fadeOut)
    }
    fun addGreenSquare(squareNumber: Int){
        val greenSquareImageView = ImageView(context)
        greenSquareImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.green_square))
        greenSquareImageView.x = this.chessBoard.squareCoordinates[squareNumber][0].toFloat()
        greenSquareImageView.y = this.chessBoard.squareCoordinates[squareNumber][1].toFloat()
        //greenSquareImageView.alpha = 2F
        constraintLayout.addView(greenSquareImageView)
        greenSquareImageView.layoutParams.height = chessBoard.squareSize
        greenSquareImageView.layoutParams.width = chessBoard.squareSize
        greenSquareList.add(greenSquareImageView)
    }

    fun removeSquares(){
        for (square in this.greenSquareList){
            constraintLayout.removeView(square)
        }
        this.greenSquareList = mutableListOf()
    }
}