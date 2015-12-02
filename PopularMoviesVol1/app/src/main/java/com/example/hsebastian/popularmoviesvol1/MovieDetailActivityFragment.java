package com.example.hsebastian.popularmoviesvol1;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    private final String LOG_TAG = MovieDetailActivityFragment.class.getSimpleName();

    @Bind(R.id.movie_title_text_view) TextView title;
    @Bind(R.id.release_date_text_view) TextView releaseDate;
    @Bind(R.id.vote_average_text_view) TextView voteAverage;
    @Bind(R.id.plot_synopsis_text_view) TextView plotSynopsis;
    @Bind(R.id.movie_poster_image_view) ImageView poster;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(
            R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);

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
            ParcelableMovieInfo parcelableMovieInfo = data.getParcelable(
                "UserTag");
            HashMap<String, String> movieInfo = parcelableMovieInfo.getMovieInfo();

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

        }
        return rootView;
    }
}
