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


    fun getLastPlayed(): Map<String, Any>? {
        val db = this.readableDatabase
        val lastPlayedMap = mutableMapOf<String, Any>()

        val query = "SELECT * FROM LAST_PLAYED LIMIT 1"
        val cursor = db.rawQuery(query, null)

        // Check if there is at least one row in the cursor
        if (cursor.moveToFirst()) {
            // Retrieve the column indices
            val tableNameIndex = cursor.getColumnIndex("table_name")
            val colorIndex = cursor.getColumnIndex("color")
            val nameIndex = cursor.getColumnIndex("name")

            // Check if all column indices are valid (>= 0)
            if (tableNameIndex >= 0) {
                val tableName = cursor.getString(tableNameIndex)
                lastPlayedMap["table_name"] = tableName
            }

            if (colorIndex >= 0) {
                val color = cursor.getInt(colorIndex)
                lastPlayedMap["color"] = color
            }

            if (nameIndex >= 0) {
                val name = cursor.getString(nameIndex)
                lastPlayedMap["name"] = name
            }
        }

        // Close the cursor and the database
        cursor.close()
        db.close()

        // Return the map of settings if it contains data, otherwise null
        return lastPlayedMap.ifEmpty { null }
    }



    fun getStreak(): Int {
        val db = this.readableDatabase

        val query = "SELECT * FROM streak LIMIT 1"
        val cursor = db.rawQuery(query, null)
        var streak = 0
        // Check if there is at least one row in the cursor
        if (cursor.moveToFirst()) {
            streak = cursor.getInt(0)
        }

        // Close the cursor and the database
        cursor.close()
        db.close()

        // Return the map of settings if it contains data, otherwise null
        return streak
    }

    fun getLastTimeStamp(): Long {
        val db = this.readableDatabase

        val query = "SELECT * FROM streak LIMIT 1"
        val cursor = db.rawQuery(query, null)
        var timeStamp = 0L
        // Check if there is at least one row in the cursor
        if (cursor.moveToFirst()) {
            timeStamp = cursor.getLong(1)
        }

        // Close the cursor and the database
        cursor.close()
        db.close()

        // Return the map of settings if it contains data, otherwise null
        return timeStamp
    }

    fun updateSettings(chessboard: String, legalMoves: Int, soundEffects: Int) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM settings") // Clears existing settings

        val query = "INSERT INTO settings (chessboard, legal_moves, sound_effects) VALUES (?, ?, ?)"
        val statement = db.compileStatement(query)

        statement.bindString(1, chessboard)
        statement.bindLong(2, legalMoves.toLong())
        statement.bindLong(3, soundEffects.toLong())

        statement.execute()
        statement.close()
        db.close()
    }

    fun updateLastPlayed(openingName: String, color: Int, name: String) {
        val db = this.writableDatabase

        // Clear existing entry to ensure only one last played table is stored
        db.execSQL("DELETE FROM LAST_PLAYED")

        val query = "INSERT INTO LAST_PLAYED (table_name, color, name) VALUES (?, ?, ?)"
        val statement = db.compileStatement(query)

        statement.bindString(1, openingName)
        statement.bindLong(2, color.toLong())
        statement.bindString(3, name)
        statement.execute()
        statement.close()
        db.close()
    }


    fun updateStreak(streak: Int, lastTimestamp: Long) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM streak")

        val query = "INSERT INTO streak (streak, last_timestamp) VALUES (?, ?)"
        val statement = db.compileStatement(query)

        statement.bindLong(1, streak.toLong())
        statement.bindLong(2, lastTimestamp)

        statement.execute()
        statement.close()
        db.close()
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