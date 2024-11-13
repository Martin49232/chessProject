package com.martinsapps.chessproject

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class ChessPiece(ID: Int, context: Context, constraintLayout:ConstraintLayout,color: Int,  chessBoard: ChessBoard,
                 private val squareFactory: GreenSquareFactory
) {

    private val context: Context
    private val ID: Int
    val imageView: ImageView
    private val constraintLayout: ConstraintLayout
    private val chessBoard: ChessBoard
    private val color: Int

    init {
        this.color = color
        this.ID = ID
        this.context = context
        this.imageView = ImageView(context)
        this.constraintLayout = constraintLayout
        this.chessBoard = chessBoard
    }

    companion object {
        const val BLACK_KING = R.drawable.black_king
        const val BLACK_QUEEN = R.drawable.black_queen
        const val BLACK_BISHOP = R.drawable.black_bishop
        const val BLACK_KNIGHT = R.drawable.black_knight
        const val BLACK_ROOK = R.drawable.black_rook
        const val BLACK_PAWN = R.drawable.black_pawn

        const val WHITE_KING = R.drawable.white_king
        const val WHITE_QUEEN = R.drawable.white_queen
        const val WHITE_BISHOP = R.drawable.white_bishop
        const val WHITE_KNIGHT = R.drawable.white_knight
        const val WHITE_ROOK = R.drawable.white_rook
        const val WHITE_PAWN = R.drawable.white_pawn
    }

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


    @SuppressLint("ClickableViewAccessibility")
    private fun makePieceClickable() {
        imageView.setOnClickListener {

            chessBoard.greenSquareImageView = chessBoard.createGreenSquare(
                context,
                imageView.x,
                imageView.y,
                imageView.layoutParams.height,
                constraintLayout
            )

            squareFactory.removeSquares()

            //prints the green squares
            chessBoard.moves = chessBoard.pseudoLegalMoves(chessBoard.fen)
            val pseudoLegalMoves = mutableListOf<Move>()
            var position = 0
            for (i in 0 until chessBoard.squareCoordinates.size) {
                if (this.imageView.x.toInt() == chessBoard.squareCoordinates[i][0] && this.imageView.y.toInt() == chessBoard.squareCoordinates[i][1]) {
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
                    if (chessBoard.pieceClicked!!.x.toInt() == chessBoard.squareCoordinates[i][0] && chessBoard.pieceClicked!!.y.toInt() == chessBoard.squareCoordinates[i][1]) {
                        pieceClickedPosition = i
                    }
                }


                for (move in chessBoard.moves) {
                    if (move.from == pieceClickedPosition) {
                        if (move.where == position) {

                            //castling
                            if (((move.flag == Move.KING_CASTLE && 'k' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag])|| (move.flag == Move.KING_CASTLE && 'K' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag]))&&chessBoard.color==0){
                                imageView.x = chessBoard.pieceClicked!!.x +chessBoard.squareSize
                                imageView.y = chessBoard.pieceClicked!!.y
                                chessBoard.pieceClicked!!.x += 2*chessBoard.squareSize
                                squareFactory.removeSquares()
                                chessBoard.removeGreenSquare(constraintLayout)
                                chessBoard.turn = !chessBoard.turn
                                chessBoard.fen = chessBoard.generateFen(color)
                                return@setOnClickListener
                            }

                            if (((move.flag == Move.QUEEN_CASTLE && 'K' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag])|| (move.flag == Move.QUEEN_CASTLE && 'k' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag]))&&chessBoard.color==0){
                                imageView.x = chessBoard.pieceClicked!!.x -chessBoard.squareSize
                                imageView.y = chessBoard.pieceClicked!!.y
                                chessBoard.pieceClicked!!.x -= 2*chessBoard.squareSize
                                squareFactory.removeSquares()
                                chessBoard.removeGreenSquare(constraintLayout)
                                chessBoard.turn = !chessBoard.turn
                                chessBoard.fen = chessBoard.generateFen(color)
                                return@setOnClickListener
                            }

                            if (((move.flag == Move.KING_CASTLE && 'k' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag])|| (move.flag == Move.KING_CASTLE && 'K' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag]))&&chessBoard.color==1){
                                imageView.x = chessBoard.pieceClicked!!.x -chessBoard.squareSize
                                imageView.y = chessBoard.pieceClicked!!.y
                                chessBoard.pieceClicked!!.x -= 2*chessBoard.squareSize
                                squareFactory.removeSquares()
                                chessBoard.removeGreenSquare(constraintLayout)
                                chessBoard.turn = !chessBoard.turn
                                chessBoard.fen = chessBoard.generateFen(color)
                                return@setOnClickListener
                            }

                            if (((move.flag == Move.QUEEN_CASTLE && 'K' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag])|| (move.flag == Move.QUEEN_CASTLE && 'k' == ChessBoard.mapOfNotationToImages[chessBoard.pieceClicked!!.tag]))&&chessBoard.color==1){
                                imageView.x = chessBoard.pieceClicked!!.x +chessBoard.squareSize
                                imageView.y = chessBoard.pieceClicked!!.y
                                chessBoard.pieceClicked!!.x += 2*chessBoard.squareSize
                                squareFactory.removeSquares()
                                chessBoard.removeGreenSquare(constraintLayout)
                                chessBoard.turn = !chessBoard.turn
                                chessBoard.fen = chessBoard.generateFen(color)
                                return@setOnClickListener
                            }


                            chessBoard.pieceClicked!!.x = imageView.x
                            chessBoard.pieceClicked!!.y = imageView.y

                            for (i in 0 until chessBoard.pieces.size) {
                                if (chessBoard.pieces[i] == imageView) {
                                    chessBoard.pieces.removeAt(i)
                                    break
                                }
                            }
                            removePiece()

                            chessBoard.turn = !chessBoard.turn
                            chessBoard.fen = chessBoard.generateFen(color)

                            constraintLayout.setOnTouchListener(null)
                            chessBoard.pieceClicked = null

                            squareFactory.removeSquares()
                            chessBoard.removeGreenSquare(constraintLayout)
                            return@setOnClickListener
                        }
                    }
                }
                squareFactory.removeSquares()
                chessBoard.removeGreenSquare(constraintLayout)
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
                                    chessBoard.turn = !chessBoard.turn
                                    chessBoard.fen = chessBoard.generateFen(color)
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
}

