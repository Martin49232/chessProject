package com.martinsapps.chessproject

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class ChessPiece(
    private val ID: Int, private val context: Context,
    private val constraintLayout: ConstraintLayout,
    private val color: Int, private val chessBoard: ChessBoard,
    private val squareFactory: GreenSquareFactory
) {

    private val imageView: ImageView = ImageView(context)

    companion object {
        val BLACK_KING = R.drawable.black_king
        val BLACK_QUEEN = R.drawable.black_queen
        val BLACK_BISHOP = R.drawable.black_bishop
        val BLACK_KNIGHT = R.drawable.black_knight
        val BLACK_ROOK = R.drawable.black_rook
        val BLACK_PAWN = R.drawable.black_pawn

        val WHITE_KING = R.drawable.white_king
        val WHITE_QUEEN = R.drawable.white_queen
        val WHITE_BISHOP = R.drawable.white_bishop
        val WHITE_KNIGHT = R.drawable.white_knight
        val WHITE_ROOK = R.drawable.white_rook
        val WHITE_PAWN = R.drawable.white_pawn
    }


    // Draws piece using ImageView. Than calls the monstrosity makePieceClickable()
    fun drawPiece(x: Float, y: Float, width: Int, height: Int): ImageView {
        constraintLayout.removeView(imageView)
        imageView.setImageDrawable(ContextCompat.getDrawable(context, ID))
        imageView.tag = ID
        imageView.y = y
        imageView.x = x
        imageView.isClickable
        imageView.adjustViewBounds = true
        constraintLayout.addView(imageView)
        imageView.layoutParams.height = height
        imageView.layoutParams.width = width

        makePieceClickable()
        return imageView
    }

    private fun removePiece() {
        constraintLayout.removeView(imageView)

    }

    /*
    This code is a problem. Im lucky it didnt break :D. We set a ClickListener on the image view that waits for the click event.
    Than the function creates legal moves where they are legal. Creates sound effects, visual effects, resets the position if the user fucks up.
    Than we set a constraintLayout.setOnTouchListener that listens for lick on the constrant layout. Than the function gets the x and y coordianates and cheks if the move
    is legal using the native function and not out of bounds using the methods defined in ChessBoard.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun makePieceClickable() {

        /*
        this is a function that is called whenever a piece is moved. It checks if the move is in the opening database. And than acts accordingly
         */
        fun movingPiece(moveType: Int, position: FloatArray){
            if ((chessBoard.dbHandler.getOpening(chessBoard.opening, (chessBoard.plyCounter+1)).isNotBlank())){
                val algebraic = chessBoard.getPlayedMoveAlgebraicWithPiece(chessBoard.fen, chessBoard.dbHandler.getOpening(chessBoard.opening, (chessBoard.plyCounter+1)))
                chessBoard.notationTextView.append(" $algebraic")
            }

            chessBoard.plyCounter+=1
            squareFactory.removeSquares()
            chessBoard.removeGreenSquare(constraintLayout)
            chessBoard.turn = !chessBoard.turn
            chessBoard.fen = chessBoard.generateFen(color)
            chessBoard.previousMovesMovedBackList = mutableListOf()

            if (chessBoard.dbHandler.getOpening(chessBoard.opening, (chessBoard.plyCounter)) != (chessBoard.fen.split(" "))[0]){
                println(chessBoard.dbHandler.getOpening(chessBoard.opening, (chessBoard.plyCounter)))
                println((chessBoard.fen.split(" "))[0])
                chessBoard.notationTextView.text = ""
                chessBoard.soundPlayer.playWrongSound()
                squareFactory.addRedSquare(position)

                chessBoard.fen = ChessBoard.BASIC_CHESS_SETTINGS
                chessBoard.plyCounter = 0
                chessBoard.turn = true
                chessBoard.removePieces(constraintLayout)
                chessBoard.pieceClicked = null
                chessBoard.previousMovesMovedBackList = mutableListOf()
                chessBoard.previousMovesList = mutableListOf(chessBoard.fen)
                chessBoard.drawPieces(chessBoard.fen.toCharArray(), chessBoard.startY, chessBoard.startX, chessBoard.squareSize.toFloat(), constraintLayout, chessBoard.squareSize, chessBoard, squareFactory, color, context, chessBoard.screenWidth)
                return
            }else{
                if (chessBoard.dbHandler.getOpening(chessBoard.opening, (chessBoard.plyCounter+1)).isBlank()){
                    chessBoard.fen = ChessBoard.BASIC_CHESS_SETTINGS
                    chessBoard.plyCounter = 0
                    chessBoard.turn = true
                    chessBoard.removePieces(constraintLayout)
                    chessBoard.pieceClicked = null
                    chessBoard.previousMovesMovedBackList = mutableListOf()
                    chessBoard.previousMovesList = mutableListOf(chessBoard.fen)
                    chessBoard.notationTextView.text = ""
                    chessBoard.drawPieces(chessBoard.fen.toCharArray(), chessBoard.startY, chessBoard.startX, chessBoard.squareSize.toFloat(), constraintLayout, chessBoard.squareSize, chessBoard, squareFactory, color, context, chessBoard.screenWidth)
                    chessBoard.soundPlayer.playGoodSound()
                    return
                }
            }
            when(moveType){
                1->chessBoard.soundPlayer.playMoveSound()
                2->chessBoard.soundPlayer.playCaptureSound()
            }

        }

        imageView.setOnClickListener {
            squareFactory.removeSquares()
            chessBoard.greenSquareImageView = chessBoard.createGreenSquare(
                context,
                imageView.x,
                getImageViewY(this.imageView),
                imageView.layoutParams.height,
                constraintLayout
            )

            //squareFactory.removeSquares()

            //prints the green squares
            chessBoard.moves = chessBoard.pseudoLegalMoves(chessBoard.fen)
            val pseudoLegalMoves = mutableListOf<Move>()
            var position = 0
            for (i in 0 until chessBoard.squareCoordinates.size) {
                if (this.imageView.x.toInt() == chessBoard.squareCoordinates[i][0] && getImageViewY(this.imageView).toInt() == chessBoard.squareCoordinates[i][1]) {
                    position = i
                }
            }
            //printing legal moves signs
            for (move in chessBoard.moves) {
                if (move.from == position) {
                    pseudoLegalMoves.add(move)
                    when(move.flag){
                        Move.QUIET_MOVES->squareFactory.addDot(move.where)
                        Move.DOUBLE_PAWN_PUSH->squareFactory.addDot(move.where)
                        Move.CAPTURES->squareFactory.addHollowCircle(move.where)
                        Move.KING_CASTLE->squareFactory.addHollowCircle(move.where)
                        Move.QUEEN_CASTLE->squareFactory.addHollowCircle(move.where)
                        //TODO rest
                    }

                }
            }

            if (chessBoard.pieceClicked != null && chessBoard.pieceClicked != imageView) {

                var pieceClickedPosition = 0
                for (i in 0 until chessBoard.squareCoordinates.size) {
                    if (chessBoard.pieceClicked!!.x.toInt() == chessBoard.squareCoordinates[i][0] && getImageViewY(chessBoard.pieceClicked!!).toInt() == chessBoard.squareCoordinates[i][1]) {
                        pieceClickedPosition = i
                    }
                }


                for (move in chessBoard.moves) {
                    if (move.from == pieceClickedPosition) {
                        if (move.where == position) {

                            //castling
                            if (((move.flag == Move.KING_CASTLE && 'k' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag])|| (move.flag == Move.KING_CASTLE && 'K' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag]))&&chessBoard.color==0){
                                imageView.x = chessBoard.pieceClicked!!.x +chessBoard.squareSize
                                imageView.y = getImageViewY(chessBoard.pieceClicked!!)
                                chessBoard.pieceClicked!!.x += 2*chessBoard.squareSize
                                movingPiece(1, floatArrayOf(imageView.x, getImageViewY(this.imageView)))
                                return@setOnClickListener
                            }

                            if (((move.flag == Move.QUEEN_CASTLE && 'K' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag])|| (move.flag == Move.QUEEN_CASTLE && 'k' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag]))&&chessBoard.color==0){
                                imageView.x = chessBoard.pieceClicked!!.x -chessBoard.squareSize
                                imageView.y = getImageViewY(chessBoard.pieceClicked!!)
                                chessBoard.pieceClicked!!.x -= 2*chessBoard.squareSize
                                movingPiece(1, floatArrayOf(imageView.x, getImageViewY(this.imageView)))
                                return@setOnClickListener
                            }

                            if (((move.flag == Move.KING_CASTLE && 'k' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag])|| (move.flag == Move.KING_CASTLE && 'K' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag]))&&chessBoard.color==1){
                                imageView.x = chessBoard.pieceClicked!!.x -chessBoard.squareSize
                                imageView.y = getImageViewY(chessBoard.pieceClicked!!)
                                chessBoard.pieceClicked!!.x -= 2*chessBoard.squareSize
                                movingPiece(1, floatArrayOf(imageView.x, getImageViewY(this.imageView)))
                                return@setOnClickListener
                            }

                            if (((move.flag == Move.QUEEN_CASTLE && 'K' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag])|| (move.flag == Move.QUEEN_CASTLE && 'k' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag]))&&chessBoard.color==1){
                                imageView.x = chessBoard.pieceClicked!!.x +chessBoard.squareSize
                                imageView.y = getImageViewY(chessBoard.pieceClicked!!)
                                chessBoard.pieceClicked!!.x += 2*chessBoard.squareSize
                                movingPiece(1, floatArrayOf(imageView.x, getImageViewY(this.imageView)))
                                return@setOnClickListener
                            }


                            chessBoard.pieceClicked!!.x = imageView.x
                            chessBoard.pieceClicked!!.y = getImageViewY(this.imageView)

                            for (i in 0 until chessBoard.pieces.size) {
                                if (chessBoard.pieces[i] == imageView) {
                                    chessBoard.pieces.removeAt(i)
                                    break
                                }
                            }
                            removePiece()
                            constraintLayout.setOnTouchListener(null)
                            chessBoard.pieceClicked = null
                            movingPiece(2, floatArrayOf(imageView.x, getImageViewY(this.imageView)))
                            return@setOnClickListener
                        }
                    }
                }


                squareFactory.removeSquares()
                chessBoard.removeGreenSquare(constraintLayout)

                chessBoard.pieceClicked = imageView
                chessBoard.greenSquareImageView = chessBoard.createGreenSquare(
                    context,
                    imageView.x,
                    getImageViewY(this.imageView),
                    imageView.layoutParams.height,
                    constraintLayout
                )
                chessBoard.moves = chessBoard.pseudoLegalMoves(chessBoard.fen)
                val pseudoLegalPieceMoves = mutableListOf<Move>()
                var piecePosition = 0
                for (i in 0 until chessBoard.squareCoordinates.size) {
                    if (this.imageView.x.toInt() == chessBoard.squareCoordinates[i][0] && getImageViewY(this.imageView).toInt() == chessBoard.squareCoordinates[i][1]) {
                        piecePosition = i
                    }
                }
                //printing legal moves signs
                for (move in chessBoard.moves) {
                    if (move.from == piecePosition) {
                        pseudoLegalPieceMoves.add(move)
                        when(move.flag){
                            Move.QUIET_MOVES->squareFactory.addDot(move.where)
                            Move.DOUBLE_PAWN_PUSH->squareFactory.addDot(move.where)
                            Move.CAPTURES->squareFactory.addHollowCircle(move.where)
                            Move.KING_CASTLE->squareFactory.addHollowCircle(move.where)
                            Move.QUEEN_CASTLE->squareFactory.addHollowCircle(move.where)
                            //TODO rest
                        }

                    }
                }
            }
            chessBoard.pieceClicked = imageView

            constraintLayout.setOnTouchListener { _, event ->
                val x = event.x
                val y = event.y
                if (!chessBoard.outOfBounds(x.toInt(), y.toInt())) {
                    val closestSquare = chessBoard.getClosestSquare(x, y)
                    for (i in 0 until chessBoard.squareCoordinates.size) {
                        if (closestSquare.contentEquals(chessBoard.squareCoordinates[i])) {
                            for (move in pseudoLegalMoves) {
                                if (move.where == i) {
                                    drawPiece(
                                        closestSquare[0].toFloat(),
                                        closestSquare[1].toFloat(),
                                        chessBoard.squareSize,
                                        chessBoard.squareSize
                                    )
                                    movingPiece(1, floatArrayOf(closestSquare[0].toFloat(), closestSquare[1].toFloat()))
                                    chessBoard.pieceClicked = null
                                    constraintLayout.setOnTouchListener(null)
                                    return@setOnTouchListener true
                                }
                            }
                        }
                    }

                }
                chessBoard.removeGreenSquare(constraintLayout)
                squareFactory.removeSquares()
                constraintLayout.setOnTouchListener(null)
                chessBoard.pieceClicked = null
                true
            }
            imageView.bringToFront()
        }
    }

    private fun getImageViewY(imageView: ImageView): Float {
        return (imageView.y)
    }
}

