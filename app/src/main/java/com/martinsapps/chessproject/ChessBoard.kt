package com.martinsapps.chessproject

import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.view.WindowInsets
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlin.math.round

class ChessBoard(color: Int, context: Context, screenWidth: Int, screenHeight: Int, opening: String?, val notationTextView: TextView) {

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
    val screenHeight: Int
    val screenWidth: Int
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
    val dbHandler: DbHandler

    val soundPlayer = SoundPlayer(context)


    var moves: MutableList<Move> = mutableListOf()
    val opening = opening
    var plyCounter: Int

    init {
        this.color = color
        this.dbHandler = DbHandler(context, "openings.db",null, 1 )
        val screenRatio = screenWidth.toFloat()/screenHeight.toFloat()
        this.screenWidth = screenWidth
        this.screenHeight=screenHeight
        this.startY= round((screenHeight/(screenRatio*7)))
        this.startX = 0F
        this.squareSize = (screenWidth/8)
        this.squareCoordinates = squareCoordinates(startY, startX, squareSize.toFloat())
        this.fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - "
        this.pieceClicked = null
        this.clickedPieceCoordinates = IntArray(2)
        this.greenSquareImageView = null
        this.pieces = mutableListOf()
        this.plyCounter = 0
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

        const val BASIC_CHESS_SETTINGS = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - "

        init {
            System.loadLibrary("chessproject")
        }
    }

    /*
    function returns square coordinates starting at the top of the chess board and ending in the bottom right corner
     */
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
        return x > screenWidth || x < 0 || y < startY || y > startY+screenHeight
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


    /*
    Given two fen strings function iterates over squares and finds the difference. Returns the square coordinates
     */
    fun getPlayedMove(fen1: String, fen2: String): IntArray {


        fun parseFenPosition(fen: String): String {
            val rows = fen.split(" ")[0].split("/")
            val board = StringBuilder()

            for (row in rows) {
                for (char in row) {
                    if (char.isDigit()) {
                        repeat(char.digitToInt()) { board.append("1") }
                    } else {
                        board.append(char)
                    }
                }
            }
            return board.toString()
        }

        val board1 = parseFenPosition(fen1)
        val board2 = parseFenPosition(fen2)

        var fromSquare: Int? = null
        var toSquare: Int? = null


        if (board1[60] == 'K' && board2[60] == '1' && board2[62] == 'K') {
            return if (color == 0) intArrayOf(60, 63) else intArrayOf(4, 7)
        } else if (board1[4] == 'k' && board2[4] == '1' && board2[6] == 'k') {
            return if (color == 0) intArrayOf(4, 7) else intArrayOf(60, 63)
        } else if (board1[60] == 'K' && board2[60] == '1' && board2[58] == 'K') {
            return if (color == 0) intArrayOf(60, 56) else intArrayOf(4, 1)
        } else if (board1[4] == 'k' && board2[4] == '1' && board2[2] == 'k') {
            return if (color == 0) intArrayOf(4, 1) else intArrayOf(60, 56)
        }



        for (i in 0 until 64) {
            if (board1[i] != board2[i]) {
                when {
                    board1[i] != '1' && board2[i] == '1' -> {

                        fromSquare = i
                    }
                    board1[i] == '1' && board2[i] != '1' -> {
                        toSquare = i
                    }
                    board1[i] != '1' && board2[i] != '1' && board1[i].isLowerCase() != board2[i].isLowerCase() -> {

                        fromSquare = fromSquare ?: i
                        toSquare = toSquare ?: i
                    }
                }
            }
        }

        if (fromSquare == null || toSquare == null) {
            throw IllegalArgumentException("No move fromSquare=$fromSquare, toSquare=$toSquare")
        }

        return if (color == 0) {

            intArrayOf(toSquare, fromSquare)
        } else {

            intArrayOf(63 - toSquare, 63 - fromSquare)
        }
    }

    /*
    Its the same as the function above just returns the algebraic notation of the move.
     */

    fun getPlayedMoveAlgebraicWithPiece(fen1: String, fen2: String): String {
        fun parseFenPosition(fen: String): String {
            val rows = fen.split(" ")[0].split("/")
            val board = StringBuilder()

            for (row in rows) {
                for (char in row) {
                    if (char.isDigit()) {
                        repeat(char.digitToInt()) { board.append("1") }
                    } else {
                        board.append(char)
                    }
                }
            }
            return board.toString()
        }

        // Pawn moves
        fun indexToAlgebraic(index: Int): String {
            val file = 'a' + (index % 8)
            val rank = 8 - (index / 8)
            return "$file$rank"
        }

        val board1 = parseFenPosition(fen1)
        val board2 = parseFenPosition(fen2)

        // castles
        if (board1[60] == 'K' && board2[60] == '1' && board2[62] == 'K') {
            return "0-0"
        } else if (board1[4] == 'k' && board2[4] == '1' && board2[6] == 'k') {
            return "0-0"
        } else if (board1[60] == 'K' && board2[60] == '1' && board2[58] == 'K') {
            return "0-0-0"
        } else if (board1[4] == 'k' && board2[4] == '1' && board2[2] == 'k') {
            return "0-0-0"
        }



        var fromSquare: Int? = null
        var toSquare: Int? = null
        var isCapture =false
        for (i in 0 until 64) {
            if (board1[i] != board2[i]) {
                when {
                    board1[i] != '1' && board2[i] == '1' -> {
                        fromSquare = i
                    }
                    board1[i] == '1' && board2[i] != '1' -> {
                        toSquare = i
                    }
                    board1[i] != '1' && board2[i] != '1' && board1[i].isLowerCase() != board2[i].isLowerCase() -> {
                        fromSquare = fromSquare ?: i
                        toSquare = toSquare ?: i
                        isCapture= true
                    }
                }
            }
        }

        if (fromSquare == null || toSquare == null) {
            throw IllegalArgumentException("No move fromSquare=$fromSquare, toSquare=$toSquare")
        }

        val fromAlgebraic = indexToAlgebraic(fromSquare)
        val toAlgebraic = indexToAlgebraic(toSquare)

        val movingPiece = board1[fromSquare]
        val pieceType = when (movingPiece.lowercaseChar()) {
            'p' -> ""
            'r' -> "R"
            'n' -> "N"
            'b' -> "B"
            'q' -> "Q"
            'k' -> "K"
            else -> throw IllegalArgumentException("Unknown piece type: $movingPiece")
        }


        // captures and quiet moves
        return if (pieceType.isEmpty()) {
            if (isCapture) {
                "${fromAlgebraic[0]}x$toAlgebraic"
            } else {
                toAlgebraic
            }
        } else {
            if (isCapture) {
                "${pieceType}x$toAlgebraic"
            } else {
                "$pieceType$toAlgebraic"
            }
        }

        //TODO promotions

    }


    // Function generates fen string given the argument of color and using the instance of chessBoard. Not optimal, could get as an aargument the chessPieces matrix
    fun generateFen(color: Int): String{
        var fen = ""
        if (color == 0) {
            var emptySquareCounter = 0
            var added = 1

            for (i in squareCoordinates.indices) {
                for (j in pieces.indices) {
                    if (squareCoordinates[i][0] == pieces[j].x.toInt() && squareCoordinates[i][1] == pieces[j].y.toInt()) {
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
                if(i !=0){
                    fen += '/'
                }

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

    fun removePieces(constraintLayout: ConstraintLayout){
        for(piece in pieces){
            constraintLayout.removeView(piece)
            piece.setOnClickListener(null)
        }
        pieces = mutableListOf()
    }

    // Adds chess pieces to chessBoard.pieces. Pieces are drawn using the drawPiece mwthod. Function works differently based on the color argument
    fun drawPieces(FEN: CharArray, startY:Float, startX:Float, squareSize: Float, constraintLayout:ConstraintLayout, pieceSize: Int, chessBoard:ChessBoard, greenSquareFactory: GreenSquareFactory, color: Int, context: Context, width: Int){
        chessBoard.pieces = mutableListOf()
        fun createPiece(type: Int): ChessPiece{
            return ChessPiece(type, context, constraintLayout,color, chessBoard, greenSquareFactory)
        }

        if (color == 0) {
            var currentSquareX = startX
            var currentSquareY = startY
            for (i in FEN.indices) {
                if(FEN[i] == ' '){
                    break
                }
                else if (FEN[i] == 'r') {
                    val piece = createPiece(ChessPiece.BLACK_ROOK).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'n') {
                    val piece = createPiece(ChessPiece.BLACK_KNIGHT).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'b') {
                    val piece = createPiece(ChessPiece.BLACK_BISHOP).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'q') {
                    val piece = createPiece(ChessPiece.BLACK_QUEEN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'k') {
                    val piece = createPiece(ChessPiece.BLACK_KING).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'p') {
                    val piece = createPiece(ChessPiece.BLACK_PAWN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'R') {
                    val piece = createPiece(ChessPiece.WHITE_ROOK).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'N') {
                    val piece = createPiece(ChessPiece.WHITE_KNIGHT).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'B') {
                    val piece = createPiece(ChessPiece.WHITE_BISHOP).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'Q') {
                    val piece = createPiece(ChessPiece.WHITE_QUEEN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'K') {
                    val piece = createPiece(ChessPiece.WHITE_KING).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'P') {
                    val piece = createPiece(ChessPiece.WHITE_PAWN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == '/') {
                    currentSquareY += squareSize
                    currentSquareX = startX
                    continue
                }
                else if  (FEN[i].isDigit()) {
                    for (j in FEN[i].digitToInt() downTo 2) {
                        currentSquareX += squareSize
                    }
                }
                currentSquareX += squareSize
            }
        }else{
            var currentSquareX = startX+width-width/8
            var currentSquareY = startY+width-width/8
            for (i in FEN.indices) {
                if(FEN[i] == ' '){
                    break
                }
                else if  (FEN[i] == 'r') {
                    val piece = createPiece(ChessPiece.BLACK_ROOK).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'n') {
                    val piece = createPiece(ChessPiece.BLACK_KNIGHT).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'b') {
                    val piece = createPiece(ChessPiece.BLACK_BISHOP).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'q') {
                    val piece = createPiece(ChessPiece.BLACK_QUEEN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'k') {
                    val piece = createPiece(ChessPiece.BLACK_KING).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'p') {
                    val piece = createPiece(ChessPiece.BLACK_PAWN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'R') {
                    val piece = createPiece(ChessPiece.WHITE_ROOK).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'N') {
                    val piece = createPiece(ChessPiece.WHITE_KNIGHT).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'B') {
                    val piece = createPiece(ChessPiece.WHITE_BISHOP).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'Q') {
                    val piece = createPiece(ChessPiece.WHITE_QUEEN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'K') {
                    val piece = createPiece(ChessPiece.WHITE_KING).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == 'P') {
                    val piece = createPiece(ChessPiece.WHITE_PAWN).drawPiece(currentSquareX, currentSquareY, pieceSize, pieceSize)
                    chessBoard.pieces.add(piece)
                }
                else if  (FEN[i] == '/') {
                    currentSquareY -= squareSize
                    currentSquareX = startX+width-width/8
                    continue
                }
                else if  (FEN[i].isDigit()) {
                    for (j in FEN[i].digitToInt() downTo 2) {
                        currentSquareX -= squareSize
                    }
                }
                currentSquareX -= squareSize
            }
        }
    }


    // calls the native function getPseudoMoves. Returns list of Moves. Move is a dataclass for only storing information
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
        return returnMoves
    }

    private external fun getPseudoMoves(fenString: String): ShortArray?


}