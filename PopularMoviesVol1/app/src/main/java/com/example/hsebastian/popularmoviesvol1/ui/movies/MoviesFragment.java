package com.example.hsebastian.popularmoviesvol1.ui.movies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.hsebastian.popularmoviesvol1.R;
import com.example.hsebastian.popularmoviesvol1.api.TheMovieDb;
import com.example.hsebastian.popularmoviesvol1.data.MovieContract;
import com.example.hsebastian.popularmoviesvol1.ui.details.MovieDetailActivity;
import com.example.hsebastian.popularmoviesvol1.ui.ParcelableMovieInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private MoviesAdapter mMoviesAdapter;

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(
            R.layout.fragment_movie_list, container, false);

        mMoviesAdapter = new MoviesAdapter(
            getActivity(), new ArrayList<HashMap<String, String>>());
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(mMoviesAdapter);
        gridView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(
                        AdapterView<?> adapterView, View view,
                        int position, long l) {
                    HashMap<String, String> movieInfo = mMoviesAdapter.getItem(
                        position);
                    Log.i(
                        LOG_TAG,
                        new StringBuilder()
                            .append("position=" + position + " ")
                            .append("originalTitle='" +
                                movieInfo.get("originalTitle") + "' ")
                            .append("status=selected")
                            .toString());
                    ParcelableMovieInfo parcelableMovieInfo =
                        new ParcelableMovieInfo(movieInfo);

                    MoviesActivity thisActivity = (MoviesActivity) getActivity();
                    boolean isTablet = thisActivity.isTablet();
                    if (isTablet) {
                        thisActivity.replaceFragment(parcelableMovieInfo);
                    } else {
                        Intent intent = new Intent(
                            getActivity(), MovieDetailActivity.class);
                        intent.putExtra(
                            ParcelableMovieInfo.BUNDLE_TAG, parcelableMovieInfo);
                        startActivity(intent);
                    }
                }
            }
        );
        return rootView;

    }

    @Override
    public void onResume() {
        Log.d(LOG_TAG, "onResume");
        super.onResume();
        updateMovies();
    }

    private void updateMovies() {
        SharedPreferences sharedPref = PreferenceManager
            .getDefaultSharedPreferences(getActivity());
        String sortByPref = sharedPref.getString(
            getString(R.string.pref_sortby_key),
            getString(R.string.pref_sortby_default));
        Log.d(LOG_TAG, "sortByPref=" + sortByPref);
        new FetchMoviesTask().execute(sortByPref);
    }



    public class FetchMoviesTask
            extends AsyncTask<String, Void, HashMap<String, String>[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected HashMap<String, String>[] doInBackground(String...params) {

            String sortByPref = params[0];
            String sortBy;

            if (sortByPref.equals(
                getString(R.string.pref_sortby_value_favorites))) {

                // query DB movie entries
                Cursor movieCursor = getContext().getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    MovieContract.MovieEntry._ID + " DESC");

                HashMap<String, String>[] movieInfos = new HashMap[
                    movieCursor.getCount()];

                if (movieCursor != null) {
                    int i = 0;
                    while (movieCursor.moveToNext()) {
                        int originalTitleIndex = movieCursor.getColumnIndex(
                            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
                        int popularityIndex = movieCursor.getColumnIndex(
                            MovieContract.MovieEntry.COLUMN_POPULARITY);
                        int voteAverageIndex = movieCursor.getColumnIndex(
                            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
                        int releaseDateIndex = movieCursor.getColumnIndex(
                            MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
                        int overviewIndex = movieCursor.getColumnIndex(
                            MovieContract.MovieEntry.COLUMN_OVERVIEW);
                        int posterUrlIndex = movieCursor.getColumnIndex(
                            MovieContract.MovieEntry.COLUMN_POSTER_URL);
                        int movieIdIndex = movieCursor.getColumnIndex(
                            MovieContract.MovieEntry.COLUMN_TMDB_MOVIE_ID);

                        HashMap<String, String> movieInfo = new HashMap<>();
                        movieInfo.put(
                            "originalTitle",
                            movieCursor.getString(originalTitleIndex));
                        movieInfo.put(
                            "popularity",
                            movieCursor.getString(popularityIndex));
                        movieInfo.put(
                            "voteAverage",
                            movieCursor.getString(voteAverageIndex));
                        movieInfo.put(
                            "releaseDate",
                            movieCursor.getString(releaseDateIndex));
                        movieInfo.put(
                            "overview",
                            movieCursor.getString(overviewIndex));
                        movieInfo.put(
                            "posterUrl",
                            movieCursor.getString(posterUrlIndex));
                        movieInfo.put(
                            "movieId",
                            movieCursor.getString(movieIdIndex));
                        movieInfos[i] = movieInfo;
                        i++;
                    }
                }
                movieCursor.close();
                return movieInfos;

            }

            TheMovieDb theMovieDb = new TheMovieDb(
                getResources().getString(R.string.tmdb_api_key));

            if (sortByPref.equals(
                getString(R.string.pref_sortby_value_popularity))) {

                return theMovieDb.discoverMoviesByPopularity();

            } else if (sortByPref.equals(
                getString(R.string.pref_sortby_value_rating))) {

                return theMovieDb.discoverMoviesByVoteAverage();

            } else {

                Log.w(LOG_TAG, "sortByPref=" + sortByPref + " invalid");
                return null;

            }
        }

        @Override
        protected void onPostExecute(HashMap<String, String>[] movieInfos) {
            if (movieInfos != null) {
                mMoviesAdapter.clear();
                for (HashMap<String, String> movieInfo : movieInfos) {
                    Log.d(
                        LOG_TAG,
                        "adding to mMoviesAdapter " + movieInfo.get("originalTitle"));
                    mMoviesAdapter.add(movieInfo);
                }
            }
        }
    }
}
