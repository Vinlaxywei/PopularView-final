package com.example.hhoo7.popularview.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

public class Testprivde extends AndroidTestCase {
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testInsert() {
        ContentValues contentValues = TestUtil.createTrailerTableTestValue();
        Uri uri = mContext.getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, contentValues);
    }

    public void testQuery() {
        mContext.getContentResolver().delete(MovieContract.TrailerEntry.CONTENT_URI,null,null);

        ContentValues contentValues = TestUtil.createDetailTableTestValue();
        mContext.getContentResolver().insert(MovieContract.DetailEntry.CONTENT_URI, contentValues);
        contentValues.clear();

        contentValues = TestUtil.createTrailerTableTestValue();
        mContext.getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, contentValues);
        contentValues.clear();
        contentValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, 331412);
        contentValues.put(MovieContract.TrailerEntry.COLUMN_VIDEO_LINK, "HTTP://YOUTUBE 02");
        contentValues.put(MovieContract.TrailerEntry.COLUMN_VIDEO_TITLE, "JUNGLE BOOK 02");
        mContext.getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, contentValues);

        Cursor cursor = mContext.getContentResolver().query(MovieContract.DetailEntry.CONTENT_URI, null, null, null, null);
        assertTrue("cursor is empty", cursor.moveToFirst());
//        assertTrue(""+cursor.getString(cursor.getColumnIndex(MovieContract.DetailEntry.COLUMN_MOVIE_TITLE)),5==3);

        Uri uri = MovieContract.TrailerEntry.CONTENT_URI.buildUpon().appendPath("331412").build();

        cursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.buildMovieIdUri(331412),
                null,
                null,
                null,
                null);
        assertTrue("cursor is empty", cursor.moveToFirst());
        String link01 = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_VIDEO_LINK));
        cursor.moveToNext();
        String link02 = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_VIDEO_LINK));
        assertTrue("第一个链接"+link01+"   第二个链接"+link02,5==3);
//        assertTrue(""+cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_VIDEO_TITLE)),5==3);

        cursor.close();
    }

    public void testUpdate() {
        ContentValues contentValues = TestUtil.createDetailTableTestValue();
        mContext.getContentResolver().insert(MovieContract.DetailEntry.CONTENT_URI, contentValues);
        contentValues.clear();

        contentValues = TestUtil.createTrailerTableTestValue();
        mContext.getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, contentValues);
        contentValues.clear();
        contentValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, 227764);
        contentValues.put(MovieContract.TrailerEntry.COLUMN_VIDEO_LINK, "HTTP://ASDASDASD");
        contentValues.put(MovieContract.TrailerEntry.COLUMN_VIDEO_TITLE, "JUNGLE BOOK 02");
        mContext.getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, contentValues);
        contentValues.clear();
        contentValues.put(MovieContract.TrailerEntry.COLUMN_VIDEO_TITLE, "JUNGLE BOOK 03");

        int rowId = mContext.getContentResolver().update(
                MovieContract.TrailerEntry.CONTENT_URI, contentValues,
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{"227764"});

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[]{"227764"},
                null,
                null
        );
        assertTrue("cursor is empty",cursor.moveToFirst());

//        assertTrue(""+cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_VIDEO_TITLE)),5==3);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
}
