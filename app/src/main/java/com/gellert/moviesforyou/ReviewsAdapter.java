package com.gellert.moviesforyou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gellert.moviesforyou.objects.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gellert on 2016. 07. 15..
 */

public class ReviewsAdapter extends ArrayAdapter<Review> {
    final static String TAG = "MoviesAdapter";
    private LayoutInflater inflater;

    List<Review> reviews;

    public ReviewsAdapter(Context context, int resource) {
        super(context, resource);
        reviews = new ArrayList<Review>();
        this.inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void add(Review object) {
        super.add(object);
        reviews.add(object);
    }

    @Override
    public void clear() {
        super.clear();
        reviews.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final MainListHolder mHolder;
        View v = convertView;
        if (convertView == null)
        {
            mHolder = new MainListHolder();
            v = inflater.inflate(android.R.layout.two_line_list_item, null);
            mHolder.txt1 = (TextView) v.findViewById(android.R.id.text1);
            mHolder.txt2 = (TextView) v.findViewById(android.R.id.text2);
            v.setTag(mHolder);
        }
        else
        {
            mHolder = (MainListHolder) v.getTag();
        }

        v.setPadding(10,10,10,10);

        mHolder.txt1.setText(reviews.get(position).getAuthor());
        mHolder.txt2.setText(reviews.get(position).getContent());

        return v;
    }
    class MainListHolder
    {
        private TextView txt1;
        private TextView txt2;

    }
}