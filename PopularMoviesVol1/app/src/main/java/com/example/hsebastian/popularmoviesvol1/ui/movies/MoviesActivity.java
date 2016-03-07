package com.example.hsebastian.popularmoviesvol1.ui.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hsebastian.popularmoviesvol1.R;
import com.example.hsebastian.popularmoviesvol1.ui.ParcelableMovieInfo;
import com.example.hsebastian.popularmoviesvol1.ui.details.MovieDetailFragment;
import com.example.hsebastian.popularmoviesvol1.ui.settings.SettingsActivity;

public class MoviesActivity extends AppCompatActivity {

    private final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private static final String MOVIEDETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (getResources().getBoolean(R.bool.isTablet)) {
            Log.d(LOG_TAG, "Running on tablet");
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(
                    R.id.movie_detail_container, new MovieDetailFragment()).commit();
            }
        } else {
            mTwoPane = false;
        }

    }

    public boolean isTablet() {
        Log.d(LOG_TAG, "tablet=" + String.valueOf(mTwoPane));
        return mTwoPane;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void replaceFragment(ParcelableMovieInfo parcelableMovieInfo) {
        Log.i(LOG_TAG, "replaceFragment");
        Bundle args = new Bundle();
        args.putParcelable(ParcelableMovieInfo.BUNDLE_TAG, parcelableMovieInfo);
        MovieDetailFragment detailFragment = new MovieDetailFragment();
        detailFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(
            R.id.movie_detail_container, detailFragment).commit();
    }
}
