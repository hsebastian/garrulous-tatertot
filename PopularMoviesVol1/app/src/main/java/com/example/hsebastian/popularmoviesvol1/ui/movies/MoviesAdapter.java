package com.example.hsebastian.popularmoviesvol1.ui.movies;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.hsebastian.popularmoviesvol1.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by hsebastian on 10/19/15.
 */
public class MoviesAdapter extends ArrayAdapter<HashMap<String, String>> {

    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    static class ViewHolder {
        @Bind(R.id.list_item_movies_imageview) ImageView poster;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MoviesAdapter(
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

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
            Context.LAYOUT_INFLATER_SERVICE);
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(
                R.layout.list_item_movies, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> movieInfo = getItem(position);
        String posterUrl = movieInfo.get("posterUrl");
        Log.d(
            LOG_TAG,
            new StringBuilder().append("posterUrl=" + posterUrl + " ").toString());

        Picasso picasso = Picasso.with(getContext());
        picasso.load(posterUrl).into(holder.poster);
        return convertView;
    }
}
