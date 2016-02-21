package com.example.hsebastian.popularmoviesvol1.data;

/**
 * Created by hsebastian on 2/1/16.
 */
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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import com.example.hsebastian.popularmoviesvol1.data.MovieContract.MovieEntry;
import com.example.hsebastian.popularmoviesvol1.data.MovieContract.TrailerEntry;
import com.example.hsebastian.popularmoviesvol1.data.MovieContract.ReviewEntry;

/**
 * Manages a local database for weather data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " +
            MovieEntry.TABLE_NAME + " (" +
            MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MovieEntry.COLUMN_TMDB_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
            MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
            MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
            MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_POSTER_URL + " TEXT NOT NULL " +
            " );";

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
            TrailerEntry.TABLE_NAME + " (" +
            TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
            TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +
            TrailerEntry.COLUMN_TRAILER_URL + " TEXT NOT NULL, " +

            // Set up the location column as a foreign key to location table.
            " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
            MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +
            " );";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " +
            ReviewEntry.TABLE_NAME + " (" +
            ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
            ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
            ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +

            // Set up the location column as a foreign key to location table.
            " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
            MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") " +
            " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

