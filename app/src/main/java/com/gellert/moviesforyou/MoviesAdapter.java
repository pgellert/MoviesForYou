package com.gellert.moviesforyou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.gellert.moviesforyou.objects.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends ArrayAdapter<Movie> {
    final static String TAG = "MoviesAdapter";

    List<Movie> Movies;

    public MoviesAdapter(Context context, int resource) {
        super(context, resource);
        Movies = new ArrayList<Movie>();
    }

    @Override
    public void add(Movie object) {
        super.add(object);
        Movies.add(object);
    }

    @Override
    public void clear() {
        super.clear();
        Movies.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.custom_list_view, parent, false);
        }

        Picasso.with(getContext()).load(Movies.get(position).getImageUrl())
                .into((ImageView) convertView.findViewById(R.id.movieImage));

        return convertView;
    }
}
