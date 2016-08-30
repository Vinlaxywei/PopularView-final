package com.example.hhoo7.popularview.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.hhoo7.popularview.data.MovieContract.DetailEntry;
import com.example.hhoo7.popularview.data.MovieContract.TrailerEntry;

/*
* 定义一个继承自SQLiteOpenHelper的子类，作为数据库。
* 包含三张表格，分别是MovieDetail，
* */
public class MovieDbHelper extends SQLiteOpenHelper {

    //数据库版本号
    private static final int DATABASE_VERSION = 1;
    //数据库名称
    static final String DATABASE_NAME = "movie.db";

    /*
    * 第三个参数允许我们在查询数据的时候返回一个自定义的Cursor，一般都是传入null
    * */
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*
        * 在数据库中创建一张存放MovieDetail的表格
        * */
        final String SQL_CREATE_DETAIL_TABLE = "CREATE TABLE " + DetailEntry.TABLE_NAME + " ("
                + DetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DetailEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + DetailEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                + DetailEntry.COLUMN_OVER_VIEW + " TEXT NOT NULL, "
                + DetailEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, "
                + DetailEntry.COLUMN_DATE + " TEXT NOT NULL, "
                + DetailEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, "
                + DetailEntry.COLUMN_FAVORITE + " BIT );";

        /*
        * 创建一张存放Trailer的表格
        * */
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " ("
                + TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TrailerEntry.COLUMN_MOVIEID_KEY + " INTEGER NOT NULL, "
                + TrailerEntry.COLUMN_YOUTUBE_KEY + " TEXT NOT NULL, "
                + TrailerEntry.COLUMN_TRAILER_TITLE + " TEXT NOT NULL, "

                //添加外键，获取movieID
                + " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIEID_KEY + ") REFERENCES "
                + DetailEntry.TABLE_NAME + "(" + DetailEntry._ID + "));";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DETAIL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DetailEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
