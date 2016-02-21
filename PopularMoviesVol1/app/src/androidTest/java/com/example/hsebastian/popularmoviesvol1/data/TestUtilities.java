package com.example.hsebastian.popularmoviesvol1.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;


/**
 * Created by hsebastian on 2/4/16.
 */
public class TestUtilities extends AndroidTestCase{

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                "' did not match the expected value '" +
                expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }


    public static ContentValues createJurassicParkMovieValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(
            MovieContract.MovieEntry.COLUMN_TMDB_MOVIE_ID, 1234);
        testValues.put(
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Jurassic Park");
        testValues.put(
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            "A wealthy entrepreneur secretly creates a theme park featuring living dinosaurs drawn from prehistoric DNA. Before opening day, he invites a team of experts and his two eager grandchildren to experience the park and help calm anxious investors. However, the park is anything but amusing as the security systems go off-line and the dinosaurs escape.");
        testValues.put(
            MovieContract.MovieEntry.COLUMN_POPULARITY, 2398);
        testValues.put(
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7.2);
        testValues.put(
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "1993-06-08");
        testValues.put(
            MovieContract.MovieEntry.COLUMN_POSTER_URL,
            "https://image.tmdb.org/t/p/original/yyCKYaW908ZbpexpnBJ3p8o87HA.jpg");
        return testValues;
    }

    public static ContentValues createReviewValues(long movieRowId) {
        ContentValues testValues = new ContentValues();
        testValues.put(
            MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movieRowId);
        testValues.put(
            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, "BinaryCrunch");
        testValues.put(
            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
            "If you somehow missed this movie and have never seen it then watch it immediately. As a young boy of 9 on my way to the cinema I wasn't at all prepared for the on-screen awesomeness I was about to witness, one of the defining movies of my childhood and of the modern age. With special effects that simply blew any and all previous dino movies out of the water, compelling story and the odd comic moment such as the Mr Arnolds arm it really did make that evening something I will remember forever. So successful it went on to spawn 3 sequels, the second was enjoyable, the third not so much. The newest Chris Pratt one, I'll leave that for you but its worth a watch, especially if you want to wash the taste of the 3rd one out of your mouth. It gave me nightmares for weeks, really really wonderful nightmares.");
        return testValues;
    }

    public static ContentValues createTrailerValues(long movieRowId) {
        ContentValues testValues = new ContentValues();
        testValues.put(
            MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, movieRowId);
        testValues.put(
            MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, "Official Trailer");
        testValues.put(
            MovieContract.TrailerEntry.COLUMN_TRAILER_URL,
            "https://youtu.be/QWBKEmWWL38");
        return testValues;
    }
}
