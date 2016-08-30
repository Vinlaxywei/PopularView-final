package com.example.hhoo7.popularview;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.hhoo7.popularview.data.MovieContract;
import com.example.hhoo7.popularview.data.MovieDbHelper;

/**
 * Created by hhoo7 on 2016/8/29.
 */
public class PublicMethod {

    public static void testDB(Context context) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValue = new ContentValues();
        testValue.put(MovieContract.DetailEntry.COLUMN_MOVIE_TITLE, "SUPERMAN");
        testValue.put(MovieContract.DetailEntry.COLUMN_POSTER_PATH, "posterpath");
        testValue.put(MovieContract.DetailEntry.COLUMN_OVER_VIEW, "over view");
        testValue.put(MovieContract.DetailEntry.COLUMN_VOTE_AVERAGE, 7.2);
        testValue.put(MovieContract.DetailEntry.COLUMN_DATE, "2015-02-03");
        testValue.put(MovieContract.DetailEntry.COLUMN_MOVIE_ID, 1233123);
        testValue.put(MovieContract.DetailEntry.COLUMN_FAVORITE, 1);

        long movieID = db.insert(MovieContract.DetailEntry.TABLE_NAME, null, testValue);
    }

}
