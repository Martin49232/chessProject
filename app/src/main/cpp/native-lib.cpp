#include <jni.h>
#include <cstring>
#include "pseudo_move_generation.h"

char* jstringToChar(JNIEnv* env, jstring jstr) {
    const char* c_str = env->GetStringUTFChars(jstr, nullptr);
    if (c_str == nullptr) {
        return nullptr;
    }
    char* copy = new char[strlen(c_str) + 1];
    strcpy(copy, c_str);
    env->ReleaseStringUTFChars(jstr, c_str);
    return copy;
}
extern "C"
JNIEXPORT jshortArray JNICALL
Java_com_martinsapps_chessproject_ChessBoard_getPseudoMoves(
        JNIEnv *env,
        jobject,
        jstring fenString) {


    char* fen = jstringToChar(env, fenString);
    if (fen == nullptr) {
        return nullptr;
    }
    short * moves = pseudo_moves_generator(fen);

    int num_moves = moves[0]+1;

    jshortArray java_array = env->NewShortArray(num_moves);
    env->SetShortArrayRegion(java_array, 0, num_moves, moves);

    delete[] fen;
    delete[] moves;
    return java_array;
}