package com.example.kanpelibrary.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ITPMDataOpenHelper(context: Context): SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "itpmDB.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "itpmdb"
        const val _ID = "_id"
        const val COLUMN_TITLE = "title"

        private const val CREATE_TABLE = "CREATE TABLE $TABLE_NAME ($_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT)"
        private const val INIT_TABLE = "INSERT INTO $TABLE_NAME VALUES (1,'ホーム'),(2,'事業内容'),(3,'企業情報'),(4,'採用情報'),(5,'お問い合わせ')"
        private const val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
        db?.execSQL(INIT_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL(DROP_TABLE)
        onCreate(db)
    }
}