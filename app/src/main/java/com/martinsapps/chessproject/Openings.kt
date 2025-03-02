package com.martinsapps.chessproject


import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.io.IOException
import kotlin.math.round


class Openings : AppCompatActivity() {
    private lateinit var chessBoard: ChessBoard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.openings_layout)

        val constraintLayout = findViewById<ConstraintLayout>(R.id.cl)

        val screenWidth = getScreenWidth()
        val screenHeight = getScreenHeight()

        //enableEdgeToEdge()
        //enabling edge to edge braeks the code!!! I forgot and waster few hours
        supportActionBar?.hide()
        /*val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.panel)*/



        val moveBackButton = findViewById<Button>(R.id.moveBack)
        val moveForwardButton = findViewById<Button>(R.id.moveForward)
        val notationTextView = findViewById<TextView>(R.id.notation)
        val hintButton = findViewById<ImageButton>(R.id.hint)

        val color = intent.getIntExtra("color", 0)
        val dbOpeningName = intent.getStringExtra("opening")
        val openingName = intent.getStringExtra("name")


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@Openings, OpeningsRoot::class.java)
                intent.putExtra("color", color)
                startActivity(intent)
                finish()
            }
        })




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
        val dbHandler = DbHandler(applicationContext, "openings.db", null, 1)


        val screenRatio = screenWidth.toFloat()/screenHeight.toFloat()
        val chessBoardStartY= round((screenHeight/(screenRatio*7)))
        val squareSide = screenWidth/8


        chessBoard = ChessBoard(color, this, screenWidth, screenHeight, dbOpeningName, notationTextView)
        val greenSquareFactory = GreenSquareFactory(this, constraintLayout, chessBoard)



        if (dbOpeningName != null && openingName != null) {
            dbHandler.updateLastPlayed(dbOpeningName, color, openingName)
        }else{
            throw IOException("database error")
        }


        val title = findViewById<TextView>(R.id.title)
        title.text = openingName



        /*
        Takes care of the venet when user needs hint
         */
        hintButton.setOnClickListener{
            greenSquareFactory.removeSquares()
            if (chessBoard.dbHandler.getOpening(chessBoard.opening, (chessBoard.plyCounter+1)).isNotBlank()){
                val squares = chessBoard.getPlayedMove(chessBoard.fen, chessBoard.dbHandler.getOpening(chessBoard.opening, (chessBoard.plyCounter+1)))
                greenSquareFactory.addGreenSquare(squares[0])
                greenSquareFactory.addGreenSquare(squares[1])
                for (imageView in chessBoard.pieces){
                    imageView.bringToFront()
                }
            }
        }

        /*
        Button to go one move back and one move forward, there is some logic because
        i keep a list of played moves and when you go back you need to not delete the list.
        Only when you go back and play a new move the list gets deleted. Also I remove and add moves
        To the notation text view that shows played moves so far.
         */

        moveBackButton.setOnClickListener {
            if(chessBoard.previousMovesList.size-1>0) {
                chessBoard.previousMovesMovedBackList.add(chessBoard.previousMovesList.removeAt(chessBoard.previousMovesList.size-1))
                chessBoard.fen=chessBoard.previousMovesList.last()
                for (piece in chessBoard.pieces){
                    constraintLayout.removeView(piece)
                }

                if (chessBoard.notationTextView.text.isNotBlank()) {

                    val words = chessBoard.notationTextView.text.split("\\s+".toRegex())

                    if (words.size > 1) {
                        val newText = words.dropLast(1).joinToString(" ")
                        chessBoard.notationTextView.text = newText
                    } else {
                        chessBoard.notationTextView.text = ""
                    }
                }

                chessBoard.pieces = mutableListOf()
                greenSquareFactory.removeSquares()
                chessBoard.soundPlayer.playMoveSound()
                chessBoard.removeGreenSquare(constraintLayout)
                val splitFen = chessBoard.fen.split(" ")
                chessBoard.turn = splitFen[1] == "w"
                if (chessBoard.plyCounter >0){
                    chessBoard.plyCounter-=1
                }
                drawPieces(chessBoard.fen.toCharArray(), chessBoardStartY, 0F, squareSide.toFloat(), constraintLayout, squareSide, chessBoard, greenSquareFactory, color)
            }
        }

        moveForwardButton.setOnClickListener {
            if(chessBoard.previousMovesMovedBackList.size>0){
                val previousFen = chessBoard.fen
                chessBoard.fen= chessBoard.previousMovesMovedBackList.removeAt(chessBoard.previousMovesMovedBackList.size-1)
                chessBoard.previousMovesList.add(chessBoard.fen)
                for (piece in chessBoard.pieces){
                    constraintLayout.removeView(piece)
                }
                chessBoard.notationTextView.append(" "+chessBoard.getPlayedMoveAlgebraicWithPiece(previousFen, chessBoard.fen))
                chessBoard.pieces = mutableListOf()
                chessBoard.soundPlayer.playMoveSound()
                greenSquareFactory.removeSquares()
                chessBoard.removeGreenSquare(constraintLayout)
                val splitFen = chessBoard.fen.split(" ")
                chessBoard.turn = splitFen[1] == "w"
                chessBoard.plyCounter+=1
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
        drawPieces(FEN.toCharArray(), chessBoardStartY, 0F, squareSide.toFloat(), constraintLayout, squareSide, chessBoard, greenSquareFactory, color)
        println(chessBoard.generateFen(color))
    }

    override fun onDestroy() {
        super.onDestroy()
        chessBoard.soundPlayer.release()
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

    private fun drawPieces(FEN: CharArray, startY:Float, startX:Float, squareSize: Float, constraintLayout:ConstraintLayout, pieceSize: Int, chessBoard:ChessBoard, greenSquareFactory: GreenSquareFactory, color: Int){
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