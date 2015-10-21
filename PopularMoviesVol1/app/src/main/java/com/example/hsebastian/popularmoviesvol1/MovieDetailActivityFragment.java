package com.example.hsebastian.popularmoviesvol1;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(
            R.layout.fragment_movie_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("UserTag")) {

            Log.d(
                LOG_TAG,
                new StringBuilder()
                    .append("onCreateView ")
                    .append("intent=" + intent.toString() + " ")
                    .append("action=getParcelable")
                    .toString());

            Bundle data = intent.getExtras();
            ParcelableMovieInfo parcelableMovieInfo = data.getParcelable("UserTag");
            HashMap<String, String> movieInfo = parcelableMovieInfo.getMovieInfo();

            Log.i(
                LOG_TAG,
                new StringBuilder()
                    .append("onCreateView ")
                    .append("originalTitle='" + movieInfo.get("originalTitle") + "' ")
                    .append("action=showDetail")
                    .toString());

            TextView movieTitleTextView = (TextView) rootView.findViewById(
                R.id.movie_title_text_view);
            movieTitleTextView.setText(movieInfo.get("originalTitle"));

            TextView releaseDateTextView = (TextView) rootView.findViewById(
                R.id.release_date_text_view);
            releaseDateTextView.setText(movieInfo.get("releaseDate"));

            TextView voteAverageTextView = (TextView) rootView.findViewById(
                R.id.vote_average_text_view);
            voteAverageTextView.setText(
                "Vote average: " + movieInfo.get("voteAverage") + "/10.0");

            TextView plotSynopsisTextView = (TextView) rootView.findViewById(
                R.id.plot_synopsis_text_view);
            plotSynopsisTextView.setText(movieInfo.get("overview"));

            ImageView imageView = (ImageView) rootView.findViewById(
                R.id.movie_poster_image_view);
            Picasso picasso = Picasso.with(imageView.getContext());
            picasso.load(movieInfo.get("posterUrl")).into(imageView);

        }
        return rootView;
    }
}
