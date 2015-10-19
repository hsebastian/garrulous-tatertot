package com.example.hsebastian.popularmoviesvol1;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(
            R.layout.fragment_movie_detail, container, false);

        ImageView imageView = (ImageView) rootView.findViewById(
            R.id.movie_poster_image_view);
        imageView.setImageResource(R.drawable.sample_6);

        return rootView;
    }
}
