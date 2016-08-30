package com.example.hhoo7.popularview;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hhoo7.popularview.data.MovieContract;

/**
 * Created by hhoo7 on 2016/8/30.
 */
public class ForecastAdapter extends CursorAdapter {

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.gridview_item,parent,false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view;
        String posterUri = cursor.getString(cursor.getColumnIndex(MovieContract.DetailEntry.COLUMN_POSTER_PATH));
        PublicMethod.loadPicture(context,posterUri,imageView);
    }
}
