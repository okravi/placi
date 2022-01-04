package com.example.placi.database

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.telephony.TelephonyCallback


class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
        companion object {
            private const val DATABASE_VERSION = 1
            private const val DATABASE_NAME = "HappyPlacesDatabase"
            private const val TABLE_HAPPY_PLACE = "HappyPlacesTable"

            private const val KEY_ID = "_id"
            private const val KEY_TITLE = "title"
            private const val KEY_IMAGE = "image"
            private const val KEY_DESCRIPTION = "description"
            private const val KEY_DATE = "date"
            private const val KEY_LOCATION = "location"
            private const val KEY_LATITUDE = "latitude"
            private const val KEY_LONGITUDE = "longitude"
        }

    override fun onCreate(db: SQLiteDatabase?) {

        val CREATE_HAPPY_PLACE_TABLE = ("CREATE TABLE" + TABLE_HAPPY_PLACE + "TEXT,"
                + KEY_ID + "INTEGER PRIMARY KEY,"
                + KEY_TITLE + "TEXT,"
                + KEY_IMAGE + "TEXT,"
                + KEY_DESCRIPTION + "TEXT,"
                + KEY_DATE + "TEXT,"
                + KEY_LOCATION + "TEXT,"
                + KEY_LATITUDE + "TEXT,"
                + KEY_LONGITUDE + "TEXT)")
        db?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

}