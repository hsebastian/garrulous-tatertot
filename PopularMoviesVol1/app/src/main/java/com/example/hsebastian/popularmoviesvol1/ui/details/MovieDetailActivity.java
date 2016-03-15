package com.example.hsebastian.popularmoviesvol1.ui.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.hsebastian.popularmoviesvol1.R;
import com.example.hsebastian.popularmoviesvol1.ui.ParcelableMovieInfo;


public class MovieDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = this.getIntent();
        if (intent != null && intent.hasExtra(ParcelableMovieInfo.BUNDLE_TAG)) {
            Log.d(
                LOG_TAG,
                new StringBuilder()
                    .append("intent=" + intent.toString() + " ")
                    .append("action=getMovieInfo")
                    .toString());
            Bundle bundle = intent.getExtras();
            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(
                R.id.movie_detail_container, movieDetailFragment).commit();
        }
    }
}
