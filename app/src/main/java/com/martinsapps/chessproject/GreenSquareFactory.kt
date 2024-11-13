package com.martinsapps.chessproject

import android.content.Context
import android.media.Image
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

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

    fun addHollowCircle(squareNumber: Int){
        val hollowCircleImageView = ImageView(context)
        hollowCircleImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.map_circle_green))
        hollowCircleImageView.y = this.chessBoard.squareCoordinates[squareNumber][1].toFloat()
        hollowCircleImageView.x = this.chessBoard.squareCoordinates[squareNumber][0].toFloat()
        hollowCircleImageView.setAlpha(45)
        constraintLayout.addView(hollowCircleImageView)
        hollowCircleImageView.layoutParams.height = chessBoard.squareSize
        hollowCircleImageView.layoutParams.width = chessBoard.squareSize
        greenSquareList.add(hollowCircleImageView)
    }

    fun addDot(squareNumber: Int){
        val dotImageView = ImageView(context)
        dotImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.dot_green))
        dotImageView.y = this.chessBoard.squareCoordinates[squareNumber][1].toFloat()+chessBoard.squareSize/3
        dotImageView.x = this.chessBoard.squareCoordinates[squareNumber][0].toFloat()+chessBoard.squareSize/3
        dotImageView.setAlpha(45)
        constraintLayout.addView(dotImageView)
        dotImageView.layoutParams.height = chessBoard.squareSize/3
        dotImageView.layoutParams.width = chessBoard.squareSize/3
        greenSquareList.add(dotImageView)
    }

    fun removeSquares(){
        for (square in this.greenSquareList){
            constraintLayout.removeView(square)
        }
        this.greenSquareList = mutableListOf()
    }
}