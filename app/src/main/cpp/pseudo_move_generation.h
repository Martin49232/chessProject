//
// Created by marti on 05/11/2024.
//

#ifndef CHESSPROJECT_PSEUDO_MOVE_GENERATION_H
#define CHESSPROJECT_PSEUDO_MOVE_GENERATION_H
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>

typedef uint64_t Bitboard;
typedef int16_t Move;



//Board is represented by 64 bit words. A1 -> 57th bit in the word. H1 -> 64th bit and A8 -> 1st bit.

//0 1 1 1 1 1 1 1
//0 1 1 1 1 1 1 1
//0 1 1 1 1 1 1 1
//0 1 1 1 1 1 1 1
//0 1 1 1 1 1 1 1
//0 1 1 1 1 1 1 1
//0 1 1 1 1 1 1 1
//0 1 1 1 1 1 1 1
const Bitboard not_a_file = 0xfefefefefefefefeull;
//1 1 1 1 1 1 1 0
//1 1 1 1 1 1 1 0
//1 1 1 1 1 1 1 0
//1 1 1 1 1 1 1 0
//1 1 1 1 1 1 1 0
//1 1 1 1 1 1 1 0
//1 1 1 1 1 1 1 0
//1 1 1 1 1 1 1 0
const Bitboard not_h_file = 0x7f7f7f7f7f7f7f7full;
//0 0 0 0 0 0 0 0
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
const Bitboard not_eigth_rank = 0xFFFFFFFFFFFFFF00ull;
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
//1 1 1 1 1 1 1 1
//0 0 0 0 0 0 0 0
const Bitboard not_first_rank = 0xFFFFFFFFFFFFFFull;
/*
1 0 1 1 1 1 1 1
1 0 1 1 1 1 1 1
1 0 1 1 1 1 1 1
1 0 1 1 1 1 1 1
1 0 1 1 1 1 1 1
1 0 1 1 1 1 1 1
1 0 1 1 1 1 1 1
1 0 1 1 1 1 1 1
*/
const Bitboard not_b_file = 0xFDFDFDFDFDFDFDFDull;
/*
1 1 1 1 1 1 0 1
1 1 1 1 1 1 0 1
1 1 1 1 1 1 0 1
1 1 1 1 1 1 0 1
1 1 1 1 1 1 0 1
1 1 1 1 1 1 0 1
1 1 1 1 1 1 0 1
1 1 1 1 1 1 0 1
*/
const Bitboard not_g_file = 0xBFBFBFBFBFBFBFBFull;
/*
1 1 1 1 1 1 1 1
0 0 0 0 0 0 0 0
1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1
*/
const Bitboard not_seventh_rank = 0xFFFFFFFFFFFF00FFull;
/*
1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1
1 1 1 1 1 1 1 1
0 0 0 0 0 0 0 0
1 1 1 1 1 1 1 1
*/
const Bitboard not_second_rank = 0xFF00FFFFFFFFFFFFull;




// starting position FEN

static char * fen_e4 = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
static char * fen_test_castling = "8/8/8/8/8/8/8/4K2R w K - 0 1";
static char * fen_test_castling_not_possible = "8/8/8/8/8/8/8/4KB1R w K - 0 1";
static char * starting_position_fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1";
static char * empty_board_fen = "8/8/8/8/8/8/8/8 w KQkq - 0 1";
static char * fen_testing_capture_promotion = "6b1/5P2/8/8/8/8/8/8 w - - 0 1";
static char * fen_testing_ep_capture = "8/8/8/5pP1/8/8/8/8 w - f6 0 1";

char turn = 0;
unsigned char castling_rights = 0;
Bitboard en_passant_square = 0ull;


//piece move vectors
static const int king_move_offset[8] = { 8, -8, 1, -1, 7, 9, -7, -9 };
static const int queen_move_offset[8] = { 8, -8, 1, -1, 7, 9, -7, -9 };
static const int rook_move_offset[4] = { 8, -8, 1, -1 };
static const int knight_move_offset[8] = { 15, 17, 10, -6, -15, -17, -10, 6 };
static const int bishop_move_offset[4] = { 7, 9, -7, -9 };
static const int pawn_move_offset[1] = { 8 };


//bitboards
Bitboard white_king = 0ULL;
Bitboard white_queen = 0ULL;
Bitboard white_rook = 0ULL;
Bitboard white_knight = 0ULL;
Bitboard white_bishop = 0ULL;
Bitboard white_pawn = 0ULL;

Bitboard black_king = 0ULL;
Bitboard black_queen = 0ULL;
Bitboard black_rook = 0ULL;
Bitboard black_knight = 0ULL;
Bitboard black_bishop = 0ULL;
Bitboard black_pawn = 0ULL;

//buffer to store moves
Move pseudo_moves[512];
short pseudo_moves_counter = -1;


#define bitset(bitboard, nbit) ((bitboard) |= (1ull<<(nbit)))
#define bitclear(bitboard,nbit) ((bitboard) &= ~(1ull<<(nbit)))
#define bitflip(bitboard,nbit) ((bitboard) ^= (1ull<<(nbit)))
#define bitcheck(bitboard,nbit) ((bitboard) & (1ull<<(nbit)))

#define EAST 1
//→
#define NORTHEAST 2
//↗
#define SOUTHEAST 3
//↘
#define WEST 4
//←
#define SOUTHWEST 5
//↙
#define NORTHWEST 6
//↖
#define NORTH 7
//↑
#define SOUTH 8
//↓

#define QUIET_MOVES 0
#define DOUBLE_PAWN_PUSH 1
#define KING_CASTLE 2
#define QUEEN_CASTLE 3
#define CAPTURES 4
#define EP_CAPTURE 5
#define KNIGHT_PROMOTION 8
#define BISHOP_PROMOTION 9
#define ROOK_PROMOTION 10
#define QUEEN_PROMOTION 11
#define KNIGHT_PROMOTION_CAPTURE 12
#define BISHOP_PROMOTION_CAPTURE 13
#define ROOK_PROMOTION_CAPTURE 14
#define QUEEN_PROMOTION_CAPTURE 15




int print_bitboard(Bitboard bitboard);
int move_piece(Bitboard* bitboard, Move move);
Move create_move(int flag, int from, int where);
static int load_fen(char* fen);
int print_move(Move move);

static Bitboard pseudo_direction_moves(Bitboard piece, Bitboard friendly_pieces, Bitboard opponent_pieces, int direction);

static Bitboard pseudo_king_moves(Bitboard king, Bitboard friendly_pieces, Bitboard opponent_pieces);
static Bitboard pseudo_queen_moves(Bitboard queen, Bitboard friendly_pieces, Bitboard opponent_pieces);
static Bitboard pseudo_bishop_moves(Bitboard bishop, Bitboard friendly_pieces, Bitboard opponent_pieces);
static Bitboard pseudo_rook_moves(Bitboard rook, Bitboard friendly_pieces, Bitboard opponent_pieces);
static Bitboard pseudo_knight_moves(Bitboard knight, Bitboard friendly_pieces, Bitboard opponent_pieces);

static Bitboard pseudo_white_pawn_moves(Bitboard pawn, Bitboard friendly_pieces, Bitboard opponent_pieces);
static Bitboard pseudo_black_pawn_moves(Bitboard pawn, Bitboard friendly_pieces, Bitboard opponent_pieces);



Bitboard eastOne (Bitboard bitboard);
Bitboard noEaOne (Bitboard bitboard);
Bitboard soEaOne (Bitboard bitboard);
Bitboard westOne (Bitboard bitboard);
Bitboard soWeOne (Bitboard bitboard);
Bitboard noWeOne (Bitboard bitboard);
Bitboard northOne(Bitboard bitboard);
Bitboard southOne(Bitboard bitboard);

Move * pseudo_moves_generator(char* fen){
    load_fen(fen);


    Bitboard whitePieces = white_king|white_queen|white_rook|white_knight|white_bishop|white_pawn;
    Bitboard blackPieces = black_king|black_queen|black_rook|black_knight|black_bishop|black_pawn;
    Bitboard pieces = whitePieces|blackPieces;

    //print_bitboard(pseudo_queen_moves(white_queen, whitePieces, blackPieces));
    if(turn == 'w'){
        for(int i =0;i<64;i++){
            if(bitcheck(white_knight, i)){
                (pseudo_knight_moves(1ull<<i, whitePieces, blackPieces));
            }
            if(bitcheck(white_bishop, i)){
                (pseudo_bishop_moves(1ull<<i, whitePieces, blackPieces));
            }
            if(bitcheck(white_rook, i)){
                (pseudo_rook_moves(1ull<<i, whitePieces, blackPieces));
            }
            if(bitcheck(white_queen, i)){
                (pseudo_queen_moves(1ull<<i, whitePieces, blackPieces));
            }
            if(bitcheck(white_king, i)){
                (pseudo_king_moves(1ull<<i, whitePieces, blackPieces));
            }
            if(bitcheck(white_pawn, i)){
                (pseudo_white_pawn_moves(1ull<<i, whitePieces, blackPieces));
            }
        }
    }
    else{
        for(int i =0;i<64;i++){
            if(bitcheck(black_knight, i)){
                (pseudo_knight_moves(1ull<<i, blackPieces, whitePieces));
            }
            if(bitcheck(black_bishop, i)){
                (pseudo_bishop_moves(1ull<<i, blackPieces, whitePieces));
            }
            if(bitcheck(black_rook, i)){
                (pseudo_rook_moves(1ull<<i, blackPieces, whitePieces));
            }
            if(bitcheck(black_queen, i)){
                (pseudo_queen_moves(1ull<<i, blackPieces, whitePieces));
            }
            if(bitcheck(black_king, i)){
                (pseudo_king_moves(1ull<<i, blackPieces, whitePieces));
            }
            if(bitcheck(black_pawn, i)){
                (pseudo_black_pawn_moves(1ull<<i, blackPieces, whitePieces));
            }
        }
    }
    Move * moves = (Move*)malloc((pseudo_moves_counter+2) * sizeof(Move));
    moves[0] = pseudo_moves_counter+1;
    for(int i =1; i<pseudo_moves_counter+2; i++){
        moves[i] = pseudo_moves[i-1];
    }


    white_king = 0ULL;
    white_queen = 0ULL;
    white_rook = 0ULL;
    white_knight = 0ULL;
    white_bishop = 0ULL;
    white_pawn = 0ULL;

    black_king = 0ULL;
    black_queen = 0ULL;
    black_rook = 0ULL;
    black_knight = 0ULL;
    black_bishop = 0ULL;
    black_pawn = 0ULL;

    pseudo_moves_counter = -1;
    return moves;
}

int print_bitboard(Bitboard bitboard){
    for(int i = 0; i < 64; i++){
        if((i+1)%8 == 0){
            if(bitcheck(bitboard, i)){
                printf("1 ");
            }
            else
            {
                printf("0 ");
            }
            printf("\n");
        }

        else if (bitcheck(bitboard, i))
        {
            printf("1 ");
        }
        else
        {
            printf("0 ");
        }
    }
    printf("\n");
    return 0;
}

int print_move(Move move){
    unsigned short last_four_bits = 0xF000;
    unsigned short flag = (move&last_four_bits)>>12;
    for(int i =0; i<16;i++){
        if(move & 1<<i){
            printf("1");
        }else{
            printf("0");
        }
    }
    unsigned char from = move & ((1 << 6)-1);
    unsigned char where = (move & (((1 << 6) - 1)<<6))>>6;
    printf("\nflag: %d\n", flag);
    printf("from: %d\n", from);
    printf("where: %d\n", where);
    return 0;
}


/*
0	0000	quiet moves
1	0001    double pawn push
2	0010	king castle
3	0011	queen castle
4	0100	captures
5	0101	ep-capture
8	1000	knight-promotion
9	1001	bishop-promotion
10	1010	rook-promotion
11	1011	queen-promotion
12	1100	knight-promo capture
13	1101	bishop-promo capture
14	1110	rook-promo capture
15	1111	queen-promo capture
*/
Move create_move(int flag, int from, int where){
    return ((((0x0|(where<<6))|from)|(flag<<12)));
}

int move_piece(Bitboard* bitboard, Move move){
    //last six bites for from and next six to where. Four remaining for flags like promotion etc.
    unsigned char from = move & ((1 << 6)-1);
    unsigned char where = (move & (((1 << 6) - 1)<<6))>>6;
    bitclear(*bitboard, from);
    bitset(*bitboard, where);
    return 0;
}

static int load_fen(char* fen){

    //setting bitboards
    int i = 0;
    int squareCounter = 0;
    while(fen[i] != 32){
        if(fen[i] == 'K'){
            bitset(white_king, squareCounter);
        }
        if(fen[i] == 'Q'){
            bitset(white_queen, squareCounter);
        }
        if(fen[i] == 'R'){
            bitset(white_rook, squareCounter);
        }
        if(fen[i] == 'B'){
            bitset(white_bishop, squareCounter);
        }
        if(fen[i] == 'N'){
            bitset(white_knight, squareCounter);
        }
        if(fen[i] == 'P'){
            bitset(white_pawn, squareCounter);
        }
        if(fen[i] == 'k'){
            bitset(black_king, squareCounter);
        }
        if(fen[i] == 'q'){
            bitset(black_queen, squareCounter);
        }
        if(fen[i] == 'r'){
            bitset(black_rook, squareCounter);
        }
        if(fen[i] == 'b'){
            bitset(black_bishop, squareCounter);
        }
        if(fen[i] == 'n'){
            bitset(black_knight, squareCounter);
        }
        if(fen[i] == 'p'){
            bitset(black_pawn, squareCounter);
        }
        if((fen[i] >= 49) && (fen[i] <= 58)){
            squareCounter+=fen[i]-49;

        }
        if(fen[i] == '/'){
            squareCounter-=1;
        }
        i+=1;
        squareCounter+=1;
    }

    //loads in whose turn it is
    i+=1;
    turn = fen[i];

    i+=2;
    while(fen[i] != 32){
        if(fen[i] == 'K'){
            castling_rights = castling_rights | 1;
        }
        if(fen[i] == 'Q'){
            castling_rights = castling_rights | 2;
        }
        if(fen[i] == 'k'){
            castling_rights = castling_rights | 4;
        }
        if(fen[i] == 'q'){
            castling_rights = castling_rights | 8;
        }
        i+=1;
    }

    i+=1;
    //converts the char to number than shiftes the bitboard
    int en_passant_position = -1;
    if(fen[i] != 45){
        en_passant_position = ((fen[i]-97)+(abs(fen[i+1]-56))*8);
        en_passant_square = 1ull<<en_passant_position;
    }
    else{
        en_passant_square = 0;
    }
    //printf("Loaded fen in\nTurn: %c\nCastling rights: %d\nEn passant square: %d\n",turn, castling_rights, en_passant_position);

    return 0;
}


static Bitboard pseudo_direction_moves(Bitboard piece, Bitboard friendly_pieces, Bitboard opponent_pieces, int direction){
    Bitboard iterator = piece;
    Bitboard result = 0ull;
    switch (direction)
    {
        case 1:
            for(int i=0; i<8;i++){
                iterator = eastOne(iterator) &~ friendly_pieces;
                if((opponent_pieces & iterator) != 0ull){
                    result = result|iterator;
                    break;
                }
                result = result|iterator;}
            break;

        case 2:
            for(int i=0; i<8;i++){
                iterator = noEaOne(iterator) &~ friendly_pieces;
                if((opponent_pieces & iterator) != 0ull){
                    result = result|iterator;
                    break;
                }
                result = result|iterator;}
            break;

        case 3:
            for(int i=0; i<8;i++){
                iterator = soEaOne(iterator) &~ friendly_pieces;
                if((opponent_pieces & iterator) != 0ull){
                    result = result|iterator;
                    break;
                }
                result = result|iterator;}
            break;

        case 4:
            for(int i=0; i<8;i++){
                iterator = westOne(iterator) &~ friendly_pieces;
                if((opponent_pieces & iterator) != 0ull){
                    result = result|iterator;
                    break;
                }
                result = result|iterator;}
            break;

        case 5:
            for(int i=0; i<8;i++){
                iterator = soWeOne(iterator) &~ friendly_pieces;
                if((opponent_pieces & iterator) != 0ull){
                    result = result|iterator;
                    break;
                }
                result = result|iterator;}
            break;

        case 6:
            for(int i=0; i<8;i++){
                iterator = noWeOne(iterator) &~ friendly_pieces;
                if((opponent_pieces & iterator) != 0ull){
                    result = result|iterator;
                    break;
                }
                result = result|iterator;}
            break;

        case 7:
            for(int i=0; i<8;i++){
                iterator = northOne(iterator) &~ friendly_pieces;
                if((opponent_pieces & iterator) != 0ull){
                    result = result|iterator;
                    break;
                }
                result = result|iterator;}
            break;

        case 8:
            for(int i=0; i<8;i++){
                iterator = southOne(iterator) &~ friendly_pieces;
                if((opponent_pieces & iterator) != 0ull){
                    result = result|iterator;
                    break;
                }
                result = result|iterator;}
            break;

        default:
            return 1;
            break;
    }
    return result;
}

static Bitboard pseudo_king_moves(Bitboard king, Bitboard friendly_pieces, Bitboard opponent_pieces){
    //Bitboard pieces_without_king = (friendly_pieces|opponent_pieces) &~ king;
    Bitboard moveEast = eastOne(king) &~ friendly_pieces;
    Bitboard moveNoEa = noEaOne(king) &~ friendly_pieces;
    Bitboard moveSoEa = soEaOne(king) &~ friendly_pieces;
    Bitboard moveWest = westOne(king) &~ friendly_pieces;
    Bitboard moveSoWe = soWeOne(king) &~ friendly_pieces;
    Bitboard moveNoWe = noWeOne(king) &~ friendly_pieces;
    Bitboard moveNorth = northOne(king) &~ friendly_pieces;
    Bitboard moveSouth = southOne(king) &~ friendly_pieces;


    //castling
    Bitboard kingside_castle = 0ull;
    Bitboard queenside_castle = 0ull;
    if(turn == 'w'){
        if((castling_rights&(1ull)) && !bitcheck(friendly_pieces, 62) && !bitcheck(friendly_pieces, 61)){
            kingside_castle = kingside_castle |1ull<<63;
            pseudo_moves_counter += 1;
            pseudo_moves[pseudo_moves_counter] = create_move(KING_CASTLE, 60, 63);
        }
        if(castling_rights&(1ull<<1) && !bitcheck(friendly_pieces, 57) && !bitcheck(friendly_pieces, 58) && !bitcheck(friendly_pieces, 59)){
            kingside_castle = kingside_castle |1ull<<56;
            pseudo_moves_counter += 1;
            pseudo_moves[pseudo_moves_counter] = create_move(QUEEN_CASTLE, 60, 56);
        }
    }

    if(turn == 'b'){
        if(castling_rights&(1ull<<2)  && !bitcheck(friendly_pieces, 6) && !bitcheck(friendly_pieces, 5) ){
            kingside_castle = kingside_castle |1ull;
            pseudo_moves_counter += 1;
            pseudo_moves[pseudo_moves_counter] = create_move(KING_CASTLE, 4, 7);
        }
        if(castling_rights&(1ull<<3) && !bitcheck(friendly_pieces, 1) && !bitcheck(friendly_pieces, 2)&& !bitcheck(friendly_pieces, 3)){
            kingside_castle = kingside_castle |1ull<<7;
            pseudo_moves_counter += 1;
            pseudo_moves[pseudo_moves_counter] = create_move(QUEEN_CASTLE, 4, 0);
        }
    }

    //add moves to move array
    Bitboard pseudo_moves_bitboard = moveEast|moveNoEa|moveSoEa|moveWest|moveSoWe|moveNoWe|moveNorth|moveSouth;
    for(int i =0; i<64; i++){
        if(bitcheck(king, i)){
            for(int j =0; j<64; j++){
                if(bitcheck(pseudo_moves_bitboard, j)){
                    if(opponent_pieces & 1ull << j){
                        pseudo_moves_counter +=1;
                        pseudo_moves[pseudo_moves_counter] = create_move(CAPTURES,i,j);
                    }
                    else {
                        pseudo_moves_counter +=1;
                        pseudo_moves[pseudo_moves_counter] = create_move(QUIET_MOVES,i,j);
                    }
                }
            }
        }
    }

    return (moveEast|moveNoEa|moveSoEa|moveWest|moveSoWe|moveNoWe|moveNorth|moveSouth|kingside_castle|queenside_castle);
}

static Bitboard pseudo_queen_moves(Bitboard queen, Bitboard friendly_pieces, Bitboard opponent_pieces){
    Bitboard pseudo_moves_bitboard =
            pseudo_direction_moves(queen, friendly_pieces, opponent_pieces, SOUTH) |
            pseudo_direction_moves(queen, friendly_pieces, opponent_pieces, WEST) |
            pseudo_direction_moves(queen, friendly_pieces, opponent_pieces, NORTH) |
            pseudo_direction_moves(queen, friendly_pieces, opponent_pieces, EAST) |
            pseudo_direction_moves(queen, friendly_pieces, opponent_pieces, NORTHEAST) |
            pseudo_direction_moves(queen, friendly_pieces, opponent_pieces, NORTHWEST) |
            pseudo_direction_moves(queen, friendly_pieces, opponent_pieces, SOUTHEAST) |
            pseudo_direction_moves(queen, friendly_pieces, opponent_pieces, SOUTHWEST);

    for(int i =0; i<64; i++){
        if(bitcheck(queen, i)){
            for(int j =0; j<64; j++){
                if(bitcheck(pseudo_moves_bitboard, j)){
                    if(opponent_pieces & 1ull << j){
                        pseudo_moves_counter +=1;
                        pseudo_moves[pseudo_moves_counter] = create_move(CAPTURES,i,j);
                    }
                    else {
                        pseudo_moves_counter +=1;
                        pseudo_moves[pseudo_moves_counter] = create_move(QUIET_MOVES,i,j);
                    }
                }
            }
        }
    }

    return pseudo_moves_bitboard;
}

static Bitboard pseudo_bishop_moves(Bitboard bishop, Bitboard friendly_pieces, Bitboard opponent_pieces){
    Bitboard pseudo_moves_bitboard =
            pseudo_direction_moves(bishop, friendly_pieces, opponent_pieces, NORTHEAST) |
            pseudo_direction_moves(bishop, friendly_pieces, opponent_pieces, NORTHWEST) |
            pseudo_direction_moves(bishop, friendly_pieces, opponent_pieces, SOUTHEAST) |
            pseudo_direction_moves(bishop, friendly_pieces, opponent_pieces, SOUTHWEST);

    for(int i =0; i<64; i++){
        if(bitcheck(bishop, i)){
            for(int j =0; j<64; j++){
                if(bitcheck(pseudo_moves_bitboard, j)){
                    if(opponent_pieces & 1ull << j){
                        pseudo_moves_counter +=1;
                        pseudo_moves[pseudo_moves_counter] = create_move(CAPTURES,i,j);
                    }
                    else {
                        pseudo_moves_counter +=1;
                        pseudo_moves[pseudo_moves_counter] = create_move(QUIET_MOVES,i,j);
                    }
                }
            }
        }
    }

    return pseudo_moves_bitboard;
}

static Bitboard pseudo_rook_moves(Bitboard rook, Bitboard friendly_pieces, Bitboard opponent_pieces){
    Bitboard pseudo_moves_bitboard =
            pseudo_direction_moves(rook, friendly_pieces, opponent_pieces, SOUTH) |
            pseudo_direction_moves(rook, friendly_pieces, opponent_pieces, WEST) |
            pseudo_direction_moves(rook, friendly_pieces, opponent_pieces, NORTH) |
            pseudo_direction_moves(rook, friendly_pieces, opponent_pieces, EAST);

    for(int i =0; i<64; i++){
        if(bitcheck(rook, i)){
            for(int j =0; j<64; j++){
                if(bitcheck(pseudo_moves_bitboard, j)){
                    if(opponent_pieces & 1ull << j){
                        pseudo_moves_counter +=1;
                        pseudo_moves[pseudo_moves_counter] = create_move(CAPTURES,i,j);
                    }
                    else {
                        pseudo_moves_counter +=1;
                        pseudo_moves[pseudo_moves_counter] = create_move(QUIET_MOVES,i,j);
                    }
                }
            }
        }
    }

    return pseudo_moves_bitboard;
}

static Bitboard pseudo_knight_moves(Bitboard knight, Bitboard friendly_pieces, Bitboard opponent_pieces){

    //{ 15, 17, 10, -6, -15, -17, -10, 6 };

    //Bitboard pieces_without_knight = (friendly_pieces|opponent_pieces) &~ knight;
    Bitboard not_ab_file = ~(~not_a_file|~not_b_file);
    Bitboard not_hg_file = ~(~not_h_file|~not_g_file);

    Bitboard northNoWe = ((knight >> 17)&not_h_file)&~ friendly_pieces;
    Bitboard northNoEa = ((knight >> 15)&not_a_file)&~ friendly_pieces;
    Bitboard westNoWe = ((knight >> 10)&not_hg_file)&~ friendly_pieces;
    Bitboard westSoWe = ((knight << 15)&not_h_file)&~ friendly_pieces;
    Bitboard SouthSoWe = ((knight << 6)&not_hg_file)&~ friendly_pieces;
    Bitboard southSoEa = ((knight << 17)&not_a_file)&~ friendly_pieces;
    Bitboard eastSoEa = ((knight << 10)&not_ab_file)&~ friendly_pieces;
    Bitboard eastNoEa = ((knight >> 6)&not_ab_file)&~ friendly_pieces;

    Bitboard pseudo_moves_bitboard = northNoEa|northNoWe|westNoWe|westSoWe|SouthSoWe|southSoEa|eastSoEa|eastNoEa;

    for(int i =0; i<64; i++){
        if(bitcheck(knight, i)){
            for(int j =0; j<64; j++){
                if(bitcheck(pseudo_moves_bitboard, j)){
                    if(opponent_pieces & 1ull << j){
                        pseudo_moves_counter +=1;
                        pseudo_moves[pseudo_moves_counter] = create_move(CAPTURES,i,j);
                    }
                    else {
                        pseudo_moves_counter +=1;
                        pseudo_moves[pseudo_moves_counter] = create_move(QUIET_MOVES,i,j);
                    }
                }
            }
        }
    }

    return pseudo_moves_bitboard;
}


static Bitboard pseudo_white_pawn_moves(Bitboard pawn, Bitboard friendly_pieces, Bitboard opponent_pieces){
    Bitboard pseudo_legal_pawn_moves = 0ull;


    int pawn_position = -1;
    for(int i = 0;i<64;i++){
        if(bitcheck(pawn, i)){
            pawn_position = i;
        }
    }

    if((pawn&(~not_second_rank))){
        if(((pawn>>16) &(~((opponent_pieces)|(friendly_pieces))))&&(((pawn>>8)&(~((opponent_pieces)|(friendly_pieces)))))){
            pseudo_legal_pawn_moves = (pseudo_legal_pawn_moves|(pawn>>16)) &(~((opponent_pieces)|(friendly_pieces)));
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(DOUBLE_PAWN_PUSH, pawn_position, pawn_position-16);
        }
    }
    if(pawn>>8&~not_eigth_rank &(~((opponent_pieces)|(friendly_pieces)))){
        pseudo_legal_pawn_moves = (pseudo_legal_pawn_moves|(pawn>>8)) &(~((opponent_pieces)|(friendly_pieces)));

        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(ROOK_PROMOTION, pawn_position, pawn_position-8);
        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(QUEEN_PROMOTION, pawn_position, pawn_position-8);
        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(KNIGHT_PROMOTION, pawn_position, pawn_position-8);
        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(BISHOP_PROMOTION, pawn_position, pawn_position-8);
    }
    else{
        if(pawn>>8&((~opponent_pieces)|(friendly_pieces))){
            pseudo_legal_pawn_moves = (pseudo_legal_pawn_moves|(pawn>>8)) &((~opponent_pieces)|(friendly_pieces));
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(QUIET_MOVES, pawn_position, pawn_position-8);
        }
    }

    if(opponent_pieces&(soEaOne(pawn))){
        if(pawn&~not_seventh_rank){
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(ROOK_PROMOTION_CAPTURE, pawn_position, pawn_position-7);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(QUEEN_PROMOTION_CAPTURE, pawn_position, pawn_position-7);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(BISHOP_PROMOTION_CAPTURE, pawn_position, pawn_position-7);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(KNIGHT_PROMOTION_CAPTURE, pawn_position, pawn_position-7);
        }
        else{
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(CAPTURES, pawn_position, pawn_position-7);
        }
    }
    if((opponent_pieces&(soWeOne(pawn)))){
        if(pawn&~not_seventh_rank){
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(ROOK_PROMOTION_CAPTURE, pawn_position, pawn_position-9);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(QUEEN_PROMOTION_CAPTURE, pawn_position, pawn_position-9);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(BISHOP_PROMOTION_CAPTURE, pawn_position, pawn_position-9);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(KNIGHT_PROMOTION_CAPTURE, pawn_position, pawn_position-9);
        }else{
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(CAPTURES, pawn_position, pawn_position-9);
        }
    }
    if((en_passant_square)&(soEaOne(pawn))){
        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(EP_CAPTURE, pawn_position, pawn_position-7);
    }
    if(en_passant_square&(soWeOne(pawn))){
        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(EP_CAPTURE, pawn_position, pawn_position-9);
    }
    //adds captures and en passant
    return pseudo_legal_pawn_moves |((opponent_pieces&(pawn>>7)) | (opponent_pieces&(pawn>>9))|(en_passant_square)&(pawn>>7))|(en_passant_square&(pawn>>9));
}

static Bitboard pseudo_black_pawn_moves(Bitboard pawn, Bitboard friendly_pieces, Bitboard opponent_pieces){
    Bitboard pseudo_legal_pawn_moves = 0ull;

    int pawn_position = -1;
    for(int i = 0;i<64;i++){
        if(bitcheck(pawn, i)){
            pawn_position = i;
        }
    }


    if((pawn&(~not_seventh_rank))){

        if(((pawn<<16)&(~((opponent_pieces)|(friendly_pieces))))&&(((pawn<<8)&(~((opponent_pieces)|(friendly_pieces)))))){
            pseudo_legal_pawn_moves = (pseudo_legal_pawn_moves|(pawn<<16)) &(~((opponent_pieces)|(friendly_pieces)));
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(DOUBLE_PAWN_PUSH, pawn_position, pawn_position+16);
        }

    }
    if(pawn<<8&(~not_first_rank) &(~((opponent_pieces)|(friendly_pieces)))){
        pseudo_legal_pawn_moves = (pseudo_legal_pawn_moves|(pawn<<8)) &(~((opponent_pieces)|(friendly_pieces)));

        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(ROOK_PROMOTION, pawn_position, pawn_position+8);
        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(QUEEN_PROMOTION, pawn_position, pawn_position+8);
        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(KNIGHT_PROMOTION, pawn_position, pawn_position+8);
        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(BISHOP_PROMOTION, pawn_position, pawn_position+8);
    }
    else{
        if(pawn<<8&(~((opponent_pieces)|(friendly_pieces)))){
            pseudo_legal_pawn_moves = (pseudo_legal_pawn_moves|(pawn<<8)) &(~((opponent_pieces)|(friendly_pieces)));
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(QUIET_MOVES, pawn_position, pawn_position+8);
        }
    }

    if(opponent_pieces&(noWeOne(pawn))){
        if(pawn&~not_second_rank){
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(ROOK_PROMOTION_CAPTURE, pawn_position, pawn_position+7);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(QUEEN_PROMOTION_CAPTURE, pawn_position, pawn_position+7);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(BISHOP_PROMOTION_CAPTURE, pawn_position, pawn_position+7);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(KNIGHT_PROMOTION_CAPTURE, pawn_position, pawn_position+7);
        }
        else{
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(CAPTURES, pawn_position, pawn_position+7);
        }
    }
    if((opponent_pieces&(noEaOne(pawn)))){
        if(pawn&~not_second_rank){
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(ROOK_PROMOTION_CAPTURE, pawn_position, pawn_position+9);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(QUEEN_PROMOTION_CAPTURE, pawn_position, pawn_position+9);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(BISHOP_PROMOTION_CAPTURE, pawn_position, pawn_position+9);
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(KNIGHT_PROMOTION_CAPTURE, pawn_position, pawn_position+9);
        }else{
            pseudo_moves_counter+=1;
            pseudo_moves[pseudo_moves_counter] = create_move(CAPTURES, pawn_position, pawn_position+9);
        }
    }
    if((en_passant_square)&(noWeOne(pawn))){
        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(EP_CAPTURE, pawn_position, pawn_position+7);
    }
    if(en_passant_square&(noEaOne(pawn))){
        pseudo_moves_counter+=1;
        pseudo_moves[pseudo_moves_counter] = create_move(EP_CAPTURE, pawn_position, pawn_position+9);
    }
    //adds captures and en passant
    return pseudo_legal_pawn_moves |((opponent_pieces&(pawn<<7)) | (opponent_pieces&(pawn<<9))|(en_passant_square)&(pawn<<7))|(en_passant_square&(pawn<<9));
}


Bitboard eastOne (Bitboard bitboard) {return (bitboard << 1) & not_a_file;}
//→
Bitboard noEaOne (Bitboard bitboard) {return (bitboard << 9) & not_a_file;}
//↗
Bitboard soEaOne (Bitboard bitboard) {return (bitboard >> 7) & not_a_file;}
//↘
Bitboard westOne (Bitboard bitboard) {return (bitboard >> 1) & not_h_file;}
//←
Bitboard soWeOne (Bitboard bitboard) {return (bitboard >> 9) & not_h_file;}
//↙
Bitboard noWeOne (Bitboard bitboard) {return (bitboard << 7) & not_h_file;}
//↖
Bitboard northOne(Bitboard bitboard) {return (bitboard << 8)& not_eigth_rank;}
//↑
Bitboard southOne(Bitboard bitboard) {return (bitboard >> 8)& not_first_rank;}
//↓








#endif //CHESSPROJECT_PSEUDO_MOVE_GENERATION_H
