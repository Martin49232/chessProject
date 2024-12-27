package com.martinsapps.chessproject


import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.transition.Explode
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlin.math.round


class Openings : AppCompatActivity() {
    private lateinit var chessBoard: ChessBoard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.openings_layout)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.cl)
        val screenWidth = getScreenWidth()
        val screenHeight = getScreenHeight()
        enableEdgeToEdge()
        supportActionBar?.hide()
        changeTheAnnoyingBar()

        val moveBackButton = findViewById<Button>(R.id.moveBack)
        val moveForwardButton = findViewById<Button>(R.id.moveForward)

        val hintButton = findViewById<ImageButton>(R.id.hint)

        val color = intent.getIntExtra("color", 0)
        val dbOpeningName = intent.getStringExtra("opening")
        val openingName = intent.getStringExtra("name")

        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            val intent = Intent(this, OpeningsRoot::class.java)
            intent.putExtra("color", color)
            this.startActivity(intent)
            finish()
        }
        //val intent = intent
        //white is 0
        //black is 1


        val screenRatio = screenWidth.toFloat()/screenHeight.toFloat()
        val chessBoardStartY= round((screenHeight/(screenRatio*7)))
        val squareSide = screenWidth/8

        chessBoard = ChessBoard(color, this, screenWidth, screenHeight, dbOpeningName)
        val greenSquareFactory = GreenSquareFactory(this, constraintLayout, chessBoard)

        val titleLayout = findViewById<LinearLayout>(R.id.titleLayout)
        titleLayout.requestLayout()
        titleLayout.layoutParams.width = screenWidth
        titleLayout.layoutParams.height = screenHeight/8

        backButton.requestLayout()
        backButton.layoutParams.width = screenWidth/4
        backButton.layoutParams.height = screenWidth/4

        val title = findViewById<TextView>(R.id.title)
        title.requestLayout()
        title.text = openingName
        title.textSize = 25F
        title.layoutParams.width = screenWidth/2
        title.layoutParams.height = screenHeight/8

        val logo = findViewById<ImageView>(R.id.logo)
        logo.requestLayout()
        logo.layoutParams.width = screenWidth/4
        logo.layoutParams.height = screenWidth/4


        moveBackButton.setOnClickListener {
            if(chessBoard.previousMovesList.size-1>0) {
                chessBoard.previousMovesMovedBackList.add(chessBoard.previousMovesList.removeAt(chessBoard.previousMovesList.size-1))
                chessBoard.fen=chessBoard.previousMovesList.last()
                for (piece in chessBoard.pieces){
                    constraintLayout.removeView(piece)
                }
                chessBoard.pieces = mutableListOf()
                greenSquareFactory.removeSquares()
                chessBoard.soundPlayer.playMoveSound()
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
                chessBoard.soundPlayer.playMoveSound()
                greenSquareFactory.removeSquares()
                chessBoard.removeGreenSquare(constraintLayout)
                val splitFen = chessBoard.fen.split(" ")
                chessBoard.turn = splitFen[1] == "w"
                drawPieces(chessBoard.fen.toCharArray(), chessBoardStartY, 0F, squareSide.toFloat(), constraintLayout, squareSide, chessBoard, greenSquareFactory, color)
            }
        }


        if (color == 0){
            val imageView = ImageView(this)
            imageView.setImageDrawable(ContextCompat.getDrawable(this,(R.drawable.vibrantchessboard)))
            constraintLayout.addView(imageView)
            imageView.y = chessBoardStartY
            imageView.layoutParams.height = screenWidth
            imageView.layoutParams.width = screenWidth}
        else{
            val imageView = ImageView(this)
            imageView.setImageDrawable(ContextCompat.getDrawable(this,(R.drawable.vibrantchessboardopposite)))
            constraintLayout.addView(imageView)
            imageView.y = chessBoardStartY
            imageView.layoutParams.height = screenWidth
            imageView.layoutParams.width = screenWidth}



        val FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"
        println(chessBoardStartY)
        drawPieces(FEN.toCharArray(), chessBoardStartY, 0F, squareSide.toFloat(), constraintLayout, squareSide, chessBoard, greenSquareFactory, color)
        println(chessBoard.generateFen(color))
    }

    override fun onDestroy() {
        super.onDestroy()
        chessBoard.soundPlayer.release()
    }
    private fun changeTheAnnoyingBar(){
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.panel)
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
    }

    fun drawPieces(FEN: CharArray, startY:Float, startX:Float, squareSize: Float, constraintLayout:ConstraintLayout, pieceSize: Int, chessBoard:ChessBoard, greenSquareFactory: GreenSquareFactory, color: Int){
        chessBoard.pieces = mutableListOf()
        fun createPiece(type: Int): ChessPiece{
            return ChessPiece(type, this, constraintLayout,color, chessBoard, greenSquareFactory)
        }

        if (color == 0) {
            var currentSquareX = startX
            var currentSquareY = startY
            println(startY)
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
            var currentSquareX = startX+getScreenWidth()-getScreenWidth()/8
            var currentSquareY = startY+getScreenWidth()-getScreenWidth()/8
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
                        currentSquareX = startX+getScreenWidth()-getScreenWidth()/8
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

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.popup_enter, R.anim.popup_exit)
    }



}