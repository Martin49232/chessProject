package com.martinsapps.chessproject


import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlin.math.round


class Openings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.openings_layout)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.cl)
        val screenWidth = getScreenWidth()
        val screenHeight = getScreenHeight()
        supportActionBar?.hide()
        changeTheAnnoyingBar()

        val moveBackButton = findViewById<Button>(R.id.moveBack)
        val moveForwardButton = findViewById<Button>(R.id.moveForward)

        val backButton = findViewById<Button>(R.id.back)
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }

        val intent = intent
        val color = intent.getIntExtra("Color", 0)

        val screenRatio = screenWidth.toFloat()/screenHeight.toFloat()
        val chessBoardStartY= round((screenHeight/(screenRatio*7)))
        val squareSide = screenWidth/8

        val chessBoard = ChessBoard(color)
        val greenSquareFactory = GreenSquareFactory(this, constraintLayout, chessBoard)


        //moveBackButton.setOnClickListener {
            /*chessBoard.previousMovesList.removeAt(chessBoard.previousMovesList.size-1)
            chessBoard.fen=chessBoard.previousMovesList.last()
            for (piece in chessBoard.pieces){
                constraintLayout.removeView(piece)
            }
            chessBoard.pieces = mutableListOf()
            drawPieces(chessBoard.fen.toCharArray(), chessBoardStartY, 0F, squareSide.toFloat(), constraintLayout, squareSide, chessBoard, greenSquareFactory, color)*/
        //}


        val imageView = ImageView(this)
        imageView.setImageDrawable(ContextCompat.getDrawable(this,(R.drawable.green_chess_board)))
        constraintLayout.addView(imageView)
        imageView.y = chessBoardStartY
        imageView.layoutParams.height = screenWidth
        imageView.layoutParams.width = screenWidth


        val FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"

        drawPieces(FEN.toCharArray(), chessBoardStartY, 0F, squareSide.toFloat(), constraintLayout, squareSide, chessBoard, greenSquareFactory, color)
        chessBoard.generateFen(color)
    }
    private fun changeTheAnnoyingBar(){
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.black)
    }

    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    private fun drawPieces(FEN: CharArray, startY:Float, startX:Float, squareSize: Float, constraintLayout:ConstraintLayout, pieceSize: Int, chessBoard:ChessBoard, greenSquareFactory: GreenSquareFactory, color: Int){
        chessBoard.pieces = mutableListOf()

        if (color == 0) {
            var currentSquareX = startX
            var currentSquareY = startY
            for (i in FEN.indices) {
                try {
                    if (FEN[i] == 'r') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_ROOK,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'n') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_KNIGHT,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'b') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_BISHOP,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'q') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_QUEEN,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'k') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_KING,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'p') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_PAWN,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'R') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_ROOK,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'N') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_KNIGHT,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'B') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_BISHOP,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'Q') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_QUEEN,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'K') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_KING,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'P') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_PAWN,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == '/') {
                        currentSquareY += squareSize
                        currentSquareX = startX
                        continue
                    }
                    if (FEN[i].isDigit()) {
                        for (j in FEN[i].digitToInt() downTo 2) {
                            currentSquareX += squareSize
                        }
                    }
                    currentSquareX += squareSize
                } catch (_: ArrayIndexOutOfBoundsException) {
                }
            }
        }else{
            var currentSquareX = startX+getScreenWidth()-getScreenWidth()/8
            var currentSquareY = startY+getScreenWidth()-getScreenWidth()/8
            for (i in FEN.indices) {
                try {
                    if (FEN[i] == 'r') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_ROOK,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'n') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_KNIGHT,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'b') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_BISHOP,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'q') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_QUEEN,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'k') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_KING,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'p') {
                        val piece = ChessPiece(
                            ChessPiece.BLACK_PAWN,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'R') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_ROOK,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'N') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_KNIGHT,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'B') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_BISHOP,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'Q') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_QUEEN,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'K') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_KING,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'P') {
                        val piece = ChessPiece(
                            ChessPiece.WHITE_PAWN,
                            this,
                            constraintLayout,color,
                            chessBoard,
                            greenSquareFactory
                        ).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == '/') {
                        currentSquareY -= squareSize
                        currentSquareX = startX+getScreenWidth()-getScreenWidth()/8
                        continue
                    }
                    if (FEN[i].isDigit()) {
                        for (j in FEN[i].digitToInt() downTo 2) {
                            currentSquareX -= squareSize
                        }
                    }
                    currentSquareX -= squareSize
                } catch (_: ArrayIndexOutOfBoundsException) {
                }
            }

        }
    }



}