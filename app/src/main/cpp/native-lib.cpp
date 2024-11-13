#include <jni.h>
#include <cstring>
#include "pseudo_move_generation.h"

// Function to convert a Java string to a C-style char array
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
        jobject /* this */,
        jstring fenString) {

    // Convert Java string to C-style char array
    char* fen = jstringToChar(env, fenString);
    if (fen == nullptr) {
        return nullptr; // Handle conversion error
    }

    // Call your C function
    short * moves = pseudo_moves_generator(fen);

    // Get the number of moves returned (assuming the function provides this information)
    int num_moves = moves[0]+1;

    // Create a Java short array
    jshortArray java_array = env->NewShortArray(num_moves);
    env->SetShortArrayRegion(java_array, 0, num_moves, moves);

    // Clean up memory allocated for the C-style char array
    delete[] fen;
    delete[] moves;
    return java_array;
}
