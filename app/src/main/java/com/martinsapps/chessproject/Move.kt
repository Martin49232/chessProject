package com.martinsapps.chessproject

class Move(flag: Int, from: Int, where: Int) {
    val flag: Int
    var from: Int
    var where: Int

    init {
        this.flag = flag
        this.from = from
        this.where = where
    }

    companion object{
        const val QUIET_MOVES =  0
        const val DOUBLE_PAWN_PUSH =1
        const val KING_CASTLE =2
        const val QUEEN_CASTLE =3
        const val CAPTURES =4
        const val EP_CAPTURE =5
        const val KNIGHT_PROMOTION =8
        const val BISHOP_PROMOTION =9
        const val ROOK_PROMOTION =10
        const val QUEEN_PROMOTION =11
        const val KNIGHT_PROMOTION_CAPTURE =12
        const val BISHOP_PROMOTION_CAPTURE =13
        const val ROOK_PROMOTION_CAPTURE =14
        const val QUEEN_PROMOTION_CAPTURE =15
    }
}