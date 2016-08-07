package com.gellert.moviesforyou;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gellert.moviesforyou.objects.Movie;
import com.gellert.moviesforyou.provider.FavoritesColumns;
import com.gellert.moviesforyou.provider.FavoritesProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class FavoritesActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private FavoritesAdapter adapter;
    private ListView favoritesListView;

    private int LOADER_ID = 10;

    private static final String TAG = "FavoritesActivityFragment";



    public FavoritesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favoritesListView = (ListView)view.findViewById(R.id.favoritesListView);
        if (adapter != null) {
            favoritesListView.setAdapter(adapter);
        }

        favoritesListView.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Cursor cursor = adapter.getCursor();
                        cursor.moveToPosition(position);

                        Intent details = new Intent(getActivity(), DetailsActivity.class)
                                .putExtra(Movie.MOVIE_ID, cursor.getInt(cursor.getColumnIndex(FavoritesColumns.MOVIE_ID)))
                                .putExtra(Movie.MOVIE_IMAGE_URL, cursor.getString(cursor.getColumnIndex(FavoritesColumns.IMAGE_URL)))
                                .putExtra(Movie.MOVIE_ORIGINAL_TITLE, cursor.getString(cursor.getColumnIndex(FavoritesColumns.TITLE)))
                                .putExtra(Movie.MOVIE_PLOT_SYNOPSIS, cursor.getString(cursor.getColumnIndex(FavoritesColumns.PLOT_SYNOPSIS)))
                                .putExtra(Movie.MOVIE_RELEASE_DATE, cursor.getString(cursor.getColumnIndex(FavoritesColumns.RELEASE_DATE)))
                                .putExtra(Movie.MOVIE_VOTE_AVERAGE, cursor.getFloat(cursor.getColumnIndex(FavoritesColumns.VOTE_AVERAGE)));
                        startActivity(details);
                    }
                }
        );


        getLoaderManager().initLoader(LOADER_ID,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), FavoritesProvider.Favorites.CONTENT_URI, null, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data == null){
            return;
        }

        if(adapter == null){
            adapter = new FavoritesAdapter(getActivity(),data);
            favoritesListView.setAdapter(adapter);
        } else{
            adapter.changeCursor(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
