package com.gellert.moviesforyou;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class FavoritesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
