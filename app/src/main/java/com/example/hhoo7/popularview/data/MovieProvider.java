package com.example.hhoo7.popularview.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    public static final int TABLE_DETAIL_DIR = 0;
    public static final int TABLE_DETAIL_ITEM = 1;

    public static UriMatcher uriMatcher;
    private static final String authority = "com.example.hhoo7.popularview.data";
    private MovieDbHelper dbHelper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // content://com.example.hhoo7.popularview.data/detail
        uriMatcher.addURI(authority, "detail", TABLE_DETAIL_DIR);
        // content://com.example.hhoo7.popularview.data/detail
        uriMatcher.addURI(authority, "detail/#", TABLE_DETAIL_ITEM);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = null;

        switch (uriMatcher.match(uri)) {
            case TABLE_DETAIL_DIR:
                cursor = db.query(MovieContract.DetailEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TABLE_DETAIL_ITEM:
                String id = uri.getPathSegments().get(1);
                break;
        }
        return cursor;
    }

    /*
    * 用于获取Uri对象所对应的MIME模型，一个内容URI所对应的MIME字符串主要由三部分组成。
    * 1、必须以vnd开头
    * 2、如果内容URI以路径结尾，则后接android.cursor.dir/，如果内容URI以id结尾，则后接android.cursor.item/
    * 3、最后接上vnd.<authority>.<path>
    * 示例 vnd.android.cursor.dir/vnd.com.example.hhoo7.popularview.data.detail
    * */
    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TABLE_DETAIL_DIR:
                return "vnd.android.cursor.dir/vnd.com.example.hhoo7.popularview.data.detail";
            case TABLE_DETAIL_ITEM:
                return "vnd.android.cursor.item/vnd.com.example.hhoo7.popularview.data.detail";
        }
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
