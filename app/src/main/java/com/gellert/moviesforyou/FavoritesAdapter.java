package com.gellert.moviesforyou;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gellert.moviesforyou.provider.FavoritesColumns;


public class FavoritesAdapter extends CursorAdapter {

    public FavoritesAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    private static class ViewHolder{
        TextView tw1;

        public ViewHolder(View v) {
            this.tw1 = (TextView) v.findViewById(android.R.id.text1);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        v.setTag(new ViewHolder(v));
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();
        final String title = cursor.getString(cursor.getColumnIndex(FavoritesColumns.TITLE));
        vh.tw1.setText(title);
    }
}
