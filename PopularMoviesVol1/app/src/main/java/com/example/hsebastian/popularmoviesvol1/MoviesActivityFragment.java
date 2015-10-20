package com.example.hsebastian.popularmoviesvol1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesActivityFragment extends Fragment {

    private final String LOG_TAG = MoviesActivityFragment.class.getSimpleName();
    private MovieAdapter mMovieAdapter;

    public MoviesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(
            R.layout.fragment_movies, container, false);

//        mMovieAdapter = new MovieInfoAdapter(
//            getActivity(),
//            R.layout.list_item_movies,
//            R.id.list_item_movies_imageview,
//            new ArrayList<HashMap<String, String>>());

        mMovieAdapter = new MovieAdapter(
            getActivity(), new ArrayList<HashMap<String, String>>());
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(
                        AdapterView<?> adapterView, View view,
                        int position, long l) {
                    HashMap<String, String> movieInfo = mMovieAdapter.getItem(
                        position);
                    Log.i(
                        LOG_TAG,
                        new StringBuilder()
                            .append("position=" + position + " ")
                            .append("originalTitle='" +
                                movieInfo.get("originalTitle") + "' ")
                            .append("status=selected")
                            .append("intent=" +
                                MovieDetailActivity.class.toString())
                            .toString());
                    ParcelableMovieInfo parcelableMovieInfo =
                        new ParcelableMovieInfo(movieInfo);
                    Intent intent = new Intent(
                        getActivity(), MovieDetailActivity.class);
                    intent.putExtra("UserTag", parcelableMovieInfo);
                    startActivity(intent);
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

    ///////////////////////////////////////////////////////////////////////////

    public class FetchMoviesTask
            extends AsyncTask<String, Void, HashMap<String, String>[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private String buildPosterUrl(String posterPath) {
            // Construct the URL to return the poster
            // An example would look like this:
            //     https://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg
            // More explanation found here:
            //     http://docs.themoviedb.apiary.io/#reference/configuration/configuration/get?console=1
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.scheme("https");
            uriBuilder.authority("image.tmdb.org");
            uriBuilder.appendPath("t");
            uriBuilder.appendPath("p");
            uriBuilder.appendPath("w185");
            uriBuilder.appendPath(posterPath.replace("/", ""));

            String posterUrl = uriBuilder.build().toString();
            Log.i(LOG_TAG, "posterUrl=" + posterUrl);
            return posterUrl;
        }

        /**
         * Take the String representing the complete movie list in JSON Format
         * and pull out the poster path for each movie.
         *
         * Contruct the complete poster URL using the base URL, the image size,
         * and the poster path.
         */
        private HashMap<String, String>[] getMovieInfosFromJson(String moviesJsonStr)
            throws JSONException {

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray movies = moviesJson.getJSONArray("results");

            HashMap<String, String>[] movieInfos = new HashMap[movies.length()];
            for(int i = 0; i < movies.length(); i++) {

                JSONObject movie = movies.getJSONObject(i);
                String originalTitle = movie.getString("original_title");
                String popularity = movie.getString("popularity");
                String voteAverage = movie.getString("vote_average");
                String releaseDate = movie.getString("release_date");
                String overview = movie.getString("overview");
                String posterPath = movie.getString("poster_path");
                Log.d(
                    LOG_TAG,
                    new StringBuilder()
                        .append("originalTitle='" + originalTitle + "' ")
                        .append("popularity=" + popularity + " ")
                        .append("voteAverage=" + voteAverage + " ")
                        .append("releaseDate=" + releaseDate + " ")
                        .append("overview='" + overview + "' ")
                        .append("posterPath=" + posterPath + " ")
                        .toString());

                HashMap<String, String> movieInfo = new HashMap<>();
                movieInfo.put("originalTitle", originalTitle);
                movieInfo.put("popularity", popularity);
                movieInfo.put("voteAverage", voteAverage);
                movieInfo.put("releaseDate", releaseDate);
                movieInfo.put("overview", overview);
                movieInfo.put("posterUrl", buildPosterUrl(posterPath));

                movieInfos[i] = movieInfo;

            }
            return movieInfos;

        }

        @Override
        protected HashMap<String, String>[] doInBackground(String...params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String sortByPref = params[0];
            String sortBy;
            if (sortByPref.equals(
                    getString(R.string.pref_sortby_value_popularity))) {
                sortBy = "popularity.desc";
            } else if (sortByPref.equals(
                    getString(R.string.pref_sortby_value_rating))) {
                sortBy = "vote_average.desc";
            } else {
                Log.w(
                    LOG_TAG, "sortByPref=" + sortByPref + " invalid");
                return null;
            }

            String urlString = null;
            String moviesJsonStr = null;

            try {
                // Construct the URL for themoviedb query
                // Possible parameters are avaiable at the API page, at
                // https://www.themoviedb.org/documentation/api/discover
                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme("https");
                uriBuilder.authority("api.themoviedb.org");
                uriBuilder.appendPath("3");
                uriBuilder.appendPath("discover");
                uriBuilder.appendPath("movie");
                uriBuilder.appendQueryParameter("sort_by", sortBy);
                uriBuilder.appendQueryParameter(
                    "api_key", getResources().getString(R.string.tmdb_api_key));

                // Create the request to themoviedb, and open the connection
                urlString = uriBuilder.build().toString();
                Log.i(
                    LOG_TAG, "url=" + urlString + " " + "action=connect");
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Log.d(
                    LOG_TAG, "url=" + urlString + " " + "status=connected");

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    Log.w(
                        LOG_TAG, "url=" + urlString + " " + "inputStream=null");
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary
                    // (it won't affect parsing). But it does make debugging
                    // a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.w(
                        LOG_TAG, "url=" + urlString + " " + "buffer=empty");
                    return null;
                }
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG, moviesJsonStr);
            } catch (IOException e) {
                // If the code didn't successfully get the movies,
                // there's no point in attempting to parse it.
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                    Log.d(
                        LOG_TAG,
                        "url=" + urlString + " " + "status=disconnected");
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }


            try {
                return getMovieInfosFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, String>[] movieInfos) {
            if (movieInfos != null) {
                mMovieAdapter.clear();
                for (HashMap<String, String> movieInfo : movieInfos) {
                    Log.d(
                        LOG_TAG,
                        "adding to mMovieAdapter " + movieInfo.get("originalTitle"));
                    mMovieAdapter.add(movieInfo);
                }
            }
        }
    }
}
