package com.martinsapps.chessproject

import android.content.Context
import android.content.res.Resources
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlin.math.round

class ChessBoard(color: Int) {

    val squareCoordinates: Array<IntArray>
    val startY:Float
    val startX: Float
    val squareSize:Int
    var fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - "
    var pieceClicked: ImageView?
    var clickedPieceCoordinates: IntArray
    var greenSquareImageView:ImageView?
    var pieces: MutableList<ImageView>
    val color: Int
    var previousMovesList = mutableListOf<String>()
    var previousMovesMovedBackList = mutableListOf<String>()
    //true for white false for black
    var turn = true

    //castling rights
    var whiteKingsideCastle = true
    var whiteQueensideCastle = true

    var blackKingsideCastle = true
    var blackQueensideCastle = true

    //en passant square
    var enPassantSquare = false


    var moves: MutableList<Move> = mutableListOf()

    init {
        this.color = color
        val screenRatio = getScreenWidth().toFloat()/getScreenHeight().toFloat()

        this.startY= round((getScreenHeight()/(screenRatio*7)))
        this.startX = 0F
        this.squareSize = (getScreenWidth()/8)
        this.squareCoordinates = squareCoordinates(startY, startX, squareSize.toFloat())
        this.fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - "
        this.pieceClicked = null
        this.clickedPieceCoordinates = IntArray(2)
        this.greenSquareImageView = null
        this.pieces = mutableListOf()
    }
    companion object{
        val mapOfNotationToImages = mapOf(
            R.drawable.black_king to 'k',
            R.drawable.black_queen to 'q',
            R.drawable.black_bishop to 'b',
            R.drawable.black_knight to 'n',
            R.drawable.black_rook to 'r',
            R.drawable.black_pawn to 'p',

            R.drawable.white_king to 'K',
            R.drawable.white_queen to 'Q',
            R.drawable.white_bishop to 'B',
            R.drawable.white_knight to 'N',
            R.drawable.white_rook to 'R',
            R.drawable.white_pawn to 'P'
        )

        val switchedMapOfNotationToImages = mapOf(
            R.drawable.black_king to 'K',
            R.drawable.black_queen to 'Q',
            R.drawable.black_bishop to 'B',
            R.drawable.black_knight to 'N',
            R.drawable.black_rook to 'R',
            R.drawable.black_pawn to 'P',

            R.drawable.white_king to 'k',
            R.drawable.white_queen to 'q',
            R.drawable.white_bishop to 'b',
            R.drawable.white_knight to 'n',
            R.drawable.white_rook to 'r',
            R.drawable.white_pawn to 'p'
        )

        init {
            System.loadLibrary("chessproject")
        }
    }



    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    private fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    private fun squareCoordinates(startY: Float, startX: Float, squareSize: Float): Array<IntArray> {
        val squaresArray: Array<IntArray> = Array(64) { IntArray(2) }
        var currentSquareX = startX
        var currentSquareY = startY
        for (i in 1..64){
            squaresArray[i-1][0]= currentSquareX.toInt()
            squaresArray[i-1][1]= currentSquareY.toInt()
            if (i%8==0){
                currentSquareY+=squareSize
                currentSquareX=startX
                continue
            }
            currentSquareX+=squareSize
        }
        return squaresArray
    }
    fun getClosestSquare(x: Float, y: Float): IntArray {
        var closestXIndex = 0
        var closestYIndex = 0
        var distanceX = 1000F
        var distanceY = 1000F

        //get closest X
        for (i in squareCoordinates.indices) {
            val currentDistance = x - squareCoordinates[i][0]
            //it looks odd but future me leave the minus one here. There once was zero and it throws wrongly rounded values because of it
            if (currentDistance < distanceX && currentDistance >= -1) {
                distanceX = currentDistance
                closestXIndex = i
            }
        }
        //get closest Y
        for (i in squareCoordinates.indices) {
            val currentDistance = y - squareCoordinates[i][1]
            //same as the above minus 1
            if (currentDistance < distanceY && currentDistance >= -1) {
                distanceY = currentDistance
                closestYIndex = i
            }
        }
        val corX = squareCoordinates[closestXIndex][0]
        val corY = squareCoordinates[closestYIndex][1]

        //set the x and y value
        return intArrayOf(corX, corY)
    }
    fun outOfBounds(x:Int, y:Int): Boolean{
        return x > getScreenWidth() || x < 0 || y < startY || y > startY+getScreenWidth()
    }
    fun createGreenSquare(context:Context, x:Float, y:Float, size:Int, constraintLayout: ConstraintLayout): ImageView{
        removeGreenSquare(constraintLayout)
        val greenSquareImageView = ImageView(context)
        greenSquareImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.green_square))
        greenSquareImageView.y = y
        greenSquareImageView.x = x
        constraintLayout.addView(greenSquareImageView)
        greenSquareImageView.layoutParams.height = size
        greenSquareImageView.layoutParams.width = size
        return greenSquareImageView
    }
    fun removeGreenSquare(constraintLayout: ConstraintLayout){
        constraintLayout.removeView(this.greenSquareImageView)
    }

    fun generateFen(color: Int): String{
        var fen = ""
        if (color == 0) {
            var emptySquareCounter = 0
            var added = 1

            for (i in squareCoordinates.indices) {
                for (j in pieces.indices) {
                    if (squareCoordinates[i][0].toFloat() == pieces[j].x && squareCoordinates[i][1].toFloat() == pieces[j].y) {
                        if (emptySquareCounter != 0) {
                            val character = (emptySquareCounter + 48).toChar()
                            fen += character
                            emptySquareCounter = 0
                        }
                        added = 2
                        val resourceID = pieces[j].tag
                        fen += mapOfNotationToImages[resourceID]
                    }
                }
                when (added) {
                    1 -> emptySquareCounter += 1
                    2 -> added = 1
                }
                if ((i + 1) % 8 == 0 && i != 0 && i + 1 != 64) {
                    if (emptySquareCounter != 0) {
                        val character = (emptySquareCounter + 48).toChar()
                        fen += character
                        emptySquareCounter = 0
                    }
                    fen += '/'

                }
            }
            if (emptySquareCounter != 0) {
                val character = (emptySquareCounter + 48).toChar()
                fen += character
            }
        }else {
            var emptySquareCounter = 0
            var added = 1

            for (i in 7 downTo 0) {
                for (j in 7 downTo 0) {
                    for (k in pieces.indices) {
                        if (squareCoordinates[i * 8 + j][0].toFloat() == pieces[k].x && squareCoordinates[i * 8 + j][1].toFloat() == pieces[k].y) {
                            if (emptySquareCounter != 0) {
                                val character = (emptySquareCounter + 48).toChar()
                                fen += character
                                emptySquareCounter = 0
                            }
                            added = 2
                            val resourceID = pieces[k].tag
                            fen += mapOfNotationToImages[resourceID]
                        }
                    }
                    when (added) {
                        1 -> emptySquareCounter += 1
                        2 -> added = 1
                    }
                }
                if (emptySquareCounter != 0) {
                    val character = (emptySquareCounter + 48).toChar()
                    fen += character
                    emptySquareCounter = 0
                }
                fen += '/'

            }

            }

        fen+=" "
        fen += if (turn) {
            "w"
        } else{
            "b"
        }
        //println(turn)
        fen+=" "


        //TODO
        if(whiteKingsideCastle){
            fen+="K"
        }
        if(whiteQueensideCastle){
            fen+="Q"
        }
        if(blackKingsideCastle){
            fen+="k"
        }
        if(blackQueensideCastle){
            fen+="q"
        }

        fen+=" "
        if (enPassantSquare){
            fen+="coordinate"
        }
        else{
            fen+="-"
        }

        this.fen = fen

        if (previousMovesList.isEmpty()){
            previousMovesList.add(fen)
        }else{
            if (previousMovesList.last() != fen){
                previousMovesList.add(fen)
            }
        }

        return fen
    }

    fun pseudoLegalMoves(fen: String): MutableList<Move>{
        val returnMoves = mutableListOf<Move>()
        val moves = getPseudoMoves(fen)
        val lastFourBits = 0xF000
        if (moves != null){
            val numberOfMoves = moves[0]
            for(i in 1..(numberOfMoves)){
                    val flag = (moves[i].toInt() and lastFourBits) shr 12
                    val from = moves[i].toInt() and ((1 shl 6) - 1)
                    val where = moves[i].toInt() and(((1 shl 6) - 1) shl 6) shr(6)
                    returnMoves.add(Move(flag, from, where))
            }
        }
        if (color == 1){
            for (i in returnMoves.indices){
                returnMoves[i].where = 63-returnMoves[i].where
                returnMoves[i].from = 63-returnMoves[i].from
            }
        }
        println(fen)
        return returnMoves
    }

    private external fun getPseudoMoves(fenString: String): ShortArray?


}