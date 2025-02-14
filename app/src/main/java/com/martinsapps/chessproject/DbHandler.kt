package com.martinsapps.chessproject

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHandler
    (context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version){

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {}

    fun getOpening(openingsName: String?, move: Int): String{
        if (openingsName == null){
            throw IllegalArgumentException("You tried to search for table with name null you fucking donkey")
        }
        val db = this.readableDatabase
        var returnString = ""
        val query = "SELECT * FROM $openingsName WHERE move_number = $move"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()){
            returnString = cursor.getString(1)
        }
        cursor.close()
        db.close()
        return returnString
    }

    fun getSettings(): Map<String, Any>? {
        val db = this.readableDatabase
        val settingsMap = mutableMapOf<String, Any>()

        val query = "SELECT * FROM settings LIMIT 1"
        val cursor = db.rawQuery(query, null)

        // Check if there is at least one row in the cursor
        if (cursor.moveToFirst()) {
            // Retrieve the column indices
            val chessboardIndex = cursor.getColumnIndex("chessboard")
            val legalMovesIndex = cursor.getColumnIndex("legal_moves")
            val soundEffectsIndex = cursor.getColumnIndex("sound_effects")

            // Check if all column indices are valid (>= 0)
            if (chessboardIndex >= 0) {
                val chessboard = cursor.getString(chessboardIndex)
                settingsMap["chessboard"] = chessboard
            }

            if (legalMovesIndex >= 0) {
                val legalMoves = cursor.getInt(legalMovesIndex)
                settingsMap["legal_moves"] = legalMoves
            }

            if (soundEffectsIndex >= 0) {
                val soundEffects = cursor.getInt(soundEffectsIndex)
                settingsMap["sound_effects"] = soundEffects
            }
        }

        // Close the cursor and the database
        cursor.close()
        db.close()

        // Return the map of settings if it contains data, otherwise null
        return settingsMap.ifEmpty { null }
    }


    fun createTable(name: String){
        val db = this.writableDatabase
        val query = "CREATE TABLE $name (move_number INTEGER PRIMARY KEY, fen TEXT NOT NULL)"
        db.execSQL(query)
        db.close()
    }

    fun insertIntoTable(tableName: String, fen: String){
        val db = this.writableDatabase
        val query = "INSERT INTO $tableName (fen) VALUES ('$fen')"
        db.execSQL(query)
        db.close()
    }
    fun getAllTableNames():List<String>{
        val listOfNames = mutableListOf<String>()
        val db = this.readableDatabase
        val query = "SELECT name FROM sqlite_master WHERE type='table'"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()){
            if(cursor.getString(0).equals("android_metadata")){
                continue;
            }else{
                listOfNames.add(cursor.getString(0));
            }
        }
        cursor.close()
        db.close()
        return listOfNames
    }
}