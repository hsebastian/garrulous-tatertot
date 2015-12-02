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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hsebastian on 10/19/15.
 */
public class MovieAdapter extends ArrayAdapter<HashMap<String, String>> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    static class ViewHolder {
        @Bind(R.id.list_item_movies_imageview) ImageView poster;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

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
        String posterUrl = movieInfo.get("posterUrl");

        Log.d(
            LOG_TAG,
            new StringBuilder()
                .append("posterUrl=" + posterUrl + " ")
                .toString());

        ViewHolder viewHolder;
        View rootView = LayoutInflater.from(getContext())
            .inflate(R.layout.list_item_movies, parent, false);
        viewHolder = new ViewHolder(rootView);
        rootView.setTag(viewHolder);

        Picasso picasso = Picasso.with(getContext());
        picasso.load(posterUrl).into(viewHolder.poster);
        return rootView;
    }
}
