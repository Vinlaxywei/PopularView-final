package com.example.hhoo7.popularview.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MovieProvider extends ContentProvider {

    /*
    * 构建URI的识别编号
    * */
    static final int TABLE_DETAIL_DIR = 1;
    static final int TABLE_DETAIL_ITEM = 2;
    static final int TABLE_TRAILER_DIR = 3;
    static final int TABLE_TRAILER_ITEM = 4;
    static final int TABLE_REVIEW_DIR = 5;
    static final int TABLE_REVIEW_ITEM = 6;

    private static final UriMatcher uriMatcher;

    private MovieDbHelper dbHelper;
    private SQLiteDatabase database;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // content://com.example.hhoo7.popularview.data/detail
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_DETAILE, TABLE_DETAIL_DIR);
        // content://com.example.hhoo7.popularview.data/detail/#
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_DETAILE + "/#", TABLE_DETAIL_ITEM);
        // content://com.example.hhoo7.popularview.data/trailer
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_TRAILER, TABLE_TRAILER_DIR);
        // content://com.example.hhoo7.popularview.data/trailer/#
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_TRAILER + "/#", TABLE_TRAILER_ITEM);
        // content://com.example.hhoo7.popularview.data/review
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_REVIEW, TABLE_REVIEW_DIR);
        // content://com.example.hhoo7.popularview.data/review/#
        uriMatcher.addURI(DatabaseContract.CONTENT_AUTHORITY, DatabaseContract.PATH_REVIEW + "/#", TABLE_REVIEW_ITEM);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        database = dbHelper.getWritableDatabase();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        String movieId;
        switch (uriMatcher.match(uri)) {
            case TABLE_DETAIL_DIR:
                cursor = database.query(DatabaseContract.DetailEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, sortOrder);
                break;
            case TABLE_DETAIL_ITEM:
                movieId = uri.getPathSegments().get(1);
                cursor = database.query(
                        DatabaseContract.DetailEntry.TABLE_NAME,
                        columns,
                        DatabaseContract.DetailEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movieId}, null, null, sortOrder);
                break;
            case TABLE_TRAILER_DIR:
                cursor = database.query(DatabaseContract.TrailerEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, sortOrder);
                break;
            case TABLE_TRAILER_ITEM:
                movieId = uri.getLastPathSegment();
                cursor = database.query(
                        DatabaseContract.TrailerEntry.TABLE_NAME,
                        columns,
                        DatabaseContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movieId}, null, null, sortOrder);
                break;
            case TABLE_REVIEW_DIR:
                cursor = database.query(DatabaseContract.ReviewEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, sortOrder);
                break;
            case TABLE_REVIEW_ITEM:
                movieId = uri.getLastPathSegment();
                cursor = database.query(
                        DatabaseContract.ReviewEntry.TABLE_NAME,
                        columns,
                        DatabaseContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movieId}, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            case TABLE_DETAIL_DIR:
            case TABLE_DETAIL_ITEM:
                long detailRowId = database.insert(DatabaseContract.DetailEntry.TABLE_NAME, null, contentValues);
                returnUri = DatabaseContract.DetailEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(detailRowId)).build();
                break;
            case TABLE_TRAILER_DIR:
            case TABLE_TRAILER_ITEM:
                long trailerRowId = database.insert(DatabaseContract.TrailerEntry.TABLE_NAME, null, contentValues);
                returnUri = DatabaseContract.TrailerEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(trailerRowId)).build();
                break;
            case TABLE_REVIEW_DIR:
            case TABLE_REVIEW_ITEM:
                long reviewRowId = database.insert(DatabaseContract.ReviewEntry.TABLE_NAME, null, contentValues);
                returnUri = DatabaseContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(reviewRowId)).build();
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String whereClause, String[] whereArgs) {
        int deleteRowId = 0;
        switch (uriMatcher.match(uri)) {
            case TABLE_DETAIL_DIR:
                deleteRowId = database.delete(DatabaseContract.DetailEntry.TABLE_NAME, whereClause, whereArgs);
                break;
            case TABLE_TRAILER_DIR:
                deleteRowId = database.delete(DatabaseContract.TrailerEntry.TABLE_NAME, whereClause, whereArgs);
                break;
            case TABLE_REVIEW_DIR:
                deleteRowId = database.delete(DatabaseContract.ReviewEntry.TABLE_NAME, whereClause, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return deleteRowId;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String whereClause, String[] whereArgs) {
        int updateRowId = 0;
        switch (uriMatcher.match(uri)) {
            case TABLE_DETAIL_DIR:
                updateRowId = database.update(DatabaseContract.DetailEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
                break;
            case TABLE_DETAIL_ITEM:
                String movieId = uri.getLastPathSegment();
                updateRowId = database.update(DatabaseContract.DetailEntry.TABLE_NAME, contentValues,
                        DatabaseContract.DetailEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{movieId});
                break;
            case TABLE_TRAILER_DIR:
                updateRowId = database.update(DatabaseContract.TrailerEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
                break;
            case TABLE_REVIEW_DIR:
                updateRowId = database.update(DatabaseContract.ReviewEntry.TABLE_NAME, contentValues, whereClause, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (updateRowId != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateRowId;
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
        return null;
    }

}
