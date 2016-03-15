package com.example.hsebastian.popularmoviesvol1.ui.details;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hsebastian.popularmoviesvol1.R;
import com.example.hsebastian.popularmoviesvol1.api.TheMovieDb;
import com.example.hsebastian.popularmoviesvol1.data.MovieContract;
import com.example.hsebastian.popularmoviesvol1.ui.ParcelableMovieInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {
    /*
    Show details
        show movie details
        query movie by _ID
        if found
            query trailers and reviews by movie _ID
        else
            query trailers and reviews from internet
        indicate by favorite button
    Click favorite
        query movie by _ID
        if found
            delete trailers and reviews by movie _ID
            delete movie by _ID
        else
            insert movie by _ID
            insert trailers and reviews by movie _ID
     */

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    private LinearLayout mTrailerLinearLayout;
    private LinearLayout mReviewLinearLayout;
    private ArrayList<HashMap<String, String>> mTrailers;
    private ArrayList<HashMap<String, String>> mReviews;
    private boolean mIsFavorite;
    private String mOfflineMovieId;

    @Bind(R.id.movie_title_text_view) TextView title;
    @Bind(R.id.release_date_text_view) TextView releaseDate;
    @Bind(R.id.vote_average_text_view) TextView voteAverage;
    @Bind(R.id.plot_synopsis_text_view) TextView plotSynopsis;
    @Bind(R.id.movie_poster_image_view) ImageView poster;
    @Bind(R.id.favorite_button) Button favorite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(
            R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (getMovieInfo() != null) {
            HashMap<String, String> movieInfo = getMovieInfo();
            Log.i(
                LOG_TAG,
                new StringBuilder()
                    .append("onCreateView ")
                    .append("originalTitle='" +
                        movieInfo.get("originalTitle") + "' ")
                    .append("action=showDetail")
                    .toString());

            title.setText(movieInfo.get("originalTitle"));
            releaseDate.setText(movieInfo.get("releaseDate"));
            voteAverage.setText(
                "Vote average: " + movieInfo.get("voteAverage") + "/10.0");
            plotSynopsis.setText(movieInfo.get("overview"));
            Picasso picasso = Picasso.with(getActivity());
            picasso.load(movieInfo.get("posterUrl")).into(poster);

            setUpFavoriteButton();

            mTrailerLinearLayout = (LinearLayout) rootView.findViewById(
                R.id.trailers_linear_layout);
            mReviewLinearLayout = (LinearLayout) rootView.findViewById(
                R.id.reviews_linear_layout);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();

        HashMap<String, String> movieInfo = getMovieInfo();
        if (movieInfo != null) {

            String tmdbMovieId = movieInfo.get("movieId");

            // query movie DB entry by movieId
            Cursor movieCursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID},
                MovieContract.MovieEntry.COLUMN_TMDB_MOVIE_ID + " = ?",
                new String[]{tmdbMovieId},
                null);
            mIsFavorite = movieCursor.moveToFirst();

            mTrailers = new ArrayList<>();
            mReviews = new ArrayList<>();

            if (mIsFavorite) {
                int movieIdIndex = movieCursor.getColumnIndex(
                    MovieContract.MovieEntry._ID);
                String movieId = movieCursor.getString(movieIdIndex);
                new GetTrailersOfflineTask().execute(movieId);
                new GetReviewsOfflineTask().execute(movieId);
                mIsFavorite = true;
                mOfflineMovieId = movieId;
            } else {
                new GetTrailersOnlineTask().execute(tmdbMovieId);
                new GetReviewsOnlineTask().execute(tmdbMovieId);
                mIsFavorite = false;
            }
            toggleFavoriteButton();
            movieCursor.close();
        }
    }

    private HashMap<String, String> getMovieInfo() {
        Bundle args = getArguments();
        if (args != null) {
            ParcelableMovieInfo parcelableMovieInfo = args.getParcelable(
                ParcelableMovieInfo.BUNDLE_TAG);
            HashMap<String, String> movieInfo = parcelableMovieInfo.getMovieInfo();
            return movieInfo;
        } else {
            // TODO: how to handle this error?
            Log.e(LOG_TAG, "Initialized without arguments");
            return null;
        }
    }

    private void toggleFavoriteButton() {
        if (mIsFavorite) {
            favorite.setText(R.string.button_label_already_favorite);
        } else {
            favorite.setText(R.string.button_label_not_favorite);
        }
    }

    private void setUpFavoriteButton() {

        favorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                if (mIsFavorite) {
                    // delete movie, its trailers, and its reviews
                    mIsFavorite = false;
                    if (mOfflineMovieId != null) {
                        getContext().getContentResolver().delete(
                            MovieContract.ReviewEntry.CONTENT_URI,
                            MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ?",
                            new String[]{mOfflineMovieId});

                        getContext().getContentResolver().delete(
                            MovieContract.TrailerEntry.CONTENT_URI,
                            MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ?",
                            new String[]{mOfflineMovieId});

                        getContext().getContentResolver().delete(
                            MovieContract.MovieEntry.CONTENT_URI,
                            MovieContract.MovieEntry._ID + " = ?",
                            new String[]{mOfflineMovieId});

                    } else {
                        // TODO: handle error
                    }
                } else {
                    // add movie, its trailers, and its reviews

                    long movieId;
                    HashMap<String, String> movieInfo = getMovieInfo();

                    ContentValues movieValues = new ContentValues();
                    movieValues.put(
                        MovieContract.MovieEntry.COLUMN_TMDB_MOVIE_ID,
                        movieInfo.get("movieId"));
                    movieValues.put(
                        MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
                        movieInfo.get("originalTitle"));
                    movieValues.put(
                        MovieContract.MovieEntry.COLUMN_POPULARITY,
                        movieInfo.get("popularity"));
                    movieValues.put(
                        MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                        movieInfo.get("voteAverage"));
                    movieValues.put(
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                        movieInfo.get("releaseDate"));
                    movieValues.put(
                        MovieContract.MovieEntry.COLUMN_OVERVIEW,
                        movieInfo.get("overview"));
                    movieValues.put(
                        MovieContract.MovieEntry.COLUMN_POSTER_URL,
                        movieInfo.get("posterUrl"));

                    Uri insertedMovieUri = getContext().getContentResolver().insert(
                        MovieContract.MovieEntry.CONTENT_URI, movieValues);
                    movieId = ContentUris.parseId(insertedMovieUri);

                    ////////////////////////////////////////////////////////////

                    Vector<ContentValues> cVVector;
                    int inserted;

                    cVVector = new Vector<>(mTrailers.size());

                    for (HashMap<String, String> trailer : mTrailers) {
                        ContentValues trailerValues = new ContentValues();
                        trailerValues.put(
                            MovieContract.TrailerEntry.COLUMN_MOVIE_KEY,
                            movieId);
                        trailerValues.put(
                            MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
                            trailer.get("name"));
                        trailerValues.put(
                            MovieContract.TrailerEntry.COLUMN_TRAILER_URL,
                            trailer.get("trailerUrl"));
                        cVVector.add(trailerValues);
                    }

                    if (cVVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        inserted = getContext().getContentResolver().bulkInsert(
                            MovieContract.TrailerEntry.CONTENT_URI, cvArray);
                        Log.i(LOG_TAG, "TBD" + inserted);
                    }

                    ////////////////////////////////////////////////////////////

                    cVVector = new Vector<>(mReviews.size());

                    for (HashMap<String, String> review : mReviews) {
                        ContentValues trailerValues = new ContentValues();
                        trailerValues.put(
                            MovieContract.ReviewEntry.COLUMN_MOVIE_KEY,
                            movieId);
                        trailerValues.put(
                            MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
                            review.get("author"));
                        trailerValues.put(
                            MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,
                            review.get("content"));
                        cVVector.add(trailerValues);
                    }

                    if (cVVector.size() > 0) {
                        ContentValues[] cvArray = new ContentValues[cVVector.size()];
                        cVVector.toArray(cvArray);
                        inserted = getContext().getContentResolver().bulkInsert(
                            MovieContract.ReviewEntry.CONTENT_URI, cvArray);
                        Log.i(LOG_TAG, "TBD" + inserted);
                    }

                    mIsFavorite = true;
                }
                toggleFavoriteButton();
            }
        });
        toggleFavoriteButton();
    }

    private void populateReviews(HashMap<String, String>[] reviews) {
        if (reviews != null) {
            mReviews = new ArrayList<>();
            mReviews.addAll(Arrays.asList(reviews));
        }
    }
    private void showReviews() {
        mReviewLinearLayout.removeAllViews();
        for (HashMap<String, String> review : mReviews) {
            Log.d(
                LOG_TAG, "adding to mReviewLinearLayout=" + review.get("author"));
            TextView reviewTextView = new TextView(getContext());
            String text = "By "
                + review.get("author")
                + ": "
                + review.get("content");
            reviewTextView.setText(text);
            mReviewLinearLayout.addView(reviewTextView);
        }
    }

    private void populateTrailers(HashMap<String, String>[] trailers) {
        if (trailers != null) {
            mTrailers = new ArrayList<>();
            mTrailers.addAll(Arrays.asList(trailers));
        }
    }

    private void showTrailers() {
        mTrailerLinearLayout.removeAllViews();
        for (HashMap<String, String> trailer : mTrailers) {
            Log.d(
                LOG_TAG, "adding to mTrailerAdapter=" + trailer.get("name"));
            TrailerTextView trailerName = new TrailerTextView(getContext());
            trailerName.setText(trailer.get("name"));
            trailerName.setUrl(trailer.get("trailerUrl"));
            trailerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrailerTextView ttv = (TrailerTextView) v;
                    Log.i(
                        LOG_TAG,
                        new StringBuilder()
                            .append("onClick")
                            .append("name='" + ttv.getText() + "' ")
                            .append("trailerUrl=" + ttv.getUrl() + " ")
                            .append("status=selected ")
                            .append("intent=action_view")
                            .toString());
                    Intent intent = new Intent(
                        Intent.ACTION_VIEW, Uri.parse(ttv.getUrl()));
                    startActivity(intent);
                }
            });
            mTrailerLinearLayout.addView(trailerName);
        }
    }

    public class TrailerTextView extends TextView {
        private String url;
        public TrailerTextView(Context context) { super(context);}
        public void setUrl(String url) {this.url = url;}
        public String getUrl() {return this.url;}
    }

    public class GetTrailersOnlineTask
        extends AsyncTask<String, Void, HashMap<String, String>[]> {

        private final String LOG_TAG = GetTrailersOnlineTask.class.getSimpleName();

        @Override
        protected HashMap<String, String>[] doInBackground(String...params) {
            String tmdbMovieId = params[0];
            TheMovieDb theMovieDb = new TheMovieDb(
                getResources().getString(R.string.tmdb_api_key));
            return theMovieDb.getTrailers(tmdbMovieId);
        }

        @Override
        protected void onPostExecute(HashMap<String, String>[] trailers) {
            populateTrailers(trailers);
            showTrailers();
        }
    }

    public class GetReviewsOnlineTask
        extends AsyncTask<String, Void, HashMap<String, String>[]> {

        private final String LOG_TAG = GetReviewsOnlineTask.class.getSimpleName();

        @Override
        protected HashMap<String, String>[] doInBackground(String...params) {
            String tmdbMovieId = params[0];
            TheMovieDb theMovieDb = new TheMovieDb(
                getResources().getString(R.string.tmdb_api_key));
            return theMovieDb.getReviews(tmdbMovieId);
        }

        @Override
        protected void onPostExecute(HashMap<String, String>[] reviews) {
            populateReviews(reviews);
            showReviews();
        }
    }

    public class GetTrailersOfflineTask
        extends AsyncTask<String, Void, HashMap<String, String>[]> {

        private final String LOG_TAG = GetTrailersOfflineTask.class.getSimpleName();

        @Override
        protected HashMap<String, String>[] doInBackground(String...params) {
            String movieId = params[0];

            // query DB entry by movieId
            Cursor trailerCursor = getContext().getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                new String[] {
                    MovieContract.TrailerEntry.COLUMN_TRAILER_NAME,
                    MovieContract.TrailerEntry.COLUMN_TRAILER_URL
                },
                MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ?",
                new String[]{movieId},
                null);

            HashMap<String, String>[] trailers = new HashMap[trailerCursor.getCount()];

            if (trailerCursor != null) {
                int i = 0;
                while (trailerCursor.moveToNext()) {
                    int trailerNameIndex = trailerCursor.getColumnIndex(
                        MovieContract.TrailerEntry.COLUMN_TRAILER_NAME);
                    int trailerUrlIndex = trailerCursor.getColumnIndex(
                        MovieContract.TrailerEntry.COLUMN_TRAILER_URL);

                    HashMap<String, String> trailer = new HashMap<>();
                    trailer.put("name", trailerCursor.getString(trailerNameIndex));
                    trailer.put("trailerUrl",
                        trailerCursor.getString(trailerUrlIndex));
                    trailers[i] = trailer;
                    i++;
                }
            }
            trailerCursor.close();
            return trailers;
        }

        @Override
        protected void onPostExecute(HashMap<String, String>[] trailers) {
            populateTrailers(trailers);
            showTrailers();
        }
    }

    public class GetReviewsOfflineTask
        extends AsyncTask<String, Void, HashMap<String, String>[]> {

        private final String LOG_TAG = GetReviewsOfflineTask.class.getSimpleName();

        @Override
        protected HashMap<String, String>[] doInBackground(String...params) {
            String movieId = params[0];

            // query DB entry by movieId
            Cursor reviewCursor = getContext().getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                new String[] {
                    MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,
                    MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT
                },
                MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + "=?",
                new String[]{movieId},
                null);

            HashMap<String, String>[] reviews = new HashMap[
                reviewCursor.getCount()];

            if (reviewCursor != null) {
                int i = 0;
                while (reviewCursor.moveToNext()) {
                    int reviewAuthorIndex = reviewCursor.getColumnIndex(
                        MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR);
                    int reviewContentIndex = reviewCursor.getColumnIndex(
                        MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT);

                    HashMap<String, String> review = new HashMap<>();
                    review.put("author", reviewCursor.getString(reviewAuthorIndex));
                    review.put("content", reviewCursor.getString(reviewContentIndex));
                    reviews[i] = review;
                    i++;
                }
            } else {
                Log.w(
                    LOG_TAG,
                    new StringBuilder()
                        .append("uri=" + reviewCursor)
                        .toString());
            }
            reviewCursor.close();
            return reviews;
        }

        @Override
        protected void onPostExecute(HashMap<String, String>[] reviews) {
            populateReviews(reviews);
            showReviews();
        }
    }

}
