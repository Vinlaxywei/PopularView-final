package com.example.hhoo7.popularview.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hhoo7.popularview.data.DatabaseContract.DetailEntry;
import com.example.hhoo7.popularview.data.DatabaseContract.TrailerEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_DETAIL_TABLE = "CREATE TABLE " + DetailEntry.TABLE_NAME + " ("
                + DetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DetailEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + DetailEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                + DetailEntry.COLUMN_OVER_VIEW + " TEXT NOT NULL, "
                + DetailEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, "
                + DetailEntry.COLUMN_RELEASE_DATE + " DATE NOT NULL, "
                + DetailEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + DetailEntry.COLUMN_POPULARITY + " REAL NOT NULL, "
                + DetailEntry.COLUMN_RUNTIME + " INTEGER, "
                + DetailEntry.COLUMN_FAVORITE + " BIT NOT NULL DEFAULT 0 NOT NULL "
                + " ); ";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " ("
                + TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + TrailerEntry.COLUMN_VIDEO_LINK + " TEXT NOT NULL, "
                + TrailerEntry.COLUMN_VIDEO_TITLE + " TEXT NOT NULL "
                + " );";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + DatabaseContract.ReviewEntry.TABLE_NAME + " ("
                + DatabaseContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + DatabaseContract.ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, "
                + DatabaseContract.ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL "
                + " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*
        * 由于这些电影数据都具有时效性，所以更新数据库的操作这里没有进行数据的转存，
        * 而是直接删除数据库中的所有表格，然后调用 onCreate 方法重新创建表格。
        * */
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DetailEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
