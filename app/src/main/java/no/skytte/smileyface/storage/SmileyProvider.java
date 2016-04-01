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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class SmileyProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SmileyDbHelper mOpenHelper;

    static final int INSPECTION = 100;
    static final int LOCATION = 300;

//    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
//
//    static{
//        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
//
//        //This is an inner join which looks like
//        //weather INNER JOIN location ON weather.location_id = location._id
//        sWeatherByLocationSettingQueryBuilder.setTables(
//                SmileyContract.InspectionEntry.TABLE_NAME + " INNER JOIN " +
//                        SmileyContract.LocationEntry.TABLE_NAME +
//                        " ON " + SmileyContract.InspectionEntry.TABLE_NAME +
//                        "." + SmileyContract.InspectionEntry.COLUMN_LOC_KEY +
//                        " = " + SmileyContract.LocationEntry.TABLE_NAME +
//                        "." + SmileyContract.LocationEntry._ID);
//    }

    //location.location_setting = ?
//    private static final String sLocationSettingSelection =
//            SmileyContract.LocationEntry.TABLE_NAME+
//                    "." + SmileyContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";
//
//    //location.location_setting = ? AND date >= ?
//    private static final String sLocationSettingWithStartDateSelection =
//            SmileyContract.LocationEntry.TABLE_NAME+
//                    "." + SmileyContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
//                    SmileyContract.InspectionEntry.COLUMN_DATE + " >= ? ";
//
//    //location.location_setting = ? AND date = ?
//    private static final String sLocationSettingAndDaySelection =
//            SmileyContract.LocationEntry.TABLE_NAME +
//                    "." + SmileyContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
//                    SmileyContract.InspectionEntry.COLUMN_DATE + " = ? ";
//
//    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
//        String locationSetting = SmileyContract.InspectionEntry.getLocationSettingFromUri(uri);
//        long startDate = SmileyContract.InspectionEntry.getStartDateFromUri(uri);
//
//        String[] selectionArgs;
//        String selection;
//
//        if (startDate == 0) {
//            selection = sLocationSettingSelection;
//            selectionArgs = new String[]{locationSetting};
//        } else {
//            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
//            selection = sLocationSettingWithStartDateSelection;
//        }
//
//        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
//                projection,
//                selection,
//                selectionArgs,
//                null,
//                null,
//                sortOrder
//        );
//    }
//
//    private Cursor getWeatherByLocationSettingAndDate(
//            Uri uri, String[] projection, String sortOrder) {
//        String locationSetting = SmileyContract.InspectionEntry.getLocationSettingFromUri(uri);
//        long date = SmileyContract.InspectionEntry.getDateFromUri(uri);
//
//        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
//                projection,
//                sLocationSettingAndDaySelection,
//                new String[]{locationSetting, Long.toString(date)},
//                null,
//                null,
//                sortOrder
//        );
//    }

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SmileyContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, SmileyContract.PATH_INSPECTION, INSPECTION);
        matcher.addURI(authority, SmileyContract.PATH_LOCATION, LOCATION);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new SmileyDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case INSPECTION:
                return SmileyContract.InspectionEntry.CONTENT_TYPE;
            case LOCATION:
                return SmileyContract.LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case INSPECTION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SmileyContract.InspectionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LOCATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        SmileyContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case INSPECTION: {
                long _id = db.insert(SmileyContract.InspectionEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = SmileyContract.InspectionEntry.buildInspectionUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION: {
                long _id = db.insert(SmileyContract.LocationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = SmileyContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case INSPECTION:
                rowsDeleted = db.delete(
                        SmileyContract.InspectionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION:
                rowsDeleted = db.delete(
                        SmileyContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case INSPECTION:
                rowsUpdated = db.update(SmileyContract.InspectionEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LOCATION:
                rowsUpdated = db.update(SmileyContract.LocationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}