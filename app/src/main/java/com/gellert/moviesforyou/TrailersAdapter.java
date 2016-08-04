package com.gellert.moviesforyou;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gellert.moviesforyou.objects.Trailer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gellert on 2016. 07. 15..
 */

public class TrailersAdapter extends ArrayAdapter<Trailer> {
    final static String TAG = "TrailersAdapter";
    private LayoutInflater inflater;

    List<Trailer> trailers;

    public TrailersAdapter(Context context, int resource) {
        super(context, resource);
        trailers = new ArrayList<Trailer>();
        this.inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void add(Trailer object) {
        super.add(object);
        trailers.add(object);
    }

    @Override
    public void clear() {
        super.clear();
        trailers.clear();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final MainListHolder mHolder;
        View v = convertView;
        if (convertView == null)
        {
            mHolder = new MainListHolder();
            v = inflater.inflate(R.layout.custom_trailer_view, null);
            mHolder.nameTextView = (TextView) v.findViewById(R.id.trailerName);
            mHolder.playTrailerButton = (ImageButton) v.findViewById(R.id.playTrailerButton);
            v.setTag(mHolder);
        }

        else
        {
            mHolder = (MainListHolder) v.getTag();
            v.setTag(mHolder);
        }

        mHolder.nameTextView.setText(trailers.get(position).getName());


        mHolder.playTrailerButton.setOnClickListener(
                new ImageButton.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(trailers.get(position).getUrl()));
                        getContext().startActivity(youtubeIntent);
                    }
                }
        );

        return v;
    }
    class MainListHolder
    {
        private TextView nameTextView;
        private ImageButton playTrailerButton;

    }
}