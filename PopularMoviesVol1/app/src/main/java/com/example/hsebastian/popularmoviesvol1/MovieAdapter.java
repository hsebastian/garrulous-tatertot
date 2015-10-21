package com.example.hsebastian.popularmoviesvol1;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hsebastian on 10/19/15.
 */
public class MovieAdapter extends ArrayAdapter<HashMap<String, String>> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter (
            Activity content, List<HashMap<String, String>> movieInfos) {
        super(content, 0, movieInfos);
        Log.d(
            LOG_TAG,
            new StringBuilder()
                .append("Activity=" + content.toString() + " ")
                .append("mMovieInfos=" + String.valueOf(movieInfos.size()))
                .toString());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<String, String> movieInfo = getItem(position);
        View rootView = LayoutInflater.from(getContext())
            .inflate(R.layout.list_item_movies, parent, false);
        ImageView imageView = (ImageView) rootView.findViewById(
            R.id.list_item_movies_imageview);
        String posterUrl = movieInfo.get("posterUrl");
        Log.d(
            LOG_TAG,
            new StringBuilder()
                .append("posterUrl=" + posterUrl + " ")
                .toString());
        Picasso picasso = Picasso.with(imageView.getContext());
        picasso.load(posterUrl).into(imageView);
        return rootView;
    }
}
