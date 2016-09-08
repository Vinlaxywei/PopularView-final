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
    * 第二个参数给出数据库名称
    * 第三个参数允许我们在查询数据的时候返回一个自定义的Cursor，一般都是传入null
    * 第四个参数给出数据库版本号，更新数据库结构时，记得更新版本号
    * */
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
        * 创建电影详情表格的字符串
        * */
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

        /*
        * 创建电影预告片表格的字符串
        * */
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " ("
                + TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + TrailerEntry.COLUMN_VIDEO_LINK + " TEXT NOT NULL, "
                + TrailerEntry.COLUMN_VIDEO_TITLE + " TEXT NOT NULL "
                + " );";

        /*
        * 创建电影评论表格的字符串
        * */
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " ("
                + MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, "
                + MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL "
                + " );";

        /*
        * 创建三个表格
        * */
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_DETAIL_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*
        * 由于这些电影数据都具有时效性，所以更新数据库的操作这里没有进行数据的转存，
        * 而是直接删除表格，然后调用 onCreate 方法重新创建。
        * */
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DetailEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
