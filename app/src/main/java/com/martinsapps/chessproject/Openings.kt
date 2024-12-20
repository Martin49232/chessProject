package com.martinsapps.chessproject


import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
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


        moveBackButton.setOnClickListener {
            if(chessBoard.previousMovesList.size-1>0) {
                chessBoard.previousMovesMovedBackList.add(chessBoard.previousMovesList.removeAt(chessBoard.previousMovesList.size-1))
                chessBoard.fen=chessBoard.previousMovesList.last()
                for (piece in chessBoard.pieces){
                    constraintLayout.removeView(piece)
                }
                chessBoard.pieces = mutableListOf()
                greenSquareFactory.removeSquares()
                chessBoard.removeGreenSquare(constraintLayout)
                val splitFen = chessBoard.fen.split(" ")
                chessBoard.turn = splitFen[1] == "w"
                drawPieces(chessBoard.fen.toCharArray(), chessBoardStartY, 0F, squareSide.toFloat(), constraintLayout, squareSide, chessBoard, greenSquareFactory, color)
            }
        }

        moveForwardButton.setOnClickListener {
            if(chessBoard.previousMovesMovedBackList.size>0){
                chessBoard.fen= chessBoard.previousMovesMovedBackList.removeAt(chessBoard.previousMovesMovedBackList.size-1)
                chessBoard.previousMovesList.add(chessBoard.fen)
                for (piece in chessBoard.pieces){
                    constraintLayout.removeView(piece)
                }
                chessBoard.pieces = mutableListOf()
                greenSquareFactory.removeSquares()
                chessBoard.removeGreenSquare(constraintLayout)
                val splitFen = chessBoard.fen.split(" ")
                chessBoard.turn = splitFen[1] == "w"
                drawPieces(chessBoard.fen.toCharArray(), chessBoardStartY, 0F, squareSide.toFloat(), constraintLayout, squareSide, chessBoard, greenSquareFactory, color)
            }
        }


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

        fun createPiece(type: Int): ChessPiece{
            return ChessPiece(type, this, constraintLayout,color, chessBoard, greenSquareFactory)
        }

        if (color == 0) {
            var currentSquareX = startX
            var currentSquareY = startY
            for (i in FEN.indices) {
                try {
                    if (FEN[i] == 'r') {
                        val piece = createPiece(ChessPiece.BLACK_ROOK).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'n') {
                        val piece = createPiece(ChessPiece.BLACK_KNIGHT).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'b') {
                        val piece = createPiece(ChessPiece.BLACK_BISHOP).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'q') {
                        val piece = createPiece(ChessPiece.BLACK_QUEEN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'k') {
                        val piece = createPiece(ChessPiece.BLACK_KING).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'p') {
                        val piece = createPiece(ChessPiece.BLACK_PAWN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'R') {
                        val piece = createPiece(ChessPiece.WHITE_ROOK).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'N') {
                        val piece = createPiece(ChessPiece.WHITE_KNIGHT).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'B') {
                        val piece = createPiece(ChessPiece.WHITE_BISHOP).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'Q') {
                        val piece = createPiece(ChessPiece.WHITE_QUEEN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'K') {
                        val piece = createPiece(ChessPiece.WHITE_KING).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'P') {
                        val piece = createPiece(ChessPiece.WHITE_PAWN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
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
                        val piece = createPiece(ChessPiece.BLACK_ROOK).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'n') {
                        val piece = createPiece(ChessPiece.BLACK_KNIGHT).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'b') {
                        val piece = createPiece(ChessPiece.BLACK_BISHOP).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'q') {
                        val piece = createPiece(ChessPiece.BLACK_QUEEN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'k') {
                        val piece = createPiece(ChessPiece.BLACK_KING).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'p') {
                        val piece = createPiece(ChessPiece.BLACK_PAWN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'R') {
                        val piece = createPiece(ChessPiece.WHITE_ROOK).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'N') {
                        val piece = createPiece(ChessPiece.WHITE_KNIGHT).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'B') {
                        val piece = createPiece(ChessPiece.WHITE_BISHOP).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'Q') {
                        val piece = createPiece(ChessPiece.WHITE_QUEEN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'K') {
                        val piece = createPiece(ChessPiece.WHITE_KING).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                        chessBoard.pieces.add(piece)
                    }
                    if (FEN[i] == 'P') {
                        val piece = createPiece(ChessPiece.WHITE_PAWN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
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