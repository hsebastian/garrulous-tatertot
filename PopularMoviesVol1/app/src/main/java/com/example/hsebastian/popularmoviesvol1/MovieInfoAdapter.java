package com.example.hsebastian.popularmoviesvol1;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;


/**
 * Created by hsebastian on 10/11/15.
 */
public class MovieInfoAdapter extends ArrayAdapter<HashMap<String, String>> {

    private static final String LOG_TAG = MovieInfoAdapter.class.getSimpleName();

    private Context mContext;
    private int mResource;
    private int mImageViewResourceId;
    private List<HashMap<String, String>> mMovieInfos;

    public MovieInfoAdapter(
        Context context, int resource, int ImageViewResourceId,
        List<HashMap<String, String>> objects) {
        super(context, resource, ImageViewResourceId, objects);
        this.mContext = context;
        this.mResource = resource;
        this.mImageViewResourceId = ImageViewResourceId;
        this.mMovieInfos = objects;
        Log.d(
            LOG_TAG,
            new StringBuilder()
                .append("mContext=" + this.mContext.toString() + " ")
                .append("mResource=" + String.valueOf(this.mResource) + " ")
                .append("mImageViewResourceId=" + String.valueOf(this.mImageViewResourceId) + " ")
                .append("mMovieInfos=" + String.valueOf(this.mMovieInfos.size()) + " ")
                .toString());
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        HashMap<String, String> movieInfo = mMovieInfos.get(position);
        Log.d(
            LOG_TAG,
            new StringBuilder()
                .append("position=" + position + " ")
                .append("originalTitle='" +
                    movieInfo.get("originalTitle") + "' ")
                .toString());
        return movieInfo;
    }

    @Override
    public void add(HashMap<String, String> object) {
        mMovieInfos.add(object);
        Log.d(LOG_TAG, "added");
    }

    @Override
    public void clear() {
        mMovieInfos.clear();
        Log.d(LOG_TAG, "cleared");
    }

    @Override
    public int getCount() {
        int itemCount = mMovieInfos.size();
        Log.d(LOG_TAG, "itemCount=" + itemCount);
        return itemCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HashMap<String, String> movieInfo = getItem(position);

        View rootView = LayoutInflater.from(mContext).inflate(
            mResource, parent, false);
        ImageView imageView = (ImageView) rootView.findViewById(mImageViewResourceId);
        imageView.setImageResource(mThumbIds[position]);
//        Picasso.with(mContext).load(movieInfo.get("posterUrl")).into(imageView);
        return rootView;
    }

    // references to our images
    private Integer[] mThumbIds = {
        R.drawable.sample_2, R.drawable.sample_3,
        R.drawable.sample_4, R.drawable.sample_5,
        R.drawable.sample_6, R.drawable.sample_7,
        R.drawable.sample_0, R.drawable.sample_1,
        R.drawable.sample_2, R.drawable.sample_3,
        R.drawable.sample_4, R.drawable.sample_5,
        R.drawable.sample_6, R.drawable.sample_7,
        R.drawable.sample_0, R.drawable.sample_1,
        R.drawable.sample_2, R.drawable.sample_3,
        R.drawable.sample_4, R.drawable.sample_5,
        R.drawable.sample_6, R.drawable.sample_7
    };
}