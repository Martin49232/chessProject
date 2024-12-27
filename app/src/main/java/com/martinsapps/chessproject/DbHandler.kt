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