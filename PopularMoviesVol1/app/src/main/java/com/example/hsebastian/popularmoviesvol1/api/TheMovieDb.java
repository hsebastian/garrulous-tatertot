package com.example.hsebastian.popularmoviesvol1.api;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by hsebastian on 1/29/16.
 */
public class TheMovieDb {
    private final String LOG_TAG = TheMovieDb.class.getSimpleName();
    private String apiKey;
    private static final String SCHEME = "https";
    private static final String HOST = "api.themoviedb.org";
    private static final String VERSION = "3";
    private static final String ORDER_BY_POPULARITY = "popularity.desc";
    private static final String ORDER_BY_VOTE_AVERAGE = "vote_average.desc";

    public TheMovieDb(String apiKey) {
        this.apiKey = apiKey;
    }

    private String get(String urlString) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {

            Log.i(LOG_TAG, "url=" + urlString + " " + "action=connect");
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.d(LOG_TAG, "url=" + urlString + " " + "status=connected");

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.w(LOG_TAG, "url=" + urlString + " " + "inputStream=null");
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
                Log.w(LOG_TAG, "url=" + urlString + " " + "buffer=empty");
                return null;
            }
            String JsonStr = buffer.toString();
            Log.i(LOG_TAG, "url=" + urlString + " " + "action=response_parsed");
            Log.v(LOG_TAG, JsonStr);
            return JsonStr;
        } catch (IOException e) {
            // If the code didn't successfully get the movies,
            // there's no point in attempting to parse it.
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
                Log.d(
                    LOG_TAG, "url=" + urlString + " " + "status=disconnected");
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

    }

    private Uri.Builder getTheMovieDbUrlBuilder() {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(this.SCHEME);
        uriBuilder.authority(this.HOST);
        uriBuilder.appendPath(this.VERSION);
        return uriBuilder;
    }

    public String getBaseUrl() {
        return this.getTheMovieDbUrlBuilder().toString();
    }

    public HashMap<String, String>[] discoverMovies(String sortBy) {

        Uri.Builder uriBuilder = this.getTheMovieDbUrlBuilder();
        uriBuilder.appendPath("discover");
        uriBuilder.appendPath("movie");
        uriBuilder.appendQueryParameter("sort_by", sortBy);
        uriBuilder.appendQueryParameter(
            "api_key", this.apiKey);

        try {
            String jsonString = this.get(uriBuilder.build().toString());
            return getMovieInfosFromJson(jsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, String>[] discoverMoviesByPopularity() {
        return this.discoverMovies(TheMovieDb.ORDER_BY_POPULARITY);
    }

    public HashMap<String, String>[] discoverMoviesByVoteAverage() {
        return this.discoverMovies(TheMovieDb.ORDER_BY_VOTE_AVERAGE);
    }

    public HashMap<String, String>[] getReviews(String movieId) {

        Uri.Builder uriBuilder = this.getTheMovieDbUrlBuilder();
        uriBuilder.appendPath("movie");
        uriBuilder.appendPath(movieId);
        uriBuilder.appendPath("reviews");
        uriBuilder.appendQueryParameter("api_key", this.apiKey);

        try {
            String jsonString = this.get(uriBuilder.build().toString());
            return this.getReviewsFromJson(jsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public HashMap<String, String>[] getTrailers(String movieId) {

        Uri.Builder uriBuilder = this.getTheMovieDbUrlBuilder();
        uriBuilder.appendPath("movie");
        uriBuilder.appendPath(movieId);
        uriBuilder.appendPath("videos");
        uriBuilder.appendQueryParameter("api_key", this.apiKey);

        try {
            String jsonString = this.get(uriBuilder.build().toString());
            return this.getTrailersFromJson(jsonString);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private HashMap<String, String>[] getTrailersFromJson(String trailersJsonStr)
        throws JSONException {

        JSONObject trailersJson = new JSONObject(trailersJsonStr);
        JSONArray trailerResults = trailersJson.getJSONArray("results");

        HashMap<String, String>[] trailers = new HashMap[trailerResults.length()];
        for(int i = 0; i < trailerResults.length(); i++) {

            JSONObject trailerJsonObject = trailerResults.getJSONObject(i);
            Log.i(LOG_TAG, trailerJsonObject.toString());

            HashMap<String, String> trailer = new HashMap<>();
            trailer.put("name", trailerJsonObject.getString("name"));
            trailer.put(
                "trailerUrl", buildTrailerUrl(trailerJsonObject.getString("key")));
//            trailer.put("site", trailerJsonObject.getString("site"));
//            trailer.put("size", trailerJsonObject.getString("size"));

            trailers[i] = trailer;

        }
        return trailers;
    }

    private String buildTrailerUrl(String key) {
        // Build a URL like this https://www.youtube.com/watch?v=U7rw8SYumx8
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority("www.youtube.com");
        uriBuilder.appendPath("watch");
        uriBuilder.appendQueryParameter("v", key);
        String trailerUrl = uriBuilder.build().toString();
        Log.d(LOG_TAG, "trailerUrl=" + trailerUrl);
        return trailerUrl;
    }

    private HashMap<String, String>[] getReviewsFromJson(String trailersJsonStr)
        throws JSONException {

        JSONObject reviewsJson = new JSONObject(trailersJsonStr);
        JSONArray reviewResults = reviewsJson.getJSONArray("results");

        HashMap<String, String>[] reviews = new HashMap[reviewResults.length()];
        for(int i = 0; i < reviewResults.length(); i++) {

            JSONObject reviewJsonObject = reviewResults.getJSONObject(i);
            Log.i(LOG_TAG, reviewJsonObject.toString());

            HashMap<String, String> review = new HashMap<>();
            review.put("author", reviewJsonObject.getString("author"));
            review.put("content", reviewJsonObject.getString("content"));

            reviews[i] = review;

        }
        return reviews;
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
            String movieId = movie.getString(("id"));
            Log.i(
                LOG_TAG,
                new StringBuilder()
                    .append("originalTitle='" + originalTitle + "' ")
                    .append("popularity=" + popularity + " ")
                    .append("voteAverage=" + voteAverage + " ")
                    .append("releaseDate=" + releaseDate + " ")
                    .append("overview='" + overview + "' ")
                    .append("posterPath=" + posterPath + " ")
                    .append("movieId=" + movieId + " ")
                    .toString());

            HashMap<String, String> movieInfo = new HashMap<>();
            movieInfo.put("originalTitle", originalTitle);
            movieInfo.put("popularity", popularity);
            movieInfo.put("voteAverage", voteAverage);
            movieInfo.put("releaseDate", releaseDate);
            movieInfo.put("overview", overview);
            movieInfo.put("posterUrl", buildPosterUrl(posterPath));
            movieInfo.put("movieId", movieId);

            movieInfos[i] = movieInfo;

        }
        return movieInfos;
    }

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
        Log.d(LOG_TAG, "posterUrl=" + posterUrl);
        return posterUrl;
    }

}
