package com.example.hsebastian.popularmoviesvol1.data;

/**
 * Created by hsebastian on 2/1/16.
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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;


public class MovieProvider extends ContentProvider {

    private final String LOG_TAG = MovieProvider.class.getSimpleName();

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieDbHelper;

    static final int MOVIES = 100;
    static final int MOVIE = 101;
    static final int TRAILERS = 200;
    static final int REVIEWS = 300;
    static final int MOVIE_TRAILERS = 400;
    static final int MOVIE_REVIEWS = 500;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, MOVIE_TRAILERS);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, MOVIE_REVIEWS);
        matcher.addURI(
            authority,
            MovieContract.PATH_MOVIE + "/#",
            MOVIE);
        matcher.addURI(
            authority,
            MovieContract.PATH_MOVIE + "/" + MovieContract.PATH_TRAILER + "/#",
            MOVIE_TRAILERS);
        matcher.addURI(
            authority,
            MovieContract.PATH_MOVIE + "/" + MovieContract.PATH_REVIEW + "/#",
            MOVIE_REVIEWS);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        SQLiteCursorFactory sqLiteCursorFactory = new SQLiteCursorFactory(true);
        this.mMovieDbHelper = new MovieDbHelper(
            getContext(), sqLiteCursorFactory);
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        Log.d(
            LOG_TAG,
            "getType" + " "
                .concat("uri=" + uri.toString() + " ")
                .concat("match=" + String.valueOf(match) + " "));

        switch (match) {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case MOVIE_TRAILERS:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case MOVIE_REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = this.mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case MOVIES: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        Log.d(
            LOG_TAG,
            "insert" + " "
                .concat("uri=" + uri.toString() + " ")
                .concat("match=" + String.valueOf(match) + " ")
                .concat("returnUri=" + returnUri.toString() + " "));
        return returnUri;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = this.mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIE_TRAILERS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(
                            MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                            Log.d(
                                LOG_TAG,
                                "bulkInsert" + " "
                                    .concat("uri=" + uri.toString() + " ")
                                    .concat("match=" + String.valueOf(match) + " ")
                                    .concat("_id=" + String.valueOf(_id) + " ")
                                    .concat("value=" + value.toString() + " "));
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MOVIE_REVIEWS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(
                            MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                            Log.d(
                                LOG_TAG,
                                "bulkInsert" + " "
                                    .concat("uri=" + uri.toString() + " ")
                                    .concat("match=" + String.valueOf(match) + " ")
                                    .concat("_id=" + String.valueOf(_id) + " ")
                                    .concat("value=" + value.toString() + " "));
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                Log.d(
                    LOG_TAG,
                    "bulkInsert" + " "
                        .concat("returnCount=" + String.valueOf(returnCount) + " "));
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Cursor query(
            Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        final int match = sUriMatcher.match(uri);
        Log.d(
            LOG_TAG,
            "query" + " "
                .concat("uri=" + uri.toString() + " ")
                .concat("projection=" + Arrays.toString(projection) + " ")
                .concat("selection=" + selection + " ")
                .concat("selectionArgs=" + Arrays.toString(selectionArgs) + " ")
                .concat("sortOrder=" + sortOrder + " ")
                .concat("match=" + String.valueOf(match) + " "));
        Cursor retCursor;
        switch (match) {
            case MOVIE_TRAILERS: {
                retCursor = this.mMovieDbHelper.getReadableDatabase().query(
                    MovieContract.TrailerEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;
            }
            case MOVIE_REVIEWS: {
                retCursor = this.mMovieDbHelper.getReadableDatabase().query(
                    MovieContract.ReviewEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;
            }
            case MOVIES: {
                retCursor = this.mMovieDbHelper.getReadableDatabase().query(
                    MovieContract.MovieEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;
            }
            case MOVIE: {
                retCursor = this.mMovieDbHelper.getReadableDatabase().query(
                    MovieContract.MovieEntry.TABLE_NAME,
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
        Log.d(
            LOG_TAG,
            "query" + " "
                .concat(DatabaseUtils.dumpCursorToString(retCursor)));
        return retCursor;
    }

    @Override
    public int update(
            @NonNull Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        final SQLiteDatabase db = this.mMovieDbHelper.getWritableDatabase();
        int rowsUpdated;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,
                    values, selection, selectionArgs);
                break;
            case MOVIE_TRAILERS:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME,
                    values, selection, selectionArgs);
                break;
            case MOVIE_REVIEWS:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME,
                    values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = this.mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                    MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_TRAILERS:
                rowsDeleted = db.delete(
                    MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_REVIEWS:
                rowsDeleted = db.delete(
                    MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        Log.d(
            LOG_TAG,
            "delete" + " "
                .concat("uri=" + uri.toString() + " ")
                .concat("selection=" + selection + " ")
                .concat("selectionArgs=" + Arrays.toString(selectionArgs) + " ")
                .concat("match=" + String.valueOf(match) + " ")
                .concat("rowsDeleted=" + String.valueOf(rowsDeleted) + " "));
        return rowsDeleted;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        this.mMovieDbHelper.close();
        super.shutdown();
    }
}