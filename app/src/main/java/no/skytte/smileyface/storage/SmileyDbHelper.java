/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.skytte.smileyface.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import no.skytte.smileyface.storage.SmileyContract.InspectionEntry;
import no.skytte.smileyface.storage.SmileyContract.LocationEntry;

/**
 * Manages a local database for inspection data.
 */
public class SmileyDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "smileys.db";

    public SmileyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY," +
                LocationEntry.COLUMN_TO_ID + " TEXT NOT NULL, " +
                LocationEntry.COLUMN_NAME + " TEXT, " +
                LocationEntry.COLUMN_ADDRESS + " TEXT, " +
                LocationEntry.COLUMN_POSTCODE + " TEXT, " +
                LocationEntry.COLUMN_CITY + " TEXT, " +
                LocationEntry.COLUMN_COORD_LAT + " REAL, " +
                LocationEntry.COLUMN_COORD_LONG + " REAL, " +
                " UNIQUE (" + LocationEntry.COLUMN_TO_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_INSPECTION_TABLE = "CREATE TABLE " + InspectionEntry.TABLE_NAME + " (" +
                InspectionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InspectionEntry.COLUMN_INSP_ID + " TEXT NOT NULL, " +
                InspectionEntry.COLUMN_TO_ID + " TEXT NOT NULL, " +
                InspectionEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                InspectionEntry.COLUMN_STATUS + " INTEGER NOT NULL," +
                InspectionEntry.COLUMN_TYPE + " INTEGER NOT NULL, " +
                InspectionEntry.COLUMN_GRADE + " INTEGER NOT NULL, " +
                InspectionEntry.COLUMN_GRADE1 + " INTEGER NOT NULL, " +
                InspectionEntry.COLUMN_GRADE2 + " INTEGER NOT NULL, " +
                InspectionEntry.COLUMN_GRADE3 + " INTEGER NOT NULL, " +
                InspectionEntry.COLUMN_GRADE4 + " INTEGER NOT NULL, " +
                " UNIQUE (" + InspectionEntry.COLUMN_INSP_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_INSPECTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + InspectionEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
