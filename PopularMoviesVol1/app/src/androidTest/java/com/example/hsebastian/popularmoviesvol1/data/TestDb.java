package com.example.hsebastian.popularmoviesvol1.data;

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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import java.util.HashSet;


public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        // GIVEN no database
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);

        // WHEN instantiating the DB helper
        SQLiteCursorFactory sqLiteCursorFactory = new SQLiteCursorFactory(true);
        SQLiteDatabase db = new MovieDbHelper(
            this.mContext, sqLiteCursorFactory).getWritableDatabase();

        // THEN the DB is open
        assertEquals(true, db.isOpen());

        // THEN the cursor returns at least an entry
        Cursor cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue(
            "Error: Meaning that the database has not been created correctly",
            cursor.moveToFirst());

        // THEN when moving through the cursor, it iterates all the tables in DB
        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while( cursor.moveToNext() );
        assertTrue(
            "Error: Your database was created without both all the tables",
            tableNameHashSet.isEmpty());

        // THEN the tables should have the right columns

        cursor = db.rawQuery(
            "PRAGMA table_info(" + MovieContract.TrailerEntry.TABLE_NAME + ")",
            null);

        assertTrue(
            "Error: This means that we were unable to query " +
                "the database for table information.",
            cursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> trailerColumnHashSet = new HashSet<>();
        trailerColumnHashSet.add(MovieContract.TrailerEntry._ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TRAILER_URL);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            trailerColumnHashSet.remove(columnName);
        } while(cursor.moveToNext());
        assertTrue(
            "Error: The database doesn't contain all of " +
                "the required location entry columns",
            trailerColumnHashSet.isEmpty());

        ///////////////////////////////////////////////////////////////////////

        cursor = db.rawQuery(
            "PRAGMA table_info(" + MovieContract.ReviewEntry.TABLE_NAME + ")",
            null);

        assertTrue(
            "Error: This means that we were unable to query " +
                "the database for table information.",
            cursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> reviewColumnHashSet = new HashSet<>();
        reviewColumnHashSet.add(MovieContract.TrailerEntry._ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT);

        columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            reviewColumnHashSet.remove(columnName);
        } while(cursor.moveToNext());
        assertTrue(
            "Error: The database doesn't contain all of " +
                "the required location entry columns",
            reviewColumnHashSet.isEmpty());

        ///////////////////////////////////////////////////////////////////////

        cursor = db.rawQuery(
            "PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
            null);

        assertTrue(
            "Error: This means that we were unable to query " +
                "the database for table information.",
            cursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TMDB_MOVIE_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_URL);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);

        columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(cursor.moveToNext());
        assertTrue(
            "Error: The database doesn't contain all of " +
                "the required location entry columns",
            movieColumnHashSet.isEmpty());

        db.close();
    }

    public void testMovieTable() {
        insertMovie();
    }

    public void testTrailerTable() {
        long movieRowId = insertMovie();

        // Make sure we have a valid row ID.
        assertFalse("Error: Movie Not Inserted Correctly", movieRowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        SQLiteCursorFactory sqLiteCursorFactory = new SQLiteCursorFactory(true);
        MovieDbHelper dbHelper = new MovieDbHelper(mContext, sqLiteCursorFactory);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues trailerValues = TestUtilities.createTrailerValues(movieRowId);

        long trailerRowId = db.insert(
            MovieContract.TrailerEntry.TABLE_NAME, null, trailerValues);
        assertTrue(trailerRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor trailerCursor = db.query(
            MovieContract.TrailerEntry.TABLE_NAME,  // Table to Query
            null, // leaving "columns" null just returns all the columns.
            null, // cols for "where" clause
            null, // values for "where" clause
            null, // columns to group by
            null, // columns to filter by row groups
            null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue(
            "Error: No Records returned from query", trailerCursor.moveToFirst());

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord(
            "Error: failed to validate", trailerCursor, trailerValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse(
            "Error: More than one record returned from query",
            trailerCursor.moveToNext());

        trailerCursor.close();
        dbHelper.close();
    }
    public void testReviewTable() {

        long movieRowId = insertMovie();

        // Make sure we have a valid row ID.
        assertFalse("Error: Movie Not Inserted Correctly", movieRowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        SQLiteCursorFactory sqLiteCursorFactory = new SQLiteCursorFactory(true);
        MovieDbHelper dbHelper = new MovieDbHelper(mContext, sqLiteCursorFactory);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);

        long reviewRowId = db.insert(
            MovieContract.ReviewEntry.TABLE_NAME, null, reviewValues);
        assertTrue(reviewRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor reviewCursor = db.query(
            MovieContract.ReviewEntry.TABLE_NAME,  // Table to Query
            null, // leaving "columns" null just returns all the columns.
            null, // cols for "where" clause
            null, // values for "where" clause
            null, // columns to group by
            null, // columns to filter by row groups
            null  // sort order
        );

        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue(
            "Error: No Records returned from query", reviewCursor.moveToFirst());

        // Fifth Step: Validate the location Query
        TestUtilities.validateCurrentRecord(
            "Error: failed to validate", reviewCursor, reviewValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse(
            "Error: More than one record returned from query",
            reviewCursor.moveToNext());

        reviewCursor.close();
        dbHelper.close();
    }

    public long insertMovie() {
        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        SQLiteCursorFactory sqLiteCursorFactory = new SQLiteCursorFactory(true);
        MovieDbHelper dbHelper = new MovieDbHelper(mContext, sqLiteCursorFactory);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step: Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues testValues = TestUtilities.createJurassicParkMovieValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long locationRowId;
        locationRowId = db.insert(
            MovieContract.MovieEntry.TABLE_NAME, null, testValues);
        assertTrue(locationRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
            MovieContract.MovieEntry.TABLE_NAME,  // Table to Query
            null, // all columns
            null, // Columns for the "where" clause
            null, // Values for the "where" clause
            null, // columns to group by
            null, // columns to filter by row groups
            null // sort order
        );

        assertTrue(
            "Error: No Records returned from location query",
            cursor.moveToFirst());

        TestUtilities.validateCurrentRecord(
            "Error: Location Query Validation Failed", cursor, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse(
            "Error: More than one record returned from location query",
            cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
        return locationRowId;
    }
}
